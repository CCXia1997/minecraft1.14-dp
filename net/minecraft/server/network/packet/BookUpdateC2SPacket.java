package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Hand;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class BookUpdateC2SPacket implements Packet<ServerPlayPacketListener>
{
    private ItemStack book;
    private boolean signed;
    private Hand hand;
    
    public BookUpdateC2SPacket() {
    }
    
    @Environment(EnvType.CLIENT)
    public BookUpdateC2SPacket(final ItemStack book, final boolean signed, final Hand hand) {
        this.book = book.copy();
        this.signed = signed;
        this.hand = hand;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.book = buf.readItemStack();
        this.signed = buf.readBoolean();
        this.hand = buf.<Hand>readEnumConstant(Hand.class);
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeItemStack(this.book);
        buf.writeBoolean(this.signed);
        buf.writeEnumConstant(this.hand);
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onBookUpdate(this);
    }
    
    public ItemStack getBook() {
        return this.book;
    }
    
    public boolean wasSigned() {
        return this.signed;
    }
    
    public Hand getHand() {
        return this.hand;
    }
}
