package com.moptra.invoice.tenants.entites.nrd;

import io.mongock.utils.field.Field;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalFlow {

    @Field("manager_id")
    private String managerId;

    @Field("reviewed_at")
    private Instant reviewedAt;

    @Field("comments")
    private String comments;
}
