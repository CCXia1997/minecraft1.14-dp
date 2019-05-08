package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class CraftResponseS2CPacket implements Packet<ClientPlayPacketListener>
{
    private int syncId;
    private Identifier recipeId;
    
    public CraftResponseS2CPacket() {
    }
    
    public CraftResponseS2CPacket(final int integer, final Recipe<?> recipe) {
        this.syncId = integer;
        this.recipeId = recipe.getId();
    }
    
    @Environment(EnvType.CLIENT)
    public Identifier getRecipeId() {
        return this.recipeId;
    }
    
    @Environment(EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.syncId = buf.readByte();
        this.recipeId = buf.readIdentifier();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeByte(this.syncId);
        buf.writeIdentifier(this.recipeId);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onCraftResponse(this);
    }
}
