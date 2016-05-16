package com.kelto.dispatcher;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Charles Fallourd on 25/03/16.
 */
public class ServerMapper {

    private static final Logger LOGGER = Logger.getLogger(ServerMapper.class.getName());

    public boolean add(String host, String port) {

        LOGGER.log(Level.INFO,"Request to ServerMapper : add");
        LOGGER.log(Level.INFO,"Port : " + port);
        LOGGER.log(Level.INFO,"Host : " + host);
        return ServerDirectory.getInstance().add(host,Integer.valueOf(port));
    }

    public boolean del(String host, String port) {
        return ServerDirectory.getInstance().del(host,Integer.valueOf(port));
    }

}
