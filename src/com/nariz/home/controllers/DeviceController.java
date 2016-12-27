package com.nariz.home.controllers;

import com.caronte.db.MySQL;
import com.caronte.json.JSON;
import com.caronte.json.JSONObject;
import com.caronte.rest.exceptions.OperationExecutionException;
import com.nariz.home.listeners.ServletContextListener;
import com.nariz.home.utils.Utils;
import com.sun.istack.internal.NotNull;
import org.apache.log4j.Logger;

/**
 * Created by armando.castillo on 06/09/2016.
 */
public class DeviceController
{

    public JSONObject device(@NotNull JSONObject device) throws OperationExecutionException
    {
        JSONObject response;
        try
        {
            String accion = Utils.getJSONValue(device, "/accion");

            switch ( accion.toUpperCase() )
            {
                case "CREATE":
                    response = ServletContextListener.mySQL.executeSP("SP_New_Device", device);
                    break;
                case "EDIT":
                    response = ServletContextListener.mySQL.executeSP("SP_Edit_Device", device);
                    break;
                case "DELETE":
                    response = ServletContextListener.mySQL.executeSP("SP_Delete_Device", device);
                    break;
                default:
                    throw new OperationExecutionException("ACTION NOT SUPPORTED");
            }
        }
        catch (Exception e)
        {
            ServletContextListener.LOGGER.error(this.getClass().getName(), e);
            throw new OperationExecutionException( e.getMessage().replaceAll("\n", "").replaceAll("\"", "") );
        }
        return response;
    }

    public JSONObject list(@NotNull String home) throws OperationExecutionException
    {
        JSONObject jsonObject = new JSONObject();
        try {
            String query = "SELECT v_id_home AS 'home', v_ip AS 'ip', v_mac AS 'mac', v_name AS 'name', v_connect AS 'isConnect', d_last_update AS 'lastUpdate', v_hostname AS 'hostname' FROM device WHERE v_id_home = ? ";

            JSONObject param = new JSONObject();
            JSONObject params = new JSONObject();

            try
            {
                params.resetArray();
                param = new JSONObject();
                param.addPair("type", "string");
                param.addPair("value", home);
                params.addToArray(param);

                params.saveArray("parameters");
            }
            catch (Exception e)
            {
                ServletContextListener.LOGGER.error("Parametros No Validos");
                ServletContextListener.LOGGER.error(this.getClass().getName(), e);
                throw new OperationExecutionException("Parametros No Validos");
            }

            jsonObject = ServletContextListener.mySQL.executeQuery(query, params);
        }
        catch (Exception e)
        {
            ServletContextListener.LOGGER.error(this.getClass().getName(), e);
            throw new OperationExecutionException( e.getMessage().replaceAll("\n", "").replaceAll("\"", "") );
        }

        return jsonObject;
    }

    public static void changeStatus(@NotNull String device) throws OperationExecutionException
    {
        try
        {
            JSONObject json = new JSONObject( JSON.parse(device) );
            String connect = String.valueOf( !Boolean.parseBoolean(Utils.getJSONValue(json, "/isConnect")) );
            JSONObject aux = new JSONObject();
            aux.addPair("p_mac", Utils.getJSONValue(json, "/mac"));
            aux.addPair("p_id_home", Utils.getJSONValue(json, "/home"));
            aux.addPair("p_name", Utils.getJSONValue(json, "/name"));
            aux.addPair("p_ip", Utils.getJSONValue(json, "/ip"));
            aux.addPair("p_hostname", Utils.getJSONValue(json, "/hostname"));
            aux.addPair("p_connect", connect);
            aux.addPair("accion", "EDIT");

            DeviceController deviceController = new DeviceController();
            deviceController.device(aux);
            Utils.notifyPush(Utils.getJSONValue(aux, "p_name"), connect);
        }
        catch (Exception e)
        {
            ServletContextListener.LOGGER.error("changeStatus", e);
            throw new OperationExecutionException( e.getMessage().replaceAll("\n", "").replaceAll("\"", "") );
        }
    }
}
