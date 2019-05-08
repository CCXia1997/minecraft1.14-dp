package net.minecraft.client.tutorial;

import net.minecraft.text.TextFormat;
import net.minecraft.text.KeybindTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.world.GameMode;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.input.Input;
import javax.annotation.Nullable;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TutorialManager
{
    private final MinecraftClient client;
    @Nullable
    private TutorialStepHandler currentHandler;
    
    public TutorialManager(final MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }
    
    public void onMovement(final Input input) {
        if (this.currentHandler != null) {
            this.currentHandler.onMovement(input);
        }
    }
    
    public void onUpdateMouse(final double deltaX, final double deltaY) {
        if (this.currentHandler != null) {
            this.currentHandler.onMouseUpdate(deltaX, deltaY);
        }
    }
    
    public void tick(@Nullable final ClientWorld world, @Nullable final HitResult hitResult) {
        if (this.currentHandler != null && hitResult != null && world != null) {
            this.currentHandler.onTarget(world, hitResult);
        }
    }
    
    public void onBlockAttacked(final ClientWorld clientWorld, final BlockPos blockPos, final BlockState blockState, final float float4) {
        if (this.currentHandler != null) {
            this.currentHandler.onBlockAttacked(clientWorld, blockPos, blockState, float4);
        }
    }
    
    public void onInventoryOpened() {
        if (this.currentHandler != null) {
            this.currentHandler.onInventoryOpened();
        }
    }
    
    public void onSlotUpdate(final ItemStack itemStack) {
        if (this.currentHandler != null) {
            this.currentHandler.onSlotUpdate(itemStack);
        }
    }
    
    public void destroyHandler() {
        if (this.currentHandler == null) {
            return;
        }
        this.currentHandler.destroy();
        this.currentHandler = null;
    }
    
    public void createHandler() {
        if (this.currentHandler != null) {
            this.destroyHandler();
        }
        this.currentHandler = this.client.options.tutorialStep.createHandler(this);
    }
    
    public void tick() {
        if (this.currentHandler != null) {
            if (this.client.world != null) {
                this.currentHandler.tick();
            }
            else {
                this.destroyHandler();
            }
        }
        else if (this.client.world != null) {
            this.createHandler();
        }
    }
    
    public void setStep(final TutorialStep tutorialStep) {
        this.client.options.tutorialStep = tutorialStep;
        this.client.options.write();
        if (this.currentHandler != null) {
            this.currentHandler.destroy();
            this.currentHandler = tutorialStep.createHandler(this);
        }
    }
    
    public MinecraftClient getClient() {
        return this.client;
    }
    
    public GameMode getGameMode() {
        if (this.client.interactionManager == null) {
            return GameMode.INVALID;
        }
        return this.client.interactionManager.getCurrentGameMode();
    }
    
    public static TextComponent getKeybindName(final String string) {
        return new KeybindTextComponent("key." + string).applyFormat(TextFormat.r);
    }
}
