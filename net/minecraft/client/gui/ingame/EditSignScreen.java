package net.minecraft.client.gui.ingame;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.util.math.Direction;
import net.minecraft.block.WallSignBlock;
import net.minecraft.state.property.Property;
import net.minecraft.block.SignBlock;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.UpdateSignC2SPacket;
import net.minecraft.text.StringTextComponent;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.block.entity.SignBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class EditSignScreen extends Screen
{
    private final SignBlockEntity sign;
    private int ticksSinceOpened;
    private int currentRow;
    private SelectionManager selectionManager;
    
    public EditSignScreen(final SignBlockEntity sign) {
        super(new TranslatableTextComponent("sign.edit", new Object[0]));
        this.sign = sign;
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.finishEditing()));
        this.sign.setEditable(false);
        this.selectionManager = new SelectionManager(this.minecraft, () -> this.sign.getTextOnRow(this.currentRow).getString(), string -> this.sign.setTextOnRow(this.currentRow, new StringTextComponent(string)), 90);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
        final ClientPlayNetworkHandler clientPlayNetworkHandler1 = this.minecraft.getNetworkHandler();
        if (clientPlayNetworkHandler1 != null) {
            clientPlayNetworkHandler1.sendPacket(new UpdateSignC2SPacket(this.sign.getPos(), this.sign.getTextOnRow(0), this.sign.getTextOnRow(1), this.sign.getTextOnRow(2), this.sign.getTextOnRow(3)));
        }
        this.sign.setEditable(true);
    }
    
    @Override
    public void tick() {
        ++this.ticksSinceOpened;
    }
    
    private void finishEditing() {
        this.sign.markDirty();
        this.minecraft.openScreen(null);
    }
    
    @Override
    public boolean charTyped(final char chr, final int keyCode) {
        this.selectionManager.insert(chr);
        return true;
    }
    
    @Override
    public void onClose() {
        this.finishEditing();
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (keyCode == 265) {
            this.currentRow = (this.currentRow - 1 & 0x3);
            this.selectionManager.moveCaretToEnd();
            return true;
        }
        if (keyCode == 264 || keyCode == 257 || keyCode == 335) {
            this.currentRow = (this.currentRow + 1 & 0x3);
            this.selectionManager.moveCaretToEnd();
            return true;
        }
        return this.selectionManager.handleSpecialKey(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 40, 16777215);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)(this.width / 2), 0.0f, 50.0f);
        final float float4 = 93.75f;
        GlStateManager.scalef(-93.75f, -93.75f, -93.75f);
        GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        final BlockState blockState5 = this.sign.getCachedState();
        float float5;
        if (blockState5.getBlock() instanceof SignBlock) {
            float5 = blockState5.<Integer>get((Property<Integer>)SignBlock.ROTATION) * 360 / 16.0f;
        }
        else {
            if (!(blockState5.getBlock() instanceof WallSignBlock)) {
                GlStateManager.popMatrix();
                this.finishEditing();
                return;
            }
            float5 = blockState5.<Direction>get((Property<Direction>)WallSignBlock.FACING).asRotation();
        }
        GlStateManager.rotatef(float5, 0.0f, 1.0f, 0.0f);
        GlStateManager.translatef(0.0f, -1.0625f, 0.0f);
        this.sign.setSelectionState(this.currentRow, this.selectionManager.getSelectionStart(), this.selectionManager.getSelectionEnd(), this.ticksSinceOpened / 6 % 2 == 0);
        BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.sign, -0.5, -0.75, -0.5, 0.0f);
        this.sign.resetSelectionState();
        GlStateManager.popMatrix();
        super.render(mouseX, mouseY, delta);
    }
}
