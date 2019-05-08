package net.minecraft.client.tutorial;

import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.input.Input;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface TutorialStepHandler
{
    default void destroy() {
    }
    
    default void tick() {
    }
    
    default void onMovement(final Input input) {
    }
    
    default void onMouseUpdate(final double deltaX, final double deltaY) {
    }
    
    default void onTarget(final ClientWorld world, final HitResult hitResult) {
    }
    
    default void onBlockAttacked(final ClientWorld clientWorld, final BlockPos blockPos, final BlockState blockState, final float float4) {
    }
    
    default void onInventoryOpened() {
    }
    
    default void onSlotUpdate(final ItemStack itemStack) {
    }
}
