package com.example.apilogin.model.uaf.response.reg.req_reg_res;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class RequestRegRespResponseBody {
    private ArrayList<UAFRegistrationRequest> regRequests;
}
