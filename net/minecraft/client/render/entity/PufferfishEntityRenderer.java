package net.minecraft.client.render.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.util.math.MathHelper;
import javax.annotation.Nullable;
import net.minecraft.client.render.entity.model.LargePufferfishEntityModel;
import net.minecraft.client.render.entity.model.MediumPufferfishEntityModel;
import net.minecraft.client.render.entity.model.SmallPufferfishEntityModel;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.passive.PufferfishEntity;

@Environment(EnvType.CLIENT)
public class PufferfishEntityRenderer extends MobEntityRenderer<PufferfishEntity, EntityModel<PufferfishEntity>>
{
    private static final Identifier SKIN;
    private int j;
    private final SmallPufferfishEntityModel<PufferfishEntity> smallModel;
    private final MediumPufferfishEntityModel<PufferfishEntity> mediumModel;
    private final LargePufferfishEntityModel<PufferfishEntity> largeModel;
    
    public PufferfishEntityRenderer(final EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new LargePufferfishEntityModel(), 0.2f);
        this.smallModel = new SmallPufferfishEntityModel<PufferfishEntity>();
        this.mediumModel = new MediumPufferfishEntityModel<PufferfishEntity>();
        this.largeModel = new LargePufferfishEntityModel<PufferfishEntity>();
        this.j = 3;
    }
    
    @Nullable
    protected Identifier getTexture(final PufferfishEntity pufferfishEntity) {
        return PufferfishEntityRenderer.SKIN;
    }
    
    @Override
    public void render(final PufferfishEntity entity, final double x, final double y, final double z, final float yaw, final float tickDelta) {
        final int integer10 = entity.getPuffState();
        if (integer10 != this.j) {
            if (integer10 == 0) {
                this.model = (M)this.smallModel;
            }
            else if (integer10 == 1) {
                this.model = (M)this.mediumModel;
            }
            else {
                this.model = (M)this.largeModel;
            }
        }
        this.j = integer10;
        this.c = 0.1f + 0.1f * integer10;
        super.render(entity, x, y, z, yaw, tickDelta);
    }
    
    @Override
    protected void setupTransforms(final PufferfishEntity entity, final float pitch, final float yaw, final float delta) {
        GlStateManager.translatef(0.0f, MathHelper.cos(pitch * 0.05f) * 0.08f, 0.0f);
        super.setupTransforms(entity, pitch, yaw, delta);
    }
    
    static {
        SKIN = new Identifier("textures/entity/fish/pufferfish.png");
    }
}
