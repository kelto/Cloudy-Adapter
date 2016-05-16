package com.kelto.load_balancer;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Payloads;
import org.openstack4j.model.compute.*;
import org.openstack4j.model.image.ContainerFormat;
import org.openstack4j.model.image.DiskFormat;
import org.openstack4j.model.image.Image;
import org.openstack4j.model.network.NetFloatingIP;
import org.openstack4j.openstack.OSFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

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
    public static final String IMG_ID = "f903d7f3-af65-4f83-a1bd-02f921d52ec2";
    public static final String SERVER_NAME = "FallourdOdegaard-VM-";
    public final static OSClient osClient;

    public Queue<Server> vmLists;
    public int count;
    private org.openstack4j.model.compute.ActionResponse ActionResponse;

    public CloudyClient() {
        vmLists = new PriorityQueue<Server>();
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

    public void stopServer(String id) {

        //Server latest = vmLists.poll();

        if (id != null) {
            System.out.println("Stoping vm with id : " + id);
            osClient.compute().servers().action(id, Action.STOP);
        } else {
            System.out.println("There is no more vm to stop.");
        }
    }

    /*public Image createImage() {

        Image image = Builders.image().name(IMG_NAME).containerFormat(ContainerFormat.BARE).diskFormat(DiskFormat.QCOW2).build();
        image = osClient.images().create(image, Payloads.create(new File(PATH_TO_IMG)));

        System.out.println("Image created - Id : " + image.getId());

        return image;
    }*/

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
