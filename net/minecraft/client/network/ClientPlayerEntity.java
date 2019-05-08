package net.minecraft.client.network;

import java.util.stream.Stream;
import net.minecraft.client.audio.AmbientSoundLoops;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Vec2f;
import java.util.Collections;
import net.minecraft.world.BlockView;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.tag.FluidTags;
import net.minecraft.item.ElytraItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.EntityPose;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.item.Item;
import net.minecraft.client.gui.ingame.EditBookScreen;
import net.minecraft.item.Items;
import net.minecraft.client.gui.ingame.JigsawBlockScreen;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.client.gui.ingame.StructureBlockScreen;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.client.gui.CommandBlockScreen;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.gui.CommandBlockMinecartScreen;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.client.gui.ingame.EditSignScreen;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.client.audio.ElytraSoundInstance;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.TextComponent;
import net.minecraft.server.network.packet.RecipeBookDataC2SPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.math.MathHelper;
import net.minecraft.server.network.packet.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.client.gui.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.packet.GuiCloseC2SPacket;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.server.network.packet.HandSwingC2SPacket;
import net.minecraft.server.network.packet.ChatMessageC2SPacket;
import javax.annotation.Nullable;
import net.minecraft.util.math.Direction;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.server.network.packet.ClientCommandC2SPacket;
import java.util.Iterator;
import net.minecraft.server.network.packet.VehicleMoveC2SPacket;
import net.minecraft.server.network.packet.PlayerInputC2SPacket;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.audio.MinecartSoundInstance;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.client.audio.BubbleColumnSoundPlayer;
import net.minecraft.client.audio.AmbientSoundPlayer;
import net.minecraft.world.dimension.DimensionType;
import com.google.common.collect.Lists;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Hand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.util.ClientPlayerTickable;
import java.util.List;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.stat.StatHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientPlayerEntity extends AbstractClientPlayerEntity
{
    public final ClientPlayNetworkHandler networkHandler;
    private final StatHandler stats;
    private final ClientRecipeBook recipeBook;
    private final List<ClientPlayerTickable> tickables;
    private int clientPermissionLevel;
    private double lastX;
    private double lastBaseY;
    private double lastZ;
    private float lastYaw;
    private float lastPitch;
    private boolean lastOnGround;
    private boolean lastIsHoldingSneakKey;
    private boolean lastSprinting;
    private int co;
    private boolean cp;
    private String serverBrand;
    public Input input;
    protected final MinecraftClient client;
    protected int bU;
    public int bV;
    public float renderYaw;
    public float renderPitch;
    public float lastRenderYaw;
    public float lastRenderPitch;
    private int cr;
    private float cs;
    public float nextNauseaStrength;
    public float lastNauseaStrength;
    private boolean ct;
    private Hand activeHand;
    private boolean riding;
    private boolean lastAutoJump;
    private int cx;
    private boolean cy;
    private int cz;
    
    public ClientPlayerEntity(final MinecraftClient client, final ClientWorld clientWorld, final ClientPlayNetworkHandler clientPlayNetworkHandler, final StatHandler statHandler, final ClientRecipeBook clientRecipeBook) {
        super(clientWorld, clientPlayNetworkHandler.getProfile());
        this.tickables = Lists.newArrayList();
        this.clientPermissionLevel = 0;
        this.lastAutoJump = true;
        this.networkHandler = clientPlayNetworkHandler;
        this.stats = statHandler;
        this.recipeBook = clientRecipeBook;
        this.client = client;
        this.dimension = DimensionType.a;
        this.tickables.add(new AmbientSoundPlayer(this, client.getSoundManager()));
        this.tickables.add(new BubbleColumnSoundPlayer(this));
    }
    
    @Override
    public boolean damage(final DamageSource source, final float amount) {
        return false;
    }
    
    @Override
    public void heal(final float amount) {
    }
    
    @Override
    public boolean startRiding(final Entity entity, final boolean boolean2) {
        if (!super.startRiding(entity, boolean2)) {
            return false;
        }
        if (entity instanceof AbstractMinecartEntity) {
            this.client.getSoundManager().play(new MinecartSoundInstance(this, (AbstractMinecartEntity)entity));
        }
        if (entity instanceof BoatEntity) {
            this.prevYaw = entity.yaw;
            this.yaw = entity.yaw;
            this.setHeadYaw(entity.yaw);
        }
        return true;
    }
    
    @Override
    public void stopRiding() {
        super.stopRiding();
        this.riding = false;
    }
    
    @Override
    public float getPitch(final float tickDelta) {
        return this.pitch;
    }
    
    @Override
    public float getYaw(final float tickDelta) {
        if (this.hasVehicle()) {
            return super.getYaw(tickDelta);
        }
        return this.yaw;
    }
    
    @Override
    public void tick() {
        if (!this.world.isBlockLoaded(new BlockPos(this.x, 0.0, this.z))) {
            return;
        }
        super.tick();
        if (this.hasVehicle()) {
            this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(this.yaw, this.pitch, this.onGround));
            this.networkHandler.sendPacket(new PlayerInputC2SPacket(this.sidewaysSpeed, this.forwardSpeed, this.input.jumping, this.input.sneaking));
            final Entity entity1 = this.getTopmostVehicle();
            if (entity1 != this && entity1.isLogicalSideForUpdatingMovement()) {
                this.networkHandler.sendPacket(new VehicleMoveC2SPacket(entity1));
            }
        }
        else {
            this.sendMovementPackets();
        }
        for (final ClientPlayerTickable clientPlayerTickable2 : this.tickables) {
            clientPlayerTickable2.tick();
        }
    }
    
    private void sendMovementPackets() {
        final boolean boolean1 = this.isSprinting();
        if (boolean1 != this.lastSprinting) {
            final ClientCommandC2SPacket.Mode mode2 = boolean1 ? ClientCommandC2SPacket.Mode.d : ClientCommandC2SPacket.Mode.e;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode2));
            this.lastSprinting = boolean1;
        }
        final boolean boolean2 = this.isHoldingSneakKey();
        if (boolean2 != this.lastIsHoldingSneakKey) {
            final ClientCommandC2SPacket.Mode mode3 = boolean2 ? ClientCommandC2SPacket.Mode.a : ClientCommandC2SPacket.Mode.b;
            this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode3));
            this.lastIsHoldingSneakKey = boolean2;
        }
        if (this.isCamera()) {
            final BoundingBox boundingBox3 = this.getBoundingBox();
            final double double4 = this.x - this.lastX;
            final double double5 = boundingBox3.minY - this.lastBaseY;
            final double double6 = this.z - this.lastZ;
            final double double7 = this.yaw - this.lastYaw;
            final double double8 = this.pitch - this.lastPitch;
            ++this.co;
            boolean boolean3 = double4 * double4 + double5 * double5 + double6 * double6 > 9.0E-4 || this.co >= 20;
            final boolean boolean4 = double7 != 0.0 || double8 != 0.0;
            if (this.hasVehicle()) {
                final Vec3d vec3d16 = this.getVelocity();
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(vec3d16.x, -999.0, vec3d16.z, this.yaw, this.pitch, this.onGround));
                boolean3 = false;
            }
            else if (boolean3 && boolean4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.Both(this.x, boundingBox3.minY, this.z, this.yaw, this.pitch, this.onGround));
            }
            else if (boolean3) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(this.x, boundingBox3.minY, this.z, this.onGround));
            }
            else if (boolean4) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookOnly(this.yaw, this.pitch, this.onGround));
            }
            else if (this.lastOnGround != this.onGround) {
                this.networkHandler.sendPacket(new PlayerMoveC2SPacket(this.onGround));
            }
            if (boolean3) {
                this.lastX = this.x;
                this.lastBaseY = boundingBox3.minY;
                this.lastZ = this.z;
                this.co = 0;
            }
            if (boolean4) {
                this.lastYaw = this.yaw;
                this.lastPitch = this.pitch;
            }
            this.lastOnGround = this.onGround;
            this.lastAutoJump = this.client.options.autoJump;
        }
    }
    
    @Nullable
    @Override
    public ItemEntity dropSelectedItem(final boolean boolean1) {
        final PlayerActionC2SPacket.Action action2 = boolean1 ? PlayerActionC2SPacket.Action.d : PlayerActionC2SPacket.Action.e;
        this.networkHandler.sendPacket(new PlayerActionC2SPacket(action2, BlockPos.ORIGIN, Direction.DOWN));
        this.inventory.takeInvStack(this.inventory.selectedSlot, (boolean1 && !this.inventory.getMainHandStack().isEmpty()) ? this.inventory.getMainHandStack().getAmount() : 1);
        return null;
    }
    
    public void sendChatMessage(final String string) {
        this.networkHandler.sendPacket(new ChatMessageC2SPacket(string));
    }
    
    @Override
    public void swingHand(final Hand hand) {
        super.swingHand(hand);
        this.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
    }
    
    @Override
    public void requestRespawn() {
        this.networkHandler.sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.a));
    }
    
    @Override
    protected void applyDamage(final DamageSource source, final float damage) {
        if (this.isInvulnerableTo(source)) {
            return;
        }
        this.setHealth(this.getHealth() - damage);
    }
    
    public void closeContainer() {
        this.networkHandler.sendPacket(new GuiCloseC2SPacket(this.container.syncId));
        this.closeScreen();
    }
    
    public void closeScreen() {
        this.inventory.setCursorStack(ItemStack.EMPTY);
        super.closeContainer();
        this.client.openScreen(null);
    }
    
    public void updateHealth(final float float1) {
        if (this.cp) {
            final float float2 = this.getHealth() - float1;
            if (float2 <= 0.0f) {
                this.setHealth(float1);
                if (float2 < 0.0f) {
                    this.T = 10;
                }
            }
            else {
                this.aZ = float2;
                this.setHealth(this.getHealth());
                this.T = 20;
                this.applyDamage(DamageSource.GENERIC, float2);
                this.ay = 10;
                this.hurtTime = this.ay;
            }
        }
        else {
            this.setHealth(float1);
            this.cp = true;
        }
    }
    
    @Override
    public void sendAbilitiesUpdate() {
        this.networkHandler.sendPacket(new UpdatePlayerAbilitiesC2SPacket(this.abilities));
    }
    
    @Override
    public boolean isMainPlayer() {
        return true;
    }
    
    protected void startRidingJump() {
        this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.f, MathHelper.floor(this.G() * 100.0f)));
    }
    
    public void openRidingInventory() {
        this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.h));
    }
    
    public void setServerBrand(final String serverBrand) {
        this.serverBrand = serverBrand;
    }
    
    public String getServerBrand() {
        return this.serverBrand;
    }
    
    public StatHandler getStats() {
        return this.stats;
    }
    
    public ClientRecipeBook getRecipeBook() {
        return this.recipeBook;
    }
    
    public void onRecipeDisplayed(final Recipe<?> recipe) {
        if (this.recipeBook.shouldDisplay(recipe)) {
            this.recipeBook.onRecipeDisplayed(recipe);
            this.networkHandler.sendPacket(new RecipeBookDataC2SPacket(recipe));
        }
    }
    
    @Override
    protected int getPermissionLevel() {
        return this.clientPermissionLevel;
    }
    
    public void setClientPermissionLevel(final int integer) {
        this.clientPermissionLevel = integer;
    }
    
    @Override
    public void addChatMessage(final TextComponent message, final boolean boolean2) {
        if (boolean2) {
            this.client.inGameHud.setOverlayMessage(message, false);
        }
        else {
            this.client.inGameHud.getChatHud().addMessage(message);
        }
    }
    
    @Override
    protected void pushOutOfBlocks(final double x, final double y, final double z) {
        final BlockPos blockPos7 = new BlockPos(x, y, z);
        if (this.cannotFitAt(blockPos7)) {
            final double double8 = x - blockPos7.getX();
            final double double9 = z - blockPos7.getZ();
            Direction direction12 = null;
            double double10 = 9999.0;
            if (!this.cannotFitAt(blockPos7.west()) && double8 < double10) {
                double10 = double8;
                direction12 = Direction.WEST;
            }
            if (!this.cannotFitAt(blockPos7.east()) && 1.0 - double8 < double10) {
                double10 = 1.0 - double8;
                direction12 = Direction.EAST;
            }
            if (!this.cannotFitAt(blockPos7.north()) && double9 < double10) {
                double10 = double9;
                direction12 = Direction.NORTH;
            }
            if (!this.cannotFitAt(blockPos7.south()) && 1.0 - double9 < double10) {
                double10 = 1.0 - double9;
                direction12 = Direction.SOUTH;
            }
            if (direction12 != null) {
                final Vec3d vec3d15 = this.getVelocity();
                switch (direction12) {
                    case WEST: {
                        this.setVelocity(-0.1, vec3d15.y, vec3d15.z);
                        break;
                    }
                    case EAST: {
                        this.setVelocity(0.1, vec3d15.y, vec3d15.z);
                        break;
                    }
                    case NORTH: {
                        this.setVelocity(vec3d15.x, vec3d15.y, -0.1);
                        break;
                    }
                    case SOUTH: {
                        this.setVelocity(vec3d15.x, vec3d15.y, 0.1);
                        break;
                    }
                }
            }
        }
    }
    
    private boolean cannotFitAt(final BlockPos pos) {
        final BoundingBox boundingBox2 = this.getBoundingBox();
        final BlockPos.Mutable mutable3 = new BlockPos.Mutable(pos);
        for (int integer4 = MathHelper.floor(boundingBox2.minY); integer4 < MathHelper.ceil(boundingBox2.maxY); ++integer4) {
            mutable3.setY(integer4);
            if (!this.doesNotSuffocate(mutable3)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void setSprinting(final boolean sprinting) {
        super.setSprinting(sprinting);
        this.bV = 0;
    }
    
    public void a(final float float1, final int integer2, final int integer3) {
        this.experienceLevelProgress = float1;
        this.experienceLevel = integer2;
        this.experience = integer3;
    }
    
    @Override
    public void sendMessage(final TextComponent message) {
        this.client.inGameHud.getChatHud().addMessage(message);
    }
    
    @Override
    public void handleStatus(final byte status) {
        if (status >= 24 && status <= 28) {
            this.setClientPermissionLevel(status - 24);
        }
        else {
            super.handleStatus(status);
        }
    }
    
    @Override
    public void playSound(final SoundEvent sound, final float volume, final float float3) {
        this.world.playSound(this.x, this.y, this.z, sound, this.getSoundCategory(), volume, float3, false);
    }
    
    @Override
    public void playSound(final SoundEvent soundEvent, final SoundCategory soundCategory, final float float3, final float float4) {
        this.world.playSound(this.x, this.y, this.z, soundEvent, soundCategory, float3, float4, false);
    }
    
    @Override
    public boolean canMoveVoluntarily() {
        return true;
    }
    
    @Override
    public void setCurrentHand(final Hand hand) {
        final ItemStack itemStack2 = this.getStackInHand(hand);
        if (itemStack2.isEmpty() || this.isUsingItem()) {
            return;
        }
        super.setCurrentHand(hand);
        this.ct = true;
        this.activeHand = hand;
    }
    
    @Override
    public boolean isUsingItem() {
        return this.ct;
    }
    
    @Override
    public void clearActiveItem() {
        super.clearActiveItem();
        this.ct = false;
    }
    
    @Override
    public Hand getActiveHand() {
        return this.activeHand;
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (ClientPlayerEntity.LIVING_FLAGS.equals(data)) {
            final boolean boolean2 = (this.dataTracker.<Byte>get(ClientPlayerEntity.LIVING_FLAGS) & 0x1) > 0;
            final Hand hand3 = ((this.dataTracker.<Byte>get(ClientPlayerEntity.LIVING_FLAGS) & 0x2) > 0) ? Hand.b : Hand.a;
            if (boolean2 && !this.ct) {
                this.setCurrentHand(hand3);
            }
            else if (!boolean2 && this.ct) {
                this.clearActiveItem();
            }
        }
        if (ClientPlayerEntity.FLAGS.equals(data) && this.isFallFlying() && !this.cy) {
            this.client.getSoundManager().play(new ElytraSoundInstance(this));
        }
    }
    
    public boolean hasJumpingMount() {
        final Entity entity1 = this.getVehicle();
        return this.hasVehicle() && entity1 instanceof JumpingMount && ((JumpingMount)entity1).canJump();
    }
    
    public float G() {
        return this.cs;
    }
    
    @Override
    public void openEditSignScreen(final SignBlockEntity signBlockEntity) {
        this.client.openScreen(new EditSignScreen(signBlockEntity));
    }
    
    @Override
    public void openCommandBlockMinecartScreen(final CommandBlockExecutor commandBlockExecutor) {
        this.client.openScreen(new CommandBlockMinecartScreen(commandBlockExecutor));
    }
    
    @Override
    public void openCommandBlockScreen(final CommandBlockBlockEntity commandBlockBlockEntity) {
        this.client.openScreen(new CommandBlockScreen(commandBlockBlockEntity));
    }
    
    @Override
    public void openStructureBlockScreen(final StructureBlockBlockEntity structureBlockBlockEntity) {
        this.client.openScreen(new StructureBlockScreen(structureBlockBlockEntity));
    }
    
    @Override
    public void openJigsawScreen(final JigsawBlockEntity jigsawBlockEntity) {
        this.client.openScreen(new JigsawBlockScreen(jigsawBlockEntity));
    }
    
    @Override
    public void openEditBookScreen(final ItemStack itemStack, final Hand hand) {
        final Item item3 = itemStack.getItem();
        if (item3 == Items.nD) {
            this.client.openScreen(new EditBookScreen(this, itemStack, hand));
        }
    }
    
    @Override
    public void addCritParticles(final Entity entity) {
        this.client.particleManager.addEmitter(entity, ParticleTypes.g);
    }
    
    @Override
    public void addEnchantedHitParticles(final Entity entity) {
        this.client.particleManager.addEmitter(entity, ParticleTypes.r);
    }
    
    @Override
    public boolean isSneaking() {
        return this.isHoldingSneakKey();
    }
    
    public boolean isHoldingSneakKey() {
        return this.input != null && this.input.sneaking;
    }
    
    @Override
    public boolean isInSneakingPose() {
        return !this.abilities.flying && this.wouldPoseNotCollide(EntityPose.f) && (this.isHoldingSneakKey() || !this.wouldPoseNotCollide(EntityPose.a));
    }
    
    public void tickNewAi() {
        super.tickNewAi();
        if (this.isCamera()) {
            this.sidewaysSpeed = this.input.movementSideways;
            this.forwardSpeed = this.input.movementForward;
            this.jumping = this.input.jumping;
            this.lastRenderYaw = this.renderYaw;
            this.lastRenderPitch = this.renderPitch;
            this.renderPitch += (float)((this.pitch - this.renderPitch) * 0.5);
            this.renderYaw += (float)((this.yaw - this.renderYaw) * 0.5);
        }
    }
    
    protected boolean isCamera() {
        return this.client.getCameraEntity() == this;
    }
    
    @Override
    public void updateState() {
        ++this.bV;
        if (this.bU > 0) {
            --this.bU;
        }
        this.updateNausea();
        final boolean boolean1 = this.input.jumping;
        final boolean boolean2 = this.input.sneaking;
        final float float3 = 0.8f;
        final boolean boolean3 = this.input.movementForward >= 0.8f;
        final boolean boolean4 = this.isInSneakingPose() || this.bk();
        this.input.tick(boolean4, this.isSpectator());
        this.client.getTutorialManager().onMovement(this.input);
        if (this.isUsingItem() && !this.hasVehicle()) {
            final Input input = this.input;
            input.movementSideways *= 0.2f;
            final Input input2 = this.input;
            input2.movementForward *= 0.2f;
            this.bU = 0;
        }
        boolean boolean5 = false;
        if (this.cx > 0) {
            --this.cx;
            boolean5 = true;
            this.input.jumping = true;
        }
        if (!this.noClip) {
            final BoundingBox boundingBox7 = this.getBoundingBox();
            this.pushOutOfBlocks(this.x - this.getWidth() * 0.35, boundingBox7.minY + 0.5, this.z + this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.x - this.getWidth() * 0.35, boundingBox7.minY + 0.5, this.z - this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.x + this.getWidth() * 0.35, boundingBox7.minY + 0.5, this.z - this.getWidth() * 0.35);
            this.pushOutOfBlocks(this.x + this.getWidth() * 0.35, boundingBox7.minY + 0.5, this.z + this.getWidth() * 0.35);
        }
        final boolean boolean6 = this.getHungerManager().getFoodLevel() > 6.0f || this.abilities.allowFlying;
        if ((this.onGround || this.isInWater()) && !boolean2 && !boolean3 && this.input.movementForward >= 0.8f && !this.isSprinting() && boolean6 && !this.isUsingItem() && !this.hasStatusEffect(StatusEffects.o)) {
            if (this.bU > 0 || this.client.options.keySprint.isPressed()) {
                this.setSprinting(true);
            }
            else {
                this.bU = 7;
            }
        }
        if (!this.isSprinting() && (!this.isInsideWater() || this.isInWater()) && this.input.movementForward >= 0.8f && boolean6 && !this.isUsingItem() && !this.hasStatusEffect(StatusEffects.o) && this.client.options.keySprint.isPressed()) {
            this.setSprinting(true);
        }
        if (this.isSprinting()) {
            final boolean boolean7 = this.input.movementForward < 0.8f || !boolean6;
            final boolean boolean8 = boolean7 || this.horizontalCollision || (this.isInsideWater() && !this.isInWater());
            if (this.isSwimming()) {
                if ((!this.onGround && !this.input.sneaking && boolean7) || !this.isInsideWater()) {
                    this.setSprinting(false);
                }
            }
            else if (boolean8) {
                this.setSprinting(false);
            }
        }
        if (this.abilities.allowFlying) {
            if (this.client.interactionManager.isFlyingLocked()) {
                if (!this.abilities.flying) {
                    this.abilities.flying = true;
                    this.sendAbilitiesUpdate();
                }
            }
            else if (!boolean1 && this.input.jumping && !boolean5) {
                if (this.bC == 0) {
                    this.bC = 7;
                }
                else if (!this.isSwimming()) {
                    this.abilities.flying = !this.abilities.flying;
                    this.sendAbilitiesUpdate();
                    this.bC = 0;
                }
            }
        }
        if (this.input.jumping && !boolean1 && !this.onGround && this.getVelocity().y < 0.0 && !this.isFallFlying() && !this.abilities.flying) {
            final ItemStack itemStack8 = this.getEquippedStack(EquipmentSlot.CHEST);
            if (itemStack8.getItem() == Items.oX && ElytraItem.isUsable(itemStack8)) {
                this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, ClientCommandC2SPacket.Mode.i));
            }
        }
        this.cy = this.isFallFlying();
        if (this.isInsideWater() && this.input.sneaking) {
            this.cY();
        }
        if (this.isInFluid(FluidTags.a)) {
            final int integer8 = this.isSpectator() ? 10 : 1;
            this.cz = MathHelper.clamp(this.cz + integer8, 0, 600);
        }
        else if (this.cz > 0) {
            this.isInFluid(FluidTags.a);
            this.cz = MathHelper.clamp(this.cz - 10, 0, 600);
        }
        if (this.abilities.flying && this.isCamera()) {
            int integer8 = 0;
            if (this.input.sneaking) {
                final Input input3 = this.input;
                input3.movementSideways /= (float)0.3;
                final Input input4 = this.input;
                input4.movementForward /= (float)0.3;
                --integer8;
            }
            if (this.input.jumping) {
                ++integer8;
            }
            if (integer8 != 0) {
                this.setVelocity(this.getVelocity().add(0.0, integer8 * this.abilities.getFlySpeed() * 3.0f, 0.0));
            }
        }
        if (this.hasJumpingMount()) {
            final JumpingMount jumpingMount8 = (JumpingMount)this.getVehicle();
            if (this.cr < 0) {
                ++this.cr;
                if (this.cr == 0) {
                    this.cs = 0.0f;
                }
            }
            if (boolean1 && !this.input.jumping) {
                this.cr = -10;
                jumpingMount8.setJumpStrength(MathHelper.floor(this.G() * 100.0f));
                this.startRidingJump();
            }
            else if (!boolean1 && this.input.jumping) {
                this.cr = 0;
                this.cs = 0.0f;
            }
            else if (boolean1) {
                ++this.cr;
                if (this.cr < 10) {
                    this.cs = this.cr * 0.1f;
                }
                else {
                    this.cs = 0.8f + 2.0f / (this.cr - 9) * 0.1f;
                }
            }
        }
        else {
            this.cs = 0.0f;
        }
        super.updateState();
        if (this.onGround && this.abilities.flying && !this.client.interactionManager.isFlyingLocked()) {
            this.abilities.flying = false;
            this.sendAbilitiesUpdate();
        }
    }
    
    private void updateNausea() {
        this.lastNauseaStrength = this.nextNauseaStrength;
        if (this.inPortal) {
            if (this.client.currentScreen != null && !this.client.currentScreen.isPauseScreen()) {
                if (this.client.currentScreen instanceof ContainerScreen) {
                    this.closeContainer();
                }
                this.client.openScreen(null);
            }
            if (this.nextNauseaStrength == 0.0f) {
                this.client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.iZ, this.random.nextFloat() * 0.4f + 0.8f));
            }
            this.nextNauseaStrength += 0.0125f;
            if (this.nextNauseaStrength >= 1.0f) {
                this.nextNauseaStrength = 1.0f;
            }
            this.inPortal = false;
        }
        else if (this.hasStatusEffect(StatusEffects.i) && this.getStatusEffect(StatusEffects.i).getDuration() > 60) {
            this.nextNauseaStrength += 0.006666667f;
            if (this.nextNauseaStrength > 1.0f) {
                this.nextNauseaStrength = 1.0f;
            }
        }
        else {
            if (this.nextNauseaStrength > 0.0f) {
                this.nextNauseaStrength -= 0.05f;
            }
            if (this.nextNauseaStrength < 0.0f) {
                this.nextNauseaStrength = 0.0f;
            }
        }
        this.tickPortalCooldown();
    }
    
    @Override
    public void tickRiding() {
        super.tickRiding();
        this.riding = false;
        if (this.getVehicle() instanceof BoatEntity) {
            final BoatEntity boatEntity1 = (BoatEntity)this.getVehicle();
            boatEntity1.a(this.input.pressingLeft, this.input.pressingRight, this.input.pressingForward, this.input.pressingBack);
            this.riding |= (this.input.pressingLeft || this.input.pressingRight || this.input.pressingForward || this.input.pressingBack);
        }
    }
    
    public boolean isRiding() {
        return this.riding;
    }
    
    @Nullable
    @Override
    public StatusEffectInstance removePotionEffect(@Nullable final StatusEffect statusEffect) {
        if (statusEffect == StatusEffects.i) {
            this.lastNauseaStrength = 0.0f;
            this.nextNauseaStrength = 0.0f;
        }
        return super.removePotionEffect(statusEffect);
    }
    
    @Override
    public void move(final MovementType type, final Vec3d offset) {
        final double double3 = this.x;
        final double double4 = this.z;
        super.move(type, offset);
        this.f((float)(this.x - double3), (float)(this.z - double4));
    }
    
    public boolean getLastAutoJump() {
        return this.lastAutoJump;
    }
    
    protected void f(final float float1, final float float2) {
        if (!this.getLastAutoJump()) {
            return;
        }
        if (this.cx > 0 || !this.onGround || this.isSneaking() || this.hasVehicle()) {
            return;
        }
        final Vec2f vec2f3 = this.input.getMovementInput();
        if (vec2f3.x == 0.0f && vec2f3.y == 0.0f) {
            return;
        }
        final Vec3d vec3d4 = new Vec3d(this.x, this.getBoundingBox().minY, this.z);
        final Vec3d vec3d5 = new Vec3d(this.x + float1, this.getBoundingBox().minY, this.z + float2);
        Vec3d vec3d6 = new Vec3d(float1, 0.0, float2);
        final float float3 = this.getMovementSpeed();
        float float4 = (float)vec3d6.lengthSquared();
        if (float4 <= 0.001f) {
            final float float5 = float3 * vec2f3.x;
            final float float6 = float3 * vec2f3.y;
            final float float7 = MathHelper.sin(this.yaw * 0.017453292f);
            final float float8 = MathHelper.cos(this.yaw * 0.017453292f);
            vec3d6 = new Vec3d(float5 * float8 - float6 * float7, vec3d6.y, float6 * float8 + float5 * float7);
            float4 = (float)vec3d6.lengthSquared();
            if (float4 <= 0.001f) {
                return;
            }
        }
        final float float5 = (float)MathHelper.fastInverseSqrt(float4);
        final Vec3d vec3d7 = vec3d6.multiply(float5);
        final Vec3d vec3d8 = this.getRotationVecClient();
        final float float8 = (float)(vec3d8.x * vec3d7.x + vec3d8.z * vec3d7.z);
        if (float8 < -0.15f) {
            return;
        }
        final VerticalEntityPosition verticalEntityPosition13 = VerticalEntityPosition.fromEntity(this);
        BlockPos blockPos14 = new BlockPos(this.x, this.getBoundingBox().maxY, this.z);
        final BlockState blockState15 = this.world.getBlockState(blockPos14);
        if (!blockState15.getCollisionShape(this.world, blockPos14, verticalEntityPosition13).isEmpty()) {
            return;
        }
        blockPos14 = blockPos14.up();
        final BlockState blockState16 = this.world.getBlockState(blockPos14);
        if (!blockState16.getCollisionShape(this.world, blockPos14, verticalEntityPosition13).isEmpty()) {
            return;
        }
        final float float9 = 7.0f;
        float float10 = 1.2f;
        if (this.hasStatusEffect(StatusEffects.h)) {
            float10 += (this.getStatusEffect(StatusEffects.h).getAmplifier() + 1) * 0.75f;
        }
        final float float11 = Math.max(float3 * 7.0f, 1.0f / float5);
        Vec3d vec3d9 = vec3d4;
        Vec3d vec3d10 = vec3d5.add(vec3d7.multiply(float11));
        final float float12 = this.getWidth();
        final float float13 = this.getHeight();
        final BoundingBox boundingBox24 = new BoundingBox(vec3d9, vec3d10.add(0.0, float13, 0.0)).expand(float12, 0.0, float12);
        vec3d9 = vec3d9.add(0.0, 0.5099999904632568, 0.0);
        vec3d10 = vec3d10.add(0.0, 0.5099999904632568, 0.0);
        final Vec3d vec3d11 = vec3d7.crossProduct(new Vec3d(0.0, 1.0, 0.0));
        final Vec3d vec3d12 = vec3d11.multiply(float12 * 0.5f);
        final Vec3d vec3d13 = vec3d9.subtract(vec3d12);
        final Vec3d vec3d14 = vec3d10.subtract(vec3d12);
        final Vec3d vec3d15 = vec3d9.add(vec3d12);
        final Vec3d vec3d16 = vec3d10.add(vec3d12);
        final Iterator<BoundingBox> iterator31 = this.world.getCollisionShapes(this, boundingBox24, Collections.<Entity>emptySet()).flatMap(voxelShape -> voxelShape.getBoundingBoxes().stream()).iterator();
        float float14 = Float.MIN_VALUE;
        while (iterator31.hasNext()) {
            final BoundingBox boundingBox25 = iterator31.next();
            if (!boundingBox25.intersects(vec3d13, vec3d14) && !boundingBox25.intersects(vec3d15, vec3d16)) {
                continue;
            }
            float14 = (float)boundingBox25.maxY;
            final Vec3d vec3d17 = boundingBox25.getCenter();
            final BlockPos blockPos15 = new BlockPos(vec3d17);
            for (int integer37 = 1; integer37 < float10; ++integer37) {
                final BlockPos blockPos16 = blockPos15.up(integer37);
                final BlockState blockState17 = this.world.getBlockState(blockPos16);
                final VoxelShape voxelShape2;
                if (!(voxelShape2 = blockState17.getCollisionShape(this.world, blockPos16, verticalEntityPosition13)).isEmpty()) {
                    float14 = (float)voxelShape2.getMaximum(Direction.Axis.Y) + blockPos16.getY();
                    if (float14 - this.getBoundingBox().minY > float10) {
                        return;
                    }
                }
                if (integer37 > 1) {
                    blockPos14 = blockPos14.up();
                    final BlockState blockState18 = this.world.getBlockState(blockPos14);
                    if (!blockState18.getCollisionShape(this.world, blockPos14, verticalEntityPosition13).isEmpty()) {
                        return;
                    }
                }
            }
            break;
        }
        if (float14 == Float.MIN_VALUE) {
            return;
        }
        final float float15 = (float)(float14 - this.getBoundingBox().minY);
        if (float15 <= 0.5f || float15 > float10) {
            return;
        }
        this.cx = 1;
    }
    
    public float L() {
        if (!this.isInFluid(FluidTags.a)) {
            return 0.0f;
        }
        final float float1 = 600.0f;
        final float float2 = 100.0f;
        if (this.cz >= 600.0f) {
            return 1.0f;
        }
        final float float3 = MathHelper.clamp(this.cz / 100.0f, 0.0f, 1.0f);
        final float float4 = (this.cz < 100.0f) ? 0.0f : MathHelper.clamp((this.cz - 100.0f) / 500.0f, 0.0f, 1.0f);
        return float3 * 0.6f + float4 * 0.39999998f;
    }
    
    @Override
    public boolean isInWater() {
        return this.isInWater;
    }
    
    @Override
    protected boolean updateInWater() {
        final boolean boolean1 = this.isInWater;
        final boolean boolean2 = super.updateInWater();
        if (this.isSpectator()) {
            return this.isInWater;
        }
        if (!boolean1 && boolean2) {
            this.world.playSound(this.x, this.y, this.z, SoundEvents.b, SoundCategory.i, 1.0f, 1.0f, false);
            this.client.getSoundManager().play(new AmbientSoundLoops.Underwater(this));
        }
        if (boolean1 && !boolean2) {
            this.world.playSound(this.x, this.y, this.z, SoundEvents.c, SoundCategory.i, 1.0f, 1.0f, false);
        }
        return this.isInWater;
    }
}
