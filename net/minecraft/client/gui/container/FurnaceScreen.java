package net.minecraft.client.gui.container;

import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.FurnaceContainer;

@Environment(EnvType.CLIENT)
public class FurnaceScreen extends AbstractFurnaceScreen<FurnaceContainer>
{
    private static final Identifier BG_TEX;
    
    public FurnaceScreen(final FurnaceContainer furnaceContainer, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(furnaceContainer, new FurnaceRecipeBookScreen(), playerInventory, textComponent, FurnaceScreen.BG_TEX);
    }
    
    static {
        BG_TEX = new Identifier("textures/gui/container/furnace.png");
    }
}
