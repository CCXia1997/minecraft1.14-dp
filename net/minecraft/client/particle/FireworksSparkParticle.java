package net.minecraft.client.particle;

import net.minecraft.particle.DefaultParticleType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.DyeColor;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.item.FireworkItem;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import net.minecraft.nbt.ListTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class FireworksSparkParticle
{
    @Environment(EnvType.CLIENT)
    public static class FireworkParticle extends NoRenderParticle
    {
        private int age;
        private final ParticleManager particleManager;
        private ListTag explosions;
        private boolean flicker;
        
        public FireworkParticle(final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ, final ParticleManager particleManager, @Nullable final CompoundTag tag) {
            super(world, x, y, z);
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.velocityZ = velocityZ;
            this.particleManager = particleManager;
            this.maxAge = 8;
            if (tag != null) {
                this.explosions = tag.getList("Explosions", 10);
                if (this.explosions.isEmpty()) {
                    this.explosions = null;
                }
                else {
                    this.maxAge = this.explosions.size() * 2 - 1;
                    for (int integer16 = 0; integer16 < this.explosions.size(); ++integer16) {
                        final CompoundTag compoundTag17 = this.explosions.getCompoundTag(integer16);
                        if (compoundTag17.getBoolean("Flicker")) {
                            this.flicker = true;
                            this.maxAge += 15;
                            break;
                        }
                    }
                }
            }
        }
        
        @Override
        public void update() {
            if (this.age == 0 && this.explosions != null) {
                final boolean boolean1 = this.isFar();
                boolean boolean2 = false;
                if (this.explosions.size() >= 3) {
                    boolean2 = true;
                }
                else {
                    for (int integer3 = 0; integer3 < this.explosions.size(); ++integer3) {
                        final CompoundTag compoundTag4 = this.explosions.getCompoundTag(integer3);
                        if (FireworkItem.Type.fromId(compoundTag4.getByte("Type")) == FireworkItem.Type.b) {
                            boolean2 = true;
                            break;
                        }
                    }
                }
                SoundEvent soundEvent3;
                if (boolean2) {
                    soundEvent3 = (boolean1 ? SoundEvents.dk : SoundEvents.dj);
                }
                else {
                    soundEvent3 = (boolean1 ? SoundEvents.di : SoundEvents.dh);
                }
                this.world.playSound(this.x, this.y, this.z, soundEvent3, SoundCategory.i, 20.0f, 0.95f + this.random.nextFloat() * 0.1f, true);
            }
            if (this.age % 2 == 0 && this.explosions != null && this.age / 2 < this.explosions.size()) {
                final int integer4 = this.age / 2;
                final CompoundTag compoundTag5 = this.explosions.getCompoundTag(integer4);
                final FireworkItem.Type type3 = FireworkItem.Type.fromId(compoundTag5.getByte("Type"));
                final boolean boolean3 = compoundTag5.getBoolean("Trail");
                final boolean boolean4 = compoundTag5.getBoolean("Flicker");
                int[] arr6 = compoundTag5.getIntArray("Colors");
                final int[] arr7 = compoundTag5.getIntArray("FadeColors");
                if (arr6.length == 0) {
                    arr6 = new int[] { DyeColor.BLACK.getFireworkColor() };
                }
                switch (type3) {
                    default: {
                        this.explodeBall(0.25, 2, arr6, arr7, boolean3, boolean4);
                        break;
                    }
                    case b: {
                        this.explodeBall(0.5, 4, arr6, arr7, boolean3, boolean4);
                        break;
                    }
                    case c: {
                        this.explodeStar(0.5, new double[][] { { 0.0, 1.0 }, { 0.3455, 0.309 }, { 0.9511, 0.309 }, { 0.3795918367346939, -0.12653061224489795 }, { 0.6122448979591837, -0.8040816326530612 }, { 0.0, -0.35918367346938773 } }, arr6, arr7, boolean3, boolean4, false);
                        break;
                    }
                    case d: {
                        this.explodeStar(0.5, new double[][] { { 0.0, 0.2 }, { 0.2, 0.2 }, { 0.2, 0.6 }, { 0.6, 0.6 }, { 0.6, 0.2 }, { 0.2, 0.2 }, { 0.2, 0.0 }, { 0.4, 0.0 }, { 0.4, -0.6 }, { 0.2, -0.6 }, { 0.2, -0.4 }, { 0.0, -0.4 } }, arr6, arr7, boolean3, boolean4, true);
                        break;
                    }
                    case e: {
                        this.explodeBurst(arr6, arr7, boolean3, boolean4);
                        break;
                    }
                }
                final int integer5 = arr6[0];
                final float float9 = ((integer5 & 0xFF0000) >> 16) / 255.0f;
                final float float10 = ((integer5 & 0xFF00) >> 8) / 255.0f;
                final float float11 = ((integer5 & 0xFF) >> 0) / 255.0f;
                final Particle particle12 = this.particleManager.addParticle(ParticleTypes.B, this.x, this.y, this.z, 0.0, 0.0, 0.0);
                particle12.setColor(float9, float10, float11);
            }
            ++this.age;
            if (this.age > this.maxAge) {
                if (this.flicker) {
                    final boolean boolean1 = this.isFar();
                    final SoundEvent soundEvent4 = boolean1 ? SoundEvents.do_ : SoundEvents.dn;
                    this.world.playSound(this.x, this.y, this.z, soundEvent4, SoundCategory.i, 20.0f, 0.9f + this.random.nextFloat() * 0.15f, true);
                }
                this.markDead();
            }
        }
        
        private boolean isFar() {
            final MinecraftClient minecraftClient1 = MinecraftClient.getInstance();
            return minecraftClient1.gameRenderer.getCamera().getPos().squaredDistanceTo(this.x, this.y, this.z) >= 256.0;
        }
        
        private void addExplosionParticle(final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ, final int[] colors, final int[] fadeColors, final boolean trail, final boolean flicker) {
            final ExplosionParticle explosionParticle17 = (ExplosionParticle)this.particleManager.addParticle(ParticleTypes.y, x, y, z, velocityX, velocityY, velocityZ);
            explosionParticle17.setTrail(trail);
            explosionParticle17.setFlicker(flicker);
            explosionParticle17.setColorAlpha(0.99f);
            final int integer18 = this.random.nextInt(colors.length);
            explosionParticle17.setColor(colors[integer18]);
            if (fadeColors.length > 0) {
                explosionParticle17.setTargetColor(fadeColors[this.random.nextInt(fadeColors.length)]);
            }
        }
        
        private void explodeBall(final double size, final int amount, final int[] colors, final int[] fadeColors, final boolean trail, final boolean flicker) {
            final double double8 = this.x;
            final double double9 = this.y;
            final double double10 = this.z;
            for (int integer14 = -amount; integer14 <= amount; ++integer14) {
                for (int integer15 = -amount; integer15 <= amount; ++integer15) {
                    for (int integer16 = -amount; integer16 <= amount; ++integer16) {
                        final double double11 = integer15 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        final double double12 = integer14 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        final double double13 = integer16 + (this.random.nextDouble() - this.random.nextDouble()) * 0.5;
                        final double double14 = MathHelper.sqrt(double11 * double11 + double12 * double12 + double13 * double13) / size + this.random.nextGaussian() * 0.05;
                        this.addExplosionParticle(double8, double9, double10, double11 / double14, double12 / double14, double13 / double14, colors, fadeColors, trail, flicker);
                        if (integer14 != -amount && integer14 != amount && integer15 != -amount && integer15 != amount) {
                            integer16 += amount * 2 - 1;
                        }
                    }
                }
            }
        }
        
        private void explodeStar(final double size, final double[][] pattern, final int[] colors, final int[] fadeColors, final boolean trail, final boolean flicker, final boolean keepShape) {
            final double double9 = pattern[0][0];
            final double double10 = pattern[0][1];
            this.addExplosionParticle(this.x, this.y, this.z, double9 * size, double10 * size, 0.0, colors, fadeColors, trail, flicker);
            final float float13 = this.random.nextFloat() * 3.1415927f;
            final double double11 = keepShape ? 0.034 : 0.34;
            for (int integer16 = 0; integer16 < 3; ++integer16) {
                final double double12 = float13 + integer16 * 3.1415927f * double11;
                double double13 = double9;
                double double14 = double10;
                for (int integer17 = 1; integer17 < pattern.length; ++integer17) {
                    final double double15 = pattern[integer17][0];
                    final double double16 = pattern[integer17][1];
                    for (double double17 = 0.25; double17 <= 1.0; double17 += 0.25) {
                        double double18 = MathHelper.lerp(double17, double13, double15) * size;
                        final double double19 = MathHelper.lerp(double17, double14, double16) * size;
                        final double double20 = double18 * Math.sin(double12);
                        double18 *= Math.cos(double12);
                        for (double double21 = -1.0; double21 <= 1.0; double21 += 2.0) {
                            this.addExplosionParticle(this.x, this.y, this.z, double18 * double21, double19, double20 * double21, colors, fadeColors, trail, flicker);
                        }
                    }
                    double13 = double15;
                    double14 = double16;
                }
            }
        }
        
        private void explodeBurst(final int[] colors, final int[] fadeColors, final boolean trail, final boolean flocker) {
            final double double5 = this.random.nextGaussian() * 0.05;
            final double double6 = this.random.nextGaussian() * 0.05;
            for (int integer9 = 0; integer9 < 70; ++integer9) {
                final double double7 = this.velocityX * 0.5 + this.random.nextGaussian() * 0.15 + double5;
                final double double8 = this.velocityZ * 0.5 + this.random.nextGaussian() * 0.15 + double6;
                final double double9 = this.velocityY * 0.5 + this.random.nextDouble() * 0.5;
                this.addExplosionParticle(this.x, this.y, this.z, double7, double9, double8, colors, fadeColors, trail, flocker);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class ExplosionParticle extends AnimatedParticle
    {
        private boolean trail;
        private boolean flicker;
        private final ParticleManager particleManager;
        private float I;
        private float J;
        private float K;
        private boolean L;
        
        private ExplosionParticle(final World world, final double double2, final double double4, final double double6, final double velocityX, final double velocityY, final double velocityZ, final ParticleManager particleManager14, final SpriteProvider spriteProvider15) {
            super(world, double2, double4, double6, spriteProvider15, -0.004f);
            this.velocityX = velocityX;
            this.velocityY = velocityY;
            this.velocityZ = velocityZ;
            this.particleManager = particleManager14;
            this.scale *= 0.75f;
            this.maxAge = 48 + this.random.nextInt(12);
            this.setSpriteForAge(spriteProvider15);
        }
        
        public void setTrail(final boolean trail) {
            this.trail = trail;
        }
        
        public void setFlicker(final boolean flicker) {
            this.flicker = flicker;
        }
        
        @Override
        public void buildGeometry(final BufferBuilder bufferBuilder, final Camera camera, final float tickDelta, final float float4, final float float5, final float float6, final float float7, final float float8) {
            if (!this.flicker || this.age < this.maxAge / 3 || (this.age + this.maxAge) / 3 % 2 == 0) {
                super.buildGeometry(bufferBuilder, camera, tickDelta, float4, float5, float6, float7, float8);
            }
        }
        
        @Override
        public void update() {
            super.update();
            if (this.trail && this.age < this.maxAge / 2 && (this.age + this.maxAge) % 2 == 0) {
                final ExplosionParticle explosionParticle1 = new ExplosionParticle(this.world, this.x, this.y, this.z, 0.0, 0.0, 0.0, this.particleManager, this.spriteProvider);
                explosionParticle1.setColorAlpha(0.99f);
                explosionParticle1.setColor(this.colorRed, this.colorGreen, this.colorBlue);
                explosionParticle1.age = explosionParticle1.maxAge / 2;
                if (this.L) {
                    explosionParticle1.L = true;
                    explosionParticle1.I = this.I;
                    explosionParticle1.J = this.J;
                    explosionParticle1.K = this.K;
                }
                explosionParticle1.flicker = this.flicker;
                this.particleManager.addParticle(explosionParticle1);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class FlashParticle extends SpriteBillboardParticle
    {
        private FlashParticle(final World x, final double y, final double double4, final double double6) {
            super(x, y, double4, double6);
            this.maxAge = 4;
        }
        
        @Override
        public ParticleTextureSheet getTextureSheet() {
            return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
        }
        
        @Override
        public void buildGeometry(final BufferBuilder bufferBuilder, final Camera camera, final float tickDelta, final float float4, final float float5, final float float6, final float float7, final float float8) {
            this.setColorAlpha(0.6f - (this.age + tickDelta - 1.0f) * 0.25f * 0.5f);
            super.buildGeometry(bufferBuilder, camera, tickDelta, float4, float5, float6, float7, float8);
        }
        
        @Override
        public float getSize(final float tickDelta) {
            return 7.1f * MathHelper.sin((this.age + tickDelta - 1.0f) * 0.25f * 3.1415927f);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class FlashFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider spriteProvider;
        
        public FlashFactory(final SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final FlashParticle flashParticle15 = new FlashParticle(world, x, y, z);
            flashParticle15.setSprite(this.spriteProvider);
            return flashParticle15;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static class ExplosionFactory implements ParticleFactory<DefaultParticleType>
    {
        private final SpriteProvider spriteProvider;
        
        public ExplosionFactory(final SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }
        
        @Override
        public Particle createParticle(final DefaultParticleType parameters, final World world, final double x, final double y, final double z, final double velocityX, final double velocityY, final double velocityZ) {
            final ExplosionParticle explosionParticle15 = new ExplosionParticle(world, x, y, z, velocityX, velocityY, velocityZ, MinecraftClient.getInstance().particleManager, this.spriteProvider);
            explosionParticle15.setColorAlpha(0.99f);
            return explosionParticle15;
        }
    }
}
