package com.nariz.home.listeners;

import com.caronte.db.MySQL;
import com.caronte.json.JSONObject;
import com.nariz.home.process.Presence;
import com.nariz.home.utils.Utils;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.Properties;

@WebListener
public class ServletContextListener implements javax.servlet.ServletContextListener
{
	public static final Logger LOGGER = Logger.getLogger("nariz-iot-logger");
	private String pathWindows = "C:\\usr\\local\\apps\\properties\\";
	private String pathLinux = "/usr/local/apps/properties/";
	private String propertiesFile = "";
	private String projectName = "";
	private String osName = "";
	public static MySQL mySQL = null;
	private Presence p = null;

	@Override
	public void contextInitialized(ServletContextEvent event) 
	{
        projectName = event.getServletContext().getContextPath().replaceAll("/", "");
		osName = System.getProperty("os.name");
		propertiesFile = (osName.toUpperCase().contains("WINDOWS") ? pathWindows : pathLinux) + projectName + ".properties";

		LOGGER.info("Inicia despliegue de aplicativo " + projectName);
		LOGGER.info("Iniciando servicio " + projectName + " en sistema operativo " + osName);
		LOGGER.info("Cargando archivo de propiedades " + propertiesFile);

		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			Properties properties = new Properties();
			FileInputStream fileInputStream = new FileInputStream(propertiesFile);
			
			Utils.appProperties = new JSONObject();

			if (fileInputStream != null) 
			{
				properties.load(fileInputStream);

				Enumeration<Object> propertiesEnumeration = properties.keys();
				
				while(propertiesEnumeration.hasMoreElements())
				{
					String key = (String)propertiesEnumeration.nextElement();
					String value = properties.getProperty(key);
					Utils.appProperties.addPair(key, value);
				}
				  
				fileInputStream.close();
				mySQL = Utils.createMySQLInstance();

				if( !osName.toUpperCase().contains("WINDOWS") )
				{
					p = new Presence();
					p.whosThere();
				}
			}
		}
		catch(Exception e)
		{
			LOGGER.error("Error al cargar el archivo de configuracion " + propertiesFile);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) 
	{
		if( p!=null )
		{
			p.stop();
		}
		LOGGER.info("Deteniendo aplicativo " + projectName);
	}
}
