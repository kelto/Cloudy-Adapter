package com.kelto.client;

import org.apache.xmlrpc.XmlRpcException;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Charles Fallourd on 25/03/16.
 */
public class Client {

    private static Logger LOGGER = Logger.getLogger(Client.class.getName());
    public static final String PROTOCOLE = "http";



    public static void main(String args[]) throws IOException, XmlRpcException {
        if(args.length < 4) {
            LOGGER.log(Level.SEVERE, "Need 4 arguments for the program to run.");
            System.exit(-1);
        }
        System.out.println("Args0: " +  args[0]);
        Integer listeningPort = Integer.valueOf(args[0]);
        Integer nbRequest = Integer.valueOf(args[1]);
        start(createClient(args[2], args[3]), nbRequest);
        createServer(listeningPort);
    }

    public static void start(XmlRpcClient client, int nbRequest) {
        RequestSender.getInstance().setClient(client);
        RequestSender.getInstance().setRequestPerSecond(nbRequest);
        new Thread(RequestSender.getInstance()).start();
    }

    public static XmlRpcClient createClient(String host, String port) throws MalformedURLException {
        Integer dispatcherPort = Integer.valueOf(port);
        // create configuration
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(PROTOCOLE,host,dispatcherPort,""));
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
        RequestSender.getInstance().setRequestPerSecond(number);
    }
}
