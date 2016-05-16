package com.kelto.updater;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class Updater {

    private static final String PROTOCOL = "http";

    //TODO: should split logic in a new class, the main method should only instantiate it.
    public static void main(String args[]) throws MalformedURLException, XmlRpcException {
        if(args.length < 5) {
            System.err.println("Help: arguments must be [dispatcher-host] [dispatcher-port] [add/del] [server-host] [server-port]");
            System.exit(1);
        }
        String method = args[2];
        String serverHost = args[3];
        Integer serverPort = Integer.valueOf(args[4]);

        String dispatcherHost = args[0];
        Integer dispatcherPort = Integer.valueOf(args[1]);
        XmlRpcClient client = createClient(dispatcherHost,dispatcherPort);
        switch (method) {
            case "add":
                addServer(client,serverHost,serverPort);
                break;
            case "del":
                removeServer(client,serverHost,serverPort);
                break;
            default:
                System.err.println("Invalid method name");
                System.exit(1);
        }
    }

    private static void removeServer(XmlRpcClient client, String serverHost, Integer serverPort) {
        try {
            client.execute("ServerMapper.del",new Object[] {serverHost, serverPort});
        } catch (XmlRpcException e) {
            System.err.println("could not remove server, reason: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Server removed from dispatcher");
    }

    private static XmlRpcClient createClient(String dispatcherHost, Integer dispatcherPort) {
        // create configuration
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        try {
            config.setServerURL(new URL(PROTOCOL,dispatcherHost,dispatcherPort,""));
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

    private static void addServer(XmlRpcClient client, String serverHost, Integer serverPort) {

        try {
            client.execute("ServerMapper.add", new Object[] {serverHost,serverPort});
        } catch (XmlRpcException e) {
            System.err.println("could not add server, reason: " + e.getMessage());
            System.exit(1);
        }
        System.out.println("Server added to dispatcher");

    }
}