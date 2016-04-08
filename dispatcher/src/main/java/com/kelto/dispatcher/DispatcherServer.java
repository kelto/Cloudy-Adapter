package com.kelto.dispatcher;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import java.io.IOException;

/**
 * @author Charles Fallourd on 25/03/16.
 */
public class DispatcherServer {

    public static void main(String args[]) throws IOException, XmlRpcException {
        Integer port = Integer.valueOf(args[0]);
        WebServer webServer = new WebServer(port);

        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

        PropertyHandlerMapping phm = new PropertyHandlerMapping();
          /* Load handler definitions from a property file.
           * The property file might look like:
           *   Calculator=org.apache.xmlrpc.demo.Calculator
           *   org.apache.xmlrpc.demo.proxy.Adder=org.apache.xmlrpc.demo.proxy.AdderImpl
           */
        phm.load(Thread.currentThread().getContextClassLoader(),
                "XmlRpcServlet.properties");
          /* You may also provide the handler classes directly,
           * like this:
           * phm.addHandler("Calculator",
           *     org.apache.xmlrpc.demo.Calculator.class);
           * phm.addHandler(org.apache.xmlrpc.demo.proxy.Adder.class.getName(),
           *     org.apache.xmlrpc.demo.proxy.AdderImpl.class);
           */
        xmlRpcServer.setHandlerMapping(phm);
        XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();

        serverConfig.setEnabledForExtensions(true);
        serverConfig.setContentLengthOptional(false);

        webServer.start();
        System.out.println("Hello World");
    }
}
