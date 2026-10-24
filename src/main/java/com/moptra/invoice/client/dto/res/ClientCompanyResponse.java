package com.moptra.invoice.client.dto.res;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientCompanyResponse {
    private String companyName;
    private String companyCode;
    private String email;
    private String phone;
    private String gst;
    private String billingAddress;
    private String city;
    private String state;
    private String country;
    private List<ClientBranchResponse> branches;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}