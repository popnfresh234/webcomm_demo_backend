package com.example.apilogin.model.uaf.request.reg.req_reg;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)

public class UafRequestRegRestRequestBody {
    String username;
    String rpServerData;
    String appID;
}
