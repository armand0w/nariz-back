package com.nariz.home.controllers;

import com.caronte.json.JSONObject;
import com.caronte.rest.exceptions.OperationExecutionException;
import com.nariz.home.listeners.ServletContextListener;
import com.nariz.home.utils.Utils;
import com.sun.istack.internal.NotNull;

/**
 * Created by armando.castillo on 06/09/2016.
 */
public class UserController {

    public JSONObject user(@NotNull JSONObject usr) throws OperationExecutionException
    {
        JSONObject response;
        try {
            String accion = Utils.getJSONValue(usr, "/accion");
            switch ( accion ){
                case "CREATE":
                    response = ServletContextListener.mySQL.executeSP("SP_Crea_Usuario", usr);
                    break;
                case "EDIT":
                    response = ServletContextListener.mySQL.executeSP("SP_Edita_Usuario", usr);
                    break;
                case "DELETE":
                    response = ServletContextListener.mySQL.executeSP("SP_Elimina_Usuario", usr);
                    break;
                default:
                    throw new OperationExecutionException("ACTION NOT SUPPORTED");
            }

        } catch (Exception e){
            ServletContextListener.LOGGER.error(this.getClass().getName(), e);
            throw new OperationExecutionException( e.getMessage().replaceAll("\n", "").replaceAll("\"", "") );
        }
        return response;
    }

    public JSONObject list(@NotNull String home) throws OperationExecutionException
    {
        JSONObject jsonObject = new JSONObject();
        try {
            String query = "SELECT v_id_android AS device_id FROM `user` INNER JOIN userandroid on `user`.v_id_user = userandroid.v_id_user WHERE v_id_home = ? ";

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

    public JSONObject android(@NotNull JSONObject android) throws OperationExecutionException
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = ServletContextListener.mySQL.executeSP("SP_Inserta_Android", android);
        } catch (Exception e){
            ServletContextListener.LOGGER.error(this.getClass().getName(), e);
            throw new OperationExecutionException( e.getMessage().replaceAll("\n", "").replaceAll("\"", "") );
        }
        return jsonObject;
    }
}
