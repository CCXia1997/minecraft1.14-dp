package net.minecraft.client.gui.container;

import javax.annotation.Nullable;
import net.minecraft.item.map.MapState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Items;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.CartographyTableContainer;
import net.minecraft.client.gui.ContainerScreen;

@Environment(EnvType.CLIENT)
public class CartographyTableScreen extends ContainerScreen<CartographyTableContainer>
{
    private static final Identifier TEXTURE;
    
    public CartographyTableScreen(final CartographyTableContainer atl2, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(atl2, playerInventory, textComponent);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
    }
    
    @Override
    protected void drawForeground(final int mouseX, final int mouseY) {
        this.font.draw(this.title.getFormattedText(), 8.0f, 4.0f, 4210752);
        this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, (float)(this.containerHeight - 96 + 2), 4210752);
    }
    
    @Override
    protected void drawBackground(final float delta, final int mouseX, final int mouseY) {
        this.renderBackground();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(CartographyTableScreen.TEXTURE);
        final int integer4 = this.left;
        final int integer5 = this.top;
        this.blit(integer4, integer5, 0, 0, this.containerWidth, this.containerHeight);
        final Item item6 = ((CartographyTableContainer)this.container).getSlot(1).getStack().getItem();
        final boolean boolean7 = item6 == Items.nM;
        final boolean boolean8 = item6 == Items.kR;
        final boolean boolean9 = item6 == Items.GLASS_PANE;
        final ItemStack itemStack10 = ((CartographyTableContainer)this.container).getSlot(0).getStack();
        boolean boolean10 = false;
        MapState mapState11;
        if (itemStack10.getItem() == Items.lV) {
            mapState11 = FilledMapItem.getMapState(itemStack10, this.minecraft.world);
            if (mapState11 != null) {
                if (mapState11.locked) {
                    boolean10 = true;
                    if (boolean8 || boolean9) {
                        this.blit(integer4 + 35, integer5 + 31, this.containerWidth + 50, 132, 28, 21);
                    }
                }
                if (boolean8 && mapState11.scale >= 4) {
                    boolean10 = true;
                    this.blit(integer4 + 35, integer5 + 31, this.containerWidth + 50, 132, 28, 21);
                }
            }
        }
        else {
            mapState11 = null;
        }
        this.drawMap(mapState11, boolean7, boolean8, boolean9, boolean10);
    }
    
    private void drawMap(@Nullable final MapState mapState, final boolean isMap, final boolean isPaper, final boolean isGlassPane, final boolean boolean5) {
        final int integer6 = this.left;
        final int integer7 = this.top;
        if (isPaper && !boolean5) {
            this.blit(integer6 + 67, integer7 + 13, this.containerWidth, 66, 66, 66);
            this.drawMap(mapState, integer6 + 85, integer7 + 31, 0.226f);
        }
        else if (isMap) {
            this.blit(integer6 + 67 + 16, integer7 + 13, this.containerWidth, 132, 50, 66);
            this.drawMap(mapState, integer6 + 86, integer7 + 16, 0.34f);
            this.minecraft.getTextureManager().bindTexture(CartographyTableScreen.TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 0.0f, 1.0f);
            this.blit(integer6 + 67, integer7 + 13 + 16, this.containerWidth, 132, 50, 66);
            this.drawMap(mapState, integer6 + 70, integer7 + 32, 0.34f);
            GlStateManager.popMatrix();
        }
        else if (isGlassPane) {
            this.blit(integer6 + 67, integer7 + 13, this.containerWidth, 0, 66, 66);
            this.drawMap(mapState, integer6 + 71, integer7 + 17, 0.45f);
            this.minecraft.getTextureManager().bindTexture(CartographyTableScreen.TEXTURE);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 0.0f, 1.0f);
            this.blit(integer6 + 66, integer7 + 12, 0, this.containerHeight, 66, 66);
            GlStateManager.popMatrix();
        }
        else {
            this.blit(integer6 + 67, integer7 + 13, this.containerWidth, 0, 66, 66);
            this.drawMap(mapState, integer6 + 71, integer7 + 17, 0.45f);
        }
    }
    
    private void drawMap(@Nullable final MapState state, final int x, final int y, final float size) {
        if (state != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translatef((float)x, (float)y, 1.0f);
            GlStateManager.scalef(size, size, 1.0f);
            this.minecraft.gameRenderer.getMapRenderer().draw(state, true);
            GlStateManager.popMatrix();
        }
    }
    
    static {
        TEXTURE = new Identifier("textures/gui/container/cartography_table.png");
    }
}
