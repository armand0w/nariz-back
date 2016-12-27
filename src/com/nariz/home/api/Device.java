package com.nariz.home.api;

import com.caronte.json.JSONObject;
import com.caronte.rest.annotatios.*;
import com.caronte.rest.enums.CharsetType;
import com.caronte.rest.enums.ContentParamType;
import com.caronte.rest.enums.ContentType;
import com.caronte.rest.enums.MethodType;
import com.caronte.rest.exceptions.AuthorizationException;
import com.caronte.rest.exceptions.OperationExecutionException;
import com.nariz.home.controllers.DeviceController;

/**
 * Created by armando.castillo on 06/09/2016.
 */

@RESTController("/device")
public class Device {
    @RESTMethod(path="/action", method= MethodType.POST, contentType=ContentType.APPLICATION_JSON, produces=ContentType.APPLICATION_JSON, producesCharset=CharsetType.UTF_8)
    public JSONObject deviceAction(@RESTContentParam(ContentParamType.JSON) JSONObject device) throws OperationExecutionException, AuthorizationException {
        DeviceController controller = new DeviceController();
        return controller.device(device);
    }

    @RESTMethod(path="/list/{home}", method=MethodType.GET, contentType=ContentType.APPLICATION_JSON, produces=ContentType.APPLICATION_JSON, producesCharset=CharsetType.UTF_8)
    public JSONObject deviceList(@RESTPathParam("home") String home) throws OperationExecutionException, AuthorizationException {
        DeviceController controller = new DeviceController();
        return controller.list(home);
    }
}
