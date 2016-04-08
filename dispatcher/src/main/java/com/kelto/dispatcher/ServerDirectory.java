package com.kelto.dispatcher;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * @author Charles Fallourd on 25/03/16.
 */
public class ServerDirectory {
    private static final ServerDirectory instance = new ServerDirectory();
    public Queue<MappedServer> mappedServers;
    private ServerDirectory() {
        mappedServers = new PriorityQueue<>();
    }

    public static ServerDirectory getInstance() {
        return instance;
    }

    public boolean add(String host, Integer port) {
        MappedServer mappedServer = new MappedServer(host,port);
        return add(mappedServer);
    }

    public boolean del(String host, Integer port) {
        return this.mappedServers.remove(new MappedServer(host,port));
    }

    public Queue<MappedServer> getMappedServers() {
        return this.mappedServers;
    }

    public boolean add(MappedServer mappedServer) {

        if(this.mappedServers.contains(mappedServer)) {
            return false;
        }
        this.mappedServers.add(mappedServer);
        return true;
    }

    public MappedServer getLeastBusy() {
        return this.mappedServers.poll();
    }
    /*
    public float getMaxLoad() {
        return this.mappedServers.
    }
    */
}
