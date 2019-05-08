package net.minecraft.block;

import net.minecraft.state.AbstractPropertyContainer;
import net.minecraft.state.property.Properties;
import org.apache.logging.log4j.LogManager;
import net.minecraft.world.GameRules;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.util.ChatUtil;
import java.util.Random;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.BlockView;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import org.apache.logging.log4j.Logger;

public class CommandBlock extends BlockWithEntity
{
    private static final Logger LOGGER;
    public static final DirectionProperty FACING;
    public static final BooleanProperty CONDITIONAL;
    
    public CommandBlock(final Settings settings) {
        super(settings);
        this.setDefaultState((((AbstractPropertyContainer<O, BlockState>)this.stateFactory.getDefaultState()).with((Property<Comparable>)CommandBlock.FACING, Direction.NORTH)).<Comparable, Boolean>with((Property<Comparable>)CommandBlock.CONDITIONAL, false));
    }
    
    @Override
    public BlockEntity createBlockEntity(final BlockView view) {
        final CommandBlockBlockEntity commandBlockBlockEntity2 = new CommandBlockBlockEntity();
        commandBlockBlockEntity2.setAuto(this == Blocks.iz);
        return commandBlockBlockEntity2;
    }
    
    @Override
    public void neighborUpdate(final BlockState state, final World world, final BlockPos pos, final Block block, final BlockPos neighborPos, final boolean boolean6) {
        if (world.isClient) {
            return;
        }
        final BlockEntity blockEntity7 = world.getBlockEntity(pos);
        if (!(blockEntity7 instanceof CommandBlockBlockEntity)) {
            return;
        }
        final CommandBlockBlockEntity commandBlockBlockEntity8 = (CommandBlockBlockEntity)blockEntity7;
        final boolean boolean7 = world.isReceivingRedstonePower(pos);
        final boolean boolean8 = commandBlockBlockEntity8.isPowered();
        commandBlockBlockEntity8.setPowered(boolean7);
        if (boolean8 || commandBlockBlockEntity8.isAuto() || commandBlockBlockEntity8.getType() == CommandBlockBlockEntity.Type.a) {
            return;
        }
        if (boolean7) {
            commandBlockBlockEntity8.updateConditionMet();
            world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
        }
    }
    
    @Override
    public void onScheduledTick(final BlockState state, final World world, final BlockPos pos, final Random random) {
        if (world.isClient) {
            return;
        }
        final BlockEntity blockEntity5 = world.getBlockEntity(pos);
        if (blockEntity5 instanceof CommandBlockBlockEntity) {
            final CommandBlockBlockEntity commandBlockBlockEntity6 = (CommandBlockBlockEntity)blockEntity5;
            final CommandBlockExecutor commandBlockExecutor7 = commandBlockBlockEntity6.getCommandExecutor();
            final boolean boolean8 = !ChatUtil.isEmpty(commandBlockExecutor7.getCommand());
            final CommandBlockBlockEntity.Type type9 = commandBlockBlockEntity6.getType();
            final boolean boolean9 = commandBlockBlockEntity6.isConditionMet();
            if (type9 == CommandBlockBlockEntity.Type.b) {
                commandBlockBlockEntity6.updateConditionMet();
                if (boolean9) {
                    this.execute(state, world, pos, commandBlockExecutor7, boolean8);
                }
                else if (commandBlockBlockEntity6.isConditionalCommandBlock()) {
                    commandBlockExecutor7.setSuccessCount(0);
                }
                if (commandBlockBlockEntity6.isPowered() || commandBlockBlockEntity6.isAuto()) {
                    world.getBlockTickScheduler().schedule(pos, this, this.getTickRate(world));
                }
            }
            else if (type9 == CommandBlockBlockEntity.Type.c) {
                if (boolean9) {
                    this.execute(state, world, pos, commandBlockExecutor7, boolean8);
                }
                else if (commandBlockBlockEntity6.isConditionalCommandBlock()) {
                    commandBlockExecutor7.setSuccessCount(0);
                }
            }
            world.updateHorizontalAdjacent(pos, this);
        }
    }
    
    private void execute(final BlockState state, final World world, final BlockPos pos, final CommandBlockExecutor executor, final boolean hasCommand) {
        if (hasCommand) {
            executor.execute(world);
        }
        else {
            executor.setSuccessCount(0);
        }
        executeCommandChain(world, pos, state.<Direction>get((Property<Direction>)CommandBlock.FACING));
    }
    
    @Override
    public int getTickRate(final ViewableWorld world) {
        return 1;
    }
    
