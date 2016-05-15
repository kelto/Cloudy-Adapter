package com.kelto.client_updater;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientUpdater {

    private static final Logger LOGGER = Logger.getLogger(ClientUpdater.class.getName());
    private static String PROTOCOLE = "http";

    public static void main(String[] args) {
        Integer nbRequest = Integer.valueOf(args[0]);
        String clientHost = args[1];
        Integer clientPort = Integer.valueOf(args[2]);
        XmlRpcClient client = createClient(clientHost, clientPort);
        try {
            client.execute("Client.updateNumberRequest", new Object[] {nbRequest});
            LOGGER.log(Level.INFO,"Number of request has been updated");
        } catch (XmlRpcException e) {
            LOGGER.log(Level.SEVERE,"Could not update number of request", e);
        }
    }

    private static XmlRpcClient createClient(String host, Integer port) {
        // create configuration
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            config.setServerURL(new URL(PROTOCOLE,host,port,""));
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL produced from arguments: "+e.getMessage());
        }
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
}