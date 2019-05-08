package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.HorseEntityModel;
import net.minecraft.entity.passive.HorseBaseEntity;

@Environment(EnvType.CLIENT)
public class ZombieHorseEntityRenderer extends HorseBaseEntityRenderer<HorseBaseEntity, HorseEntityModel<HorseBaseEntity>>
{
    private static final Map<Class<?>, Identifier> TEXTURES;
    
    public ZombieHorseEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new HorseEntityModel(0.0f), 1.0f);
    }
    
    protected Identifier getTexture(final HorseBaseEntity horseBaseEntity) {
        return ZombieHorseEntityRenderer.TEXTURES.get(horseBaseEntity.getClass());
    }
    
    static {
        TEXTURES = Maps.newHashMap(ImmutableMap.<Class<ZombieHorseEntity>, Identifier>of(ZombieHorseEntity.class, new Identifier("textures/entity/horse/horse_zombie.png"), (Class<ZombieHorseEntity>)SkeletonHorseEntity.class, new Identifier("textures/entity/horse/horse_skeleton.png")));
    }
}
