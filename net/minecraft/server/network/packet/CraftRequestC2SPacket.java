package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class CraftRequestC2SPacket implements Packet<ServerPlayPacketListener>
{
    private int syncId;
    private Identifier recipe;
    private boolean craftAll;
    
    public CraftRequestC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public CraftRequestC2SPacket(final int syncId, final Recipe<?> recipe, final boolean boolean3) {
        this.syncId = syncId;
        this.recipe = recipe.getId();
        this.craftAll = boolean3;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.syncId = buf.readByte();
        this.recipe = buf.readIdentifier();
        this.craftAll = buf.readBoolean();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.syncId);
        buf.writeIdentifier(this.recipe);
        buf.writeBoolean(this.craftAll);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onCraftRequest(this);
    }
    
    public int getSyncId() {
        return this.syncId;
    }
    
    public Identifier getRecipe() {
        return this.recipe;
    }
    
    public boolean shouldCraftAll() {
        return this.craftAll;
    }
}
