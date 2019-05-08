package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class MapItem extends Item
{
    public MapItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean isMap() {
        return true;
    }
    
    @Nullable
    public Packet<?> createMapPacket(final ItemStack stack, final World world, final PlayerEntity playerEntity) {
        return null;
    }
}
