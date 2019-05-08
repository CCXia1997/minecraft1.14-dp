package net.minecraft.entity.vehicle;

import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.data.TrackedData;

public class CommandBlockMinecartEntity extends AbstractMinecartEntity
{
    private static final TrackedData<String> COMMAND;
    private static final TrackedData<TextComponent> LAST_OUTPUT;
    private final CommandBlockExecutor commandExecutor;
    private int lastExecuted;
    
    public CommandBlockMinecartEntity(final EntityType<? extends CommandBlockMinecartEntity> type, final World world) {
        super(type, world);
        this.commandExecutor = new CommandExecutor();
    }
    
    public CommandBlockMinecartEntity(final World world, final double double2, final double double4, final double double6) {
        super(EntityType.COMMAND_BLOCK_MINECART, world, double2, double4, double6);
        this.commandExecutor = new CommandExecutor();
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().<String>startTracking(CommandBlockMinecartEntity.COMMAND, "");
        this.getDataTracker().<StringTextComponent>startTracking((TrackedData<StringTextComponent>)CommandBlockMinecartEntity.LAST_OUTPUT, new StringTextComponent(""));
    }
    
    @Override
    protected void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.commandExecutor.deserialize(tag);
        this.getDataTracker().<String>set(CommandBlockMinecartEntity.COMMAND, this.getCommandExecutor().getCommand());
        this.getDataTracker().<TextComponent>set(CommandBlockMinecartEntity.LAST_OUTPUT, this.getCommandExecutor().getLastOutput());
    }
    
    @Override
    protected void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        this.commandExecutor.serialize(tag);
    }
    
    @Override
    public Type getMinecartType() {
        return Type.g;
    }
    
    @Override
    public BlockState getDefaultContainedBlock() {
        return Blocks.ej.getDefaultState();
    }
    
    public CommandBlockExecutor getCommandExecutor() {
        return this.commandExecutor;
    }
    
    @Override
    public void onActivatorRail(final int x, final int y, final int z, final boolean boolean4) {
        if (boolean4 && this.age - this.lastExecuted >= 4) {
            this.getCommandExecutor().execute(this.world);
            this.lastExecuted = this.age;
        }
    }
    
    @Override
    public boolean interact(final PlayerEntity player, final Hand hand) {
        this.commandExecutor.interact(player);
        return true;
    }
    
    @Override
    public void onTrackedDataSet(final TrackedData<?> data) {
        super.onTrackedDataSet(data);
        if (CommandBlockMinecartEntity.LAST_OUTPUT.equals(data)) {
            try {
                this.commandExecutor.setLastOutput(this.getDataTracker().<TextComponent>get(CommandBlockMinecartEntity.LAST_OUTPUT));
            }
            catch (Throwable t) {}
        }
        else if (CommandBlockMinecartEntity.COMMAND.equals(data)) {
            this.commandExecutor.setCommand(this.getDataTracker().<String>get(CommandBlockMinecartEntity.COMMAND));
        }
    }
    
    @Override
    public boolean bS() {
        return true;
    }
    
    static {
        COMMAND = DataTracker.<String>registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.STRING);
        LAST_OUTPUT = DataTracker.<TextComponent>registerData(CommandBlockMinecartEntity.class, TrackedDataHandlerRegistry.TEXT_COMPONENT);
    }
    
    public class CommandExecutor extends CommandBlockExecutor
    {
        @Override
        public ServerWorld getWorld() {
            return (ServerWorld)CommandBlockMinecartEntity.this.world;
        }
        
        @Override
        public void markDirty() {
            CommandBlockMinecartEntity.this.getDataTracker().<String>set(CommandBlockMinecartEntity.COMMAND, this.getCommand());
            CommandBlockMinecartEntity.this.getDataTracker().<TextComponent>set(CommandBlockMinecartEntity.LAST_OUTPUT, this.getLastOutput());
        }
        
        @Environment(EnvType.CLIENT)
        @Override
        public Vec3d getPos() {
            return new Vec3d(CommandBlockMinecartEntity.this.x, CommandBlockMinecartEntity.this.y, CommandBlockMinecartEntity.this.z);
        }
        
        @Environment(EnvType.CLIENT)
        public CommandBlockMinecartEntity getMinecart() {
            return CommandBlockMinecartEntity.this;
        }
        
        @Override
        public ServerCommandSource getSource() {
            return new ServerCommandSource(this, new Vec3d(CommandBlockMinecartEntity.this.x, CommandBlockMinecartEntity.this.y, CommandBlockMinecartEntity.this.z), CommandBlockMinecartEntity.this.getRotationClient(), this.getWorld(), 2, this.getCustomName().getString(), CommandBlockMinecartEntity.this.getDisplayName(), this.getWorld().getServer(), CommandBlockMinecartEntity.this);
        }
    }
}
