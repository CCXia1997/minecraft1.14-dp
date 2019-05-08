package net.minecraft.client.render.entity.model;

import net.minecraft.util.math.MathHelper;
import net.minecraft.client.model.Cuboid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;

@Environment(EnvType.CLIENT)
public class BookModel extends Model
{
    private final Cuboid leftCover;
    private final Cuboid rightCover;
    private final Cuboid leftBlock;
    private final Cuboid rightBlock;
    private final Cuboid leftPage;
    private final Cuboid rightPage;
    private final Cuboid spine;
    
    public BookModel() {
        this.leftCover = new Cuboid(this).setTextureOffset(0, 0).addBox(-6.0f, -5.0f, 0.0f, 6, 10, 0);
        this.rightCover = new Cuboid(this).setTextureOffset(16, 0).addBox(0.0f, -5.0f, 0.0f, 6, 10, 0);
        this.spine = new Cuboid(this).setTextureOffset(12, 0).addBox(-1.0f, -5.0f, 0.0f, 2, 10, 0);
        this.leftBlock = new Cuboid(this).setTextureOffset(0, 10).addBox(0.0f, -4.0f, -0.99f, 5, 8, 1);
        this.rightBlock = new Cuboid(this).setTextureOffset(12, 10).addBox(0.0f, -4.0f, -0.01f, 5, 8, 1);
        this.leftPage = new Cuboid(this).setTextureOffset(24, 10).addBox(0.0f, -4.0f, 0.0f, 5, 8, 0);
        this.rightPage = new Cuboid(this).setTextureOffset(24, 10).addBox(0.0f, -4.0f, 0.0f, 5, 8, 0);
        this.leftCover.setRotationPoint(0.0f, 0.0f, -1.0f);
        this.rightCover.setRotationPoint(0.0f, 0.0f, 1.0f);
        this.spine.yaw = 1.5707964f;
    }
    
    public void render(final float ticks, final float leftPageAngle, final float rightPageAngle, final float pageTurningSpeed, final float float5, final float float6) {
        this.setPageAngles(ticks, leftPageAngle, rightPageAngle, pageTurningSpeed, float5, float6);
        this.leftCover.render(float6);
        this.rightCover.render(float6);
        this.spine.render(float6);
        this.leftBlock.render(float6);
        this.rightBlock.render(float6);
        this.leftPage.render(float6);
        this.rightPage.render(float6);
    }
    
    private void setPageAngles(final float ticks, final float leftPageAngle, final float rightPageAngle, final float pageTurningSpeed, final float float5, final float float6) {
        final float float7 = (MathHelper.sin(ticks * 0.02f) * 0.1f + 1.25f) * pageTurningSpeed;
        this.leftCover.yaw = 3.1415927f + float7;
        this.rightCover.yaw = -float7;
        this.leftBlock.yaw = float7;
        this.rightBlock.yaw = -float7;
        this.leftPage.yaw = float7 - float7 * 2.0f * leftPageAngle;
        this.rightPage.yaw = float7 - float7 * 2.0f * rightPageAngle;
        this.leftBlock.rotationPointX = MathHelper.sin(float7);
        this.rightBlock.rotationPointX = MathHelper.sin(float7);
        this.leftPage.rotationPointX = MathHelper.sin(float7);
        this.rightPage.rotationPointX = MathHelper.sin(float7);
    }
}
