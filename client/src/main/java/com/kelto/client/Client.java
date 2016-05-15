package com.kelto.client;

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
public class Client implements Runnable {

    private static Logger LOGGER = Logger.getLogger(Client.class.getName());
    private int requestPerSecond;
    private XmlRpcClient client;
    public static final String PROTOCOLE = "http";
    private boolean running;

    public Client(XmlRpcClient client, int requestPerSecond) {
        this.client = client;
        this.requestPerSecond = requestPerSecond;
        running = true;
    }

    public void run() {
        while (running) {
            // copy value, in case of user update
            int localRequestPerSecond = requestPerSecond;
            long startTime = System.nanoTime();
            for(int i = 0 ; i < localRequestPerSecond; ++i) {
                sendRequest();
            }
            sleep(startTime);
        }
    }

    private void sleep(long startTime) {
        long elapsedTime = System.nanoTime() - startTime;
        try {
            Thread.sleep(elapsedTime > 0 ? elapsedTime : 0);
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Could not make the thread sleep, the number of request per second will be wrong", e);
        }
    }

    public void sendRequest() {
        // make the a regular call
        Object[] params = new Object[]
                {new Integer(100), new Integer(3)};
        try {
            Object[] result = (Object[]) client.execute("Dispatcher.dispatch", new Integer[]{100});
            LOGGER.log(Level.INFO,"Results received.");
            for (Object d : result) {
                LOGGER.log(Level.INFO, "Result: ",d);
            }
        } catch (XmlRpcException e) {
            LOGGER.log(Level.SEVERE, "Could not execute request",e);
        }
    }

    public void setRequestPerSecond(int number) {
        this.requestPerSecond = number;
    }

    public void stop() {
        this.running = false;
    }

    public static void main(String args[]) throws MalformedURLException {
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


        // make a call using dynamic proxy
	  /*          ClientFactory factory = new ClientFactory(client);
          Adder adder = (Adder) factory.newInstance(Adder.class);
          int sum = adder.add(2, 4);
          System.out.println("2 + 4 = " + sum);
	  */
    }
}
