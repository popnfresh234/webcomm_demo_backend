package com.example.apilogin.model.webauthn.response.reg.req_reg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
public class AuthenticatorSelectionCriteria {

	private String authenticatorAttachment;
	private Boolean requireResidentKey;
	private String userVerification;
}
