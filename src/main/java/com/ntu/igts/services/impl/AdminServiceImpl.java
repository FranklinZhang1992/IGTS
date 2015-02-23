package com.ntu.igts.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntu.igts.model.Admin;
import com.ntu.igts.model.AdminRole;
import com.ntu.igts.model.Role;
import com.ntu.igts.repository.AdminRepository;
import com.ntu.igts.repository.AdminRoleRepository;
import com.ntu.igts.repository.RoleRepository;
import com.ntu.igts.services.AdminService;
import com.ntu.igts.utils.MD5Util;

@Service
public class AdminServiceImpl implements AdminService {

    @Resource
    private AdminRepository adminRepository;
    @Resource
    private AdminRoleRepository adminRoleRepository;
    @Resource
    private RoleRepository roleRepository;

    @Override
    @Transactional
    public Admin create(Admin admin) {
        admin.setAdminPassword(MD5Util.getMd5(admin.getAdminPassword()));
        return adminRepository.create(admin);
    }

    @Override
    @Transactional
    public Admin update(Admin admin) {
        admin.setAdminPassword(MD5Util.getMd5(admin.getAdminPassword()));
        return adminRepository.update(admin);
    }

    @Override
    @Transactional
    public boolean delete(String adminId) {
        adminRepository.delete(adminId);
        ;
        Admin admin = adminRepository.findById(adminId);
        if (admin == null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Admin getAdminByAdminName(String adminName) {
        return adminRepository.getAdminByAdminName(adminName);
    }

    @Override
    public Admin getAdminDetailtById(String adminId) {
        Admin admin = adminRepository.findById(adminId);
        if (admin != null) {
            List<AdminRole> adminRoles = adminRoleRepository.getAdminRolesByAdminId(admin.getId());
            List<Role> roles = new ArrayList<Role>();
            for (AdminRole adminRole : adminRoles) {
                roles.add(roleRepository.findById(adminRole.getRoleId()));
                admin.setRoles(roles);
            }
        }
        return admin;
    }

    @Override
    public Admin getById(String adminId) {
        return adminRepository.findById(adminId);
    }

}
