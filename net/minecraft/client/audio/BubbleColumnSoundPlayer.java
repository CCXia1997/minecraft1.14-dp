package net.minecraft.client.audio;

import net.minecraft.block.BlockState;
import net.minecraft.world.World;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.ClientPlayerTickable;

@Environment(EnvType.CLIENT)
public class BubbleColumnSoundPlayer implements ClientPlayerTickable
{
    private final ClientPlayerEntity player;
    private boolean hasPlayedForCurrentColumn;
    private boolean firstTick;
    
    public BubbleColumnSoundPlayer(final ClientPlayerEntity clientPlayerEntity) {
        this.firstTick = true;
        this.player = clientPlayerEntity;
    }
    
    @Override
    public void tick() {
        final World world1 = this.player.world;
        final BlockState blockState2 = world1.getBlockState(this.player.getBoundingBox().expand(0.0, -0.4000000059604645, 0.0).contract(0.001), Blocks.kU);
        if (blockState2 != null) {
            if (!this.hasPlayedForCurrentColumn && !this.firstTick && blockState2.getBlock() == Blocks.kU && !this.player.isSpectator()) {
                final boolean boolean3 = blockState2.<Boolean>get((Property<Boolean>)BubbleColumnBlock.DRAG);
                if (boolean3) {
                    this.player.playSound(SoundEvents.av, 1.0f, 1.0f);
                }
                else {
                    this.player.playSound(SoundEvents.at, 1.0f, 1.0f);
                }
            }
            this.hasPlayedForCurrentColumn = true;
        }
        else {
            this.hasPlayedForCurrentColumn = false;
        }
        this.firstTick = false;
    }
}
