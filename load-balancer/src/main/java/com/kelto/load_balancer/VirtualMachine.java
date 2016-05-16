package com.kelto.load_balancer;

/**
 * @author Charles Fallourd on 16/05/16.
 */
public class VirtualMachine {

    private String host;
    private String id;

    public VirtualMachine(String host, String id) {
        this.host = host;
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public String getId() {
        return id;
    }
}
