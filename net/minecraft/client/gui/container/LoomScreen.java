package net.minecraft.client.gui.container;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.BannerItem;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.container.Slot;
import net.minecraft.client.gui.DrawableHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Lists;
import net.minecraft.client.texture.TextureCache;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import java.util.List;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.LoomContainer;
import net.minecraft.client.gui.ContainerScreen;

@Environment(EnvType.CLIENT)
public class LoomScreen extends ContainerScreen<LoomContainer>
{
    private static final Identifier TEXTURE;
    private static final int PATTERN_BUTTON_ROW_COUNT;
    private static final DyeColor PATTERN_BUTTON_BACKGROUND_COLOR;
    private static final DyeColor PATTERN_BUTTON_FOREGROUND_COLOR;
    private static final List<DyeColor> PATTERN_BUTTON_COLORS;
    private Identifier output;
    private ItemStack banner;
    private ItemStack dye;
    private ItemStack pattern;
    private final Identifier[] patternButtonTextureIds;
    private boolean canApplyDyePattern;
    private boolean canApplySpecialPattern;
    private boolean hasTooManyPatterns;
    private float scrollPosition;
    private boolean scrollbarClicked;
    private int firstPatternButtonId;
    private int lastCachedPatternButtonTextureId;
    
    public LoomScreen(final LoomContainer loomContainer, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(loomContainer, playerInventory, textComponent);
        this.banner = ItemStack.EMPTY;
        this.dye = ItemStack.EMPTY;
        this.pattern = ItemStack.EMPTY;
        this.patternButtonTextureIds = new Identifier[BannerPattern.COUNT];
        this.firstPatternButtonId = 1;
        this.lastCachedPatternButtonTextureId = 1;
        loomContainer.setInventoryChangeListener(this::onInventoryChanged);
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.lastCachedPatternButtonTextureId < BannerPattern.COUNT) {
            final BannerPattern bannerPattern1 = BannerPattern.values()[this.lastCachedPatternButtonTextureId];
            final String string2 = "b" + LoomScreen.PATTERN_BUTTON_BACKGROUND_COLOR.getId();
            final String string3 = bannerPattern1.getId() + LoomScreen.PATTERN_BUTTON_FOREGROUND_COLOR.getId();
            this.patternButtonTextureIds[this.lastCachedPatternButtonTextureId] = TextureCache.BANNER.get(string2 + string3, Lists.<BannerPattern>newArrayList(BannerPattern.BASE, bannerPattern1), LoomScreen.PATTERN_BUTTON_COLORS);
            ++this.lastCachedPatternButtonTextureId;
        }
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
        this.minecraft.getTextureManager().bindTexture(LoomScreen.TEXTURE);
        final int integer4 = this.left;
        final int integer5 = this.top;
        this.blit(integer4, integer5, 0, 0, this.containerWidth, this.containerHeight);
        final Slot slot6 = ((LoomContainer)this.container).getBannerSlot();
        final Slot slot7 = ((LoomContainer)this.container).getDyeSlot();
        final Slot slot8 = ((LoomContainer)this.container).getPatternSlot();
        final Slot slot9 = ((LoomContainer)this.container).getOutputSlot();
        if (!slot6.hasStack()) {
            this.blit(integer4 + slot6.xPosition, integer5 + slot6.yPosition, this.containerWidth, 0, 16, 16);
        }
        if (!slot7.hasStack()) {
            this.blit(integer4 + slot7.xPosition, integer5 + slot7.yPosition, this.containerWidth + 16, 0, 16, 16);
        }
        if (!slot8.hasStack()) {
            this.blit(integer4 + slot8.xPosition, integer5 + slot8.yPosition, this.containerWidth + 32, 0, 16, 16);
        }
        final int integer6 = (int)(41.0f * this.scrollPosition);
        this.blit(integer4 + 119, integer5 + 13 + integer6, 232 + (this.canApplyDyePattern ? 0 : 12), 0, 12, 15);
        if (this.output != null && !this.hasTooManyPatterns) {
            this.minecraft.getTextureManager().bindTexture(this.output);
            DrawableHelper.blit(integer4 + 141, integer5 + 8, 20, 40, 1.0f, 1.0f, 20, 40, 64, 64);
        }
        else if (this.hasTooManyPatterns) {
            this.blit(integer4 + slot9.xPosition - 2, integer5 + slot9.yPosition - 2, this.containerWidth, 17, 17, 16);
        }
        if (this.canApplyDyePattern) {
            final int integer7 = integer4 + 60;
            final int integer8 = integer5 + 13;
            for (int integer9 = this.firstPatternButtonId + 16, integer10 = this.firstPatternButtonId; integer10 < integer9 && integer10 < this.patternButtonTextureIds.length - 5; ++integer10) {
                final int integer11 = integer10 - this.firstPatternButtonId;
                final int integer12 = integer7 + integer11 % 4 * 14;
                final int integer13 = integer8 + integer11 / 4 * 14;
                this.minecraft.getTextureManager().bindTexture(LoomScreen.TEXTURE);
                int integer14 = this.containerHeight;
                if (integer10 == ((LoomContainer)this.container).getSelectedPattern()) {
                    integer14 += 14;
                }
                else if (mouseX >= integer12 && mouseY >= integer13 && mouseX < integer12 + 14 && mouseY < integer13 + 14) {
                    integer14 += 28;
                }
                this.blit(integer12, integer13, 0, integer14, 14, 14);
                if (this.patternButtonTextureIds[integer10] != null) {
                    this.minecraft.getTextureManager().bindTexture(this.patternButtonTextureIds[integer10]);
                    DrawableHelper.blit(integer12 + 4, integer13 + 2, 5, 10, 1.0f, 1.0f, 20, 40, 64, 64);
                }
            }
        }
        else if (this.canApplySpecialPattern) {
            final int integer7 = integer4 + 60;
            final int integer8 = integer5 + 13;
            this.minecraft.getTextureManager().bindTexture(LoomScreen.TEXTURE);
            this.blit(integer7, integer8, 0, this.containerHeight, 14, 14);
            final int integer9 = ((LoomContainer)this.container).getSelectedPattern();
            if (this.patternButtonTextureIds[integer9] != null) {
                this.minecraft.getTextureManager().bindTexture(this.patternButtonTextureIds[integer9]);
                DrawableHelper.blit(integer7 + 4, integer8 + 2, 5, 10, 1.0f, 1.0f, 20, 40, 64, 64);
            }
        }
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        this.scrollbarClicked = false;
        if (this.canApplyDyePattern) {
            int integer6 = this.left + 60;
            int integer7 = this.top + 13;
            for (int integer8 = this.firstPatternButtonId + 16, integer9 = this.firstPatternButtonId; integer9 < integer8; ++integer9) {
                final int integer10 = integer9 - this.firstPatternButtonId;
                final double double11 = mouseX - (integer6 + integer10 % 4 * 14);
                final double double12 = mouseY - (integer7 + integer10 / 4 * 14);
                if (double11 >= 0.0 && double12 >= 0.0 && double11 < 14.0 && double12 < 14.0 && ((LoomContainer)this.container).onButtonClick(this.minecraft.player, integer9)) {
                    MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.mi, 1.0f));
                    this.minecraft.interactionManager.clickButton(((LoomContainer)this.container).syncId, integer9);
                    return true;
                }
            }
            integer6 = this.left + 119;
            integer7 = this.top + 9;
            if (mouseX >= integer6 && mouseX < integer6 + 12 && mouseY >= integer7 && mouseY < integer7 + 56) {
                this.scrollbarClicked = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        if (this.scrollbarClicked && this.canApplyDyePattern) {
            final int integer10 = this.top + 13;
            final int integer11 = integer10 + 56;
            this.scrollPosition = ((float)mouseY - integer10 - 7.5f) / (integer11 - integer10 - 15.0f);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            final int integer12 = LoomScreen.PATTERN_BUTTON_ROW_COUNT - 4;
            int integer13 = (int)(this.scrollPosition * integer12 + 0.5);
            if (integer13 < 0) {
                integer13 = 0;
            }
            this.firstPatternButtonId = 1 + integer13 * 4;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        if (this.canApplyDyePattern) {
            final int integer7 = LoomScreen.PATTERN_BUTTON_ROW_COUNT - 4;
            this.scrollPosition -= (float)(amount / integer7);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            this.firstPatternButtonId = 1 + (int)(this.scrollPosition * integer7 + 0.5) * 4;
        }
        return true;
    }
    
    @Override
    protected boolean isClickOutsideBounds(final double mouseX, final double mouseY, final int left, final int top, final int button) {
        return mouseX < left || mouseY < top || mouseX >= left + this.containerWidth || mouseY >= top + this.containerHeight;
    }
    
    private void onInventoryChanged() {
        final ItemStack itemStack1 = ((LoomContainer)this.container).getOutputSlot().getStack();
        if (itemStack1.isEmpty()) {
            this.output = null;
        }
        else {
            final BannerBlockEntity bannerBlockEntity2 = new BannerBlockEntity();
            bannerBlockEntity2.deserialize(itemStack1, ((BannerItem)itemStack1.getItem()).getColor());
            this.output = TextureCache.BANNER.get(bannerBlockEntity2.getPatternCacheKey(), bannerBlockEntity2.getPatterns(), bannerBlockEntity2.getPatternColors());
        }
        final ItemStack itemStack2 = ((LoomContainer)this.container).getBannerSlot().getStack();
        final ItemStack itemStack3 = ((LoomContainer)this.container).getDyeSlot().getStack();
        final ItemStack itemStack4 = ((LoomContainer)this.container).getPatternSlot().getStack();
        final CompoundTag compoundTag5 = itemStack2.getOrCreateSubCompoundTag("BlockEntityTag");
        this.hasTooManyPatterns = (compoundTag5.containsKey("Patterns", 9) && !itemStack2.isEmpty() && compoundTag5.getList("Patterns", 10).size() >= 6);
        if (this.hasTooManyPatterns) {
            this.output = null;
        }
        if (!ItemStack.areEqual(itemStack2, this.banner) || !ItemStack.areEqual(itemStack3, this.dye) || !ItemStack.areEqual(itemStack4, this.pattern)) {
            this.canApplyDyePattern = (!itemStack2.isEmpty() && !itemStack3.isEmpty() && itemStack4.isEmpty() && !this.hasTooManyPatterns);
            this.canApplySpecialPattern = (!this.hasTooManyPatterns && !itemStack4.isEmpty() && !itemStack2.isEmpty() && !itemStack3.isEmpty());
        }
        this.banner = itemStack2.copy();
        this.dye = itemStack3.copy();
        this.pattern = itemStack4.copy();
    }
    
    static {
        TEXTURE = new Identifier("textures/gui/container/loom.png");
        PATTERN_BUTTON_ROW_COUNT = (BannerPattern.COUNT - 5 - 1 + 4 - 1) / 4;
        PATTERN_BUTTON_BACKGROUND_COLOR = DyeColor.h;
        PATTERN_BUTTON_FOREGROUND_COLOR = DyeColor.a;
        PATTERN_BUTTON_COLORS = Lists.<DyeColor>newArrayList(LoomScreen.PATTERN_BUTTON_BACKGROUND_COLOR, LoomScreen.PATTERN_BUTTON_FOREGROUND_COLOR);
    }
}
