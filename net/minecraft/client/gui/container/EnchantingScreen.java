package net.minecraft.client.gui.container;

import java.util.List;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextFormat;
import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.EnchantingPhrases;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.util.math.Matrix4f;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import java.util.Random;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.EnchantingTableContainer;
import net.minecraft.client.gui.ContainerScreen;

@Environment(EnvType.CLIENT)
public class EnchantingScreen extends ContainerScreen<EnchantingTableContainer>
{
    private static final Identifier TEXTURE;
    private static final Identifier BOOK_TEXURE;
    private static final BookModel bookModel;
    private final Random random;
    public int k;
    public float nextPageAngle;
    public float pageAngle;
    public float n;
    public float o;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;
    private ItemStack v;
    
    public EnchantingScreen(final EnchantingTableContainer enchantingTableContainer, final PlayerInventory playerInventory, final TextComponent textComponent) {
        super(enchantingTableContainer, playerInventory, textComponent);
        this.random = new Random();
        this.v = ItemStack.EMPTY;
    }
    
    @Override
    protected void drawForeground(final int mouseX, final int mouseY) {
        this.font.draw(this.title.getFormattedText(), 12.0f, 5.0f, 4210752);
        this.font.draw(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, (float)(this.containerHeight - 96 + 2), 4210752);
    }
    
