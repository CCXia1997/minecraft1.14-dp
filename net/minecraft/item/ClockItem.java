package net.minecraft.item;

import net.minecraft.util.math.MathHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public class ClockItem extends Item
{
    public ClockItem(final Settings settings) {
        super(settings);
        this.addProperty(new Identifier("time"), new ItemPropertyGetter() {
            @Environment(EnvType.CLIENT)
            private double lastClockTime;
            @Environment(EnvType.CLIENT)
            private double clockTimeChangeSpeed;
            @Environment(EnvType.CLIENT)
            private long lastWorldTime;
            
            @Environment(EnvType.CLIENT)
            @Override
            public float call(final ItemStack stack, @Nullable World world, @Nullable final LivingEntity user) {
                final boolean boolean4 = user != null;
                final Entity entity5 = boolean4 ? user : stack.getHoldingItemFrame();
                if (world == null && entity5 != null) {
                    world = entity5.world;
                }
                if (world == null) {
                    return 0.0f;
                }
                double double6;
                if (world.dimension.hasVisibleSky()) {
                    double6 = world.getSkyAngle(1.0f);
                }
                else {
                    double6 = Math.random();
                }
                double6 = this.getClockTime(world, double6);
                return (float)double6;
            }
            
            @Environment(EnvType.CLIENT)
            private double getClockTime(final World world, final double skyAngle) {
                if (world.getTime() != this.lastWorldTime) {
                    this.lastWorldTime = world.getTime();
                    double double4 = skyAngle - this.lastClockTime;
                    double4 = MathHelper.floorMod(double4 + 0.5, 1.0) - 0.5;
                    this.clockTimeChangeSpeed += double4 * 0.1;
                    this.clockTimeChangeSpeed *= 0.9;
                    this.lastClockTime = MathHelper.floorMod(this.lastClockTime + this.clockTimeChangeSpeed, 1.0);
                }
                return this.lastClockTime;
            }
        });
    }
}
