package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.state.property.Property;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.render.entity.model.BookModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.LecternBlockEntity;

@Environment(EnvType.CLIENT)
public class LecternBlockEntityRenderer extends BlockEntityRenderer<LecternBlockEntity>
{
    private static final Identifier BOOK_TEXTURE;
    private final BookModel book;
    
    public LecternBlockEntityRenderer() {
        this.book = new BookModel();
    }
    
    @Override
    public void render(final LecternBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        final BlockState blockState10 = entity.getCachedState();
        if (!blockState10.<Boolean>get((Property<Boolean>)LecternBlock.HAS_BOOK)) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 1.0f + 0.0625f, (float)zOffset + 0.5f);
        final float float11 = blockState10.<Direction>get((Property<Direction>)LecternBlock.FACING).rotateYClockwise().asRotation();
        GlStateManager.rotatef(-float11, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotatef(67.5f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translatef(0.0f, -0.125f, 0.0f);
        this.bindTexture(LecternBlockEntityRenderer.BOOK_TEXTURE);
        GlStateManager.enableCull();
        this.book.render(0.0f, 0.1f, 0.9f, 1.2f, 0.0f, 0.0625f);
        GlStateManager.popMatrix();
    }
    
    static {
        BOOK_TEXTURE = new Identifier("textures/entity/enchanting_table_book.png");
    }
}
