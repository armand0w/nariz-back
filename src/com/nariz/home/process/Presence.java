package com.nariz.home.process;

import com.caronte.jpath.JPATH;
import com.caronte.json.JSONObject;
import com.caronte.rest.exceptions.OperationExecutionException;
import com.nariz.home.controllers.DeviceController;
import com.nariz.home.listeners.ServletContextListener;
import com.nariz.home.utils.Utils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by armando.castillo on 06/09/2016.
 */
public class Presence
{
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> beeperHandle = null;

    public void whosThere() throws OperationExecutionException{
        final Runnable db = () ->
        {
            try
            {
                JSONObject jsonObject = new JSONObject();
                DeviceController dc = new DeviceController();
                Process process = null;
                String adress = "";
                String isCon = null;
                String aux = null;
                String mac = "";
                int n = 0;
                int times = Utils.getJSONValueInteger(Utils.appProperties, "/times");

                jsonObject = Utils.getJsonObject( dc.list(Utils.getJSONValue(Utils.appProperties, "/home")), "/data", "devices");
                n = JPATH.count(jsonObject, "devices");

                for(int i=0; i<10; i++)
                {
                    process = Runtime.getRuntime().exec( "sudo arp-scan -l" );
                    BufferedReader reader = new BufferedReader( new InputStreamReader(process.getInputStream()) );
                    BufferedReader readerr = new BufferedReader( new InputStreamReader(process.getErrorStream()) );
                    while ( (aux = reader.readLine()) != null )
                    {
                        adress += aux + "\n";
                    }
                    while ( (aux = readerr.readLine()) != null )
                    {
                        adress += aux + "\n";
                    }
                }

                /*adress = "Interface: eth0, datalink type: EN10MB (Ethernet)\n" +
                        "Starting arp-scan 1.8.1 with 256 hosts (http://www.nta-monitor.com/tools/arp-scan/)\n" +
                        "192.168.0.12\te8:ed:05:0b:8c:05\t(Unknown)\n" +
                        "192.168.0.6\te4:90:7e:6e:09:88\t(Unknown)\n" +
                        "\n" +
                        "2 packets received by filter, 0 packets dropped by kernel\n" +
                        "Ending arp-scan 1.8.1: 256 hosts scanned in 2.472 seconds (103.56 hosts/sec). 2 responded\n";*/

                for (int i=0; i<n; i++)
                {
                    mac = Utils.getJSONValue(jsonObject, "/devices[" + i + "]/p_mac");
                    isCon = Utils.getJSONValue(jsonObject, "/devices[" + i + "]/p_onnect");

                    if( !isCon.equals( String.valueOf(adress.contains(mac)) ) )
                    {
                        DeviceController.changeStatus( Utils.getJSONValue(jsonObject, "/devices[" + i + "]") );
                    }
                }
            }
            catch (Exception e)
            {
                ServletContextListener.LOGGER.error(this.getClass().getName(), e);
            }
        };

        beeperHandle = scheduler.scheduleAtFixedRate( db, 0,
                Long.parseLong(Utils.getJSONValue(Utils.appProperties, "/time")), TimeUnit.SECONDS );
        scheduler.schedule( () -> beeperHandle.cancel(true), 99, TimeUnit.DAYS );
    }

    public void stop()
    {
        if ( beeperHandle != null )
        {
            beeperHandle.cancel(false);
        }
    }
}
