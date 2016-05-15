package com.kelto.load_balancer;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Payloads;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.image.ContainerFormat;
import org.openstack4j.model.image.DiskFormat;
import org.openstack4j.model.image.Image;
import org.openstack4j.openstack.OSFactory;

import java.io.File;

/**
 * @author Charles Fallourd on 22/04/16.
 */
public class CloudyClient {

    public static final String END_POINT = "http://127.0.0.1:5000/v2.0";
    //c'est le nom du groupe ... selon le prof.
    public static final String TENANT_NAME = "service";
    public static final String LOGIN = "admin";
    public static final String PASSWORD = "password";

    public static final String DEFAULT_FLAVOR_NAME = "Cloudy-moon-moon";
    public static final String PATH_TO_IMG = "/nfs/home/gdacosta/ubuntu-15.10-server-cloudimg-amd64-disk1.img";
    public static final String IMG_NAME = "Ubuntu-moon-moon";
    public static final String SERVER_NAME = "Moon-moon";
    public final static OSClient osClient;

    static {
        osClient = OSFactory.builder().endpoint(END_POINT).credentials(LOGIN,PASSWORD).tenantName(TENANT_NAME).authenticate();
    }

    public boolean addServer() {


        // Create a Server Model Object
        ServerCreate sc = Builders.server().name(SERVER_NAME).flavor(DEFAULT_FLAVOR_NAME).image(IMG_NAME).build();

        // Boot the Server
        Server server = osClient.compute().servers().boot(sc);
        return true;
    }

    public boolean createFlavor() {
        Flavor flavor = Builders.flavor()
                .name(DEFAULT_FLAVOR_NAME)
                .ram(256)
                .vcpus(6)
                .disk(120)
                .rxtxFactor(1.2f)
                .build();

        osClient.compute().flavors().create(flavor);
        return true;
    }

    public boolean createImage() {

        Image image = Builders.image().name(IMG_NAME).containerFormat(ContainerFormat.BARE).diskFormat(DiskFormat.QCOW2).build();
        osClient.images().create(image, Payloads.create(new File(PATH_TO_IMG)));
        return true;
    }


}
