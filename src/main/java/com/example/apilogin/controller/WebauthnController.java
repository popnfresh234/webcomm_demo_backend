package com.example.apilogin.controller;

import com.example.apilogin.entities.RoleEntity;
import com.example.apilogin.entities.UserEntity;
import com.example.apilogin.exceptions.AuthException;
import com.example.apilogin.model.response.LoginResponse;
import com.example.apilogin.model.webauthn.request.auth.req_do_auth.Fido2DoAuthReq;
import com.example.apilogin.model.webauthn.request.reg.do_req.Fido2DoRegReq;
import com.example.apilogin.model.webauthn.request.auth.req_auth.Fido2RequestAuthReq;
import com.example.apilogin.model.webauthn.request.reg.req_reg.Fido2RequestRegReq;
import com.example.apilogin.model.webauthn.response.auth.do_auth.Fido2DoAuthResp;
import com.example.apilogin.model.webauthn.response.reg.do_reg.Fido2DoRegResp;
import com.example.apilogin.model.webauthn.response.auth.req_auth.Fido2RequestAuthResp;
import com.example.apilogin.model.webauthn.response.reg.req_reg.Fido2RequestRegResp;
import com.example.apilogin.security.JwtIssuer;
import com.example.apilogin.services.UserService;
import com.example.apilogin.services.WebauthnService;
import com.example.apilogin.utils.AuthUtils;
import com.example.apilogin.utils.LogUtils;
import com.example.apilogin.utils.UserSingleton;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
@CrossOrigin
@RestController
@RequestMapping(path = "/webauthn")
public class WebauthnController {

    @Value("${fido.origin}")
    private String fidoOrigin;

    @Value("${fido.rp-id}")
    private String fidoRpId;

    private final JwtIssuer jwtIssuer;
    private final WebauthnService webauthnService;
    private final UserService userService;

    public WebauthnController(
            JwtIssuer jwtIssuer,
            WebauthnService webauthnService,
            UserService userService
    ) {
        this.jwtIssuer = jwtIssuer;
        this.webauthnService = webauthnService;
        this.userService = userService;
    }

    @PostMapping(path = "/requestReg")
    public Fido2RequestRegResp requestReg(
            @RequestBody Fido2RequestRegReq req,
            HttpServletRequest httpServletRequest) {
        log.info("POST /requestReg");

        try {
            req.getBody().setUsername(AuthUtils.getPrincipal().getUsername());
            req.getBody().setDisplayName(AuthUtils.getPrincipal().getName());
            req.getBody().setOrigin(fidoOrigin);
            req.getBody().setRpId(fidoRpId);
            req.getBody().setRpName("Fido Lab Relying Party");
            return webauthnService.requestReg(req);
        } catch (Exception e) {
            throw AuthException.builder().msg(e.getMessage()).operation(LogUtils.OPERATION_LOGIN)
                    .ip(httpServletRequest.getRemoteAddr()).target(AuthUtils.getPrincipal().getAccount()).build();
        }
    }

    @PostMapping(path = "/doReg")
    public Fido2DoRegResp doReg(@RequestBody Fido2DoRegReq req) {
        return webauthnService.doReg(req);
    }

    @PostMapping(path = "/requestAuth")
    public Fido2RequestAuthResp requestAuth(@RequestBody Fido2RequestAuthReq req) {
        String username = req.getBody().getUsername();
        UserSingleton.getInstance().setUsername(username);
        req.getBody().setOrigin(fidoOrigin);
        req.getBody().setRpId(fidoRpId);
        return webauthnService.requestAuth(req);
    }


    @PostMapping(path = "/doAuth")
    public Fido2DoAuthResp doAuth(@RequestBody Fido2DoAuthReq req, HttpServletRequest httpServletRequest) {

        Fido2DoAuthResp res = webauthnService.doAuth(req);
        //If auth fails, throw auth exception
        if (!res.getHeader().getCode().equals("1200")) {
            throw AuthException.builder().msg("Authorization Failed").build();
        }
        try {
            // Look up the user by account
            Optional<UserEntity> opt = userService.findByAccount(UserSingleton.getInstance().getUsername());
            UserEntity user = opt.orElseThrow();

            // Login user with correct roles
            Set<RoleEntity> roles = user.getRole();
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            for (RoleEntity role : roles) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getRole());
                authorities.add(authority);
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    user.getAccount(),
                    "",
                    authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // Issue JWT for login flow
            List<String> stringAuths = new ArrayList<>();
            for (SimpleGrantedAuthority auth : authorities) {
                String authority = auth.getAuthority();
                stringAuths.add(authority);
            }
            var token = jwtIssuer.issue(
                    user.getId(),
                    user.getAccount(),
                    user.getName(),
                    user.getEmail(),
                    stringAuths);

            // Add the login response to the fido response for frontend
            res.setLoginResponse(new LoginResponse(
                    "Login Success",
                    token,
                    stringAuths));

            //Clear out singleton
            UserSingleton.getInstance().setUsername("");
        } catch (Exception e) {
            throw AuthException.builder().msg(e.getMessage()).operation(LogUtils.OPERATION_LOGIN)
                    .ip(httpServletRequest.getRemoteAddr()).target(AuthUtils.getPrincipal().getAccount()).build();
        }
        return res;
    }
}
