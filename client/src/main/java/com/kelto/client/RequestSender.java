package com.kelto.client;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Charles Fallourd on 15/05/16.
 */
public class RequestSender implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(RequestSender.class.getName());


    private int requestPerSecond;
    private XmlRpcClient client;
    private boolean running;
    private static RequestSender instance = new RequestSender();

    public RequestSender() {

    }
    public RequestSender(XmlRpcClient client, int requestPerSecond) {
        this.client = client;
        this.requestPerSecond = requestPerSecond;
    }

    public static RequestSender getInstance() {
        return instance;
    }

    public void run() {
        running = true;
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
        if(number > 0) {
            this.requestPerSecond = number;
        }
    }

    public void setClient(XmlRpcClient client) {
        this.client = client;
    }
    public void stop() {
        this.running = false;
    }
}
