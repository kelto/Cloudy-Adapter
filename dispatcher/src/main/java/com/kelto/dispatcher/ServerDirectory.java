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
        mappedServers = new ConcurrentLinkedQueue<MappedServer>();
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

    /**
     * Could be used if any load information was properly handled, which is not the case for the moment.
     * Should not be used, as it leads the user to think the server sent back is the least busy one,
     * which is statistically true, but not always the case.
     */
    @Deprecated
    public MappedServer getLeastBusy() {
        return this.mappedServers.poll();
    }

}
