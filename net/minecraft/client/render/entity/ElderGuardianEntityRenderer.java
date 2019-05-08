package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ElderGuardianEntityRenderer extends GuardianEntityRenderer
{
    private static final Identifier SKIN;
    
    public ElderGuardianEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, 1.2f);
    }
    
    @Override
    protected void scale(final GuardianEntity entity, final float tickDelta) {
        GlStateManager.scalef(ElderGuardianEntity.b, ElderGuardianEntity.b, ElderGuardianEntity.b);
    }
    
    @Override
    protected Identifier getTexture(final GuardianEntity guardianEntity) {
        return ElderGuardianEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/guardian_elder.png");
    }
}
