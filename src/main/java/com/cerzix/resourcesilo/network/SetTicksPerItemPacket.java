package com.cerzix.resourcesilo.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Legacy no-op packet kept only to satisfy old references.
 * Tick speed is controlled by ModConfig now.
 */
public class SetTicksPerItemPacket {
    public final BlockPos pos;
    public final int ticks;

    public SetTicksPerItemPacket(BlockPos pos, int ticks) {
        this.pos = pos;
        this.ticks = ticks;
    }

    public static void encode(SetTicksPerItemPacket pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeVarInt(pkt.ticks);
    }

    public static SetTicksPerItemPacket decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int ticks = buf.readVarInt();
        return new SetTicksPerItemPacket(pos, ticks);
    }

    public static void handle(SetTicksPerItemPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        // No-op; config-based now.
        ctxSupplier.get().setPacketHandled(true);
    }
}
