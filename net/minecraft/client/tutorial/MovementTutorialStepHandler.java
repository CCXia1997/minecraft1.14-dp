package net.minecraft.client.tutorial;

import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.input.Input;
import net.minecraft.client.toast.Toast;
import net.minecraft.world.GameMode;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class MovementTutorialStepHandler implements TutorialStepHandler
{
    private static final TextComponent MOVE_TITLE;
    private static final TextComponent MOVE_DESCRIPTION;
    private static final TextComponent LOOK_TITLE;
    private static final TextComponent LOOK_DESCRIPTION;
    private final TutorialManager manager;
    private TutorialToast moveToast;
    private TutorialToast lookAroundToast;
    private int ticks;
    private int movedTicks;
    private int lookedAroundTicks;
    private boolean movedLastTick;
    private boolean lookedAroundLastTick;
    private int moveAroundCompletionTicks;
    private int lookAroundCompletionTicks;
    
    public MovementTutorialStepHandler(final TutorialManager tutorialManager) {
        this.moveAroundCompletionTicks = -1;
        this.lookAroundCompletionTicks = -1;
        this.manager = tutorialManager;
    }
    
    @Override
    public void tick() {
        ++this.ticks;
        if (this.movedLastTick) {
            ++this.movedTicks;
            this.movedLastTick = false;
        }
        if (this.lookedAroundLastTick) {
            ++this.lookedAroundTicks;
            this.lookedAroundLastTick = false;
        }
        if (this.moveAroundCompletionTicks == -1 && this.movedTicks > 40) {
            if (this.moveToast != null) {
                this.moveToast.hide();
                this.moveToast = null;
            }
            this.moveAroundCompletionTicks = this.ticks;
        }
        if (this.lookAroundCompletionTicks == -1 && this.lookedAroundTicks > 40) {
            if (this.lookAroundToast != null) {
                this.lookAroundToast.hide();
                this.lookAroundToast = null;
            }
            this.lookAroundCompletionTicks = this.ticks;
        }
        if (this.moveAroundCompletionTicks != -1 && this.lookAroundCompletionTicks != -1) {
            if (this.manager.getGameMode() == GameMode.b) {
                this.manager.setStep(TutorialStep.FIND_TREE);
            }
            else {
                this.manager.setStep(TutorialStep.NONE);
            }
        }
        if (this.moveToast != null) {
            this.moveToast.setProgress(this.movedTicks / 40.0f);
        }
        if (this.lookAroundToast != null) {
            this.lookAroundToast.setProgress(this.lookedAroundTicks / 40.0f);
        }
        if (this.ticks >= 100) {
            if (this.moveAroundCompletionTicks == -1 && this.moveToast == null) {
                this.moveToast = new TutorialToast(TutorialToast.Type.a, MovementTutorialStepHandler.MOVE_TITLE, MovementTutorialStepHandler.MOVE_DESCRIPTION, true);
                this.manager.getClient().getToastManager().add(this.moveToast);
            }
            else if (this.moveAroundCompletionTicks != -1 && this.ticks - this.moveAroundCompletionTicks >= 20 && this.lookAroundCompletionTicks == -1 && this.lookAroundToast == null) {
                this.lookAroundToast = new TutorialToast(TutorialToast.Type.b, MovementTutorialStepHandler.LOOK_TITLE, MovementTutorialStepHandler.LOOK_DESCRIPTION, true);
                this.manager.getClient().getToastManager().add(this.lookAroundToast);
            }
        }
    }
    
    @Override
    public void destroy() {
        if (this.moveToast != null) {
            this.moveToast.hide();
            this.moveToast = null;
        }
        if (this.lookAroundToast != null) {
            this.lookAroundToast.hide();
            this.lookAroundToast = null;
        }
    }
    
    @Override
    public void onMovement(final Input input) {
        if (input.pressingForward || input.pressingBack || input.pressingLeft || input.pressingRight || input.jumping) {
            this.movedLastTick = true;
        }
    }
    
    @Override
    public void onMouseUpdate(final double deltaX, final double deltaY) {
        if (Math.abs(deltaX) > 0.01 || Math.abs(deltaY) > 0.01) {
            this.lookedAroundLastTick = true;
        }
    }
    
    static {
        MOVE_TITLE = new TranslatableTextComponent("tutorial.move.title", new Object[] { TutorialManager.getKeybindName("forward"), TutorialManager.getKeybindName("left"), TutorialManager.getKeybindName("back"), TutorialManager.getKeybindName("right") });
        MOVE_DESCRIPTION = new TranslatableTextComponent("tutorial.move.description", new Object[] { TutorialManager.getKeybindName("jump") });
        LOOK_TITLE = new TranslatableTextComponent("tutorial.look.title", new Object[0]);
        LOOK_DESCRIPTION = new TranslatableTextComponent("tutorial.look.description", new Object[0]);
    }
}
