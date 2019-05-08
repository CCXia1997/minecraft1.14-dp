package net.minecraft.client.render.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.DonkeyEntityModel;
import net.minecraft.entity.passive.AbstractDonkeyEntity;

@Environment(EnvType.CLIENT)
public class DonkeyEntityRenderer<T extends AbstractDonkeyEntity> extends HorseBaseEntityRenderer<T, DonkeyEntityModel<T>>
{
    private static final Map<Class<?>, Identifier> TEXTURES;
    
    public DonkeyEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher, final float float2) {
        super(entityRenderDispatcher, new DonkeyEntityModel(0.0f), float2);
    }
    
    protected Identifier getTexture(final T abstractDonkeyEntity) {
        return DonkeyEntityRenderer.TEXTURES.get(abstractDonkeyEntity.getClass());
    }
    
    static {
        TEXTURES = Maps.newHashMap(ImmutableMap.<Class<DonkeyEntity>, Identifier>of(DonkeyEntity.class, new Identifier("textures/entity/horse/donkey.png"), (Class<DonkeyEntity>)MuleEntity.class, new Identifier("textures/entity/horse/mule.png")));
    }
}
