package net.minecraft.server.dedicated;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;

public class ServerCommandOutput implements CommandOutput
{
    private final StringBuffer buffer;
    private final MinecraftServer server;
    
    public ServerCommandOutput(final MinecraftServer server) {
        this.buffer = new StringBuffer();
        this.server = server;
    }
    
    public void clear() {
        this.buffer.setLength(0);
    }
    
    public String asString() {
        return this.buffer.toString();
    }
    
    public ServerCommandSource createReconCommandSource() {
        final ServerWorld serverWorld1 = this.server.getWorld(DimensionType.a);
        return new ServerCommandSource(this, new Vec3d(serverWorld1.getSpawnPos()), Vec2f.ZERO, serverWorld1, 4, "Recon", new StringTextComponent("Rcon"), this.server, null);
    }
    
    @Override
    public void sendMessage(final TextComponent message) {
        this.buffer.append(message.getString());
    }
    
    @Override
    public boolean sendCommandFeedback() {
        return true;
    }
    
    @Override
    public boolean shouldTrackOutput() {
        return true;
    }
    
    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return this.server.shouldBroadcastRconToOps();
    }
}
