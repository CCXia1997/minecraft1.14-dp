package net.minecraft.client.render.entity;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.MooshroomMushroomFeatureRenderer;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.entity.passive.MooshroomEntity;

@Environment(EnvType.CLIENT)
public class MooshroomEntityRenderer extends MobEntityRenderer<MooshroomEntity, CowEntityModel<MooshroomEntity>>
{
    private static final Map<MooshroomEntity.Type, Identifier> SKIN;
    
    public MooshroomEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new CowEntityModel(), 0.7f);
        this.addFeature((FeatureRenderer<MooshroomEntity, CowEntityModel<MooshroomEntity>>)new MooshroomMushroomFeatureRenderer(this));
    }
    
    protected Identifier getTexture(final MooshroomEntity mooshroomEntity) {
        return MooshroomEntityRenderer.SKIN.get(mooshroomEntity.getMooshroomType());
    }
    
    static {
        SKIN = SystemUtil.<Map<MooshroomEntity.Type, Identifier>>consume(Maps.newHashMap(), hashMap -> {
            hashMap.put(MooshroomEntity.Type.b, new Identifier("textures/entity/cow/brown_mooshroom.png"));
            hashMap.put(MooshroomEntity.Type.a, new Identifier("textures/entity/cow/red_mooshroom.png"));
        });
    }
}
