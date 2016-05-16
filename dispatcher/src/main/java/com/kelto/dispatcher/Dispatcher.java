package com.kelto.dispatcher;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Charles Fallourd on 01/04/16.
 */
public class Dispatcher {
    private static final int NUMBER_THREAD = 4;
    private static final Logger LOGGER = Logger.getLogger(Dispatcher.class.getName());
    private static final ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_THREAD);

    public Object dispatch(final Integer params) throws ExecutionException, InterruptedException {

        LOGGER.log(Level.INFO,"Request received from client");
        LOGGER.log(Level.INFO,"First integer to get divisors : " + params);
        final MappedServer mappedServer = ServerDirectory.getInstance().next();
        Future future = executorService.submit(new Callable() {
            @Override
            public Object call() throws Exception {
                return mappedServer.sendRequest(new Object[]{params});
            }
        });
        LOGGER.log(Level.INFO,"load: " + mappedServer.getLoad());
        return future.get();
    }
}
