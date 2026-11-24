package com.moptra.invoice.roles.service;

import com.moptra.invoice.roles.dto.req.UserRoleReq;
import com.moptra.invoice.roles.dto.res.UserRoleRes;

import java.util.List;

public interface UserRoleService {

    UserRoleRes createRole(UserRoleReq request);

    UserRoleRes getRoleById(String id);

    List<UserRoleRes> getAllRoles();

    UserRoleRes updateRole(String id, UserRoleReq request);

    void deleteRole(String id);
}