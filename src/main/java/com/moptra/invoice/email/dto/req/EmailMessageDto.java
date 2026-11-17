package com.moptra.invoice.email.dto.req;


import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageDto {
    private String to;
    private String subject;
    private String body;
    private String replyTo; // Optional: Employee email for reply
}