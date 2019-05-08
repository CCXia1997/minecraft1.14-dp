package net.minecraft.client.gui.menu;

import net.minecraft.client.options.GameOptions;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class DemoScreen extends Screen
{
    private static final Identifier DEMO_BG;
    
    public DemoScreen() {
        super(new TranslatableTextComponent("demo.help.title", new Object[0]));
    }
    
    @Override
    protected void init() {
        final int integer1 = -16;
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 116, this.height / 2 + 62 - 16, 114, 20, I18n.translate("demo.help.buy"), buttonWidget -> {
            buttonWidget.active = false;
            SystemUtil.getOperatingSystem().open("http://www.minecraft.net/store?source=demo");
            return;
        }));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 2, this.height / 2 + 62 - 16, 114, 20, I18n.translate("demo.help.later"), buttonWidget -> {
            this.minecraft.openScreen(null);
            this.minecraft.mouse.lockCursor();
        }));
    }
    
    @Override
    public void renderBackground() {
        super.renderBackground();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(DemoScreen.DEMO_BG);
        final int integer1 = (this.width - 248) / 2;
        final int integer2 = (this.height - 166) / 2;
        this.blit(integer1, integer2, 0, 0, 248, 166);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        final int integer4 = (this.width - 248) / 2 + 10;
        int integer5 = (this.height - 166) / 2 + 8;
        this.font.draw(this.title.getFormattedText(), (float)integer4, (float)integer5, 2039583);
        integer5 += 12;
        final GameOptions gameOptions6 = this.minecraft.options;
        this.font.draw(I18n.translate("demo.help.movementShort", gameOptions6.keyForward.getLocalizedName(), gameOptions6.keyLeft.getLocalizedName(), gameOptions6.keyBack.getLocalizedName(), gameOptions6.keyRight.getLocalizedName()), (float)integer4, (float)integer5, 5197647);
        this.font.draw(I18n.translate("demo.help.movementMouse"), (float)integer4, (float)(integer5 + 12), 5197647);
        this.font.draw(I18n.translate("demo.help.jump", gameOptions6.keyJump.getLocalizedName()), (float)integer4, (float)(integer5 + 24), 5197647);
        this.font.draw(I18n.translate("demo.help.inventory", gameOptions6.keyInventory.getLocalizedName()), (float)integer4, (float)(integer5 + 36), 5197647);
        this.font.drawStringBounded(I18n.translate("demo.help.fullWrapped"), integer4, integer5 + 68, 218, 2039583);
        super.render(mouseX, mouseY, delta);
    }
    
    static {
        DEMO_BG = new Identifier("textures/gui/demo_background.png");
    }
}
