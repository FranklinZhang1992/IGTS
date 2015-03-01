package com.ntu.igts.resource;

import java.util.Date;

import javax.annotation.Resource;

import com.ntu.igts.enums.RoleEnum;
import com.ntu.igts.exception.UnAuthenticationException;
import com.ntu.igts.model.Admin;
import com.ntu.igts.model.SessionContext;
import com.ntu.igts.model.User;
import com.ntu.igts.services.AdminService;
import com.ntu.igts.services.SessionContextService;
import com.ntu.igts.services.UserService;
import com.ntu.igts.utils.CommonUtil;

public class BaseResource {

    @Resource
    private SessionContextService sessionContextService;
    @Resource
    private UserService userService;
    @Resource
    private AdminService adminService;

    /**
     * Get sessionContext by token
     * 
     * @param token
     *            The id of sessionContext
     * @param allowedRoles
     *            Roles which are allowed to access
     * @return If the user has the exact role and its session is not expired, return sessionContext, or throw exception
     */
    protected boolean filterSessionContext(String token, RoleEnum allowedRole) {
        SessionContext sessionContext = sessionContextService.getByToken(token);
        if (sessionContext != null) {
            if (!isSessionContextExpired(sessionContext)) {
                if (RoleEnum.USER.equals(allowedRole)) {
                    User user = userService.getUserDetailById(sessionContext.getUserId());
                    if (CommonUtil.isRoleAllowed(user.getRoles(), allowedRole)) {
                        return true;
                    }
                } else if (RoleEnum.ADMIN.equals(allowedRole)) {
                    Admin admin = adminService.getAdminDetailtById(sessionContext.getUserId());
                    if (CommonUtil.isRoleAllowed(admin.getRoles(), allowedRole)) {
                        return true;
                    }
                } else {
                    flushSessionContext(token);
                    return true;
                }
            }
        }
        throw new UnAuthenticationException();
    }

    protected void flushSessionContext(String token) {
        SessionContext sessionContext = sessionContextService.getByToken(token);
        sessionContextService.flushSessionContext(sessionContext);
    }

    private boolean isSessionContextExpired(SessionContext sessionContext) {
        Date date = new Date();
        if (date.getTime() < sessionContext.getExpireTime().getTime()) {
            return false;
        } else {
            sessionContextService.delete(sessionContext.getToken());
            return true;
        }
    }
}