package com.nariz.home.utils;

import com.caronte.db.MySQL;
import com.caronte.jpath.JPATH;
import com.caronte.json.JSON;
import com.caronte.json.JSONObject;
import com.caronte.json.JSONValue;
import com.caronte.rest.enums.CharsetType;
import com.caronte.rest.enums.ContentType;
import com.caronte.rest.enums.MethodType;
import com.caronte.rest.exceptions.OperationExecutionException;
import com.caronte.rest.http.RESTClient;
import com.nariz.home.controllers.DeviceController;
import com.nariz.home.controllers.UserController;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Utils 
{
	public static JSONObject appProperties;
	
	public static MySQL createMySQLInstance() throws Exception
	{
		String ip = (String) JPATH.find(appProperties, "ip").getValue();
		String puerto = (String) JPATH.find(appProperties, "puerto").getValue();
		String esquema = (String) JPATH.find(appProperties, "esquema").getValue();
		String usuario = (String) JPATH.find(appProperties, "usuario").getValue();
		String password = (String) JPATH.find(appProperties, "password").getValue();
		String tamBloquePool = (String) JPATH.find(appProperties, "tam_bloque_pool").getValue();
		String maxTamPool = (String) JPATH.find(appProperties, "max_tam_pool").getValue();

		return new MySQL(ip, puerto, usuario, password, esquema, Integer.valueOf(tamBloquePool), Integer.valueOf(maxTamPool));
	}

	public static JSONObject getJsonObject(JSONObject jsonObject, String path, String name)
	{
		JSONObject json = new JSONObject();
		try {
			JSONValue jsonValue = JPATH.find(jsonObject, path);
			if (jsonValue != null && jsonValue.getValue() != null) {
				String sJson = jsonValue.getValue().toString();
				json = JSON.parse( "{ \"" + name + "\": " + sJson + " } " );
			}
		} catch (Exception e){
			return new JSONObject();
		}

		return json;
	}
	
	public static String getJSONValue(JSONObject jsonObject, String path)
	{
		try {
			JSONValue jsonValue = JPATH.find(jsonObject, path);

			if (jsonValue != null && jsonValue.getValue() != null) {
				return jsonValue.getValue().toString();
			}
		} catch (Exception e){
			System.err.println( "Path: " + path );
			return "";
		}
		
		return "";
	}

	public static void notifyAllAndroid() throws Exception
	{
		JSONObject devices = new JSONObject();
		JSONObject idDevices = new JSONObject();
		JSONObject notification = new JSONObject();
		JSONObject request = new JSONObject();
		String home = Utils.getJSONValue(Utils.appProperties, "/home");
		int n = 0;

		DeviceController dc = new DeviceController();
		UserController uc = new UserController();

		devices = Utils.getJsonObject( dc.list(home), "/data", "devices");
		idDevices = Utils.getJsonObject( uc.list(home), "/data", "idDevices");
		n =  JPATH.count(idDevices, "/idDevices");

		notification.addPair("title", "Dispositivos");
		notification.addPair("body", "Dispositivos cambiaron de estado");
		request.addPair("notification", notification);
		request.addPair("data", devices);

		for( int i=0; i<n; i++ )
		{
			JPATH.remove(request, "/to");
			request.addPair("to", Utils.getJSONValue(idDevices, "/idDevices[" + i + "]/device_id"));
		}

		HashMap<String, String> headers = new HashMap<>();
		headers.put("Authorization", Utils.getJSONValue(Utils.appProperties, "/key-fairebase"));
		RESTClient.execute(Utils.getJSONValue(Utils.appProperties, "/url-firebase"), MethodType.POST,
				ContentType.APPLICATION_JSON, CharsetType.UTF_8, request, headers, ContentType.APPLICATION_JSON);
	}

	public static void notifyPush(String name, String status) throws Exception
	{
		JSONObject idDevices = new JSONObject();
		JSONObject notification = new JSONObject();
		JSONObject request = new JSONObject();
		String home = Utils.getJSONValue(Utils.appProperties, "/home");
		int n = 0;

		UserController uc = new UserController();

		idDevices = Utils.getJsonObject( uc.list(home), "/data", "idDevices");
		n =  JPATH.count(idDevices, "/idDevices");

		notification.addPair("title", name);
		notification.addPair("body", "Se " + (status.equals("true") ? "Conecto" : "Desconecto"));
		request.addPair("notification", notification);
		request.addPair("priority", "high");

		for( int i=0; i<n; i++ )
		{
			JPATH.remove(request, "/to");
			request.addPair("to", Utils.getJSONValue(idDevices, "/idDevices[" + i + "]/device_id"));
		}

		HashMap<String, String> headers = new HashMap<>();
		headers.put("Authorization", Utils.getJSONValue(Utils.appProperties, "/key-fairebase"));
		RESTClient.execute(Utils.getJSONValue(Utils.appProperties, "/url-firebase"), MethodType.POST,
				ContentType.APPLICATION_JSON, CharsetType.UTF_8, request, headers, ContentType.APPLICATION_JSON);
	}

	public static String getJSONValueInt(JSONObject jsonObject, String path)
	{
		try {
			JSONValue jsonValue = JPATH.find(jsonObject, path);

			if (jsonValue != null && jsonValue.getValue() != null) {
				return jsonValue.getValue().toString();
			}
		} catch (Exception e){
			System.err.println( "Path: " + path );
			return "0";
		}

		return "0";
	}

	public static Integer getJSONValueInteger(JSONObject jsonObject, String path)
	{
		try {
			JSONValue jsonValue = JPATH.find(jsonObject, path);

			if (jsonValue != null && jsonValue.getValue() != null) {
				return Integer.parseInt(jsonValue.getValue().toString());
			}
		} catch (Exception e){
			System.err.println( "Path: " + path );
			return 0;
		}

		return 0;
	}

	public static String getJSONValueDate(JSONObject jsonObject, String path)
	{
		String sdate = "";
		try {
			sdate = getJSONValue(jsonObject, path);
			if( sdate.length() > 0 ) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
				Date date = sdf.parse(sdate);
			}
		} catch (Exception e){
			System.err.println( "Path: " + path );
			return "";
		}

		return sdate;
	}

	public static String getCurrentDate()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.HOUR_OF_DAY, 0);
			return dateFormat.format( now.getTime() );
		} catch (Exception e){
			return dateFormat.format( new Date() );
		}
	}

}
