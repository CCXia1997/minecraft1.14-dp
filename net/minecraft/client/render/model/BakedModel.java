package net.minecraft.client.render.model;

import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import java.util.List;
import java.util.Random;
import net.minecraft.util.math.Direction;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface BakedModel
{
    List<BakedQuad> getQuads(@Nullable final BlockState arg1, @Nullable final Direction arg2, final Random arg3);
    
    boolean useAmbientOcclusion();
    
    boolean hasDepthInGui();
    
    boolean isBuiltin();
    
    Sprite getSprite();
    
    ModelTransformation getTransformation();
    
    ModelItemPropertyOverrideList getItemPropertyOverrides();
}
