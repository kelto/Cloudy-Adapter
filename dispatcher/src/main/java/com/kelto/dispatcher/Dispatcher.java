package com.kelto.dispatcher;

/**
 * @author Charles Fallourd on 01/04/16.
 */
public class Dispatcher {

    public Object dispatch(Object[] params) {
        MappedServer mappedServer = ServerDirectory.getInstance().getMappedServers().poll();
        ServerDirectory.getInstance().add(mappedServer);
        return mappedServer.sendRequest(params);
    }
}
