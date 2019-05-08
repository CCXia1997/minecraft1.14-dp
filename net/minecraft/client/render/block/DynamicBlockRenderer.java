package net.minecraft.client.render.block;

import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.item.ItemDynamicRenderer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class DynamicBlockRenderer
{
    public void render(final Block block, final float float2) {
        GlStateManager.color4f(float2, float2, float2, 1.0f);
        GlStateManager.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        ItemDynamicRenderer.INSTANCE.render(new ItemStack(block));
    }
}
