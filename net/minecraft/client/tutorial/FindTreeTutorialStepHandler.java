package net.minecraft.client.tutorial;

import net.minecraft.text.TranslatableTextComponent;
import com.google.common.collect.Sets;
import net.minecraft.block.Blocks;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.block.BlockState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.world.ClientWorld;
import java.util.Iterator;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.toast.Toast;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.world.GameMode;
import net.minecraft.client.toast.TutorialToast;
import net.minecraft.text.TextComponent;
import net.minecraft.block.Block;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FindTreeTutorialStepHandler implements TutorialStepHandler
{
    private static final Set<Block> MATCHING_BLOCKS;
    private static final TextComponent TITLE;
    private static final TextComponent DESCRIPTION;
    private final TutorialManager manager;
    private TutorialToast toast;
    private int ticks;
    
    public FindTreeTutorialStepHandler(final TutorialManager tutorialManager) {
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
                for (final Block block3 : FindTreeTutorialStepHandler.MATCHING_BLOCKS) {
                    if (clientPlayerEntity1.inventory.contains(new ItemStack(block3))) {
                        this.manager.setStep(TutorialStep.CRAFT_PLANKS);
                        return;
                    }
                }
                if (a(clientPlayerEntity1)) {
                    this.manager.setStep(TutorialStep.CRAFT_PLANKS);
                    return;
                }
            }
        }
        if (this.ticks >= 6000 && this.toast == null) {
            this.toast = new TutorialToast(TutorialToast.Type.c, FindTreeTutorialStepHandler.TITLE, FindTreeTutorialStepHandler.DESCRIPTION, false);
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
    public void onTarget(final ClientWorld world, final HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            final BlockState blockState3 = world.getBlockState(((BlockHitResult)hitResult).getBlockPos());
            if (FindTreeTutorialStepHandler.MATCHING_BLOCKS.contains(blockState3.getBlock())) {
                this.manager.setStep(TutorialStep.PUNCH_TREE);
            }
        }
    }
    
    @Override
    public void onSlotUpdate(final ItemStack itemStack) {
        for (final Block block3 : FindTreeTutorialStepHandler.MATCHING_BLOCKS) {
            if (itemStack.getItem() == block3.getItem()) {
                this.manager.setStep(TutorialStep.CRAFT_PLANKS);
            }
        }
    }
    
    public static boolean a(final ClientPlayerEntity clientPlayerEntity) {
        for (final Block block3 : FindTreeTutorialStepHandler.MATCHING_BLOCKS) {
            if (clientPlayerEntity.getStats().getStat(Stats.a.getOrCreateStat(block3)) > 0) {
                return true;
            }
        }
        return false;
    }
    
    static {
        MATCHING_BLOCKS = Sets.<Block>newHashSet(Blocks.I, Blocks.J, Blocks.K, Blocks.L, Blocks.M, Blocks.N, Blocks.U, Blocks.V, Blocks.W, Blocks.X, Blocks.Y, Blocks.Z, Blocks.ag, Blocks.ah, Blocks.ai, Blocks.aj, Blocks.ak, Blocks.al);
        TITLE = new TranslatableTextComponent("tutorial.find_tree.title", new Object[0]);
        DESCRIPTION = new TranslatableTextComponent("tutorial.find_tree.description", new Object[0]);
    }
}
