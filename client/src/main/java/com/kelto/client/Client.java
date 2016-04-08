package com.kelto.client;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.XmlRpcCommonsTransportFactory;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Charles Fallourd on 25/03/16.
 */
public class Client {

    public static final String PROTOCOLE = "http";
    public static void main(String args[]) throws XmlRpcException, MalformedURLException {
        Integer port = Integer.valueOf(args[0]);
        // create configuration
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        config.setServerURL(new URL(PROTOCOLE,"127.0.0.1",port,""));
        config.setEnabledForExtensions(true);
        config.setConnectionTimeout(60 * 1000);
        config.setReplyTimeout(60 * 1000);

        XmlRpcClient client = new XmlRpcClient();

        // use Commons HttpClient as transport
        client.setTransportFactory(
                new XmlRpcCommonsTransportFactory(client));
        // set configuration
        client.setConfig(config);

        // make the a regular call
        Object[] params = new Object[]
                { new Integer(100), new Integer(3) };
        Object[] result = (Object[])client.execute("Dispatcher.dispatch",new Integer[] {100});
        System.out.println("divisor of 2: ");
        for(Object d : result) {
            System.out.print(d + " ");
        }
        System.out.println();

        // make a call using dynamic proxy
	  /*          ClientFactory factory = new ClientFactory(client);
          Adder adder = (Adder) factory.newInstance(Adder.class);
          int sum = adder.add(2, 4);
          System.out.println("2 + 4 = " + sum);
	  */
    }
}
