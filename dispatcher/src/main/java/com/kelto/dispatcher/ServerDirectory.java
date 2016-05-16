package com.kelto.dispatcher;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Charles Fallourd on 25/03/16.
 */
public class ServerDirectory {
    private static final ServerDirectory instance = new ServerDirectory();
    public Queue<MappedServer> mappedServers;
    private ServerDirectory() {
        mappedServers = new ConcurrentLinkedQueue<>();
    }

    public static ServerDirectory getInstance() {
        return instance;
    }

    public static boolean add(String host, Integer port) {
        MappedServer mappedServer = new MappedServer(host,port);
        return getInstance().add(mappedServer);
    }

    public static boolean del(String host, Integer port) {
        return getInstance().mappedServers.remove(new MappedServer(host,port));
    }

    public synchronized MappedServer next() {
        MappedServer mappedServer = this.mappedServers.poll();
        if(mappedServer == null) {
            return null;
        }
        this.mappedServers.add(mappedServer);
        return mappedServer;
    }

    public boolean add(MappedServer mappedServer) {

        if(this.mappedServers.contains(mappedServer)) {
            return false;
        }
        this.mappedServers.add(mappedServer);
        return true;
    }

    public Integer getLoad() {
        MappedServer mappedServer = mappedServers.peek();
        return mappedServer == null ? -1 : mappedServer.getLoad();
    }

}
