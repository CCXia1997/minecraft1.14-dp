package net.minecraft.client.render.entity;

import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.util.math.Direction;
import net.minecraft.entity.LivingEntity;
import javax.annotation.Nullable;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.DonkeyEntity;
import net.minecraft.entity.passive.MuleEntity;
import net.minecraft.entity.mob.ZombieHorseEntity;
import net.minecraft.entity.mob.SkeletonHorseEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.projectile.FishHookEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.MobSpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TNTMinecartEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.PrimedTntEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.entity.projectile.ExplodingWitherSkullEntity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.FireworkEntity;
import net.minecraft.entity.thrown.ThrownExperienceBottleEntity;
import net.minecraft.entity.thrown.ThrownPotionEntity;
import net.minecraft.entity.thrown.ThrownEggEntity;
import net.minecraft.entity.EnderEyeEntity;
import net.minecraft.entity.thrown.ThrownEnderpearlEntity;
import net.minecraft.entity.thrown.SnowballEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.decoration.LeadKnotEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.mob.IllusionerEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.mob.RavagerEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.mob.VindicatorEntity;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.mob.GhastEntity;
import net.minecraft.entity.mob.GiantEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.HuskEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.mob.StrayEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.passive.SnowmanEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.mob.SilverfishEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.mob.CaveSpiderEntity;
import com.google.common.collect.Maps;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.Camera;
import net.minecraft.world.World;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.entity.Entity;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class EntityRenderDispatcher
{
    private final Map<Class<? extends Entity>, EntityRenderer<? extends Entity>> renderers;
    private final Map<String, PlayerEntityRenderer> modelRenderers;
    private final PlayerEntityRenderer playerRenderer;
    private TextRenderer textRenderer;
    private double renderPosX;
    private double renderPosY;
    private double renderPosZ;
    public final TextureManager textureManager;
    public World world;
    public Camera camera;
    public Entity targetedEntity;
    public float cameraYaw;
    public float cameraPitch;
    public GameOptions gameOptions;
    private boolean renderOutlines;
    private boolean renderShadows;
    private boolean renderHitboxes;
    
    private <T extends Entity> void register(final Class<T> class1, final EntityRenderer<? super T> entityRenderer) {
        this.renderers.put(class1, entityRenderer);
    }
    
    public EntityRenderDispatcher(final TextureManager textureManager, final ItemRenderer itemRenderer, final ReloadableResourceManager reloadableResourceManager) {
        this.renderers = Maps.newHashMap();
        this.modelRenderers = Maps.newHashMap();
        this.renderShadows = true;
        this.textureManager = textureManager;
        this.<CaveSpiderEntity>register(CaveSpiderEntity.class, new CaveSpiderEntityRenderer(this));
        this.<SpiderEntity>register(SpiderEntity.class, new SpiderEntityRenderer(this));
        this.<PigEntity>register(PigEntity.class, new PigEntityRenderer(this));
        this.<SheepEntity>register(SheepEntity.class, new SheepEntityRenderer(this));
        this.<CowEntity>register(CowEntity.class, new CowEntityRenderer(this));
        this.<MooshroomEntity>register(MooshroomEntity.class, new MooshroomEntityRenderer(this));
        this.<WolfEntity>register(WolfEntity.class, new WolfEntityRenderer(this));
        this.<ChickenEntity>register(ChickenEntity.class, new ChickenEntityRenderer(this));
        this.<OcelotEntity>register(OcelotEntity.class, new OcelotEntityRenderer(this));
        this.<RabbitEntity>register(RabbitEntity.class, new RabbitEntityRenderer(this));
        this.<ParrotEntity>register(ParrotEntity.class, new ParrotEntityRenderer(this));
        this.<TurtleEntity>register(TurtleEntity.class, new TurtleEntityRenderer(this));
        this.<SilverfishEntity>register(SilverfishEntity.class, new SilverfishEntityRenderer(this));
        this.<EndermiteEntity>register(EndermiteEntity.class, new EndermiteEntityRenderer(this));
        this.<CreeperEntity>register(CreeperEntity.class, new CreeperEntityRenderer(this));
        this.<EndermanEntity>register(EndermanEntity.class, new EndermanEntityRenderer(this));
        this.<SnowmanEntity>register(SnowmanEntity.class, new SnowmanEntityRenderer(this));
        this.<SkeletonEntity>register(SkeletonEntity.class, new SkeletonEntityRenderer(this));
        this.<WitherSkeletonEntity>register(WitherSkeletonEntity.class, new WitherSkeletonEntityRenderer(this));
        this.<StrayEntity>register(StrayEntity.class, new StrayEntityRenderer(this));
        this.<WitchEntity>register(WitchEntity.class, new WitchEntityRenderer(this));
        this.<BlazeEntity>register(BlazeEntity.class, new BlazeEntityRenderer(this));
        this.<ZombiePigmanEntity>register(ZombiePigmanEntity.class, new PigZombieEntityRenderer(this));
        this.<ZombieEntity>register(ZombieEntity.class, new ZombieEntityRenderer(this));
        this.<ZombieVillagerEntity>register(ZombieVillagerEntity.class, new ZombieVillagerEntityRenderer(this, reloadableResourceManager));
        this.<HuskEntity>register(HuskEntity.class, new HuskEntityRenderer(this));
        this.<DrownedEntity>register(DrownedEntity.class, new DrownedEntityRenderer(this));
        this.<SlimeEntity>register(SlimeEntity.class, new SlimeEntityRenderer(this));
        this.<MagmaCubeEntity>register(MagmaCubeEntity.class, new MagmaCubeEntityRenderer(this));
        this.<GiantEntity>register(GiantEntity.class, new GiantEntityRenderer(this, 6.0f));
        this.<GhastEntity>register(GhastEntity.class, new GhastEntityRenderer(this));
        this.<SquidEntity>register(SquidEntity.class, new SquidEntityRenderer(this));
        this.<VillagerEntity>register(VillagerEntity.class, new VillagerEntityRenderer(this, reloadableResourceManager));
        this.<WanderingTraderEntity>register(WanderingTraderEntity.class, new WanderingTraderEntityRenderer(this));
        this.<IronGolemEntity>register(IronGolemEntity.class, new IronGolemEntityRenderer(this));
        this.<BatEntity>register(BatEntity.class, new BatEntityRenderer(this));
        this.<GuardianEntity>register(GuardianEntity.class, new GuardianEntityRenderer(this));
        this.<ElderGuardianEntity>register(ElderGuardianEntity.class, new ElderGuardianEntityRenderer(this));
        this.<ShulkerEntity>register(ShulkerEntity.class, new ShulkerEntityRenderer(this));
        this.<PolarBearEntity>register(PolarBearEntity.class, new PolarBearEntityRenderer(this));
        this.<EvokerEntity>register(EvokerEntity.class, new EvokerIllagerEntityRenderer(this));
        this.<VindicatorEntity>register(VindicatorEntity.class, new VindicatorEntityRenderer(this));
        this.<PillagerEntity>register(PillagerEntity.class, new PillagerEntityRenderer(this));
        this.<RavagerEntity>register(RavagerEntity.class, new IllagerBeastEntityRenderer(this));
        this.<VexEntity>register(VexEntity.class, new VexEntityRenderer(this));
        this.<IllusionerEntity>register(IllusionerEntity.class, new IllusionerEntityRenderer(this));
        this.<PhantomEntity>register(PhantomEntity.class, new PhantomEntityRenderer(this));
        this.<PufferfishEntity>register(PufferfishEntity.class, new PufferfishEntityRenderer(this));
        this.<SalmonEntity>register(SalmonEntity.class, new SalmonEntityRenderer(this));
        this.<CodEntity>register(CodEntity.class, new CodEntityRenderer(this));
        this.<TropicalFishEntity>register(TropicalFishEntity.class, new TropicalFishEntityRenderer(this));
        this.<DolphinEntity>register(DolphinEntity.class, new DolphinEntityRenderer(this));
        this.<PandaEntity>register(PandaEntity.class, new PandaEntityRenderer(this));
        this.<CatEntity>register(CatEntity.class, new CatEntityRenderer(this));
        this.<FoxEntity>register(FoxEntity.class, new FoxEntityRenderer(this));
        this.<EnderDragonEntity>register(EnderDragonEntity.class, new EnderDragonEntityRenderer(this));
        this.<EnderCrystalEntity>register(EnderCrystalEntity.class, new EnderCrystalEntityRenderer(this));
        this.<WitherEntity>register(WitherEntity.class, new WitherEntityRenderer(this));
        this.<Entity>register(Entity.class, new DefaultEntityRenderer(this));
        this.<PaintingEntity>register(PaintingEntity.class, new PaintingEntityRenderer(this));
        this.<ItemFrameEntity>register(ItemFrameEntity.class, new ItemFrameEntityRenderer(this, itemRenderer));
        this.<LeadKnotEntity>register(LeadKnotEntity.class, new LeashKnotEntityRenderer(this));
        this.<ArrowEntity>register(ArrowEntity.class, new ArrowEntityRenderer(this));
        this.<SpectralArrowEntity>register(SpectralArrowEntity.class, new SpectralArrowEntityRenderer(this));
        this.<TridentEntity>register(TridentEntity.class, new TridentEntityRenderer(this));
        this.<SnowballEntity>register(SnowballEntity.class, new FlyingItemEntityRenderer<>(this, itemRenderer));
        this.<ThrownEnderpearlEntity>register(ThrownEnderpearlEntity.class, new FlyingItemEntityRenderer<>(this, itemRenderer));
        this.<EnderEyeEntity>register(EnderEyeEntity.class, new FlyingItemEntityRenderer<>(this, itemRenderer));
        this.<ThrownEggEntity>register(ThrownEggEntity.class, new FlyingItemEntityRenderer<>(this, itemRenderer));
        this.<ThrownPotionEntity>register(ThrownPotionEntity.class, new FlyingItemEntityRenderer<>(this, itemRenderer));
        this.<ThrownExperienceBottleEntity>register(ThrownExperienceBottleEntity.class, new FlyingItemEntityRenderer<>(this, itemRenderer));
        this.<FireworkEntity>register(FireworkEntity.class, new FireworkEntityRenderer(this, itemRenderer));
        this.<FireballEntity>register(FireballEntity.class, new FlyingItemEntityRenderer<>(this, itemRenderer, 3.0f));
        this.<SmallFireballEntity>register(SmallFireballEntity.class, new FlyingItemEntityRenderer<>(this, itemRenderer, 0.75f));
        this.<DragonFireballEntity>register(DragonFireballEntity.class, new DragonFireballEntityRenderer(this));
        this.<ExplodingWitherSkullEntity>register(ExplodingWitherSkullEntity.class, new ExplodingWitherSkullEntityRenderer(this));
        this.<ShulkerBulletEntity>register(ShulkerBulletEntity.class, new ShulkerBulletEntityRenderer(this));
        this.<ItemEntity>register(ItemEntity.class, new ItemEntityRenderer(this, itemRenderer));
        this.<ExperienceOrbEntity>register(ExperienceOrbEntity.class, new ExperienceOrbEntityRenderer(this));
        this.<PrimedTntEntity>register(PrimedTntEntity.class, new TNTPrimedEntityRenderer(this));
        this.<FallingBlockEntity>register(FallingBlockEntity.class, new FallingBlockEntityRenderer(this));
        this.<ArmorStandEntity>register(ArmorStandEntity.class, new ArmorStandEntityRenderer(this));
        this.<EvokerFangsEntity>register(EvokerFangsEntity.class, new EvokerFangsEntityRenderer(this));
        this.<TNTMinecartEntity>register(TNTMinecartEntity.class, new MinecartTNTEntityRenderer(this));
        this.<MobSpawnerMinecartEntity>register(MobSpawnerMinecartEntity.class, new MinecartEntityRenderer<>(this));
        this.<AbstractMinecartEntity>register(AbstractMinecartEntity.class, new MinecartEntityRenderer<>(this));
        this.<BoatEntity>register(BoatEntity.class, new BoatEntityRenderer(this));
        this.<FishHookEntity>register(FishHookEntity.class, new FishHookEntityRenderer(this));
        this.<AreaEffectCloudEntity>register(AreaEffectCloudEntity.class, new AreaEffectCloudEntityRenderer(this));
        this.<HorseEntity>register(HorseEntity.class, new HorseEntityRenderer(this));
        this.<SkeletonHorseEntity>register(SkeletonHorseEntity.class, new ZombieHorseEntityRenderer(this));
        this.<ZombieHorseEntity>register(ZombieHorseEntity.class, new ZombieHorseEntityRenderer(this));
        this.<MuleEntity>register(MuleEntity.class, new DonkeyEntityRenderer(this, 0.92f));
        this.<DonkeyEntity>register(DonkeyEntity.class, new DonkeyEntityRenderer(this, 0.87f));
        this.<LlamaEntity>register(LlamaEntity.class, new LlamaEntityRenderer(this));
        this.<TraderLlamaEntity>register(TraderLlamaEntity.class, new LlamaEntityRenderer(this));
        this.<LlamaSpitEntity>register(LlamaSpitEntity.class, new LlamaSpitEntityRenderer(this));
        this.<LightningEntity>register(LightningEntity.class, new LightningEntityRenderer(this));
        this.playerRenderer = new PlayerEntityRenderer(this);
        this.modelRenderers.put("default", this.playerRenderer);
        this.modelRenderers.put("slim", new PlayerEntityRenderer(this, true));
    }
    
    public void setRenderPosition(final double x, final double double3, final double double5) {
        this.renderPosX = x;
        this.renderPosY = double3;
        this.renderPosZ = double5;
    }
    
    public <T extends Entity, U extends EntityRenderer<T>> U getRenderer(final Class<? extends Entity> class1) {
        EntityRenderer<? extends Entity> entityRenderer2 = this.renderers.get(class1);
        if (entityRenderer2 == null && class1 != Entity.class) {
            entityRenderer2 = this.<Entity, EntityRenderer<? extends Entity>>getRenderer(class1.getSuperclass());
            this.renderers.put(class1, entityRenderer2);
        }
        return (U)entityRenderer2;
    }
    
    @Nullable
    public <T extends Entity, U extends EntityRenderer<T>> U getRenderer(final T entity) {
        if (!(entity instanceof AbstractClientPlayerEntity)) {
            return this.<Entity, U>getRenderer(entity.getClass());
        }
        final String string2 = ((AbstractClientPlayerEntity)entity).getModel();
        final PlayerEntityRenderer playerEntityRenderer3 = this.modelRenderers.get(string2);
        if (playerEntityRenderer3 != null) {
            return (U)playerEntityRenderer3;
        }
        return (U)this.playerRenderer;
    }
    
    public void configure(final World world, final TextRenderer textRenderer, final Camera camera, final Entity targetedEntity, final GameOptions gameOptions) {
        this.world = world;
        this.gameOptions = gameOptions;
        this.camera = camera;
        this.targetedEntity = targetedEntity;
        this.textRenderer = textRenderer;
        if (camera.getFocusedEntity() instanceof LivingEntity && ((LivingEntity)camera.getFocusedEntity()).isSleeping()) {
            final Direction direction6 = ((LivingEntity)camera.getFocusedEntity()).getSleepingDirection();
            if (direction6 != null) {
                this.cameraYaw = direction6.getOpposite().asRotation();
                this.cameraPitch = 0.0f;
            }
        }
        else {
            this.cameraYaw = camera.getYaw();
            this.cameraPitch = camera.getPitch();
        }
    }
    
    public void a(final float float1) {
        this.cameraYaw = float1;
    }
    
    public boolean shouldRenderShadows() {
        return this.renderShadows;
    }
    
    public void setRenderShadows(final boolean value) {
        this.renderShadows = value;
    }
    
    public void setRenderHitboxes(final boolean value) {
        this.renderHitboxes = value;
    }
    
    public boolean shouldRenderHitboxes() {
        return this.renderHitboxes;
    }
    
    public boolean hasSecondPass(final Entity entity) {
        return this.<Entity, EntityRenderer>getRenderer(entity).hasSecondPass();
    }
    
    public boolean shouldRender(final Entity entity, final VisibleRegion visibleRegion, final double double3, final double double5, final double double7) {
        final EntityRenderer<Entity> entityRenderer9 = this.<Entity, EntityRenderer<Entity>>getRenderer(entity);
        return entityRenderer9 != null && entityRenderer9.isVisible(entity, visibleRegion, double3, double5, double7);
    }
    
    public void render(final Entity entity, final float tickDelta, final boolean boolean3) {
        if (entity.age == 0) {
            entity.prevRenderX = entity.x;
            entity.prevRenderY = entity.y;
            entity.prevRenderZ = entity.z;
        }
        final double double4 = MathHelper.lerp(tickDelta, entity.prevRenderX, entity.x);
        final double double5 = MathHelper.lerp(tickDelta, entity.prevRenderY, entity.y);
        final double double6 = MathHelper.lerp(tickDelta, entity.prevRenderZ, entity.z);
        final float float10 = MathHelper.lerp(tickDelta, entity.prevYaw, entity.yaw);
        int integer11 = entity.getLightmapCoordinates();
        if (entity.isOnFire()) {
            integer11 = 15728880;
        }
        final int integer12 = integer11 % 65536;
        final int integer13 = integer11 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer12, (float)integer13);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.render(entity, double4 - this.renderPosX, double5 - this.renderPosY, double6 - this.renderPosZ, float10, tickDelta, boolean3);
    }
    
    public void render(final Entity entity, final double x, final double y, final double z, final float yaw, final float tickDelta, final boolean forceHideHitbox) {
        EntityRenderer<Entity> entityRenderer11 = null;
        try {
            entityRenderer11 = this.<Entity, EntityRenderer<Entity>>getRenderer(entity);
            if (entityRenderer11 != null && this.textureManager != null) {
                try {
                    entityRenderer11.a(this.renderOutlines);
                    entityRenderer11.render(entity, x, y, z, yaw, tickDelta);
                }
                catch (Throwable throwable12) {
                    throw new CrashException(CrashReport.create(throwable12, "Rendering entity in world"));
                }
                try {
                    if (!this.renderOutlines) {
                        entityRenderer11.postRender(entity, x, y, z, yaw, tickDelta);
                    }
                }
                catch (Throwable throwable12) {
                    throw new CrashException(CrashReport.create(throwable12, "Post-rendering entity in world"));
                }
                if (this.renderHitboxes && !entity.isInvisible() && !forceHideHitbox && !MinecraftClient.getInstance().hasReducedDebugInfo()) {
                    try {
                        this.renderHitbox(entity, x, y, z, yaw, tickDelta);
                    }
                    catch (Throwable throwable12) {
                        throw new CrashException(CrashReport.create(throwable12, "Rendering entity hitbox in world"));
                    }
                }
            }
        }
        catch (Throwable throwable12) {
            final CrashReport crashReport13 = CrashReport.create(throwable12, "Rendering entity in world");
            final CrashReportSection crashReportSection14 = crashReport13.addElement("Entity being rendered");
            entity.populateCrashReport(crashReportSection14);
            final CrashReportSection crashReportSection15 = crashReport13.addElement("Renderer details");
            crashReportSection15.add("Assigned renderer", entityRenderer11);
            crashReportSection15.add("Location", CrashReportSection.createPositionString(x, y, z));
            crashReportSection15.add("Rotation", yaw);
            crashReportSection15.add("Delta", tickDelta);
            throw new CrashException(crashReport13);
        }
    }
    
    public void renderSecondPass(final Entity entity, final float float2) {
        if (entity.age == 0) {
            entity.prevRenderX = entity.x;
            entity.prevRenderY = entity.y;
            entity.prevRenderZ = entity.z;
        }
        final double double3 = MathHelper.lerp(float2, entity.prevRenderX, entity.x);
        final double double4 = MathHelper.lerp(float2, entity.prevRenderY, entity.y);
        final double double5 = MathHelper.lerp(float2, entity.prevRenderZ, entity.z);
        final float float3 = MathHelper.lerp(float2, entity.prevYaw, entity.yaw);
        int integer10 = entity.getLightmapCoordinates();
        if (entity.isOnFire()) {
            integer10 = 15728880;
        }
        final int integer11 = integer10 % 65536;
        final int integer12 = integer10 / 65536;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer11, (float)integer12);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final EntityRenderer<Entity> entityRenderer13 = this.<Entity, EntityRenderer<Entity>>getRenderer(entity);
        if (entityRenderer13 != null && this.textureManager != null) {
            entityRenderer13.renderSecondPass(entity, double3 - this.renderPosX, double4 - this.renderPosY, double5 - this.renderPosZ, float3, float2);
        }
    }
    
    private void renderHitbox(final Entity entity, final double double2, final double double4, final double double6, final float float8, final float float9) {
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        final float float10 = entity.getWidth() / 2.0f;
        final BoundingBox boundingBox11 = entity.getBoundingBox();
        WorldRenderer.drawBoxOutline(boundingBox11.minX - entity.x + double2, boundingBox11.minY - entity.y + double4, boundingBox11.minZ - entity.z + double6, boundingBox11.maxX - entity.x + double2, boundingBox11.maxY - entity.y + double4, boundingBox11.maxZ - entity.z + double6, 1.0f, 1.0f, 1.0f, 1.0f);
        if (entity instanceof EnderDragonEntity) {
            for (final EnderDragonPart enderDragonPart15 : ((EnderDragonEntity)entity).dT()) {
                final double double7 = (enderDragonPart15.x - enderDragonPart15.prevX) * float9;
                final double double8 = (enderDragonPart15.y - enderDragonPart15.prevY) * float9;
                final double double9 = (enderDragonPart15.z - enderDragonPart15.prevZ) * float9;
                final BoundingBox boundingBox12 = enderDragonPart15.getBoundingBox();
                WorldRenderer.drawBoxOutline(boundingBox12.minX - this.renderPosX + double7, boundingBox12.minY - this.renderPosY + double8, boundingBox12.minZ - this.renderPosZ + double9, boundingBox12.maxX - this.renderPosX + double7, boundingBox12.maxY - this.renderPosY + double8, boundingBox12.maxZ - this.renderPosZ + double9, 0.25f, 1.0f, 0.0f, 1.0f);
            }
        }
        if (entity instanceof LivingEntity) {
            final float float11 = 0.01f;
            WorldRenderer.drawBoxOutline(double2 - float10, double4 + entity.getStandingEyeHeight() - 0.009999999776482582, double6 - float10, double2 + float10, double4 + entity.getStandingEyeHeight() + 0.009999999776482582, double6 + float10, 1.0f, 0.0f, 0.0f, 1.0f);
        }
        final Tessellator tessellator12 = Tessellator.getInstance();
        final BufferBuilder bufferBuilder13 = tessellator12.getBufferBuilder();
        final Vec3d vec3d14 = entity.getRotationVec(float9);
        bufferBuilder13.begin(3, VertexFormats.POSITION_COLOR);
        bufferBuilder13.vertex(double2, double4 + entity.getStandingEyeHeight(), double6).color(0, 0, 255, 255).next();
        bufferBuilder13.vertex(double2 + vec3d14.x * 2.0, double4 + entity.getStandingEyeHeight() + vec3d14.y * 2.0, double6 + vec3d14.z * 2.0).color(0, 0, 255, 255).next();
        tessellator12.draw();
        GlStateManager.enableTexture();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }
    
    public void setWorld(@Nullable final World world) {
        this.world = world;
        if (world == null) {
            this.camera = null;
        }
    }
    
    public double squaredDistanceToCamera(final double x, final double y, final double z) {
        return this.camera.getPos().squaredDistanceTo(x, y, z);
    }
    
    public TextRenderer getTextRenderer() {
        return this.textRenderer;
    }
    
    public void setRenderOutlines(final boolean renderOutlines) {
        this.renderOutlines = renderOutlines;
    }
}
