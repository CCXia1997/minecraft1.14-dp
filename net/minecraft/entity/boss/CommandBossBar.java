package net.minecraft.entity.boss;

import java.util.AbstractList;
import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import java.util.Iterator;
import java.util.Collection;
import net.minecraft.text.Style;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.text.TextFormatter;
import net.minecraft.util.math.MathHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import com.google.common.collect.Sets;
import net.minecraft.text.TextComponent;
import java.util.UUID;
import java.util.Set;
import net.minecraft.util.Identifier;

public class CommandBossBar extends ServerBossBar
{
    private final Identifier id;
    private final Set<UUID> playerUuids;
    private int value;
    private int maxValue;
    
    public CommandBossBar(final Identifier identifier, final TextComponent textComponent) {
        super(textComponent, Color.g, Style.a);
        this.playerUuids = Sets.newHashSet();
        this.maxValue = 100;
        this.id = identifier;
        this.setPercent(0.0f);
    }
    
    public Identifier getId() {
        return this.id;
    }
    
    @Override
    public void addPlayer(final ServerPlayerEntity serverPlayerEntity) {
        super.addPlayer(serverPlayerEntity);
        this.playerUuids.add(serverPlayerEntity.getUuid());
    }
    
    public void addPlayer(final UUID uUID) {
        this.playerUuids.add(uUID);
    }
    
    @Override
    public void removePlayer(final ServerPlayerEntity serverPlayerEntity) {
        super.removePlayer(serverPlayerEntity);
        this.playerUuids.remove(serverPlayerEntity.getUuid());
    }
    
    @Override
    public void clearPlayers() {
        super.clearPlayers();
        this.playerUuids.clear();
    }
    
    public int getValue() {
        return this.value;
    }
    
    public int getMaxValue() {
        return this.maxValue;
    }
    
    public void setValue(final int integer) {
        this.value = integer;
        this.setPercent(MathHelper.clamp(integer / (float)this.maxValue, 0.0f, 1.0f));
    }
    
    public void setMaxValue(final int integer) {
        this.maxValue = integer;
        this.setPercent(MathHelper.clamp(this.value / (float)integer, 0.0f, 1.0f));
    }
    
    public final TextComponent getTextComponent() {
        final HoverEvent hoverEvent;
        final net.minecraft.text.Style style2;
        return TextFormatter.bracketed(this.getName()).modifyStyle(style -> {
            style.setColor(this.getColor().getTextFormat());
            new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.getId().toString()));
            style2.setHoverEvent(hoverEvent).setInsertion(this.getId().toString());
        });
    }
    
    public boolean addPlayers(final Collection<ServerPlayerEntity> collection) {
        final Set<UUID> set2 = Sets.newHashSet();
        final Set<ServerPlayerEntity> set3 = Sets.newHashSet();
        for (final UUID uUID5 : this.playerUuids) {
            boolean boolean6 = false;
            for (final ServerPlayerEntity serverPlayerEntity8 : collection) {
                if (serverPlayerEntity8.getUuid().equals(uUID5)) {
                    boolean6 = true;
                    break;
                }
            }
            if (!boolean6) {
                set2.add(uUID5);
            }
        }
        for (final ServerPlayerEntity serverPlayerEntity9 : collection) {
            boolean boolean6 = false;
            for (final UUID uUID6 : this.playerUuids) {
                if (serverPlayerEntity9.getUuid().equals(uUID6)) {
                    boolean6 = true;
                    break;
                }
            }
            if (!boolean6) {
                set3.add(serverPlayerEntity9);
            }
        }
        for (final UUID uUID5 : set2) {
            for (final ServerPlayerEntity serverPlayerEntity10 : this.getPlayers()) {
                if (serverPlayerEntity10.getUuid().equals(uUID5)) {
                    this.removePlayer(serverPlayerEntity10);
                    break;
                }
            }
            this.playerUuids.remove(uUID5);
        }
        for (final ServerPlayerEntity serverPlayerEntity9 : set3) {
            this.addPlayer(serverPlayerEntity9);
        }
        return !set2.isEmpty() || !set3.isEmpty();
    }
    
    public CompoundTag toTag() {
        final CompoundTag compoundTag1 = new CompoundTag();
        compoundTag1.putString("Name", TextComponent.Serializer.toJsonString(this.name));
        compoundTag1.putBoolean("Visible", this.isVisible());
        compoundTag1.putInt("Value", this.value);
        compoundTag1.putInt("Max", this.maxValue);
        compoundTag1.putString("Color", this.getColor().getName());
        compoundTag1.putString("Overlay", this.getOverlay().getName());
        compoundTag1.putBoolean("DarkenScreen", this.getDarkenSky());
        compoundTag1.putBoolean("PlayBossMusic", this.hasDragonMusic());
        compoundTag1.putBoolean("CreateWorldFog", this.getThickenFog());
        final ListTag listTag2 = new ListTag();
        for (final UUID uUID4 : this.playerUuids) {
            ((AbstractList<CompoundTag>)listTag2).add(TagHelper.serializeUuid(uUID4));
        }
        compoundTag1.put("Players", listTag2);
        return compoundTag1;
    }
    
    public static CommandBossBar fromTag(final CompoundTag compoundTag, final Identifier identifier) {
        final CommandBossBar commandBossBar3 = new CommandBossBar(identifier, TextComponent.Serializer.fromJsonString(compoundTag.getString("Name")));
        commandBossBar3.setVisible(compoundTag.getBoolean("Visible"));
        commandBossBar3.setValue(compoundTag.getInt("Value"));
        commandBossBar3.setMaxValue(compoundTag.getInt("Max"));
        commandBossBar3.setColor(Color.byName(compoundTag.getString("Color")));
        commandBossBar3.setOverlay(Style.byName(compoundTag.getString("Overlay")));
        commandBossBar3.setDarkenSky(compoundTag.getBoolean("DarkenScreen"));
        commandBossBar3.setDragonMusic(compoundTag.getBoolean("PlayBossMusic"));
        commandBossBar3.setThickenFog(compoundTag.getBoolean("CreateWorldFog"));
        final ListTag listTag4 = compoundTag.getList("Players", 10);
        for (int integer5 = 0; integer5 < listTag4.size(); ++integer5) {
            commandBossBar3.addPlayer(TagHelper.deserializeUuid(listTag4.getCompoundTag(integer5)));
        }
        return commandBossBar3;
    }
    
    public void c(final ServerPlayerEntity serverPlayerEntity) {
        if (this.playerUuids.contains(serverPlayerEntity.getUuid())) {
            this.addPlayer(serverPlayerEntity);
        }
    }
    
    public void d(final ServerPlayerEntity serverPlayerEntity) {
        super.removePlayer(serverPlayerEntity);
    }
}
