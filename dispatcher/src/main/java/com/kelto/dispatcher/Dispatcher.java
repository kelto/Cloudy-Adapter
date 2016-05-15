package com.kelto.dispatcher;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Charles Fallourd on 01/04/16.
 */
public class Dispatcher {

    private static final Logger LOGGER = Logger.getLogger(Dispatcher.class.getName());

    public Object dispatch(Integer params) {

        LOGGER.log(Level.INFO,"Request received from client");
        LOGGER.log(Level.INFO,"First integer to get divisors : " + params);

        return ServerDirectory.getInstance().next().sendRequest(new Object[]{params});
    }
}
