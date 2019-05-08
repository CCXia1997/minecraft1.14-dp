package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.client.texture.Texture;
import net.minecraft.client.texture.LayeredTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HorseArmorFeatureRenderer;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.HorseEntity;

@Environment(EnvType.CLIENT)
public final class HorseEntityRenderer extends HorseBaseEntityRenderer<HorseEntity, HorseEntityModel<HorseEntity>>
{
    private static final Map<String, Identifier> SKINS;
    
    public HorseEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new HorseEntityModel(0.0f), 1.1f);
        this.addFeature(new HorseArmorFeatureRenderer(this));
    }
    
    protected Identifier getTexture(final HorseEntity horseEntity) {
        final String string2 = horseEntity.getTextureLocation();
        Identifier identifier3 = HorseEntityRenderer.SKINS.get(string2);
        if (identifier3 == null) {
            identifier3 = new Identifier(string2);
            MinecraftClient.getInstance().getTextureManager().registerTexture(identifier3, new LayeredTexture(horseEntity.getTextureLayers()));
            HorseEntityRenderer.SKINS.put(string2, identifier3);
        }
        return identifier3;
    }
    
    static {
        SKINS = Maps.newHashMap();
    }
}
