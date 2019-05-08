package net.minecraft.network;

import net.minecraft.util.ThreadExecutor;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.server.world.ServerWorld;

public class NetworkThreadUtils
{
    public static <T extends PacketListener> void forceMainThread(final Packet<T> packet, final T packetListener, final ServerWorld serverWorld) throws OffThreadException {
        NetworkThreadUtils.<T>forceMainThread(packet, packetListener, serverWorld.getServer());
    }
    
    public static <T extends PacketListener> void forceMainThread(final Packet<T> packet, final T packetListener, final ThreadExecutor<?> thread) throws OffThreadException {
        if (!thread.isOnThread()) {
            thread.execute(() -> packet.apply(packetListener));
            throw OffThreadException.INSTANCE;
        }
    }
}
