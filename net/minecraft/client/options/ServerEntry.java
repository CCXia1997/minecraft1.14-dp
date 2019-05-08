package net.minecraft.client.options;

import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.SharedConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ServerEntry
{
    public String name;
    public String address;
    public String playerCountLabel;
    public String label;
    public long ping;
    public int protocolVersion;
    public String version;
    public boolean online;
    public String playerListSummary;
    private ResourcePackState resourcePackState;
    private String icon;
    private boolean local;
    
    public ServerEntry(final String name, final String address, final boolean local) {
        this.protocolVersion = SharedConstants.getGameVersion().getProtocolVersion();
        this.version = SharedConstants.getGameVersion().getName();
        this.resourcePackState = ResourcePackState.PROMPT;
        this.name = name;
        this.address = address;
        this.local = local;
    }
    
    public CompoundTag serialize() {
        final CompoundTag compoundTag1 = new CompoundTag();
        compoundTag1.putString("name", this.name);
        compoundTag1.putString("ip", this.address);
        if (this.icon != null) {
            compoundTag1.putString("icon", this.icon);
        }
        if (this.resourcePackState == ResourcePackState.ENABLED) {
            compoundTag1.putBoolean("acceptTextures", true);
        }
        else if (this.resourcePackState == ResourcePackState.DISABLED) {
            compoundTag1.putBoolean("acceptTextures", false);
        }
        return compoundTag1;
    }
    
    public ResourcePackState getResourcePack() {
        return this.resourcePackState;
    }
    
    public void setResourcePackState(final ResourcePackState resourcePackState) {
        this.resourcePackState = resourcePackState;
    }
    
    public static ServerEntry deserialize(final CompoundTag tag) {
        final ServerEntry serverEntry2 = new ServerEntry(tag.getString("name"), tag.getString("ip"), false);
        if (tag.containsKey("icon", 8)) {
            serverEntry2.setIcon(tag.getString("icon"));
        }
        if (tag.containsKey("acceptTextures", 1)) {
            if (tag.getBoolean("acceptTextures")) {
                serverEntry2.setResourcePackState(ResourcePackState.ENABLED);
            }
            else {
                serverEntry2.setResourcePackState(ResourcePackState.DISABLED);
            }
        }
        else {
            serverEntry2.setResourcePackState(ResourcePackState.PROMPT);
        }
        return serverEntry2;
    }
    
    @Nullable
    public String getIcon() {
        return this.icon;
    }
    
    public void setIcon(@Nullable final String string) {
        this.icon = string;
    }
    
    public boolean isLocal() {
        return this.local;
    }
    
    public void copyFrom(final ServerEntry serverEntry) {
        this.address = serverEntry.address;
        this.name = serverEntry.name;
        this.setResourcePackState(serverEntry.getResourcePack());
        this.icon = serverEntry.icon;
        this.local = serverEntry.local;
    }
    
    @Environment(EnvType.CLIENT)
    public enum ResourcePackState
    {
        ENABLED("enabled"), 
        DISABLED("disabled"), 
        PROMPT("prompt");
        
        private final TextComponent component;
        
        private ResourcePackState(final String string1) {
            this.component = new TranslatableTextComponent("addServer.resourcePack." + string1, new Object[0]);
        }
        
        public TextComponent getComponent() {
            return this.component;
        }
    }
}
