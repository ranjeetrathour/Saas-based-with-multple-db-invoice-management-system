package com.moptra.invoice.roles.entites;

import com.moptra.invoice.comman.entity.BaseEntity;
import com.moptra.invoice.user.entities.rd.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Table(name = "user_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class UserRole extends BaseEntity {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;
    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<User> users;
}