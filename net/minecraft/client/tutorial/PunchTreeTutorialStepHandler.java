package net.minecraft.client.tutorial;

import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.Toast;
import net.minecraft.tag.ItemTags;
import net.minecraft.world.GameMode;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.TextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class PunchTreeTutorialStepHandler implements TutorialStepHandler
{
    private static final TextComponent TITLE;
    private static final TextComponent DESCRIPTION;
    private final TutorialManager manager;
    private TutorialToast d;
    private int ticks;
    private int f;
    
    public PunchTreeTutorialStepHandler(final TutorialManager tutorialManager) {
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
                if (clientPlayerEntity1.inventory.contains(ItemTags.o)) {
                    this.manager.setStep(TutorialStep.CRAFT_PLANKS);
                    return;
                }
                if (FindTreeTutorialStepHandler.a(clientPlayerEntity1)) {
                    this.manager.setStep(TutorialStep.CRAFT_PLANKS);
                    return;
                }
            }
        }
        if ((this.ticks >= 600 || this.f > 3) && this.d == null) {
            this.d = new TutorialToast(TutorialToast.Type.c, PunchTreeTutorialStepHandler.TITLE, PunchTreeTutorialStepHandler.DESCRIPTION, true);
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
    public void onBlockAttacked(final ClientWorld clientWorld, final BlockPos blockPos, final BlockState blockState, final float float4) {
        final boolean boolean5 = blockState.matches(BlockTags.o);
        if (boolean5 && float4 > 0.0f) {
            if (this.d != null) {
                this.d.setProgress(float4);
            }
            if (float4 >= 1.0f) {
                this.manager.setStep(TutorialStep.OPEN_INVENTORY);
            }
        }
        else if (this.d != null) {
            this.d.setProgress(0.0f);
        }
        else if (boolean5) {
            ++this.f;
        }
    }
    
    @Override
    public void onSlotUpdate(final ItemStack itemStack) {
        if (ItemTags.o.contains(itemStack.getItem())) {
            this.manager.setStep(TutorialStep.CRAFT_PLANKS);
        }
    }
    
    static {
        TITLE = new TranslatableTextComponent("tutorial.punch_tree.title", new Object[0]);
        DESCRIPTION = new TranslatableTextComponent("tutorial.punch_tree.description", new Object[] { TutorialManager.getKeybindName("attack") });
    }
}
