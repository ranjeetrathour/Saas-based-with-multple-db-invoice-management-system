package com.moptra.invoice.user.entities.nrd;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tenant_configs") // Collection name inside the dynamic tenant database
public class TenantConfigEntity {

    @Id
    private String id;
    private String tenantCode;
    private String companyName;
    private boolean active;
    private LocalDateTime createdAt;
}