package net.minecraft.client.tutorial;

import net.minecraft.text.TranslatableTextComponent;
import java.util.Iterator;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.tag.Tag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.Toast;
import net.minecraft.tag.ItemTags;
import net.minecraft.world.GameMode;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CraftPlanksTutorialStepHandler implements TutorialStepHandler
{
    private static final TextComponent TITLE;
    private static final TextComponent DESCRIPTION;
    private final TutorialManager manager;
    private TutorialToast toast;
    private int ticks;
    
    public CraftPlanksTutorialStepHandler(final TutorialManager tutorialManager) {
        this.manager = tutorialManager;
    }
    
    @Override
    public void tick() {
        ++this.ticks;
        if (this.manager.getGameMode() != GameMode.b) {
            this.manager.setStep(TutorialStep.NONE);
            return;
        }
        if (this.ticks == 1) {
            final ClientPlayerEntity clientPlayerEntity1 = this.manager.getClient().player;
            if (clientPlayerEntity1 != null) {
                if (clientPlayerEntity1.inventory.contains(ItemTags.b)) {
                    this.manager.setStep(TutorialStep.NONE);
                    return;
                }
                if (hasCrafted(clientPlayerEntity1, ItemTags.b)) {
                    this.manager.setStep(TutorialStep.NONE);
                    return;
                }
            }
        }
        if (this.ticks >= 1200 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Type.e, CraftPlanksTutorialStepHandler.TITLE, CraftPlanksTutorialStepHandler.DESCRIPTION, false);
            this.manager.getClient().getToastManager().add(this.toast);
        }
    }
    
    @Override
    public void destroy() {
        if (this.toast != null) {
            this.toast.hide();
            this.toast = null;
        }
    }
    
    @Override
    public void onSlotUpdate(final ItemStack itemStack) {
        final Item item2 = itemStack.getItem();
        if (ItemTags.b.contains(item2)) {
            this.manager.setStep(TutorialStep.NONE);
        }
    }
    
    public static boolean hasCrafted(final ClientPlayerEntity player, final Tag<Item> tag) {
        for (final Item item4 : tag.values()) {
            if (player.getStats().getStat(Stats.b.getOrCreateStat(item4)) > 0) {
                return true;
            }
        }
        return false;
    }
    
    static {
        TITLE = new TranslatableTextComponent("tutorial.craft_planks.title", new Object[0]);
        DESCRIPTION = new TranslatableTextComponent("tutorial.craft_planks.description", new Object[0]);
    }
}
