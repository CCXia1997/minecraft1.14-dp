package net.minecraft.item;

import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.world.IWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class CompassItem extends Item
{
    public CompassItem(final Settings settings) {
        super(settings);
        this.addProperty(new Identifier("angle"), new ItemPropertyGetter() {
            @Environment(EnvType.CLIENT)
            private double b;
            @Environment(EnvType.CLIENT)
            private double c;
            @Environment(EnvType.CLIENT)
            private long d;
            
            @Environment(EnvType.CLIENT)
            @Override
            public float call(final ItemStack stack, @Nullable World world, @Nullable final LivingEntity user) {
                if (user == null && !stack.isHeldInItemFrame()) {
                    return 0.0f;
                }
                final boolean boolean4 = user != null;
                final Entity entity5 = boolean4 ? user : stack.getHoldingItemFrame();
                if (world == null) {
                    world = entity5.world;
                }
                double double10;
                if (world.dimension.hasVisibleSky()) {
                    double double8 = boolean4 ? entity5.yaw : this.a((ItemFrameEntity)entity5);
                    double8 = MathHelper.floorMod(double8 / 360.0, 1.0);
                    final double double9 = this.a(world, entity5) / 6.2831854820251465;
                    double10 = 0.5 - (double8 - 0.25 - double9);
                }
                else {
                    double10 = Math.random();
                }
                if (boolean4) {
                    double10 = this.a(world, double10);
                }
                return MathHelper.floorMod((float)double10, 1.0f);
            }
            
            @Environment(EnvType.CLIENT)
            private double a(final World world, final double double2) {
                if (world.getTime() != this.d) {
                    this.d = world.getTime();
                    double double3 = double2 - this.b;
                    double3 = MathHelper.floorMod(double3 + 0.5, 1.0) - 0.5;
                    this.c += double3 * 0.1;
                    this.c *= 0.8;
                    this.b = MathHelper.floorMod(this.b + this.c, 1.0);
                }
                return this.b;
            }
            
            @Environment(EnvType.CLIENT)
            private double a(final ItemFrameEntity itemFrameEntity) {
                return MathHelper.wrapDegrees(180 + itemFrameEntity.facing.getHorizontal() * 90);
            }
            
            @Environment(EnvType.CLIENT)
            private double a(final IWorld iWorld, final Entity entity) {
                final BlockPos blockPos3 = iWorld.getSpawnPos();
                return Math.atan2(blockPos3.getZ() - entity.z, blockPos3.getX() - entity.x);
            }
        });
    }
}
