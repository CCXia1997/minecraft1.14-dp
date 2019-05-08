package net.minecraft.client.gui.container;

import net.minecraft.entity.LivingEntity;
import net.minecraft.client.gui.ingame.PlayerInventoryScreen;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.AbstractDonkeyEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.HorseContainer;
import net.minecraft.client.gui.ContainerScreen;

@Environment(EnvType.CLIENT)
public class HorseScreen extends ContainerScreen<HorseContainer>
{
    private static final Identifier TEXTURE;
    private final HorseBaseEntity entity;
    private float mouseX;
    private float mouseY;
    
    public HorseScreen(final HorseContainer horseContainer, final PlayerInventory playerInventory, final HorseBaseEntity horseBaseEntity) {
        super(horseContainer, playerInventory, horseBaseEntity.getDisplayName());
        this.entity = horseBaseEntity;
        this.passEvents = false;
    }
    
    @Override
    protected void drawForeground(final int mouseX, final int mouseY) {
        this.font.draw(this.title.getFormattedText(), 8.0f, 6.0f, 4210752);
        this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, (float)(this.containerHeight - 96 + 2), 4210752);
    }
    
    @Override
    protected void drawBackground(final float delta, final int mouseX, final int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(HorseScreen.TEXTURE);
        final int integer4 = (this.width - this.containerWidth) / 2;
        final int integer5 = (this.height - this.containerHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.containerWidth, this.containerHeight);
        if (this.entity instanceof AbstractDonkeyEntity) {
            final AbstractDonkeyEntity abstractDonkeyEntity6 = (AbstractDonkeyEntity)this.entity;
            if (abstractDonkeyEntity6.hasChest()) {
                this.blit(integer4 + 79, integer5 + 17, 0, this.containerHeight, abstractDonkeyEntity6.dZ() * 18, 54);
            }
        }
        if (this.entity.canBeSaddled()) {
            this.blit(integer4 + 7, integer5 + 35 - 18, 18, this.containerHeight + 54, 18, 18);
        }
        if (this.entity.canEquip()) {
            if (this.entity instanceof LlamaEntity) {
                this.blit(integer4 + 7, integer5 + 35, 36, this.containerHeight + 54, 18, 18);
            }
            else {
                this.blit(integer4 + 7, integer5 + 35, 0, this.containerHeight + 54, 18, 18);
            }
        }
        PlayerInventoryScreen.drawEntity(integer4 + 51, integer5 + 60, 17, integer4 + 51 - this.mouseX, integer5 + 75 - 50 - this.mouseY, this.entity);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.mouseX = (float)mouseX;
        this.mouseY = (float)mouseY;
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }
    
    static {
        TEXTURE = new Identifier("textures/gui/container/horse.png");
    }
}
