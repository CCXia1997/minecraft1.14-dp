package net.minecraft.client.network;

import net.minecraft.server.network.packet.QueryBlockNbtC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.QueryEntityNbtC2SPacket;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class QueryHandler
{
    private final ClientPlayNetworkHandler networkHandler;
    private int expectedTransactionId;
    @Nullable
    private Consumer<CompoundTag> queryConsumer;
    
    public QueryHandler(final ClientPlayNetworkHandler clientPlayNetworkHandler) {
        this.expectedTransactionId = -1;
        this.networkHandler = clientPlayNetworkHandler;
    }
    
    public boolean handleQueryResponse(final int integer, @Nullable final CompoundTag compoundTag) {
        if (this.expectedTransactionId == integer && this.queryConsumer != null) {
            this.queryConsumer.accept(compoundTag);
            this.queryConsumer = null;
            return true;
        }
        return false;
    }
    
    private int setNextQueryConsumer(final Consumer<CompoundTag> consumer) {
        this.queryConsumer = consumer;
        return ++this.expectedTransactionId;
    }
    
    public void queryEntityNbt(final int integer, final Consumer<CompoundTag> consumer) {
        final int integer2 = this.setNextQueryConsumer(consumer);
        this.networkHandler.sendPacket(new QueryEntityNbtC2SPacket(integer2, integer));
    }
    
    public void queryBlockNbt(final BlockPos blockPos, final Consumer<CompoundTag> consumer) {
        final int integer3 = this.setNextQueryConsumer(consumer);
        this.networkHandler.sendPacket(new QueryBlockNbtC2SPacket(integer3, blockPos));
    }
}
