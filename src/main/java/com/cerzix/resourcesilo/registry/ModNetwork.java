package com.cerzix.resourcesilo.network;

import com.cerzix.resourcesilo.ResourceSiloMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(ResourceSiloMod.MODID, "main"))
            .clientAcceptedVersions(PROTOCOL::equals)
            .serverAcceptedVersions(PROTOCOL::equals)
            .networkProtocolVersion(() -> PROTOCOL)
            .simpleChannel();

    private static int nextId = 0;
    private static int id() { return nextId++; }

    public static void register() {
        CHANNEL.registerMessage(id(), SetSiloConfigPacket.class,
                SetSiloConfigPacket::encode, SetSiloConfigPacket::decode, SetSiloConfigPacket::handle);
    }
}
