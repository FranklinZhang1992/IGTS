package com.ntu.igts.resource;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.springframework.stereotype.Component;

import com.ntu.igts.constants.Constants;
import com.ntu.igts.enums.RoleEnum;
import com.ntu.igts.exception.ServiceWarningException;
import com.ntu.igts.i18n.MessageBuilder;
import com.ntu.igts.i18n.MessageKeys;
import com.ntu.igts.model.Favorite;
import com.ntu.igts.model.SessionContext;
import com.ntu.igts.model.container.FavoriteList;
import com.ntu.igts.services.FavoriteService;
import com.ntu.igts.utils.CommonUtil;
import com.ntu.igts.utils.JsonUtil;
import com.ntu.igts.utils.StringUtil;

@Component
@Path("favorite")
public class FavoriteResource extends BaseResource {

    @Context
    private HttpServletRequest webRequest;
    @Resource
    private MessageBuilder messageBuilder;
    @Resource
    private FavoriteService favoriteService;

    @POST
    @Path("entity")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String create(@HeaderParam(Constants.HEADER_X_AUTH_HEADER) String token, String inString) {
        SessionContext sessionContext = filterSessionContext(token, RoleEnum.USER);
        Favorite pojo = JsonUtil.getPojoFromJsonString(inString, Favorite.class);
        if (StringUtil.isEmpty(pojo.getUserId())) {
            pojo.setUserId(sessionContext.getUserId());
        }
        Favorite insertedFavorite = favoriteService.create(pojo);
        return JsonUtil.getJsonStringFromPojo(insertedFavorite);
    }

    @DELETE
    @Path("entity/{favoriteid}")
    @Produces(MediaType.TEXT_PLAIN)
    public String delete(@HeaderParam(Constants.HEADER_X_AUTH_HEADER) String token,
                    @PathParam("favoriteid") String favoriteId) {
        filterSessionContext(token, RoleEnum.USER);
        checkFavoriteAvailability(favoriteId);
        boolean flag = favoriteService.delete(favoriteId);
        if (flag) {
            return messageBuilder.buildMessage(MessageKeys.DELETE_FAVORITE_SUCCESS, "Delete favorite success.",
                            CommonUtil.getLocaleFromRequest(webRequest));
        } else {
            return messageBuilder.buildMessage(MessageKeys.DELETE_FAVORITE_FAIL, "Delete favorite fail.",
                            CommonUtil.getLocaleFromRequest(webRequest));
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getFavorites(@HeaderParam(Constants.HEADER_X_AUTH_HEADER) String token) {
        SessionContext sessionContext = filterSessionContext(token, RoleEnum.USER);
        return JsonUtil.getJsonStringFromPojo(new FavoriteList(favoriteService.getByUserId(sessionContext.getUserId())));
    }

    private Favorite checkFavoriteAvailability(String favoriteId) {
        Favorite existingFavorite = favoriteService.getById(favoriteId);
        if (existingFavorite == null) {
            String[] param = { favoriteId };
            throw new ServiceWarningException("Cannot find favorite for id " + favoriteId,
                            MessageKeys.FAVORITE_NOT_FOUND_FOR_ID, param);
        } else {
            return existingFavorite;
        }
    }
}
