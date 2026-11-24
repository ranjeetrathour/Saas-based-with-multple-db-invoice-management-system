package com.moptra.invoice.user.entities.rd;

import com.moptra.invoice.comman.entity.BaseEntity;
import com.moptra.invoice.roles.entites.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "platform_user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformUser extends BaseEntity {

    private String name;
    private String email;
    private String password;
    @ManyToOne
    private UserRole role;
}
