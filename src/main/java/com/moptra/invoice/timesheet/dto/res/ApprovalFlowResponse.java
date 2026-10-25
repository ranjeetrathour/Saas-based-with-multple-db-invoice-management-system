package com.moptra.invoice.timesheet.dto.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprovalFlowResponse {
    private String managerId;
    private Instant reviewedAt;
    private String comments;
}