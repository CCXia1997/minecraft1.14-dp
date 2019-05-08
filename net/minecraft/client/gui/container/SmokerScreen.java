package net.minecraft.client.gui.container;

import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.SmokerContainer;

@Environment(EnvType.CLIENT)
public class SmokerScreen extends AbstractFurnaceScreen<SmokerContainer>
{
    private static final Identifier BG_TEX;
    
    public SmokerScreen(final SmokerContainer smokerContainer, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(smokerContainer, new SmokerRecipeBookScreen(), playerInventory, textComponent, SmokerScreen.BG_TEX);
    }
    
    static {
        BG_TEX = new Identifier("textures/gui/container/smoker.png");
    }
}
