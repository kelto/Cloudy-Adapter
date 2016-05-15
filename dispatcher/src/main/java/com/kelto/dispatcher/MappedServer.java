package com.kelto.dispatcher;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Charles Fallourd on 25/03/16.
 */
public class MappedServer implements Comparable<MappedServer>{
    private static final Logger LOGGER = Logger.getLogger(MappedServer.class.getName());

    private final Integer port;
    private final String host;
    private float load;
    private static final String PROTOCOLE = "http";
    private static final String METHOD = "Calculator.getDivisors";
    private XmlRpcClient client;

    public MappedServer(String host, Integer port) {
        this.host = host;
        this.port = port;
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

            config.setServerURL(new URL(PROTOCOLE,"127.0.0.1",port,""));

            config.setEnabledForExtensions(true);
            config.setConnectionTimeout(60 * 1000);
            config.setReplyTimeout(60 * 1000);

            client = new XmlRpcClient();

            // use Commons HttpClient as transport
            client.setTransportFactory(
                    new XmlRpcCommonsTransportFactory(client));
            // set configuration
            client.setConfig(config);

            LOGGER.log(Level.INFO,"Xml rpc client successfuly connected to server calculator.");

        } catch (MalformedURLException e) {
            LOGGER.log(Level.SEVERE,"Invalid URL, could not map Server",e);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MappedServer that = (MappedServer) o;

        if (!port.equals(that.port)) return false;
        return host.equals(that.host);

    }

    @Override
    public int hashCode() {
        int result = port.hashCode();
        result = 31 * result + host.hashCode();
        return result;
    }

    @Override
    public int compareTo(MappedServer mappedServer) {
        return Float.compare(this.load,mappedServer.load);
    }

    //TODO: use a params as a way to specify the request (Class.method)
    public Object sendRequest(Object[] params) {
        try {
            return client.execute(METHOD,params);
        } catch (XmlRpcException e) {
            LOGGER.log(Level.SEVERE,"Could not execute method on mapped server",e);
            return null;
        }
    }
}
