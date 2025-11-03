package com.cerzix.resourcesilo.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Legacy no-op packet kept only to satisfy old references.
 * Generation speed now lives in ModConfig; this packet does nothing.
 */
public class SetSiloConfigPacket {
    public final BlockPos pos;
    public final int ticksPerItem;

    public SetSiloConfigPacket(BlockPos pos, int ticksPerItem) {
        this.pos = pos;
        this.ticksPerItem = ticksPerItem;
    }

    public static void encode(SetSiloConfigPacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeVarInt(pkt.ticksPerItem);
    }

    public static SetSiloConfigPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int tpi = buf.readVarInt();
        return new SetSiloConfigPacket(pos, tpi);
    }

    public static void handle(SetSiloConfigPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        // Intentionally do nothing; configuration is now handled via Forge config.
        ctxSupplier.get().setPacketHandled(true);
    }
}
