package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.village.TraderOfferList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class SetTradeOffersPacket implements Packet<ClientPlayPacketListener>
{
    private int syncId;
    private TraderOfferList recipes;
    private int levelProgress;
    private int experience;
    private boolean levelled;
    
    public SetTradeOffersPacket() {
    }
    
    public SetTradeOffersPacket(final int syncId, final TraderOfferList recipes, final int levelProgress, final int experience, final boolean levelled) {
        this.syncId = syncId;
        this.recipes = recipes;
        this.levelProgress = levelProgress;
        this.experience = experience;
        this.levelled = levelled;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.syncId = buf.readVarInt();
        this.recipes = TraderOfferList.fromPacket(buf);
        this.levelProgress = buf.readVarInt();
        this.experience = buf.readVarInt();
        this.levelled = buf.readBoolean();
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeVarInt(this.syncId);
        this.recipes.toPacket(buf);
        buf.writeVarInt(this.levelProgress);
        buf.writeVarInt(this.experience);
        buf.writeBoolean(this.levelled);
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onSetTradeOffers(this);
    }
    
    @Environment(EnvType.CLIENT)
    public int getSyncId() {
        return this.syncId;
    }
    
    @Environment(EnvType.CLIENT)
    public TraderOfferList getOffers() {
        return this.recipes;
    }
    
    @Environment(EnvType.CLIENT)
    public int getLevelProgress() {
        return this.levelProgress;
    }
    
    @Environment(EnvType.CLIENT)
    public int getExperience() {
        return this.experience;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isLevelled() {
        return this.levelled;
    }
}
