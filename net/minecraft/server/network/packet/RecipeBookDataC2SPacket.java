package net.minecraft.server.network.packet;

import net.minecraft.network.listener.PacketListener;
import java.io.IOException;
import net.minecraft.util.PacketByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.Packet;

public class RecipeBookDataC2SPacket implements Packet<ServerPlayPacketListener>
{
    private Mode mode;
    private Identifier recipeId;
    private boolean guiOpen;
    private boolean filteringCraftable;
    private boolean furnaceGuiOpen;
    private boolean furnaceFilteringCraftable;
    private boolean blastFurnaceGuiOpen;
    private boolean blastFurnaceFilteringCraftable;
    private boolean smokerGuiOpen;
    private boolean smokerGuiFilteringCraftable;
    
    public RecipeBookDataC2SPacket() {
    }
    
    public RecipeBookDataC2SPacket(final Recipe<?> recipe) {
        this.mode = Mode.a;
        this.recipeId = recipe.getId();
    }
    
    @Environment(EnvType.CLIENT)
    public RecipeBookDataC2SPacket(final boolean boolean1, final boolean boolean2, final boolean boolean3, final boolean boolean4, final boolean boolean5, final boolean boolean6) {
        this.mode = Mode.b;
        this.guiOpen = boolean1;
        this.filteringCraftable = boolean2;
        this.furnaceGuiOpen = boolean3;
        this.furnaceFilteringCraftable = boolean4;
        this.blastFurnaceGuiOpen = boolean5;
        this.blastFurnaceFilteringCraftable = boolean6;
        this.smokerGuiOpen = boolean5;
        this.smokerGuiFilteringCraftable = boolean6;
    }
    
    @Override
    public void read(final PacketByteBuf buf) throws IOException {
        this.mode = buf.<Mode>readEnumConstant(Mode.class);
        if (this.mode == Mode.a) {
            this.recipeId = buf.readIdentifier();
        }
        else if (this.mode == Mode.b) {
            this.guiOpen = buf.readBoolean();
            this.filteringCraftable = buf.readBoolean();
            this.furnaceGuiOpen = buf.readBoolean();
            this.furnaceFilteringCraftable = buf.readBoolean();
            this.blastFurnaceGuiOpen = buf.readBoolean();
            this.blastFurnaceFilteringCraftable = buf.readBoolean();
            this.smokerGuiOpen = buf.readBoolean();
            this.smokerGuiFilteringCraftable = buf.readBoolean();
        }
    }
    
    @Override
    public void write(final PacketByteBuf buf) throws IOException {
        buf.writeEnumConstant(this.mode);
        if (this.mode == Mode.a) {
            buf.writeIdentifier(this.recipeId);
        }
        else if (this.mode == Mode.b) {
            buf.writeBoolean(this.guiOpen);
            buf.writeBoolean(this.filteringCraftable);
            buf.writeBoolean(this.furnaceGuiOpen);
            buf.writeBoolean(this.furnaceFilteringCraftable);
            buf.writeBoolean(this.blastFurnaceGuiOpen);
            buf.writeBoolean(this.blastFurnaceFilteringCraftable);
            buf.writeBoolean(this.smokerGuiOpen);
            buf.writeBoolean(this.smokerGuiFilteringCraftable);
        }
    }
    
    @Override
    public void apply(final ServerPlayPacketListener listener) {
        listener.onRecipeBookData(this);
    }
    
    public Mode getMode() {
        return this.mode;
    }
    
    public Identifier getRecipeId() {
        return this.recipeId;
    }
    
    public boolean isGuiOpen() {
        return this.guiOpen;
    }
    
    public boolean isFilteringCraftable() {
        return this.filteringCraftable;
    }
    
    public boolean isFurnaceGuiOpen() {
        return this.furnaceGuiOpen;
    }
    
    public boolean isFurnaceFilteringCraftable() {
        return this.furnaceFilteringCraftable;
    }
    
    public boolean isBlastFurnaceGuiOpen() {
        return this.blastFurnaceGuiOpen;
    }
    
    public boolean isBlastFurnaceFilteringCraftable() {
        return this.blastFurnaceFilteringCraftable;
    }
    
    public boolean isSmokerGuiOpen() {
        return this.smokerGuiOpen;
    }
    
    public boolean isSmokerGuiFilteringCraftable() {
        return this.smokerGuiFilteringCraftable;
    }
    
    public enum Mode
    {
        a, 
        b;
    }
}
