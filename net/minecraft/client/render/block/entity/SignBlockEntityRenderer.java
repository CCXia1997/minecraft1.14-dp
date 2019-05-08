package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.text.TextComponent;
import net.minecraft.client.util.TextComponentUtil;
import net.minecraft.util.math.Direction;
import net.minecraft.block.WallSignBlock;
import net.minecraft.state.property.Property;
import net.minecraft.block.SignBlock;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.SignBlockEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.SignBlockEntity;

@Environment(EnvType.CLIENT)
public class SignBlockEntityRenderer extends BlockEntityRenderer<SignBlockEntity>
{
    private static final Identifier OAK_TEX;
    private static final Identifier SPRUCE_TEX;
    private static final Identifier BIRCH_TEX;
    private static final Identifier ACACIA_TEX;
    private static final Identifier JUNGLE_TEX;
    private static final Identifier DARK_OAK_TEX;
    private final SignBlockEntityModel model;
    
    public SignBlockEntityRenderer() {
        this.model = new SignBlockEntityModel();
    }
    
    @Override
    public void render(final SignBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        final BlockState blockState10 = entity.getCachedState();
        GlStateManager.pushMatrix();
        final float float11 = 0.6666667f;
        if (blockState10.getBlock() instanceof SignBlock) {
            GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.5f, (float)zOffset + 0.5f);
            GlStateManager.rotatef(-(blockState10.<Integer>get((Property<Integer>)SignBlock.ROTATION) * 360 / 16.0f), 0.0f, 1.0f, 0.0f);
            this.model.getSignpostModel().visible = true;
        }
        else {
            if (!(blockState10.getBlock() instanceof WallSignBlock)) {
                GlStateManager.popMatrix();
                return;
            }
            GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.5f, (float)zOffset + 0.5f);
            GlStateManager.rotatef(-blockState10.<Direction>get((Property<Direction>)WallSignBlock.FACING).asRotation(), 0.0f, 1.0f, 0.0f);
            GlStateManager.translatef(0.0f, -0.3125f, -0.4375f);
            this.model.getSignpostModel().visible = false;
        }
        if (blockBreakStage >= 0) {
            this.bindTexture(SignBlockEntityRenderer.DESTROY_STAGE_TEXTURES[blockBreakStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(4.0f, 2.0f, 1.0f);
            GlStateManager.translatef(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        else {
            this.bindTexture(this.getModelTexture(blockState10.getBlock()));
        }
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        GlStateManager.scalef(0.6666667f, -0.6666667f, -0.6666667f);
        this.model.render();
        GlStateManager.popMatrix();
        final TextRenderer textRenderer12 = this.getFontRenderer();
        final float float12 = 0.010416667f;
        GlStateManager.translatef(0.0f, 0.33333334f, 0.046666667f);
        GlStateManager.scalef(0.010416667f, -0.010416667f, 0.010416667f);
        GlStateManager.normal3f(0.0f, 0.0f, -0.010416667f);
        GlStateManager.depthMask(false);
        final int integer14 = entity.getTextColor().getSignColor();
        if (blockBreakStage < 0) {
            for (int integer15 = 0; integer15 < 4; ++integer15) {
                final List<TextComponent> list3;
                final String string16 = entity.getTextBeingEditedOnRow(integer15, textComponent -> {
                    list3 = TextComponentUtil.wrapLines(textComponent, 90, textRenderer12, false, true);
                    return list3.isEmpty() ? "" : list3.get(0).getFormattedText();
                });
                if (string16 != null) {
                    textRenderer12.draw(string16, (float)(-textRenderer12.getStringWidth(string16) / 2), (float)(integer15 * 10 - entity.text.length * 5), integer14);
                    if (integer15 == entity.getCurrentRow() && entity.getSelectionStart() >= 0) {
                        final int integer16 = textRenderer12.getStringWidth(string16.substring(0, Math.max(Math.min(entity.getSelectionStart(), string16.length()), 0)));
                        final int integer17 = textRenderer12.isRightToLeft() ? -1 : 1;
                        final int integer18 = (integer16 - textRenderer12.getStringWidth(string16) / 2) * integer17;
                        final int integer19 = integer15 * 10 - entity.text.length * 5;
                        if (entity.isCaretVisible()) {
                            if (entity.getSelectionStart() < string16.length()) {
                                final int left = integer18;
                                final int top = integer19 - 1;
                                final int right = integer18 + 1;
                                final int n = integer19;
                                textRenderer12.getClass();
                                DrawableHelper.fill(left, top, right, n + 9, 0xFF000000 | integer14);
                            }
                            else {
                                textRenderer12.draw("_", (float)integer18, (float)integer19, integer14);
                            }
                        }
                        if (entity.getSelectionEnd() != entity.getSelectionStart()) {
                            final int integer20 = Math.min(entity.getSelectionStart(), entity.getSelectionEnd());
                            final int integer21 = Math.max(entity.getSelectionStart(), entity.getSelectionEnd());
                            final int integer22 = (textRenderer12.getStringWidth(string16.substring(0, integer20)) - textRenderer12.getStringWidth(string16) / 2) * integer17;
                            final int integer23 = (textRenderer12.getStringWidth(string16.substring(0, integer21)) - textRenderer12.getStringWidth(string16) / 2) * integer17;
                            final int min = Math.min(integer22, integer23);
                            final int integer24 = integer19;
                            final int max = Math.max(integer22, integer23);
                            final int n2 = integer19;
                            textRenderer12.getClass();
                            this.a(min, integer24, max, n2 + 9);
                        }
                    }
                }
            }
        }
        GlStateManager.depthMask(true);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
        if (blockBreakStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
    
    private Identifier getModelTexture(final Block block) {
        if (block == Blocks.bX || block == Blocks.ch) {
            return SignBlockEntityRenderer.OAK_TEX;
        }
        if (block == Blocks.bY || block == Blocks.ci) {
            return SignBlockEntityRenderer.SPRUCE_TEX;
        }
        if (block == Blocks.bZ || block == Blocks.cj) {
            return SignBlockEntityRenderer.BIRCH_TEX;
        }
        if (block == Blocks.ca || block == Blocks.ck) {
            return SignBlockEntityRenderer.ACACIA_TEX;
        }
        if (block == Blocks.cb || block == Blocks.cl) {
            return SignBlockEntityRenderer.JUNGLE_TEX;
        }
        if (block == Blocks.cc || block == Blocks.cm) {
            return SignBlockEntityRenderer.DARK_OAK_TEX;
        }
        return SignBlockEntityRenderer.OAK_TEX;
    }
    
    private void a(final int integer1, final int integer2, final int integer3, final int integer4) {
        final Tessellator tessellator5 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder6 = tessellator5.getBufferBuilder();
        GlStateManager.color4f(0.0f, 0.0f, 255.0f, 255.0f);
        GlStateManager.disableTexture();
        GlStateManager.enableColorLogicOp();
        GlStateManager.logicOp(GlStateManager.LogicOp.n);
        bufferBuilder6.begin(7, VertexFormats.POSITION);
        bufferBuilder6.vertex(integer1, integer4, 0.0).next();
        bufferBuilder6.vertex(integer3, integer4, 0.0).next();
        bufferBuilder6.vertex(integer3, integer2, 0.0).next();
        bufferBuilder6.vertex(integer1, integer2, 0.0).next();
        tessellator5.draw();
        GlStateManager.disableColorLogicOp();
        GlStateManager.enableTexture();
    }
    
    static {
        OAK_TEX = new Identifier("textures/entity/signs/oak.png");
        SPRUCE_TEX = new Identifier("textures/entity/signs/spruce.png");
        BIRCH_TEX = new Identifier("textures/entity/signs/birch.png");
        ACACIA_TEX = new Identifier("textures/entity/signs/acacia.png");
        JUNGLE_TEX = new Identifier("textures/entity/signs/jungle.png");
        DARK_OAK_TEX = new Identifier("textures/entity/signs/dark_oak.png");
    }
}
