package com.ntu.igts.resource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;
import com.ntu.igts.constants.Constants;
import com.ntu.igts.enums.RoleEnum;
import com.ntu.igts.exception.ServiceErrorException;
import com.ntu.igts.exception.ServiceWarningException;
import com.ntu.igts.i18n.MessageBuilder;
import com.ntu.igts.i18n.MessageKeys;
import com.ntu.igts.model.Admin;
import com.ntu.igts.services.AdminService;
import com.ntu.igts.utils.CommonUtil;
import com.ntu.igts.utils.JsonUtil;

@Component
@Path("admin")
public class AdminResource extends BaseResource {

    @Context
    private HttpServletRequest webRequest;
    @Resource
    private MessageBuilder messageBuilder;
    @Resource
    private AdminService adminService;

    /**
     * Create a new admin
     * 
     * @param token
     *            The sessionContext id
     * @param inString
     *            The post body of admin pojo
     * @return The created admin
     */
    @POST
    @Path("entity")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(@HeaderParam(Constants.HEADER_X_AUTH_HEADER) String token, String inString) {
        filterSessionContext(token, RoleEnum.ADMIN);
        Admin pojo = JsonUtil.getPojoFromJsonString(inString, Admin.class);
        Admin admin = adminService.create(pojo);
        if (admin == null) {
            String[] param = { pojo.getAdminName() };
            throw new ServiceErrorException("Create admin " + pojo.getAdminName() + " failed.",
                            MessageKeys.CREATE_ADMIN_FAILED, param);
        }
        return JsonUtil.getJsonStringFromPojo(admin);
    }

    /**
     * Update admin's password
     * 
     * @param token
     *            The sessionContext id
     * @param inString
     *            The json put body of admin pojo
     * @return The updated admin
     */
    @PUT
    @Path("entity")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String update(@HeaderParam(Constants.HEADER_X_AUTH_HEADER) String token, String inString) {
        filterSessionContext(token, RoleEnum.ADMIN);
        Admin pojo = JsonUtil.getPojoFromJsonString(inString, Admin.class);
        Admin existingAdmin = checkAdminAvailability(pojo.getId());
        if (!existingAdmin.getAdminPassword().equals(pojo.getAdminPassword())) {
            existingAdmin.setAdminPassword(pojo.getAdminPassword());
            Admin updatedAdmin = adminService.update(existingAdmin);
            if (updatedAdmin == null) {
                String[] param = { existingAdmin.getAdminName() };
                throw new ServiceErrorException("Update admin " + existingAdmin.getAdminName() + " failed.",
                                MessageKeys.UPDATE_ADMIN_FAILED, param);
            }
            return JsonUtil.getJsonStringFromPojo(updatedAdmin);
        }
        return JsonUtil.getJsonStringFromPojo(existingAdmin);
    }

    /**
     * Delete the admin
     * 
     * @param token
     *            The sessionContext id
     * @param adminId
     *            The id of the admin
     * @return If succeed to delete the admin, will return success, or return fail
     */
    @DELETE
    @Path("entity/{adminid}")
    @Produces(MediaType.TEXT_PLAIN)
    public String delete(@HeaderParam(Constants.HEADER_X_AUTH_HEADER) String token, @PathParam("adminid") String adminId) {
        filterSessionContext(token, RoleEnum.ADMIN);
        checkAdminAvailability(adminId);
        boolean flag = adminService.delete(adminId);
        String[] param = { adminId };
        if (flag) {
            return messageBuilder.buildMessage(MessageKeys.DELETE_ADMIN_FOR_ID_SUCCESS, param, "Delete admin "
                            + adminId + " success.", CommonUtil.getLocaleFromRequest(webRequest));
        } else {

            return messageBuilder.buildMessage(MessageKeys.DELETE_ADMIN_FOR_ID_FAIL, param, "Delete admin " + adminId
                            + " fail.", CommonUtil.getLocaleFromRequest(webRequest));
        }
    }

    @GET
    @Path("entity/{adminid}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAdminById(@HeaderParam(Constants.HEADER_X_AUTH_HEADER) String token,
                    @PathParam("adminid") String adminId) {
        filterSessionContext(token, RoleEnum.ADMIN);
        return JsonUtil.getJsonStringFromPojo(adminService.getById(adminId));
    }

    private Admin checkAdminAvailability(String adminId) {
        Admin existingAdmin = adminService.getById(adminId);
        if (existingAdmin == null) {
            String[] param = { adminId };
            throw new ServiceWarningException("Cannot find admin for id " + adminId,
                            MessageKeys.ADMIN_NOT_FOUND_FOR_ID, param);
        } else {
            return existingAdmin;
        }
    }
}
