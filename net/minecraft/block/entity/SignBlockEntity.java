package net.minecraft.block.entity;

import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import javax.annotation.Nullable;
import java.util.function.Function;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.text.TextFormatter;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.entity.player.PlayerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextComponent;

public class SignBlockEntity extends BlockEntity
{
    public final TextComponent[] text;
    @Environment(EnvType.CLIENT)
    private boolean caretVisible;
    private int currentRow;
    private int selectionStart;
    private int selectionEnd;
    private boolean editable;
    private PlayerEntity editor;
    private final String[] textBeingEdited;
    private DyeColor textColor;
    
    public SignBlockEntity() {
        super(BlockEntityType.SIGN);
        this.text = new TextComponent[] { new StringTextComponent(""), new StringTextComponent(""), new StringTextComponent(""), new StringTextComponent("") };
        this.currentRow = -1;
        this.selectionStart = -1;
        this.selectionEnd = -1;
        this.editable = true;
        this.textBeingEdited = new String[4];
        this.textColor = DyeColor.BLACK;
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        for (int integer2 = 0; integer2 < 4; ++integer2) {
            final String string3 = TextComponent.Serializer.toJsonString(this.text[integer2]);
            compoundTag.putString("Text" + (integer2 + 1), string3);
        }
        compoundTag.putString("Color", this.textColor.getName());
        return compoundTag;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        this.editable = false;
        super.fromTag(compoundTag);
        this.textColor = DyeColor.byName(compoundTag.getString("Color"), DyeColor.BLACK);
        for (int integer2 = 0; integer2 < 4; ++integer2) {
            final String string3 = compoundTag.getString("Text" + (integer2 + 1));
            final TextComponent textComponent4 = TextComponent.Serializer.fromJsonString(string3);
            if (this.world instanceof ServerWorld) {
                try {
                    this.text[integer2] = TextFormatter.resolveAndStyle(this.getCommandSource(null), textComponent4, null);
                }
                catch (CommandSyntaxException commandSyntaxException5) {
                    this.text[integer2] = textComponent4;
                }
            }
            else {
                this.text[integer2] = textComponent4;
            }
            this.textBeingEdited[integer2] = null;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getTextOnRow(final int row) {
        return this.text[row];
    }
    
    public void setTextOnRow(final int row, final TextComponent text) {
        this.text[row] = text;
        this.textBeingEdited[row] = null;
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public String getTextBeingEditedOnRow(final int row, final Function<TextComponent, String> function) {
        if (this.textBeingEdited[row] == null && this.text[row] != null) {
            this.textBeingEdited[row] = function.apply(this.text[row]);
        }
        return this.textBeingEdited[row];
    }
    
    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 9, this.toInitialChunkDataTag());
    }
    
    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }
    
    @Override
    public boolean shouldNotCopyTagFromItem() {
        return true;
    }
    
    public boolean isEditable() {
        return this.editable;
    }
    
    @Environment(EnvType.CLIENT)
    public void setEditable(final boolean boolean1) {
        if (!(this.editable = boolean1)) {
            this.editor = null;
        }
    }
    
    public void setEditor(final PlayerEntity playerEntity) {
        this.editor = playerEntity;
    }
    
    public PlayerEntity getEditor() {
        return this.editor;
    }
    
    public boolean onActivate(final PlayerEntity playerEntity) {
        for (final TextComponent textComponent5 : this.text) {
            final Style style6 = (textComponent5 == null) ? null : textComponent5.getStyle();
            if (style6 != null) {
                if (style6.getClickEvent() != null) {
                    final ClickEvent clickEvent7 = style6.getClickEvent();
                    if (clickEvent7.getAction() == ClickEvent.Action.RUN_COMMAND) {
                        playerEntity.getServer().getCommandManager().execute(this.getCommandSource((ServerPlayerEntity)playerEntity), clickEvent7.getValue());
                    }
                }
            }
        }
        return true;
    }
    
    public ServerCommandSource getCommandSource(@Nullable final ServerPlayerEntity player) {
        final String string2 = (player == null) ? "Sign" : player.getName().getString();
        final TextComponent textComponent3 = (player == null) ? new StringTextComponent("Sign") : player.getDisplayName();
        return new ServerCommandSource(CommandOutput.DUMMY, new Vec3d(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5), Vec2f.ZERO, (ServerWorld)this.world, 2, string2, textComponent3, this.world.getServer(), player);
    }
    
    public DyeColor getTextColor() {
        return this.textColor;
    }
    
    public boolean setTextColor(final DyeColor value) {
        if (value != this.getTextColor()) {
            this.textColor = value;
            this.markDirty();
            this.world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
            return true;
        }
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    public void setSelectionState(final int currentRow, final int selectionStart, final int selectionEnd, final boolean caretVisible) {
        this.currentRow = currentRow;
        this.selectionStart = selectionStart;
        this.selectionEnd = selectionEnd;
        this.caretVisible = caretVisible;
    }
    
    @Environment(EnvType.CLIENT)
    public void resetSelectionState() {
        this.currentRow = -1;
        this.selectionStart = -1;
        this.selectionEnd = -1;
        this.caretVisible = false;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isCaretVisible() {
        return this.caretVisible;
    }
    
    @Environment(EnvType.CLIENT)
    public int getCurrentRow() {
        return this.currentRow;
    }
    
    @Environment(EnvType.CLIENT)
    public int getSelectionStart() {
        return this.selectionStart;
    }
    
    @Environment(EnvType.CLIENT)
    public int getSelectionEnd() {
        return this.selectionEnd;
    }
}
