package com.moptra.invoice.email.service;

import com.moptra.invoice.email.dto.req.TenantEmailConfigReq;
import com.moptra.invoice.email.dto.res.TenantEmailConfigRes;

public interface TenantEmailConfigService {
    TenantEmailConfigRes addGmailConfigInfo(TenantEmailConfigReq tenantGmailConfigReq);
}
