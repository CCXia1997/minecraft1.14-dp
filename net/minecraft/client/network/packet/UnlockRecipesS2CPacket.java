package net.minecraft.client.network.packet;

import net.minecraft.network.listener.PacketListener;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import java.io.IOException;
import com.google.common.collect.Lists;
import net.minecraft.util.PacketByteBuf;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import net.minecraft.util.Identifier;
import java.util.List;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.Packet;

public class UnlockRecipesS2CPacket implements Packet<ClientPlayPacketListener>
{
    private Action action;
    private List<Identifier> recipeIdsToChange;
    private List<Identifier> recipeIdsToInit;
    private boolean guiOpen;
    private boolean filteringCraftable;
    private boolean furnaceGuiOpen;
    private boolean furnaceFilteringCraftable;
    
    public UnlockRecipesS2CPacket() {
    }
    
    public UnlockRecipesS2CPacket(final Action action, final Collection<Identifier> collection2, final Collection<Identifier> collection3, final boolean boolean4, final boolean boolean5, final boolean boolean6, final boolean boolean7) {
        this.action = action;
        this.recipeIdsToChange = ImmutableList.copyOf(collection2);
        this.recipeIdsToInit = ImmutableList.copyOf(collection3);
        this.guiOpen = boolean4;
        this.filteringCraftable = boolean5;
        this.furnaceGuiOpen = boolean6;
        this.furnaceFilteringCraftable = boolean7;
    }
    
    @Override
    public void apply(final ClientPlayPacketListener listener) {
        listener.onUnlockRecipes(this);
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.action = buf.<Action>readEnumConstant(Action.class);
        this.guiOpen = buf.readBoolean();
        this.filteringCraftable = buf.readBoolean();
        this.furnaceGuiOpen = buf.readBoolean();
        this.furnaceFilteringCraftable = buf.readBoolean();
        int integer2 = buf.readVarInt();
        this.recipeIdsToChange = Lists.newArrayList();
        for (int integer3 = 0; integer3 < integer2; ++integer3) {
            this.recipeIdsToChange.add(buf.readIdentifier());
        }
        if (this.action == Action.a) {
            integer2 = buf.readVarInt();
            this.recipeIdsToInit = Lists.newArrayList();
            for (int integer3 = 0; integer3 < integer2; ++integer3) {
                this.recipeIdsToInit.add(buf.readIdentifier());
            }
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.action);
        buf.writeBoolean(this.guiOpen);
        buf.writeBoolean(this.filteringCraftable);
        buf.writeBoolean(this.furnaceGuiOpen);
        buf.writeBoolean(this.furnaceFilteringCraftable);
        buf.writeVarInt(this.recipeIdsToChange.size());
        for (final Identifier identifier3 : this.recipeIdsToChange) {
            buf.writeIdentifier(identifier3);
        }
        if (this.action == Action.a) {
            buf.writeVarInt(this.recipeIdsToInit.size());
            for (final Identifier identifier3 : this.recipeIdsToInit) {
                buf.writeIdentifier(identifier3);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    public List<Identifier> getRecipeIdsToChange() {
        return this.recipeIdsToChange;
    }
    
    @Environment(EnvType.CLIENT)
    public List<Identifier> getRecipeIdsToInit() {
        return this.recipeIdsToInit;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isGuiOpen() {
        return this.guiOpen;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isFilteringCraftable() {
        return this.filteringCraftable;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isFurnaceGuiOpen() {
        return this.furnaceGuiOpen;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isFurnaceFilteringCraftable() {
        return this.furnaceFilteringCraftable;
    }
    
    @Environment(EnvType.CLIENT)
    public Action getAction() {
        return this.action;
    }
    
    public enum Action
    {
        a, 
        b, 
        c;
    }
}
