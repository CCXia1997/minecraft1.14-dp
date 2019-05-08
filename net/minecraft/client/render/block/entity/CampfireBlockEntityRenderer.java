package net.minecraft.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.DefaultedList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.MinecraftClient;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Property;
import net.minecraft.block.CampfireBlock;
import net.minecraft.util.math.Direction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.CampfireBlockEntity;

@Environment(EnvType.CLIENT)
public class CampfireBlockEntityRenderer extends BlockEntityRenderer<CampfireBlockEntity>
{
    @Override
    public void render(final CampfireBlockEntity entity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage) {
        final Direction direction10 = entity.getCachedState().<Direction>get((Property<Direction>)CampfireBlock.FACING);
        final DefaultedList<ItemStack> defaultedList11 = entity.getItemsBeingCooked();
        for (int integer12 = 0; integer12 < defaultedList11.size(); ++integer12) {
            final ItemStack itemStack13 = defaultedList11.get(integer12);
            if (itemStack13 != ItemStack.EMPTY) {
                GlStateManager.pushMatrix();
                GlStateManager.translatef((float)xOffset + 0.5f, (float)yOffset + 0.44921875f, (float)zOffset + 0.5f);
                final Direction direction11 = Direction.fromHorizontal((integer12 + direction10.getHorizontal()) % 4);
                GlStateManager.rotatef(-direction11.asRotation(), 0.0f, 1.0f, 0.0f);
                GlStateManager.rotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GlStateManager.translatef(-0.3125f, -0.3125f, 0.0f);
                GlStateManager.scalef(0.375f, 0.375f, 0.375f);
                MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack13, ModelTransformation.Type.FIXED);
                GlStateManager.popMatrix();
            }
        }
    }
}
