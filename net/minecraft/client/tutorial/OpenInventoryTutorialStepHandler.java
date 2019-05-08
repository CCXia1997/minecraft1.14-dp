package net.minecraft.client.tutorial;

import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.toast.Toast;
import net.minecraft.world.GameMode;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class OpenInventoryTutorialStepHandler implements TutorialStepHandler
{
    private static final TextComponent TITLE;
    private static final TextComponent DESCRIPTION;
    private final TutorialManager manager;
    private TutorialToast d;
    private int ticks;
    
    public OpenInventoryTutorialStepHandler(final TutorialManager tutorialManager) {
        this.manager = tutorialManager;
    }
    
    @Override
    public void tick() {
        ++this.ticks;
        if (this.manager.getGameMode() != GameMode.b) {
            this.manager.setStep(TutorialStep.NONE);
            return;
        }
        if (this.ticks >= 600 && this.d == null) {
            this.d = new TutorialToast(TutorialToast.Type.d, OpenInventoryTutorialStepHandler.TITLE, OpenInventoryTutorialStepHandler.DESCRIPTION, false);
            this.manager.getClient().getToastManager().add(this.d);
        }
    }
    
    @Override
    public void destroy() {
        if (this.d != null) {
            this.d.hide();
            this.d = null;
        }
    }
    
    @Override
    public void onInventoryOpened() {
        this.manager.setStep(TutorialStep.CRAFT_PLANKS);
    }
    
    static {
        TITLE = new TranslatableTextComponent("tutorial.open_inventory.title", new Object[0]);
        DESCRIPTION = new TranslatableTextComponent("tutorial.open_inventory.description", new Object[] { TutorialManager.getKeybindName("inventory") });
    }
}
