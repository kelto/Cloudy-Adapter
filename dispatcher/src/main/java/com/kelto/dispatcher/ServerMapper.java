package com.kelto.dispatcher;

/**
 * @author Charles Fallourd on 25/03/16.
 */
public class ServerMapper {

    public boolean add(String host, Integer port) {
        return ServerDirectory.getInstance().add(host,port);
    }

    public boolean del(String host, Integer port) {
        return ServerDirectory.getInstance().del(host,port);
    }

}
