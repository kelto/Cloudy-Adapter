package com.kelto.load_balancer;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.openstack4j.model.compute.Server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Charles Fallourd on 15/04/16.
 */
public class LoadBalancer implements Runnable {

    public static final int MAX_LOAD = 80;
    private static final Integer MIN_LOAD = 20;
    private static final String PROTOCOL = "http";
    private static final String DEFAULT_SERVER_PORT = "19000";
    private static final Integer LOAD_POOLING_INTERVAL = 1000 * 2; // every 2 seconds
    private static final Logger LOGGER = Logger.getLogger(LoadBalancer.class.getName());
    private CloudyClient cloudyClient;
    private boolean running;
    private XmlRpcClient client;

    public LoadBalancer(String dispatcherHost, Integer dispatcherPort) throws MalformedURLException {
        cloudyClient = new CloudyClient();
        client = createClient(dispatcherHost,dispatcherPort);
    }


    public void run() {
        running = true;
        while (running) {
            try {
                Integer load = (Integer) client.execute("ServerDirectory.getLoad",new Object[0]);
                LOGGER.log(Level.INFO, "Load received from ServerDirectory: " + load);
                if (load == -1 ) {
                    LOGGER.log(Level.INFO, "The load indicate that no server is present, adding new VM");
                    Server server = cloudyClient.addServer();
                    addServerToDispatcher(server);
                }
                else if( load > MAX_LOAD ) {
                    LOGGER.log(Level.INFO,"Load greater than the maximum setted, adding new VM");
                    Server server = cloudyClient.addServer();
                    addServerToDispatcher(server);
                } else if ( load < MIN_LOAD ) {
                    LOGGER.log(Level.INFO,"Load inferior to the minimum setted, removing a VM");
                    Server server = cloudyClient.stopServer();
                    removeServerToDispatcher(server);
                } else {
                    LOGGER.log(Level.INFO,"Acceptable load, going to sleep");
                }
                Thread.sleep(LOAD_POOLING_INTERVAL);
            } catch (XmlRpcException e) {
                LOGGER.log(Level.SEVERE,"Could not get the load from the server directory",e);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Could not make the load balancer sleep. The dispatcher might get flooded",e);
            }
        }
    }

    private void addServerToDispatcher(Server server) {
        try {
            client.execute("ServerMapper.add", new Object[] {server.getAccessIPv4(), DEFAULT_SERVER_PORT});
        } catch (XmlRpcException e) {
            LOGGER.log(Level.SEVERE, "Could not add Server to dispatcher",e);
        }
    }

    private void removeServerToDispatcher(Server server) {
        try {
            client.execute("ServerMapper.del", new Object[] {server.getAccessIPv4(), DEFAULT_SERVER_PORT});
        } catch (XmlRpcException e) {
            LOGGER.log(Level.SEVERE, "Could not remove Server from dispatcher",e);
        }
    }

    public void stop() {
        running = false;
    }

    public static XmlRpcClient createClient(String host, Integer stringPort) throws MalformedURLException {
        Integer port = Integer.valueOf(stringPort);
        // create configuration
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(PROTOCOL,host, port,""));
        config.setEnabledForExtensions(true);
        config.setConnectionTimeout(60 * 1000);
        config.setReplyTimeout(60 * 1000);

        XmlRpcClient client = new XmlRpcClient();

        // use Commons HttpClient as transport
        client.setTransportFactory(
                new XmlRpcCommonsTransportFactory(client));
        // set configuration
        client.setConfig(config);

        return client;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length != 2) {
            System.err.println("Two arguments must be provided for the application to work correctly");
            System.exit(-1);
        }
        Integer dispatcherPort = Integer.valueOf(args[1]);
        LoadBalancer loadBalancer = new LoadBalancer(args[0], dispatcherPort);
        Thread thread = new Thread(loadBalancer);
        thread.start();
        System.out.println("Press enter to stop the load balancer");
        System.in.read();
        loadBalancer.stop();
        thread.join();
    }
}
