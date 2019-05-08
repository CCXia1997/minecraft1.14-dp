package net.minecraft.server.network;

import net.minecraft.container.NameableContainerProvider;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.container.Container;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.CommandBlock;
import net.minecraft.world.IWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.world.World;
import net.minecraft.world.ViewableWorld;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.util.math.Direction;
import net.minecraft.block.BlockState;
import net.minecraft.world.BlockView;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.client.network.packet.PlayerListS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.server.world.ServerWorld;

public class ServerPlayerInteractionManager
{
    public ServerWorld world;
    public ServerPlayerEntity player;
    private GameMode gameMode;
    private boolean d;
    private int e;
    private BlockPos f;
    private int g;
    private boolean h;
    private BlockPos i;
    private int j;
    private int k;
    
    public ServerPlayerInteractionManager(final ServerWorld serverWorld) {
        this.gameMode = GameMode.INVALID;
        this.f = BlockPos.ORIGIN;
        this.i = BlockPos.ORIGIN;
        this.k = -1;
        this.world = serverWorld;
    }
    
    public void setGameMode(final GameMode gameMode) {
        (this.gameMode = gameMode).setAbilitites(this.player.abilities);
        this.player.sendAbilitiesUpdate();
        this.player.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_GAMEMODE, new ServerPlayerEntity[] { this.player }));
        this.world.updatePlayersSleeping();
    }
    
    public GameMode getGameMode() {
        return this.gameMode;
    }
    
    public boolean isSurvivalLike() {
        return this.gameMode.isSurvivalLike();
    }
    
    public boolean isCreative() {
        return this.gameMode.isCreative();
    }
    
    public void setGameModeIfNotPresent(final GameMode gameMode) {
        if (this.gameMode == GameMode.INVALID) {
            this.gameMode = gameMode;
        }
        this.setGameMode(this.gameMode);
    }
    
    public void update() {
        ++this.g;
        if (this.h) {
            final int integer1 = this.g - this.j;
            final BlockState blockState2 = this.world.getBlockState(this.i);
            if (blockState2.isAir()) {
                this.h = false;
            }
            else {
                final float float3 = blockState2.calcBlockBreakingDelta(this.player, this.player.world, this.i) * (integer1 + 1);
                final int integer2 = (int)(float3 * 10.0f);
                if (integer2 != this.k) {
                    this.world.setBlockBreakingProgress(this.player.getEntityId(), this.i, integer2);
                    this.k = integer2;
                }
                if (float3 >= 1.0f) {
                    this.h = false;
                    this.tryBreakBlock(this.i);
                }
            }
        }
        else if (this.d) {
            final BlockState blockState3 = this.world.getBlockState(this.f);
            if (blockState3.isAir()) {
                this.world.setBlockBreakingProgress(this.player.getEntityId(), this.f, -1);
                this.k = -1;
                this.d = false;
            }
            else {
                final int integer3 = this.g - this.e;
                final float float3 = blockState3.calcBlockBreakingDelta(this.player, this.player.world, this.i) * (integer3 + 1);
                final int integer2 = (int)(float3 * 10.0f);
                if (integer2 != this.k) {
                    this.world.setBlockBreakingProgress(this.player.getEntityId(), this.f, integer2);
                    this.k = integer2;
                }
            }
        }
    }
    
    public void a(final BlockPos blockPos, final Direction direction) {
        if (this.isCreative()) {
            if (!this.world.a(null, blockPos, direction)) {
                this.tryBreakBlock(blockPos);
            }
            return;
        }
        if (this.gameMode.shouldLimitWorldModification()) {
            if (this.gameMode == GameMode.e) {
                return;
            }
            if (!this.player.canModifyWorld()) {
                final ItemStack itemStack3 = this.player.getMainHandStack();
                if (itemStack3.isEmpty()) {
                    return;
                }
                final CachedBlockPosition cachedBlockPosition4 = new CachedBlockPosition(this.world, blockPos, false);
                if (!itemStack3.getCustomCanHarvest(this.world.getTagManager(), cachedBlockPosition4)) {
                    return;
                }
            }
        }
        this.world.a(null, blockPos, direction);
        this.e = this.g;
        float float3 = 1.0f;
        final BlockState blockState4 = this.world.getBlockState(blockPos);
        if (!blockState4.isAir()) {
            blockState4.onBlockBreakStart(this.world, blockPos, this.player);
            float3 = blockState4.calcBlockBreakingDelta(this.player, this.player.world, blockPos);
        }
        if (!blockState4.isAir() && float3 >= 1.0f) {
            this.tryBreakBlock(blockPos);
        }
        else {
            this.d = true;
            this.f = blockPos;
            final int integer5 = (int)(float3 * 10.0f);
            this.world.setBlockBreakingProgress(this.player.getEntityId(), blockPos, integer5);
            this.player.networkHandler.sendPacket(new BlockUpdateS2CPacket(this.world, blockPos));
            this.k = integer5;
        }
    }
    
    public void a(final BlockPos blockPos) {
        if (blockPos.equals(this.f)) {
            final int integer2 = this.g - this.e;
            final BlockState blockState3 = this.world.getBlockState(blockPos);
            if (!blockState3.isAir()) {
                final float float4 = blockState3.calcBlockBreakingDelta(this.player, this.player.world, blockPos) * (integer2 + 1);
                if (float4 >= 0.7f) {
                    this.d = false;
                    this.world.setBlockBreakingProgress(this.player.getEntityId(), blockPos, -1);
                    this.tryBreakBlock(blockPos);
                }
                else if (!this.h) {
                    this.d = false;
                    this.h = true;
                    this.i = blockPos;
                    this.j = this.e;
                }
            }
        }
    }
    
    public void e() {
        this.d = false;
        this.world.setBlockBreakingProgress(this.player.getEntityId(), this.f, -1);
    }
    
    private boolean destroyBlock(final BlockPos blockPos) {
        final BlockState blockState2 = this.world.getBlockState(blockPos);
        blockState2.getBlock().onBreak(this.world, blockPos, blockState2, this.player);
        final boolean boolean3 = this.world.clearBlockState(blockPos, false);
        if (boolean3) {
            blockState2.getBlock().onBroken(this.world, blockPos, blockState2);
        }
        return boolean3;
    }
    
    public boolean tryBreakBlock(final BlockPos blockPos) {
        final BlockState blockState2 = this.world.getBlockState(blockPos);
        if (!this.player.getMainHandStack().getItem().beforeBlockBreak(blockState2, this.world, blockPos, this.player)) {
            return false;
        }
        final BlockEntity blockEntity3 = this.world.getBlockEntity(blockPos);
        final Block block4 = blockState2.getBlock();
        if ((block4 instanceof CommandBlock || block4 instanceof StructureBlock || block4 instanceof JigsawBlock) && !this.player.isCreativeLevelTwoOp()) {
            this.world.updateListeners(blockPos, blockState2, blockState2, 3);
            return false;
        }
        if (this.gameMode.shouldLimitWorldModification()) {
            if (this.gameMode == GameMode.e) {
                return false;
            }
            if (!this.player.canModifyWorld()) {
                final ItemStack itemStack5 = this.player.getMainHandStack();
                if (itemStack5.isEmpty()) {
                    return false;
                }
                final CachedBlockPosition cachedBlockPosition6 = new CachedBlockPosition(this.world, blockPos, false);
                if (!itemStack5.getCustomCanHarvest(this.world.getTagManager(), cachedBlockPosition6)) {
                    return false;
                }
            }
        }
        final boolean boolean5 = this.destroyBlock(blockPos);
        if (!this.isCreative()) {
            final ItemStack itemStack6 = this.player.getMainHandStack();
            final boolean boolean6 = this.player.isUsingEffectiveTool(blockState2);
            itemStack6.onBlockBroken(this.world, blockState2, blockPos, this.player);
            if (boolean5 && boolean6) {
                final ItemStack itemStack7 = itemStack6.isEmpty() ? ItemStack.EMPTY : itemStack6.copy();
                blockState2.getBlock().afterBreak(this.world, this.player, blockPos, blockState2, blockEntity3, itemStack7);
            }
        }
        return boolean5;
    }
    
    public ActionResult interactItem(final PlayerEntity playerEntity, final World world, final ItemStack itemStack, final Hand hand) {
        if (this.gameMode == GameMode.e) {
            return ActionResult.PASS;
        }
        if (playerEntity.getItemCooldownManager().isCoolingDown(itemStack.getItem())) {
            return ActionResult.PASS;
        }
        final int integer5 = itemStack.getAmount();
        final int integer6 = itemStack.getDamage();
        final TypedActionResult<ItemStack> typedActionResult7 = itemStack.use(world, playerEntity, hand);
        final ItemStack itemStack2 = typedActionResult7.getValue();
        if (itemStack2 == itemStack && itemStack2.getAmount() == integer5 && itemStack2.getMaxUseTime() <= 0 && itemStack2.getDamage() == integer6) {
            return typedActionResult7.getResult();
        }
        if (typedActionResult7.getResult() == ActionResult.c && itemStack2.getMaxUseTime() > 0 && !playerEntity.isUsingItem()) {
            return typedActionResult7.getResult();
        }
        playerEntity.setStackInHand(hand, itemStack2);
        if (this.isCreative()) {
            itemStack2.setAmount(integer5);
            if (itemStack2.hasDurability()) {
                itemStack2.setDamage(integer6);
            }
        }
        if (itemStack2.isEmpty()) {
            playerEntity.setStackInHand(hand, ItemStack.EMPTY);
        }
        if (!playerEntity.isUsingItem()) {
            ((ServerPlayerEntity)playerEntity).openContainer(playerEntity.playerContainer);
        }
        return typedActionResult7.getResult();
    }
    
    public ActionResult interactBlock(final PlayerEntity player, final World world, final ItemStack stack, final Hand hand, final BlockHitResult blockHitResult) {
        final BlockPos blockPos6 = blockHitResult.getBlockPos();
        final BlockState blockState7 = world.getBlockState(blockPos6);
        if (this.gameMode == GameMode.e) {
            final NameableContainerProvider nameableContainerProvider8 = blockState7.createContainerProvider(world, blockPos6);
            if (nameableContainerProvider8 != null) {
                player.openContainer(nameableContainerProvider8);
                return ActionResult.a;
            }
            return ActionResult.PASS;
        }
        else {
            final boolean boolean8 = !player.getMainHandStack().isEmpty() || !player.getOffHandStack().isEmpty();
            final boolean boolean9 = player.isSneaking() && boolean8;
            if (!boolean9 && blockState7.activate(world, player, hand, blockHitResult)) {
                return ActionResult.a;
            }
            if (stack.isEmpty() || player.getItemCooldownManager().isCoolingDown(stack.getItem())) {
                return ActionResult.PASS;
            }
            final ItemUsageContext itemUsageContext10 = new ItemUsageContext(player, hand, blockHitResult);
            if (this.isCreative()) {
                final int integer11 = stack.getAmount();
                final ActionResult actionResult12 = stack.useOnBlock(itemUsageContext10);
                stack.setAmount(integer11);
                return actionResult12;
            }
            return stack.useOnBlock(itemUsageContext10);
        }
    }
    
    public void setWorld(final ServerWorld serverWorld) {
        this.world = serverWorld;
    }
}
