package net.minecraft.client.render.model;

import javax.annotation.Nullable;
import net.minecraft.client.texture.Sprite;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface UnbakedModel
{
    Collection<Identifier> getModelDependencies();
    
    Collection<Identifier> getTextureDependencies(final Function<Identifier, UnbakedModel> arg1, final Set<String> arg2);
    
    @Nullable
    BakedModel bake(final ModelLoader arg1, final Function<Identifier, Sprite> arg2, final ModelBakeSettings arg3);
}
