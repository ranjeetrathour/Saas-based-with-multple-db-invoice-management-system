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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tenant/email-config")
@AllArgsConstructor
public class TenantEmailConfigController {
    private final TenantEmailConfigService tenantEmailConfigService;
    private final TenantEmailDispatchService tenantEmailDispatchService;

    @PostMapping
    public ResponseEntity<TenantEmailConfigRes> saveConfig(@Valid @RequestBody TenantEmailConfigReq tenantEmailConfigReq) {
        TenantEmailConfigRes tenantEmailConfigRes = tenantEmailConfigService.addGmailConfigInfo(tenantEmailConfigReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantEmailConfigRes);
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