    @Override
    public void tick() {
        super.tick();
        this.b();
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final int integer6 = (this.width - this.containerWidth) / 2;
        final int integer7 = (this.height - this.containerHeight) / 2;
        for (int integer8 = 0; integer8 < 3; ++integer8) {
            final double double9 = mouseX - (integer6 + 60);
            final double double10 = mouseY - (integer7 + 14 + 19 * integer8);
            if (double9 >= 0.0 && double10 >= 0.0 && double9 < 108.0 && double10 < 19.0 && ((EnchantingTableContainer)this.container).onButtonClick(this.minecraft.player, integer8)) {
                this.minecraft.interactionManager.clickButton(((EnchantingTableContainer)this.container).syncId, integer8);
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    protected void drawBackground(final float delta, final int mouseX, final int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(EnchantingScreen.TEXTURE);
        final int integer4 = (this.width - this.containerWidth) / 2;
        final int integer5 = (this.height - this.containerHeight) / 2;
        this.blit(integer4, integer5, 0, 0, this.containerWidth, this.containerHeight);
        GlStateManager.pushMatrix();
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        final int integer6 = (int)this.minecraft.window.getScaleFactor();
        GlStateManager.viewport((this.width - 320) / 2 * integer6, (this.height - 240) / 2 * integer6, 320 * integer6, 240 * integer6);
        GlStateManager.translatef(-0.34f, 0.23f, 0.0f);
        GlStateManager.multMatrix(Matrix4f.a(90.0, 1.3333334f, 9.0f, 80.0f));
        final float float7 = 1.0f;
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GuiLighting.enable();
        GlStateManager.translatef(0.0f, 3.3f, -16.0f);
        GlStateManager.scalef(1.0f, 1.0f, 1.0f);
        final float float8 = 5.0f;
        GlStateManager.scalef(5.0f, 5.0f, 5.0f);
        GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(EnchantingScreen.BOOK_TEXURE);
        GlStateManager.rotatef(20.0f, 1.0f, 0.0f, 0.0f);
        final float float9 = MathHelper.lerp(delta, this.pageTurningSpeed, this.nextPageTurningSpeed);
        GlStateManager.translatef((1.0f - float9) * 0.2f, (1.0f - float9) * 0.1f, (1.0f - float9) * 0.25f);
        GlStateManager.rotatef(-(1.0f - float9) * 90.0f - 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
        float float10 = MathHelper.lerp(delta, this.pageAngle, this.nextPageAngle) + 0.25f;
        float float11 = MathHelper.lerp(delta, this.pageAngle, this.nextPageAngle) + 0.75f;
        float10 = (float10 - MathHelper.fastFloor(float10)) * 1.6f - 0.3f;
        float11 = (float11 - MathHelper.fastFloor(float11)) * 1.6f - 0.3f;
        if (float10 < 0.0f) {
            float10 = 0.0f;
        }
        if (float11 < 0.0f) {
            float11 = 0.0f;
        }
        if (float10 > 1.0f) {
            float10 = 1.0f;
        }
        if (float11 > 1.0f) {
            float11 = 1.0f;
        }
        GlStateManager.enableRescaleNormal();
        EnchantingScreen.bookModel.render(0.0f, float10, float11, float9, 0.0f, 0.0625f);
        GlStateManager.disableRescaleNormal();
        GuiLighting.disable();
        GlStateManager.matrixMode(5889);
        GlStateManager.viewport(0, 0, this.minecraft.window.getFramebufferWidth(), this.minecraft.window.getFramebufferHeight());
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GuiLighting.disable();
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        EnchantingPhrases.getInstance().setSeed(((EnchantingTableContainer)this.container).getSeed());
        final int integer7 = ((EnchantingTableContainer)this.container).getLapisCount();
        for (int integer8 = 0; integer8 < 3; ++integer8) {
            final int integer9 = integer4 + 60;
            final int integer10 = integer9 + 20;
            this.blitOffset = 0;
            this.minecraft.getTextureManager().bindTexture(EnchantingScreen.TEXTURE);
            final int integer11 = ((EnchantingTableContainer)this.container).enchantmentPower[integer8];
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (integer11 == 0) {
                this.blit(integer9, integer5 + 14 + 19 * integer8, 0, 185, 108, 19);
            }
            else {
                final String string17 = "" + integer11;
                final int integer12 = 86 - this.font.getStringWidth(string17);
                final String string18 = EnchantingPhrases.getInstance().generatePhrase(this.font, integer12);
                TextRenderer textRenderer20 = this.minecraft.getFontManager().getTextRenderer(MinecraftClient.ALT_TEXT_RENDERER_ID);
                int integer13 = 6839882;
                if ((integer7 < integer8 + 1 || this.minecraft.player.experience < integer11) && !this.minecraft.player.abilities.creativeMode) {
                    this.blit(integer9, integer5 + 14 + 19 * integer8, 0, 185, 108, 19);
                    this.blit(integer9 + 1, integer5 + 15 + 19 * integer8, 16 * integer8, 239, 16, 16);
                    textRenderer20.drawStringBounded(string18, integer10, integer5 + 16 + 19 * integer8, integer12, (integer13 & 0xFEFEFE) >> 1);
                    integer13 = 4226832;
                }
                else {
                    final int integer14 = mouseX - (integer4 + 60);
                    final int integer15 = mouseY - (integer5 + 14 + 19 * integer8);
                    if (integer14 >= 0 && integer15 >= 0 && integer14 < 108 && integer15 < 19) {
                        this.blit(integer9, integer5 + 14 + 19 * integer8, 0, 204, 108, 19);
                        integer13 = 16777088;
                    }
                    else {
                        this.blit(integer9, integer5 + 14 + 19 * integer8, 0, 166, 108, 19);
                    }
                    this.blit(integer9 + 1, integer5 + 15 + 19 * integer8, 16 * integer8, 223, 16, 16);
                    textRenderer20.drawStringBounded(string18, integer10, integer5 + 16 + 19 * integer8, integer12, integer13);
                    integer13 = 8453920;
                }
                textRenderer20 = this.minecraft.textRenderer;
                textRenderer20.drawWithShadow(string17, (float)(integer10 + 86 - textRenderer20.getStringWidth(string17)), (float)(integer5 + 16 + 19 * integer8 + 7), integer13);
            }
        }
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, float delta) {
        delta = this.minecraft.getTickDelta();
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        this.drawMouseoverTooltip(mouseX, mouseY);
        final boolean boolean4 = this.minecraft.player.abilities.creativeMode;
        final int integer5 = ((EnchantingTableContainer)this.container).getLapisCount();
        for (int integer6 = 0; integer6 < 3; ++integer6) {
            final int integer7 = ((EnchantingTableContainer)this.container).enchantmentPower[integer6];
            final Enchantment enchantment8 = Enchantment.byRawId(((EnchantingTableContainer)this.container).enchantmentId[integer6]);
            final int integer8 = ((EnchantingTableContainer)this.container).enchantmentLevel[integer6];
            final int integer9 = integer6 + 1;
            if (this.isPointWithinBounds(60, 14 + 19 * integer6, 108, 17, mouseX, mouseY) && integer7 > 0 && integer8 >= 0 && enchantment8 != null) {
                final List<String> list11 = Lists.newArrayList();
                list11.add("" + TextFormat.p + TextFormat.u + I18n.translate("container.enchant.clue", enchantment8.getTextComponent(integer8).getFormattedText()));
                if (!boolean4) {
                    list11.add("");
                    if (this.minecraft.player.experience < integer7) {
                        list11.add(TextFormat.m + I18n.translate("container.enchant.level.requirement", ((EnchantingTableContainer)this.container).enchantmentPower[integer6]));
                    }
                    else {
                        String string12;
                        if (integer9 == 1) {
                            string12 = I18n.translate("container.enchant.lapis.one");
                        }
                        else {
                            string12 = I18n.translate("container.enchant.lapis.many", integer9);
                        }
                        final TextFormat textFormat13 = (integer5 >= integer9) ? TextFormat.h : TextFormat.m;
                        list11.add(textFormat13 + "" + string12);
                        if (integer9 == 1) {
                            string12 = I18n.translate("container.enchant.level.one");
                        }
                        else {
                            string12 = I18n.translate("container.enchant.level.many", integer9);
                        }
                        list11.add(TextFormat.h + "" + string12);
                    }
                }
                this.renderTooltip(list11, mouseX, mouseY);
                break;
            }
        }
    }
    
    public void b() {
        final ItemStack itemStack1 = ((EnchantingTableContainer)this.container).getSlot(0).getStack();
        if (!ItemStack.areEqual(itemStack1, this.v)) {
            this.v = itemStack1;
            do {
                this.n += this.random.nextInt(4) - this.random.nextInt(4);
            } while (this.nextPageAngle <= this.n + 1.0f && this.nextPageAngle >= this.n - 1.0f);
        }
        ++this.k;
        this.pageAngle = this.nextPageAngle;
        this.pageTurningSpeed = this.nextPageTurningSpeed;
        boolean boolean2 = false;
        for (int integer3 = 0; integer3 < 3; ++integer3) {
            if (((EnchantingTableContainer)this.container).enchantmentPower[integer3] != 0) {
                boolean2 = true;
            }
        }
        if (boolean2) {
            this.nextPageTurningSpeed += 0.2f;
        }
        else {
            this.nextPageTurningSpeed -= 0.2f;
        }
        this.nextPageTurningSpeed = MathHelper.clamp(this.nextPageTurningSpeed, 0.0f, 1.0f);
        float float3 = (this.n - this.nextPageAngle) * 0.4f;
        final float float4 = 0.2f;
        float3 = MathHelper.clamp(float3, -0.2f, 0.2f);
        this.o += (float3 - this.o) * 0.9f;
        this.nextPageAngle += this.o;
    }
    
    static {
        TEXTURE = new Identifier("textures/gui/container/enchanting_table.png");
        BOOK_TEXURE = new Identifier("textures/entity/enchanting_table_book.png");
        bookModel = new BookModel();
    }
}
