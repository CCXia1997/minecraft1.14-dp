package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.passive.TurtleEntity;

@Environment(EnvType.CLIENT)
public class TurtleEntityRenderer extends MobEntityRenderer<TurtleEntity, EntityModelTurtle<TurtleEntity>>
{
    private static final Identifier SKIN;
    
    public TurtleEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new EntityModelTurtle(0.0f), 0.7f);
    }
    
    @Override
    public void render(final TurtleEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        if (entity.isChild()) {
            this.c *= 0.5f;
        }
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Nullable
    protected Identifier getTexture(final TurtleEntity turtleEntity) {
        return TurtleEntityRenderer.SKIN;
    }
    
    static {
        SKIN = new Identifier("textures/entity/turtle/big_sea_turtle.png");
    }
}
