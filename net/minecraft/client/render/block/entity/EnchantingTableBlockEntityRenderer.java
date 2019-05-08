package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EnchantingTableBlockEntity;

@Environment(EnvType.CLIENT)
public class EnchantingTableBlockEntityRenderer extends BlockEntityRenderer<EnchantingTableBlockEntity>
{
    private static final Identifier BOOK_TEX;
    private final BookModel book;
    
    public EnchantingTableBlockEntityRenderer() {
        this.book = new BookModel();
    }
    
    @Override
    public void render(final EnchantingTableBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.75f, (float)zOffset + 0.5f);
        final float float10 = entity.ticks + tickDelta;
        GlStateManager.translatef(0.0f, 0.1f + MathHelper.sin(float10 * 0.1f) * 0.01f, 0.0f);
        float float11;
        for (float11 = entity.k - entity.l; float11 >= 3.1415927f; float11 -= 6.2831855f) {}
        while (float11 < -3.1415927f) {
            float11 += 6.2831855f;
        }
        final float float12 = entity.l + float11 * tickDelta;
        GlStateManager.rotatef(-float12 * 57.295776f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(80.0f, 0.0f, 0.0f, 1.0f);
        this.bindTexture(EnchantingTableBlockEntityRenderer.BOOK_TEX);
        float float13 = MathHelper.lerp(tickDelta, entity.pageAngle, entity.nextPageAngle) + 0.25f;
        float float14 = MathHelper.lerp(tickDelta, entity.pageAngle, entity.nextPageAngle) + 0.75f;
        float13 = (float13 - MathHelper.fastFloor(float13)) * 1.6f - 0.3f;
        float14 = (float14 - MathHelper.fastFloor(float14)) * 1.6f - 0.3f;
        if (float13 < 0.0f) {
            float13 = 0.0f;
        }
        if (float14 < 0.0f) {
            float14 = 0.0f;
        }
        if (float13 > 1.0f) {
            float13 = 1.0f;
        }
        if (float14 > 1.0f) {
            float14 = 1.0f;
        }
        final float float15 = MathHelper.lerp(tickDelta, entity.pageTurningSpeed, entity.nextPageTurningSpeed);
        GlStateManager.enableCull();
        this.book.render(float10, float13, float14, float15, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
    }
    
    static {
        BOOK_TEX = new Identifier("textures/entity/enchanting_table_book.png");
    }
}
