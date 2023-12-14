package com.example.apilogin.utils;

import com.example.apilogin.entities.UserLogEntity;
import com.example.apilogin.security.JwtToPrincipalConverter;
import com.example.apilogin.security.UserPrincipal;
import com.example.apilogin.services.UserLogService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;

public class LogUtils {

    //FIDO2
    public static final String OPERATION_LOGIN = "login";
    public static final String OPERATION_SIGNUP = "signup";
    public static final String OPERATION_RECOVERY_REQUEST = "recovery_request";
    public static final String OPERATION_RECOVERY_VERIFY = "recovery_verify";
    public static final String OPERATION_RECOVERY_RESET = "recovery_reset";
    public static final String REQ_REG_REQ = "webauthn_req_reg_req";
    public static final String DO_REG_REQ = "webauthn_do_req_req";
    public static final String REQ_AUTH_REQ = "webauthn_req_auth_req";
    public static final String DO_AUTH_REQ = "webauthn_do_auth_req";

    //UAF

    public static final String UAF_REQ_FACETS_REQ = "uaf_req_facets_req";

    public static final String UAF_REQ_REG_REQ = "uaf_req_reg_req";
    public static final String UAF_REQ_REG_RES = "uaf_req_reg_res";

    public static final String UAF_DO_REG_REQ = "uaf_do_reg_req";
    public static final String UAF_DO_REG_RES = "uaf_do_reg_res";


    public static final String UAF_REQ_AUTH_REQ = "uaf_req_auth_req";
    public static final String UAF_REQ_AUTH_RES = "uaf_req_auth_res";

    public static final String UAF_DO_AUTH_REQ = "uaf_do_auth_req";
    public static final String UAF_DO_AUTH_RES = "uaf_do_auth_res";


    public static final String UAF_DO_DEREG_REQ = "uaf_do_dereg_req";

    public static final String UAF_REQ_QR_CODE_REQ = "uaf_req_qrcde_req";
    public static final String UAF_VALIDATE_QR_CODE_REQ = "uaf_validate_qrcde_req";


    public static final String UAF_REQ_PAIR_AUTH_REQ = "uaf_req_pair_auth_req";

    public static UserLogEntity buildLog(
            UserLogService userLogService,
            String operation,
            String target,
            String ip,
            String msg,
            boolean success) {
        UserLogEntity log = new UserLogEntity();
        log.setOperation(operation);
        log.setIp(ip);
        log.setTarget(target);
        log.setLog(msg);
        log.setTimestamp(LocalDateTime.now());
        log.setSuccess(success);
        userLogService.save(log);
        return log;
    }


}
