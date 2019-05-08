package net.minecraft.entity.boss;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import java.util.Collection;
import net.minecraft.text.TextComponent;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.minecraft.server.MinecraftServer;

public class BossBarManager
{
    private final MinecraftServer server;
    private final Map<Identifier, CommandBossBar> bossBars;
    
    public BossBarManager(final MinecraftServer minecraftServer) {
        this.bossBars = Maps.newHashMap();
        this.server = minecraftServer;
    }
    
    @Nullable
    public CommandBossBar get(final Identifier identifier) {
        return this.bossBars.get(identifier);
    }
    
    public CommandBossBar add(final Identifier identifier, final TextComponent textComponent) {
        final CommandBossBar commandBossBar3 = new CommandBossBar(identifier, textComponent);
        this.bossBars.put(identifier, commandBossBar3);
        return commandBossBar3;
    }
    
    public void remove(final CommandBossBar commandBossBar) {
        this.bossBars.remove(commandBossBar.getId());
    }
    
    public Collection<Identifier> getIds() {
        return this.bossBars.keySet();
    }
    
    public Collection<CommandBossBar> getAll() {
        return this.bossBars.values();
    }
    
    public CompoundTag toTag() {
        final CompoundTag compoundTag1 = new CompoundTag();
        for (final CommandBossBar commandBossBar3 : this.bossBars.values()) {
            compoundTag1.put(commandBossBar3.getId().toString(), commandBossBar3.toTag());
        }
        return compoundTag1;
    }
    
    public void fromTag(final CompoundTag compoundTag) {
        for (final String string3 : compoundTag.getKeys()) {
            final Identifier identifier4 = new Identifier(string3);
            this.bossBars.put(identifier4, CommandBossBar.fromTag(compoundTag.getCompound(string3), identifier4));
        }
    }
    
    public void a(final ServerPlayerEntity serverPlayerEntity) {
        for (final CommandBossBar commandBossBar3 : this.bossBars.values()) {
            commandBossBar3.c(serverPlayerEntity);
        }
    }
    
    public void b(final ServerPlayerEntity serverPlayerEntity) {
        for (final CommandBossBar commandBossBar3 : this.bossBars.values()) {
            commandBossBar3.d(serverPlayerEntity);
        }
    }
}
