package com.moptra.invoice.client.dto.res;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientBranchResponse {

    private String clientCompanyId;
    private String branchName;
    private String branchCode;
    private String gst;
    private String billingAddress;
    private String city;
    private String state;
    private String country;
    private Boolean isActive;
}