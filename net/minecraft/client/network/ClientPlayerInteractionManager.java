package net.minecraft.client.network;

import net.minecraft.server.network.packet.PickFromInventoryC2SPacket;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.server.network.packet.CreativeInventoryActionC2SPacket;
import net.minecraft.server.network.packet.ButtonClickC2SPacket;
import net.minecraft.server.network.packet.CraftRequestC2SPacket;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.packet.ClickWindowC2SPacket;
import net.minecraft.container.SlotActionType;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.server.network.packet.PlayerInteractEntityC2SPacket;
import net.minecraft.entity.Entity;
import net.minecraft.client.recipe.book.ClientRecipeBook;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.TypedActionResult;
import net.minecraft.server.network.packet.PlayerInteractItemC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.packet.PlayerInteractBlockC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.network.packet.UpdateSelectedSlotC2SPacket;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.BlockView;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.PlayerActionC2SPacket;
import net.minecraft.fluid.FluidState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.world.World;
import net.minecraft.world.IWorld;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.CommandBlock;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ClientPlayerInteractionManager
{
    private final MinecraftClient client;
    private final ClientPlayNetworkHandler networkHandler;
    private BlockPos currentBreakingPos;
    private ItemStack selectedStack;
    private float currentBreakingProgress;
    private float f;
    private int g;
    private boolean breakingBlock;
    private GameMode gameMode;
    private int lastSelectedSlot;
    
    public ClientPlayerInteractionManager(final MinecraftClient client, final ClientPlayNetworkHandler clientPlayNetworkHandler) {
        this.currentBreakingPos = new BlockPos(-1, -1, -1);
        this.selectedStack = ItemStack.EMPTY;
        this.gameMode = GameMode.b;
        this.client = client;
        this.networkHandler = clientPlayNetworkHandler;
    }
    
    public static void a(final MinecraftClient minecraftClient, final ClientPlayerInteractionManager clientPlayerInteractionManager, final BlockPos blockPos, final Direction direction) {
        if (!minecraftClient.world.a(minecraftClient.player, blockPos, direction)) {
            clientPlayerInteractionManager.breakBlock(blockPos);
        }
    }
    
    public void copyAbilities(final PlayerEntity playerEntity) {
        this.gameMode.setAbilitites(playerEntity.abilities);
    }
    
    public void setGameMode(final GameMode gameMode) {
        (this.gameMode = gameMode).setAbilitites(this.client.player.abilities);
    }
    
    public boolean hasStatusBars() {
        return this.gameMode.isSurvivalLike();
    }
    
    public boolean breakBlock(final BlockPos blockPos) {
        if (this.gameMode.shouldLimitWorldModification()) {
            if (this.gameMode == GameMode.e) {
                return false;
            }
            if (!this.client.player.canModifyWorld()) {
                final ItemStack itemStack2 = this.client.player.getMainHandStack();
                if (itemStack2.isEmpty()) {
                    return false;
                }
                final CachedBlockPosition cachedBlockPosition3 = new CachedBlockPosition(this.client.world, blockPos, false);
                if (!itemStack2.getCustomCanHarvest(this.client.world.getTagManager(), cachedBlockPosition3)) {
                    return false;
                }
            }
        }
        final World world2 = this.client.world;
        final BlockState blockState3 = world2.getBlockState(blockPos);
        if (!this.client.player.getMainHandStack().getItem().beforeBlockBreak(blockState3, world2, blockPos, this.client.player)) {
            return false;
        }
        final Block block4 = blockState3.getBlock();
        if ((block4 instanceof CommandBlock || block4 instanceof StructureBlock || block4 instanceof JigsawBlock) && !this.client.player.isCreativeLevelTwoOp()) {
            return false;
        }
        if (blockState3.isAir()) {
            return false;
        }
        block4.onBreak(world2, blockPos, blockState3, this.client.player);
        final FluidState fluidState5 = world2.getFluidState(blockPos);
        final boolean boolean6 = world2.setBlockState(blockPos, fluidState5.getBlockState(), 11);
        if (boolean6) {
            block4.onBroken(world2, blockPos, blockState3);
        }
        this.currentBreakingPos = new BlockPos(this.currentBreakingPos.getX(), -1, this.currentBreakingPos.getZ());
        return boolean6;
    }
    
    public boolean attackBlock(final BlockPos pos, final Direction direction) {
        if (this.gameMode.shouldLimitWorldModification()) {
            if (this.gameMode == GameMode.e) {
                return false;
            }
            if (!this.client.player.canModifyWorld()) {
                final ItemStack itemStack3 = this.client.player.getMainHandStack();
                if (itemStack3.isEmpty()) {
                    return false;
                }
                final CachedBlockPosition cachedBlockPosition4 = new CachedBlockPosition(this.client.world, pos, false);
                if (!itemStack3.getCustomCanHarvest(this.client.world.getTagManager(), cachedBlockPosition4)) {
                    return false;
                }
            }
        }
        if (!this.client.world.getWorldBorder().contains(pos)) {
            return false;
        }
        if (this.gameMode.isCreative()) {
            this.client.getTutorialManager().onBlockAttacked(this.client.world, pos, this.client.world.getBlockState(pos), 1.0f);
            this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.a, pos, direction));
            a(this.client, this, pos, direction);
            this.g = 5;
        }
        else if (!this.breakingBlock || !this.isCurrentlyBreaking(pos)) {
            if (this.breakingBlock) {
                this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.b, this.currentBreakingPos, direction));
            }
            final BlockState blockState3 = this.client.world.getBlockState(pos);
            this.client.getTutorialManager().onBlockAttacked(this.client.world, pos, blockState3, 0.0f);
            this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.a, pos, direction));
            final boolean boolean4 = !blockState3.isAir();
            if (boolean4 && this.currentBreakingProgress == 0.0f) {
                blockState3.onBlockBreakStart(this.client.world, pos, this.client.player);
            }
            if (boolean4 && blockState3.calcBlockBreakingDelta(this.client.player, this.client.player.world, pos) >= 1.0f) {
                this.breakBlock(pos);
            }
            else {
                this.breakingBlock = true;
                this.currentBreakingPos = pos;
                this.selectedStack = this.client.player.getMainHandStack();
                this.currentBreakingProgress = 0.0f;
                this.f = 0.0f;
                this.client.world.setBlockBreakingProgress(this.client.player.getEntityId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0f) - 1);
            }
        }
        return true;
    }
    
    public void cancelBlockBreaking() {
        if (this.breakingBlock) {
            this.client.getTutorialManager().onBlockAttacked(this.client.world, this.currentBreakingPos, this.client.world.getBlockState(this.currentBreakingPos), -1.0f);
            this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.b, this.currentBreakingPos, Direction.DOWN));
            this.breakingBlock = false;
            this.currentBreakingProgress = 0.0f;
            this.client.world.setBlockBreakingProgress(this.client.player.getEntityId(), this.currentBreakingPos, -1);
            this.client.player.dZ();
        }
    }
    
    public boolean b(final BlockPos blockPos, final Direction direction) {
        this.syncSelectedSlot();
        if (this.g > 0) {
            --this.g;
            return true;
        }
        if (this.gameMode.isCreative() && this.client.world.getWorldBorder().contains(blockPos)) {
            this.g = 5;
            this.client.getTutorialManager().onBlockAttacked(this.client.world, blockPos, this.client.world.getBlockState(blockPos), 1.0f);
            this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.a, blockPos, direction));
            a(this.client, this, blockPos, direction);
            return true;
        }
        if (!this.isCurrentlyBreaking(blockPos)) {
            return this.attackBlock(blockPos, direction);
        }
        final BlockState blockState3 = this.client.world.getBlockState(blockPos);
        if (blockState3.isAir()) {
            return this.breakingBlock = false;
        }
        this.currentBreakingProgress += blockState3.calcBlockBreakingDelta(this.client.player, this.client.player.world, blockPos);
        if (this.f % 4.0f == 0.0f) {
            final BlockSoundGroup blockSoundGroup4 = blockState3.getSoundGroup();
            this.client.getSoundManager().play(new PositionedSoundInstance(blockSoundGroup4.getHitSound(), SoundCategory.g, (blockSoundGroup4.getVolume() + 1.0f) / 8.0f, blockSoundGroup4.getPitch() * 0.5f, blockPos));
        }
        ++this.f;
        this.client.getTutorialManager().onBlockAttacked(this.client.world, blockPos, blockState3, MathHelper.clamp(this.currentBreakingProgress, 0.0f, 1.0f));
        if (this.currentBreakingProgress >= 1.0f) {
            this.breakingBlock = false;
            this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.c, blockPos, direction));
            this.breakBlock(blockPos);
            this.currentBreakingProgress = 0.0f;
            this.f = 0.0f;
            this.g = 5;
        }
        this.client.world.setBlockBreakingProgress(this.client.player.getEntityId(), this.currentBreakingPos, (int)(this.currentBreakingProgress * 10.0f) - 1);
        return true;
    }
    
    public float getReachDistance() {
        if (this.gameMode.isCreative()) {
            return 5.0f;
        }
        return 4.5f;
    }
    
    public void tick() {
        this.syncSelectedSlot();
        if (this.networkHandler.getClientConnection().isOpen()) {
            this.networkHandler.getClientConnection().tick();
        }
        else {
            this.networkHandler.getClientConnection().handleDisconnection();
        }
    }
    
    private boolean isCurrentlyBreaking(final BlockPos blockPos) {
        final ItemStack itemStack2 = this.client.player.getMainHandStack();
        boolean boolean3 = this.selectedStack.isEmpty() && itemStack2.isEmpty();
        if (!this.selectedStack.isEmpty() && !itemStack2.isEmpty()) {
            boolean3 = (itemStack2.getItem() == this.selectedStack.getItem() && ItemStack.areTagsEqual(itemStack2, this.selectedStack) && (itemStack2.hasDurability() || itemStack2.getDamage() == this.selectedStack.getDamage()));
        }
        return blockPos.equals(this.currentBreakingPos) && boolean3;
    }
    
    private void syncSelectedSlot() {
        final int integer1 = this.client.player.inventory.selectedSlot;
        if (integer1 != this.lastSelectedSlot) {
            this.lastSelectedSlot = integer1;
            this.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.lastSelectedSlot));
        }
    }
    
    public ActionResult interactBlock(final ClientPlayerEntity player, final ClientWorld world, final Hand hand, final BlockHitResult blockHitResult) {
        this.syncSelectedSlot();
        final BlockPos blockPos5 = blockHitResult.getBlockPos();
        final Vec3d vec3d6 = blockHitResult.getPos();
        if (!this.client.world.getWorldBorder().contains(blockPos5)) {
            return ActionResult.c;
        }
        final ItemStack itemStack7 = player.getStackInHand(hand);
        if (this.gameMode == GameMode.e) {
            this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, blockHitResult));
            return ActionResult.a;
        }
        final boolean boolean8 = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
        final boolean boolean9 = player.isSneaking() && boolean8;
        if (!boolean9 && world.getBlockState(blockPos5).activate(world, player, hand, blockHitResult)) {
            this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, blockHitResult));
            return ActionResult.a;
        }
        this.networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, blockHitResult));
        if (itemStack7.isEmpty() || player.getItemCooldownManager().isCoolingDown(itemStack7.getItem())) {
            return ActionResult.PASS;
        }
        final ItemUsageContext itemUsageContext11 = new ItemUsageContext(player, hand, blockHitResult);
        ActionResult actionResult10;
        if (this.gameMode.isCreative()) {
            final int integer12 = itemStack7.getAmount();
            actionResult10 = itemStack7.useOnBlock(itemUsageContext11);
            itemStack7.setAmount(integer12);
        }
        else {
            actionResult10 = itemStack7.useOnBlock(itemUsageContext11);
        }
        return actionResult10;
    }
    
    public ActionResult interactItem(final PlayerEntity player, final World world, final Hand hand) {
        if (this.gameMode == GameMode.e) {
            return ActionResult.PASS;
        }
        this.syncSelectedSlot();
        this.networkHandler.sendPacket(new PlayerInteractItemC2SPacket(hand));
        final ItemStack itemStack4 = player.getStackInHand(hand);
        if (player.getItemCooldownManager().isCoolingDown(itemStack4.getItem())) {
            return ActionResult.PASS;
        }
        final int integer5 = itemStack4.getAmount();
        final TypedActionResult<ItemStack> typedActionResult6 = itemStack4.use(world, player, hand);
        final ItemStack itemStack5 = typedActionResult6.getValue();
        if (itemStack5 != itemStack4 || itemStack5.getAmount() != integer5) {
            player.setStackInHand(hand, itemStack5);
        }
        return typedActionResult6.getResult();
    }
    
    public ClientPlayerEntity createPlayer(final ClientWorld clientWorld, final StatHandler statHandler, final ClientRecipeBook clientRecipeBook) {
        return new ClientPlayerEntity(this.client, clientWorld, this.networkHandler, statHandler, clientRecipeBook);
    }
    
    public void attackEntity(final PlayerEntity playerEntity, final Entity entity) {
        this.syncSelectedSlot();
        this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity));
        if (this.gameMode != GameMode.e) {
            playerEntity.attack(entity);
            playerEntity.dZ();
        }
    }
    
    public ActionResult interactEntity(final PlayerEntity player, final Entity entity, final Hand hand) {
        this.syncSelectedSlot();
        this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, hand));
        if (this.gameMode == GameMode.e) {
            return ActionResult.PASS;
        }
        return player.interact(entity, hand);
    }
    
    public ActionResult interactEntityAtLocation(final PlayerEntity playerEntity, final Entity entity, final EntityHitResult entityHitResult, final Hand hand) {
        this.syncSelectedSlot();
        final Vec3d vec3d5 = entityHitResult.getPos().subtract(entity.x, entity.y, entity.z);
        this.networkHandler.sendPacket(new PlayerInteractEntityC2SPacket(entity, hand, vec3d5));
        if (this.gameMode == GameMode.e) {
            return ActionResult.PASS;
        }
        return entity.interactAt(playerEntity, vec3d5, hand);
    }
    
    public ItemStack a(final int integer1, final int integer2, final int integer3, final SlotActionType slotActionType, final PlayerEntity playerEntity) {
        final short short6 = playerEntity.container.getNextActionId(playerEntity.inventory);
        final ItemStack itemStack7 = playerEntity.container.onSlotClick(integer2, integer3, slotActionType, playerEntity);
        this.networkHandler.sendPacket(new ClickWindowC2SPacket(integer1, integer2, integer3, slotActionType, itemStack7, short6));
        return itemStack7;
    }
    
    public void clickRecipe(final int syncId, final Recipe<?> recipe, final boolean boolean3) {
        this.networkHandler.sendPacket(new CraftRequestC2SPacket(syncId, recipe, boolean3));
    }
    
    public void clickButton(final int syncId, final int buttonId) {
        this.networkHandler.sendPacket(new ButtonClickC2SPacket(syncId, buttonId));
    }
    
    public void clickCreativeStack(final ItemStack stack, final int integer) {
        if (this.gameMode.isCreative()) {
            this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(integer, stack));
        }
    }
    
    public void dropCreativeStack(final ItemStack itemStack) {
        if (this.gameMode.isCreative() && !itemStack.isEmpty()) {
            this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(-1, itemStack));
        }
    }
    
    public void stopUsingItem(final PlayerEntity playerEntity) {
        this.syncSelectedSlot();
        this.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.f, BlockPos.ORIGIN, Direction.DOWN));
        playerEntity.stopUsingItem();
    }
    
    public boolean hasExperienceBar() {
        return this.gameMode.isSurvivalLike();
    }
    
    public boolean hasLimitedAttackSpeed() {
        return !this.gameMode.isCreative();
    }
    
    public boolean hasCreativeInventory() {
        return this.gameMode.isCreative();
    }
    
    public boolean hasExtendedReach() {
        return this.gameMode.isCreative();
    }
    
    public boolean hasRidingInventory() {
        return this.client.player.hasVehicle() && this.client.player.getVehicle() instanceof HorseBaseEntity;
    }
    
    public boolean isFlyingLocked() {
        return this.gameMode == GameMode.e;
    }
    
    public GameMode getCurrentGameMode() {
        return this.gameMode;
    }
    
    public boolean isBreakingBlock() {
        return this.breakingBlock;
    }
    
    public void pickFromInventory(final int slot) {
        this.networkHandler.sendPacket(new PickFromInventoryC2SPacket(slot));
    }
}
