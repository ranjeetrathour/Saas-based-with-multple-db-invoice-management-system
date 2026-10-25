package com.moptra.invoice.user.controller;
import com.moptra.invoice.user.entities.nrd.TenantConfigEntity;
import com.moptra.invoice.user.service.TenantMongoProvisioningServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenant-provision")
@RequiredArgsConstructor
public class TenantProvisionController {

    private final TenantMongoProvisioningServiceImpl provisioningService;

    @PostMapping("/mongo")
    public ResponseEntity<TenantConfigEntity> provisionMongo(
            @RequestParam String dbName,
            @RequestParam String tenantCode,
            @RequestParam String companyName) {

        TenantConfigEntity result = provisioningService.provisionAndInsertTestData(dbName, tenantCode, companyName);
        return ResponseEntity.ok(result);
    }
}
