package com.nariz.home.api;

import com.caronte.json.JSONObject;
import com.caronte.rest.annotatios.*;
import com.caronte.rest.enums.CharsetType;
import com.caronte.rest.enums.ContentParamType;
import com.caronte.rest.enums.ContentType;
import com.caronte.rest.enums.MethodType;
import com.caronte.rest.exceptions.AuthorizationException;
import com.caronte.rest.exceptions.OperationExecutionException;
import com.nariz.home.controllers.UserController;

/**
 * Created by armando.castillo on 06/09/2016.
 */

@RESTController("/user")
public class User {
    @RESTMethod(path="/action", method= MethodType.POST, contentType= ContentType.APPLICATION_JSON, produces=ContentType.APPLICATION_JSON, producesCharset=CharsetType.UTF_8)
    public JSONObject userAction(@RESTContentParam(ContentParamType.JSON) JSONObject user) throws OperationExecutionException, AuthorizationException {
        UserController controller = new UserController();
        return controller.user(user);
    }

    @RESTMethod(path="/list/{home}", method=MethodType.GET, contentType=ContentType.APPLICATION_JSON, produces=ContentType.APPLICATION_JSON, producesCharset=CharsetType.UTF_8)
    public JSONObject userList(@RESTPathParam("home") String home) throws OperationExecutionException, AuthorizationException {
        UserController controller = new UserController();
        return controller.list(home);
    }

    @RESTMethod(path="/android", method= MethodType.POST, contentType= ContentType.APPLICATION_JSON, produces=ContentType.APPLICATION_JSON, producesCharset=CharsetType.UTF_8)
    public JSONObject android(@RESTContentParam(ContentParamType.JSON) JSONObject user) throws OperationExecutionException, AuthorizationException {
        UserController controller = new UserController();
        return controller.android(user);
    }
}
