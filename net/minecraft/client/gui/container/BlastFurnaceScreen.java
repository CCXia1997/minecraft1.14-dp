package net.minecraft.client.gui.container;

import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.BlastFurnaceContainer;

@Environment(EnvType.CLIENT)
public class BlastFurnaceScreen extends AbstractFurnaceScreen<BlastFurnaceContainer>
{
    private static final Identifier BG_TEX;
    
    public BlastFurnaceScreen(final BlastFurnaceContainer blastFurnaceContainer, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(blastFurnaceContainer, new BlastFurnaceRecipeBookScreen(), playerInventory, textComponent, BlastFurnaceScreen.BG_TEX);
    }
    
    static {
        BG_TEX = new Identifier("textures/gui/container/blast_furnace.png");
    }
}
