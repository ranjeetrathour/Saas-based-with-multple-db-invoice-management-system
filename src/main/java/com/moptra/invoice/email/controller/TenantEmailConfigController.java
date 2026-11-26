package com.moptra.invoice.email.controller;

import com.moptra.invoice.authentication.utils.SecurityUtils;
import com.moptra.invoice.email.dto.req.EmailMessageDto;
import com.moptra.invoice.email.dto.req.TenantEmailConfigReq;
import com.moptra.invoice.email.dto.res.TenantEmailConfigRes;
import com.moptra.invoice.email.service.TenantEmailConfigService;
import com.moptra.invoice.email.service.TenantEmailDispatchService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenant/email-config")
@AllArgsConstructor
public class TenantEmailConfigController {
    private final TenantEmailConfigService tenantEmailConfigService;
    private final TenantEmailDispatchService tenantEmailDispatchService;

    @PostMapping
    public ResponseEntity<TenantEmailConfigRes> saveConfig(@Valid @RequestBody TenantEmailConfigReq tenantEmailConfigReq) {
        TenantEmailConfigRes tenantEmailConfigRes = tenantEmailConfigService.addMailkConfigInfo(tenantEmailConfigReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantEmailConfigRes);
    }

    /**
     * need to add auth and permission here only client or vendor can add this or need to discuss about
     * when this info will ask ?
     */
    @PostMapping("/client/{clientCompanyId}")
    public ResponseEntity<TenantEmailConfigRes> saveClientConfig(
            @PathVariable String clientCompanyId,
            @Valid @RequestBody TenantEmailConfigReq request) {
        TenantEmailConfigRes response = tenantEmailConfigService.saveOrUpdateClientConfig(clientCompanyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/test")
    public ResponseEntity<?> sendTestMail(){
        var cs = SecurityUtils.getCurrentUser();
        cs.getCompanyId();
        EmailMessageDto messageDto = new EmailMessageDto();
        messageDto.setTo("rathourm99@gmail.com");
        messageDto.setSubject("test mail");
        messageDto.setSubject("hello this is test mail");
        tenantEmailDispatchService.triggerEmailForTenant(cs.getCompanyId(), messageDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
