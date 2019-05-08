package net.minecraft.world;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import java.util.Date;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.server.command.ServerCommandSource;
import com.mojang.brigadier.ResultConsumer;
import net.minecraft.util.ChatUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import java.text.SimpleDateFormat;
import net.minecraft.server.command.CommandOutput;

public abstract class CommandBlockExecutor implements CommandOutput
{
    private static final SimpleDateFormat DATE_FORMAT;
    private long lastExecution;
    private boolean updateLastExecution;
    private int successCount;
    private boolean trackOutput;
    private TextComponent lastOutput;
    private String command;
    private TextComponent customName;
    
    public CommandBlockExecutor() {
        this.lastExecution = -1L;
        this.updateLastExecution = true;
        this.trackOutput = true;
        this.command = "";
        this.customName = new StringTextComponent("@");
    }
    
    public int getSuccessCount() {
        return this.successCount;
    }
    
    public void setSuccessCount(final int integer) {
        this.successCount = integer;
    }
    
    public TextComponent getLastOutput() {
        return (this.lastOutput == null) ? new StringTextComponent("") : this.lastOutput;
    }
    
    public CompoundTag serialize(final CompoundTag compoundTag) {
        compoundTag.putString("Command", this.command);
        compoundTag.putInt("SuccessCount", this.successCount);
        compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.customName));
        compoundTag.putBoolean("TrackOutput", this.trackOutput);
        if (this.lastOutput != null && this.trackOutput) {
            compoundTag.putString("LastOutput", TextComponent.Serializer.toJsonString(this.lastOutput));
        }
        compoundTag.putBoolean("UpdateLastExecution", this.updateLastExecution);
        if (this.updateLastExecution && this.lastExecution > 0L) {
            compoundTag.putLong("LastExecution", this.lastExecution);
        }
        return compoundTag;
    }
    
    public void deserialize(final CompoundTag compoundTag) {
        this.command = compoundTag.getString("Command");
        this.successCount = compoundTag.getInt("SuccessCount");
        if (compoundTag.containsKey("CustomName", 8)) {
            this.customName = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
        }
        if (compoundTag.containsKey("TrackOutput", 1)) {
            this.trackOutput = compoundTag.getBoolean("TrackOutput");
        }
        if (compoundTag.containsKey("LastOutput", 8) && this.trackOutput) {
            try {
                this.lastOutput = TextComponent.Serializer.fromJsonString(compoundTag.getString("LastOutput"));
            }
            catch (Throwable throwable2) {
                this.lastOutput = new StringTextComponent(throwable2.getMessage());
            }
        }
        else {
            this.lastOutput = null;
        }
        if (compoundTag.containsKey("UpdateLastExecution")) {
            this.updateLastExecution = compoundTag.getBoolean("UpdateLastExecution");
        }
        if (this.updateLastExecution && compoundTag.containsKey("LastExecution")) {
            this.lastExecution = compoundTag.getLong("LastExecution");
        }
        else {
            this.lastExecution = -1L;
        }
    }
    
    public void setCommand(final String string) {
        this.command = string;
        this.successCount = 0;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public boolean execute(final World world) {
        if (world.isClient || world.getTime() == this.lastExecution) {
            return false;
        }
        if ("Searge".equalsIgnoreCase(this.command)) {
            this.lastOutput = new StringTextComponent("#itzlipofutzli");
            this.successCount = 1;
            return true;
        }
        this.successCount = 0;
        final MinecraftServer minecraftServer2 = this.getWorld().getServer();
        if (minecraftServer2 != null && minecraftServer2.E() && minecraftServer2.areCommandBlocksEnabled() && !ChatUtil.isEmpty(this.command)) {
            try {
                this.lastOutput = null;
                final ServerCommandSource serverCommandSource3 = this.getSource().withConsumer((ResultConsumer<ServerCommandSource>)((commandContext, boolean2, integer) -> {
                    if (boolean2) {
                        ++this.successCount;
                    }
                }));
                minecraftServer2.getCommandManager().execute(serverCommandSource3, this.command);
            }
            catch (Throwable throwable3) {
                final CrashReport crashReport4 = CrashReport.create(throwable3, "Executing command block");
                final CrashReportSection crashReportSection5 = crashReport4.addElement("Command to be executed");
                crashReportSection5.add("Command", this::getCommand);
                crashReportSection5.add("Name", () -> this.getCustomName().getString());
                throw new CrashException(crashReport4);
            }
        }
        if (this.updateLastExecution) {
            this.lastExecution = world.getTime();
        }
        else {
            this.lastExecution = -1L;
        }
        return true;
    }
    
    public TextComponent getCustomName() {
        return this.customName;
    }
    
    public void setCustomName(final TextComponent textComponent) {
        this.customName = textComponent;
    }
    
    @Override
    public void sendMessage(final TextComponent message) {
        if (this.trackOutput) {
            this.lastOutput = new StringTextComponent("[" + CommandBlockExecutor.DATE_FORMAT.format(new Date()) + "] ").append(message);
            this.markDirty();
        }
    }
    
    public abstract ServerWorld getWorld();
    
    public abstract void markDirty();
    
    public void setLastOutput(@Nullable final TextComponent textComponent) {
        this.lastOutput = textComponent;
    }
    
    public void shouldTrackOutput(final boolean boolean1) {
        this.trackOutput = boolean1;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isTrackingOutput() {
        return this.trackOutput;
    }
    
    public boolean interact(final PlayerEntity playerEntity) {
        if (!playerEntity.isCreativeLevelTwoOp()) {
            return false;
        }
        if (playerEntity.getEntityWorld().isClient) {
            playerEntity.openCommandBlockMinecartScreen(this);
        }
        return true;
    }
    
    @Environment(EnvType.CLIENT)
    public abstract Vec3d getPos();
    
    public abstract ServerCommandSource getSource();
    
    @Override
    public boolean sendCommandFeedback() {
        return this.getWorld().getGameRules().getBoolean("sendCommandFeedback") && this.trackOutput;
    }
    
    @Override
    public boolean shouldTrackOutput() {
        return this.trackOutput;
    }
    
    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return this.getWorld().getGameRules().getBoolean("commandBlockOutput");
    }
    
    static {
        DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    }
}
