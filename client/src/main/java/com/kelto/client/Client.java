package com.kelto.client;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.client.AsyncCallback;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Charles Fallourd on 25/03/16.
 */
public class Client {

    public static final String PROTOCOLE = "http";
    private static int numberRequest;


    public static void main(final String args[]) throws IOException, XmlRpcException, InterruptedException {
        if (args.length < 4) {
            System.err.println("Need 4 arguments for the program to run.");
            System.exit(-1);
        }
        Integer listeningPort = Integer.valueOf(args[0]);
        numberRequest = Integer.valueOf(args[1]);
        createServer(listeningPort);
        XmlRpcClient client = createClient(args[2], args[3]);
        while (true) {
            int local = numberRequest;
            for (int i = 0; i < local; i++) {
                try {
                    client.executeAsync("Dispatcher.dispatch", new Integer[]{100}, new AsyncCallback() {
                        public void handleResult(XmlRpcRequest xmlRpcRequest, Object o) {
                            Object[] result = (Object[]) o;
                            for (Object d : result) {

                            }
                        }

                        public void handleError(XmlRpcRequest xmlRpcRequest, Throwable throwable) {

                        }
                    });


                } catch (XmlRpcException e) {
                            System.err.println("Could not execute request: "+e.toString());
                }

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.err.println("Failed to put thread to sleep: " + e);
            }
        }
    }

    public static XmlRpcClient createClient(String host, String port) throws MalformedURLException {
        Integer dispatcherPort = Integer.valueOf(port);
        // create configuration
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(PROTOCOLE, host, dispatcherPort, ""));
        config.setEnabledForExtensions(true);
        config.setEnabledForExceptions(true);
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

    public static WebServer createServer(int port) throws IOException, XmlRpcException {
        WebServer webServer = new WebServer(port);

        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

        PropertyHandlerMapping phm = new PropertyHandlerMapping();
        phm.load(Thread.currentThread().getContextClassLoader(),
                "XmlRpcServlet.properties");
        xmlRpcServer.setHandlerMapping(phm);
        XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();

        serverConfig.setEnabledForExtensions(true);
        serverConfig.setContentLengthOptional(false);

        webServer.start();
        return webServer;
    }

    public static void updateNumberRequest(int number) {
        numberRequest = number;
    }
}