    @Override
    public boolean activate(final BlockState state, final World world, final BlockPos pos, final PlayerEntity player, final Hand hand, final BlockHitResult blockHitResult) {
        final BlockEntity blockEntity7 = world.getBlockEntity(pos);
        if (blockEntity7 instanceof CommandBlockBlockEntity && player.isCreativeLevelTwoOp()) {
            player.openCommandBlockScreen((CommandBlockBlockEntity)blockEntity7);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean hasComparatorOutput(final BlockState state) {
        return true;
    }
    
    @Override
    public int getComparatorOutput(final BlockState state, final World world, final BlockPos pos) {
        final BlockEntity blockEntity4 = world.getBlockEntity(pos);
        if (blockEntity4 instanceof CommandBlockBlockEntity) {
            return ((CommandBlockBlockEntity)blockEntity4).getCommandExecutor().getSuccessCount();
        }
        return 0;
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, final LivingEntity placer, final ItemStack itemStack) {
        final BlockEntity blockEntity6 = world.getBlockEntity(pos);
        if (!(blockEntity6 instanceof CommandBlockBlockEntity)) {
            return;
        }
        final CommandBlockBlockEntity commandBlockBlockEntity7 = (CommandBlockBlockEntity)blockEntity6;
        final CommandBlockExecutor commandBlockExecutor8 = commandBlockBlockEntity7.getCommandExecutor();
        if (itemStack.hasDisplayName()) {
            commandBlockExecutor8.setCustomName(itemStack.getDisplayName());
        }
        if (!world.isClient) {
            if (itemStack.getSubCompoundTag("BlockEntityTag") == null) {
                commandBlockExecutor8.shouldTrackOutput(world.getGameRules().getBoolean("sendCommandFeedback"));
                commandBlockBlockEntity7.setAuto(this == Blocks.iz);
            }
            if (commandBlockBlockEntity7.getType() == CommandBlockBlockEntity.Type.a) {
                final boolean boolean9 = world.isReceivingRedstonePower(pos);
                commandBlockBlockEntity7.setPowered(boolean9);
            }
        }
    }
    
    @Override
    public BlockRenderType getRenderType(final BlockState state) {
        return BlockRenderType.c;
    }
    
    @Override
    public BlockState rotate(final BlockState state, final BlockRotation rotation) {
        return ((AbstractPropertyContainer<O, BlockState>)state).<Comparable, Direction>with((Property<Comparable>)CommandBlock.FACING, rotation.rotate(state.<Direction>get((Property<Direction>)CommandBlock.FACING)));
    }
    
    @Override
    public BlockState mirror(final BlockState state, final BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.<Direction>get((Property<Direction>)CommandBlock.FACING)));
    }
    
    @Override
    protected void appendProperties(final StateFactory.Builder<Block, BlockState> builder) {
        builder.add(CommandBlock.FACING, CommandBlock.CONDITIONAL);
    }
    
    @Override
    public BlockState getPlacementState(final ItemPlacementContext ctx) {
        return ((AbstractPropertyContainer<O, BlockState>)this.getDefaultState()).<Comparable, Direction>with((Property<Comparable>)CommandBlock.FACING, ctx.getPlayerFacing().getOpposite());
    }
    
    private static void executeCommandChain(final World world, final BlockPos pos, Direction facing) {
        final BlockPos.Mutable mutable4 = new BlockPos.Mutable(pos);
        final GameRules gameRules5 = world.getGameRules();
        int integer6 = gameRules5.getInteger("maxCommandChainLength");
        while (integer6-- > 0) {
            mutable4.setOffset(facing);
            final BlockState blockState7 = world.getBlockState(mutable4);
            final Block block8 = blockState7.getBlock();
            if (block8 != Blocks.iz) {
                break;
            }
            final BlockEntity blockEntity9 = world.getBlockEntity(mutable4);
            if (!(blockEntity9 instanceof CommandBlockBlockEntity)) {
                break;
            }
            final CommandBlockBlockEntity commandBlockBlockEntity10 = (CommandBlockBlockEntity)blockEntity9;
            if (commandBlockBlockEntity10.getType() != CommandBlockBlockEntity.Type.a) {
                break;
            }
            if (commandBlockBlockEntity10.isPowered() || commandBlockBlockEntity10.isAuto()) {
                final CommandBlockExecutor commandBlockExecutor11 = commandBlockBlockEntity10.getCommandExecutor();
                if (commandBlockBlockEntity10.updateConditionMet()) {
                    if (!commandBlockExecutor11.execute(world)) {
                        break;
                    }
                    world.updateHorizontalAdjacent(mutable4, block8);
                }
                else if (commandBlockBlockEntity10.isConditionalCommandBlock()) {
                    commandBlockExecutor11.setSuccessCount(0);
                }
            }
            facing = blockState7.<Direction>get((Property<Direction>)CommandBlock.FACING);
        }
        if (integer6 <= 0) {
            final int integer7 = Math.max(gameRules5.getInteger("maxCommandChainLength"), 0);
            CommandBlock.LOGGER.warn("Command Block chain tried to execute more than {} steps!", integer7);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        FACING = FacingBlock.FACING;
        CONDITIONAL = Properties.CONDITIONAL;
    }
}
