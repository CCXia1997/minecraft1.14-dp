package net.minecraft.client.render.block.entity;

import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.math.BlockPos;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.render.GuiLighting;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.entity.LecternBlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import com.google.common.collect.Maps;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.render.Camera;
import net.minecraft.world.World;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.block.entity.BlockEntity;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class BlockEntityRenderDispatcher
{
    private final Map<Class<? extends BlockEntity>, BlockEntityRenderer<? extends BlockEntity>> renderers;
    public static final BlockEntityRenderDispatcher INSTANCE;
    private TextRenderer fontRenderer;
    public static double renderOffsetX;
    public static double renderOffsetY;
    public static double renderOffsetZ;
    public TextureManager textureManager;
    public World world;
    public Camera cameraEntity;
    public HitResult hitResult;
    
    private BlockEntityRenderDispatcher() {
        (this.renderers = Maps.newHashMap()).put(SignBlockEntity.class, new SignBlockEntityRenderer());
        this.renderers.put(MobSpawnerBlockEntity.class, new MobSpawnerBlockEntityRenderer());
        this.renderers.put(PistonBlockEntity.class, new PistonBlockEntityRenderer());
        this.renderers.put(ChestBlockEntity.class, new ChestBlockEntityRenderer<BlockEntity>());
        this.renderers.put(EnderChestBlockEntity.class, new ChestBlockEntityRenderer<BlockEntity>());
        this.renderers.put(EnchantingTableBlockEntity.class, new EnchantingTableBlockEntityRenderer());
        this.renderers.put(LecternBlockEntity.class, new LecternBlockEntityRenderer());
        this.renderers.put(EndPortalBlockEntity.class, new EndPortalBlockEntityRenderer());
        this.renderers.put(EndGatewayBlockEntity.class, new EndGatewayBlockEntityRenderer());
        this.renderers.put(BeaconBlockEntity.class, new BeaconBlockEntityRenderer());
        this.renderers.put(SkullBlockEntity.class, new SkullBlockEntityRenderer());
        this.renderers.put(BannerBlockEntity.class, new BannerBlockEntityRenderer());
        this.renderers.put(StructureBlockBlockEntity.class, new StructureBlockBlockEntityRenderer());
        this.renderers.put(ShulkerBoxBlockEntity.class, new ShulkerBoxBlockEntityRenderer(new ShulkerEntityModel<>()));
        this.renderers.put(BedBlockEntity.class, new BedBlockEntityRenderer());
        this.renderers.put(ConduitBlockEntity.class, new ConduitBlockEntityRenderer());
        this.renderers.put(BellBlockEntity.class, new BellBlockEntityRenderer());
        this.renderers.put(CampfireBlockEntity.class, new CampfireBlockEntityRenderer());
        for (final BlockEntityRenderer<?> blockEntityRenderer2 : this.renderers.values()) {
            blockEntityRenderer2.setRenderManager(this);
        }
    }
    
    public <T extends BlockEntity> BlockEntityRenderer<T> get(final Class<? extends BlockEntity> class1) {
        BlockEntityRenderer<? extends BlockEntity> blockEntityRenderer2 = this.renderers.get(class1);
        if (blockEntityRenderer2 == null && class1 != BlockEntity.class) {
            blockEntityRenderer2 = this.get(class1.getSuperclass());
            this.renderers.put(class1, blockEntityRenderer2);
        }
        return (BlockEntityRenderer<T>)blockEntityRenderer2;
    }
    
    @Nullable
    public <T extends BlockEntity> BlockEntityRenderer<T> get(@Nullable final BlockEntity blockEntity) {
        if (blockEntity == null) {
            return null;
        }
        return this.<T>get(blockEntity.getClass());
    }
    
    public void configure(final World world, final TextureManager textureManager, final TextRenderer textRenderer, final Camera camera, final HitResult hitResult) {
        if (this.world != world) {
            this.setWorld(world);
        }
        this.textureManager = textureManager;
        this.cameraEntity = camera;
        this.fontRenderer = textRenderer;
        this.hitResult = hitResult;
    }
    
    public void render(final BlockEntity blockEntity, final float tickDelta, final int blockBreakStage) {
        if (blockEntity.getSquaredDistance(this.cameraEntity.getPos().x, this.cameraEntity.getPos().y, this.cameraEntity.getPos().z) < blockEntity.getSquaredRenderDistance()) {
            GuiLighting.enable();
            final int integer4 = this.world.getLightmapIndex(blockEntity.getPos(), 0);
            final int integer5 = integer4 % 65536;
            final int integer6 = integer4 / 65536;
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)integer5, (float)integer6);
            GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            final BlockPos blockPos7 = blockEntity.getPos();
            this.renderEntity(blockEntity, blockPos7.getX() - BlockEntityRenderDispatcher.renderOffsetX, blockPos7.getY() - BlockEntityRenderDispatcher.renderOffsetY, blockPos7.getZ() - BlockEntityRenderDispatcher.renderOffsetZ, tickDelta, blockBreakStage, false);
        }
    }
    
    public void renderEntity(final BlockEntity blockEntity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta) {
        this.renderEntity(blockEntity, xOffset, yOffset, zOffset, tickDelta, -1, false);
    }
    
    public void renderEntity(final BlockEntity blockEntity) {
        this.renderEntity(blockEntity, 0.0, 0.0, 0.0, 0.0f, -1, true);
    }
    
    public void renderEntity(final BlockEntity blockEntity, final double xOffset, final double yOffset, final double zOffset, final float tickDelta, final int blockBreakStage, final boolean boolean10) {
        final BlockEntityRenderer<BlockEntity> blockEntityRenderer11 = this.<BlockEntity>get(blockEntity);
        if (blockEntityRenderer11 != null) {
            try {
                if (!boolean10 && (!blockEntity.hasWorld() || !blockEntity.getCachedState().getBlock().hasBlockEntity())) {
                    return;
                }
                blockEntityRenderer11.render(blockEntity, xOffset, yOffset, zOffset, tickDelta, blockBreakStage);
            }
            catch (Throwable throwable12) {
                final CrashReport crashReport13 = CrashReport.create(throwable12, "Rendering Block Entity");
                final CrashReportSection crashReportSection14 = crashReport13.addElement("Block Entity Details");
                blockEntity.populateCrashReport(crashReportSection14);
                throw new CrashException(crashReport13);
            }
        }
    }
    
    public void setWorld(@Nullable final World world) {
        this.world = world;
        if (world == null) {
            this.cameraEntity = null;
        }
    }
    
    public TextRenderer getFontRenderer() {
        return this.fontRenderer;
    }
    
    static {
        INSTANCE = new BlockEntityRenderDispatcher();
    }
}
