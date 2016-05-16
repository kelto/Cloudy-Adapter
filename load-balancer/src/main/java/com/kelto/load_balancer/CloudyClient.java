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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Charles Fallourd on 22/04/16.
 */
public class CloudyClient {

    private static final Logger LOGGER = Logger.getLogger(CloudyClient.class.getName());

    public static final String END_POINT = "http://127.0.0.1:5000/v2.0";
    //c'est le nom du groupe ... selon le prof.
    public static final String TENANT_NAME = "service";
    public static final String LOGIN = "ens34";
    public static final String PASSWORD = "OX37Q7";

    public static final String DEFAULT_FLAVOR_ID = "3";
    public static final String PATH_TO_IMG = "/nfs/home/gdacosta/ubuntu-15.10-server-cloudimg-amd64-disk1.img";
    public static final String IMG_ID = "f903d7f3-af65-4f83-a1bd-02f921d52ec2";
    public static final String SERVER_NAME = "FallourdOdegaard-VM-";
    public final static OSClient osClient;

    private Queue<VirtualMachine> vmLists;
    private int count;
    private org.openstack4j.model.compute.ActionResponse ActionResponse;

    public CloudyClient() {
        vmLists = new LinkedList<>();
        count = 1;
    }

    static {
        osClient = OSFactory.builder().endpoint(END_POINT).credentials(LOGIN,PASSWORD).tenantName(TENANT_NAME).authenticate();
    }

    public VirtualMachine addServer() {

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

        System.out.println("Waiting 2 second for the server to boot properly");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Could not sleep");
        }

        // Associate public key to instance
        List<String> pools = osClient.compute().floatingIps().getPoolNames();
        FloatingIP ip = osClient.compute().floatingIps().allocateIP(pools.get(0));
        System.out.println("FLoatingIp : " + ip.toString());
        NetFloatingIP netFloatingIP = osClient.networking().floatingip().get(ip.getId());
        System.out.println("NetFloatingIP : " + netFloatingIP.getFloatingIpAddress());
        ActionResponse = osClient.compute().floatingIps().addFloatingIP(server, netFloatingIP.getFloatingIpAddress());
        System.out.println("ActionResponse: ");
        System.out.println(ActionResponse);
        VirtualMachine virtualMachine = new VirtualMachine(netFloatingIP.getFloatingIpAddress(), server.getId());
        count++;
        vmLists.add(virtualMachine);
        return virtualMachine;
    }

    public VirtualMachine stopServer() {
        VirtualMachine server = vmLists.poll();
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

    public void cleanCloudMip() {
        LOGGER.log(Level.INFO,"Cleaning servers ...");
        List<? extends Server> servers = osClient.compute().servers().list();
        for(Server server : servers) {
            if(server.getName().startsWith(SERVER_NAME)) {
                osClient.compute().servers().delete(server.getId());
                LOGGER.log(Level.INFO,server.getName() + " has been deleted");
            }
        }
        List<? extends NetFloatingIP> listIp = osClient.networking().floatingip().list();
        for(NetFloatingIP floatingIP : listIp) {
            if(floatingIP.getFixedIpAddress() == null || floatingIP.getFixedIpAddress().isEmpty()) {
                osClient.networking().floatingip().delete(floatingIP.getId());
                LOGGER.log(Level.INFO, String.format("Floating ip [%s] with addresse %s has been deleted", floatingIP.getId(),floatingIP.getFloatingIpAddress()));
            }
        }
    }

    public int numberOfVm() {
        return vmLists.size();
    }



    public static void main(String args[]) throws InterruptedException {

        CloudyClient cl = new CloudyClient();
        System.out.println("Start cloudy client");
        LOGGER.log(Level.INFO,"Cleaning all instance and floatingip");
        cl.cleanCloudMip();
        System.out.println("Launching a vm ...");
        VirtualMachine server = cl.addServer();
        System.out.println("VM created");

        /*System.out.println("Sleep ...");
        Thread.sleep(20000);

        System.out.println("Stoping server");
        cl.stopServer();*/
    }
}
