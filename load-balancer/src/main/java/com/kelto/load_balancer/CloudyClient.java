package com.kelto.load_balancer;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.network.NetFloatingIP;
import org.openstack4j.openstack.OSFactory;

import java.util.*;

/**
 * @author Charles Fallourd on 22/04/16.
 */
public class CloudyClient {


    public static final String END_POINT = "http://127.0.0.1:5000/v2.0";
    //c'est le nom du groupe ... selon le prof.
    public static final String TENANT_NAME = "service";
    public static final String LOGIN = "ens34";
    public static final String PASSWORD = "OX37Q7";

    public static final String DEFAULT_FLAVOR_ID = "3";
    public static final String PATH_TO_IMG = "/nfs/home/gdacosta/ubuntu-15.10-server-cloudimg-amd64-disk1.img";
    public static final String IMG_ID = "0e2e6fea-82ab-4ca4-956a-30ffdf98f0a5";
    public static final String SERVER_NAME = "FallourdOdegaard-VM-";
    public final static OSClient osClient;

    private Queue<Server> vmLists;
    private int count;
    private org.openstack4j.model.compute.ActionResponse ActionResponse;

    public CloudyClient() {
        vmLists = new LinkedList<Server>();
        count = 1;
    }

    static {
        osClient = OSFactory.builder().endpoint(END_POINT).credentials(LOGIN,PASSWORD).tenantName(TENANT_NAME).authenticate();
    }

    public Server addServer() {

        // Add netork id of cloudmip
        List<String> networks = new ArrayList<String>();
        networks.add("c1445469-4640-4c5a-ad86-9c0cb6650cca");

        // Create a Server Model Object
        ServerCreate sc = Builders.server().name(SERVER_NAME+count)
                .flavor(DEFAULT_FLAVOR_ID)
                .image(IMG_ID)
                .networks(networks)
                .build();

        // Boot the Server
        Server server = osClient.compute().servers().boot(sc);

        // add the server to the list of vm currently working
        vmLists.add(server);

        // Associate public key to instance
        List<String> pools = osClient.compute().floatingIps().getPoolNames();
        FloatingIP ip = osClient.compute().floatingIps().allocateIP(pools.get(0));
        System.out.println("FLoatingIp : " + ip.toString());
        NetFloatingIP netFloatingIP = osClient.networking().floatingip().get(ip.getId());
        System.out.println("NetFloatingIP : " + netFloatingIP.getFloatingIpAddress());
        ActionResponse = osClient.compute().floatingIps().addFloatingIP(server, netFloatingIP.getFloatingIpAddress());

        count++;
        return server;
    }

    public Server stopServer() {
        Server server = vmLists.poll();
        stopServer(server.getId());
        return server;
    }

    public void stopServer(String id) {

        if (id != null) {
            System.out.println("Stoping vm with id : " + id);
            osClient.compute().servers().action(id, Action.STOP);
        } else {
            System.out.println("There is no more vm to stop.");
        }
    }

    public int numberOfVm() {
        return vmLists.size();
    }



    public static void main(String args[]) throws InterruptedException {

        CloudyClient cl = new CloudyClient();
        System.out.println("Start cloudy client");

        System.out.println("Launching a vm ...");
        cl.addServer();
        System.out.println("VM created");

        /*System.out.println("Sleep ...");
        Thread.sleep(20000);

        System.out.println("Stoping server");
        cl.stopServer();*/
    }
}
