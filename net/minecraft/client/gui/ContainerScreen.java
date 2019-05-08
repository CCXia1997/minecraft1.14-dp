package net.minecraft.client.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.container.SlotActionType;
import java.util.Iterator;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.SystemUtil;
import net.minecraft.text.TextFormat;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.render.GuiLighting;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.Sets;
import net.minecraft.text.TextComponent;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.container.Slot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.container.Container;

@Environment(EnvType.CLIENT)
public abstract class ContainerScreen<T extends Container> extends Screen implements ContainerProvider<T>
{
    public static final Identifier BACKGROUND_TEXTURE;
    protected int containerWidth;
    protected int containerHeight;
    protected final T container;
    protected final PlayerInventory playerInventory;
    protected int left;
    protected int top;
    protected Slot focusedSlot;
    private Slot touchDragSlotStart;
    private boolean touchIsRightClickDrag;
    private ItemStack touchDragStack;
    private int touchDropX;
    private int touchDropY;
    private Slot touchDropOriginSlot;
    private long touchDropTime;
    private ItemStack touchDropReturningStack;
    private Slot touchHoveredSlot;
    private long touchDropTimer;
    protected final Set<Slot> cursorDragSlots;
    protected boolean isCursorDragging;
    private int heldButtonType;
    private int heldButtonCode;
    private boolean cancelNextRelease;
    private int draggedStackRemainder;
    private long lastButtonClickTime;
    private Slot lastClickedSlot;
    private int lastClickedButton;
    private boolean isDoubleClicking;
    private ItemStack quickMovingStack;
    
    public ContainerScreen(final T container, final PlayerInventory playerInventory, final TextComponent name) {
        super(name);
        this.containerWidth = 176;
        this.containerHeight = 166;
        this.touchDragStack = ItemStack.EMPTY;
        this.touchDropReturningStack = ItemStack.EMPTY;
        this.cursorDragSlots = Sets.newHashSet();
        this.quickMovingStack = ItemStack.EMPTY;
        this.container = container;
        this.playerInventory = playerInventory;
        this.cancelNextRelease = true;
    }
    
    @Override
    protected void init() {
        super.init();
        this.left = (this.width - this.containerWidth) / 2;
        this.top = (this.height - this.containerHeight) / 2;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        final int integer4 = this.left;
        final int integer5 = this.top;
        this.drawBackground(delta, mouseX, mouseY);
        GlStateManager.disableRescaleNormal();
        GuiLighting.disable();
        GlStateManager.disableLighting();
        GlStateManager.disableDepthTest();
        super.render(mouseX, mouseY, delta);
        GuiLighting.enableForItems();
        GlStateManager.pushMatrix();
        GlStateManager.translatef((float)integer4, (float)integer5, 0.0f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableRescaleNormal();
        this.focusedSlot = null;
        final int integer6 = 240;
        final int integer7 = 240;
        GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0f, 240.0f);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        for (int integer8 = 0; integer8 < this.container.slotList.size(); ++integer8) {
            final Slot slot9 = this.container.slotList.get(integer8);
            if (slot9.doDrawHoveringEffect()) {
                this.drawSlot(slot9);
            }
            if (this.isPointOverSlot(slot9, mouseX, mouseY) && slot9.doDrawHoveringEffect()) {
                this.focusedSlot = slot9;
                GlStateManager.disableLighting();
                GlStateManager.disableDepthTest();
                final int integer9 = slot9.xPosition;
                final int integer10 = slot9.yPosition;
                GlStateManager.colorMask(true, true, true, false);
                this.fillGradient(integer9, integer10, integer9 + 16, integer10 + 16, -2130706433, -2130706433);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepthTest();
            }
        }
        GuiLighting.disable();
        this.drawForeground(mouseX, mouseY);
        GuiLighting.enableForItems();
        final PlayerInventory playerInventory8 = this.minecraft.player.inventory;
        ItemStack itemStack9 = this.touchDragStack.isEmpty() ? playerInventory8.getCursorStack() : this.touchDragStack;
        if (!itemStack9.isEmpty()) {
            final int integer9 = 8;
            final int integer10 = this.touchDragStack.isEmpty() ? 8 : 16;
            String string12 = null;
            if (!this.touchDragStack.isEmpty() && this.touchIsRightClickDrag) {
                itemStack9 = itemStack9.copy();
                itemStack9.setAmount(MathHelper.ceil(itemStack9.getAmount() / 2.0f));
            }
            else if (this.isCursorDragging && this.cursorDragSlots.size() > 1) {
                itemStack9 = itemStack9.copy();
                itemStack9.setAmount(this.draggedStackRemainder);
                if (itemStack9.isEmpty()) {
                    string12 = "" + TextFormat.o + "0";
                }
            }
            this.drawItem(itemStack9, mouseX - integer4 - 8, mouseY - integer5 - integer10, string12);
        }
        if (!this.touchDropReturningStack.isEmpty()) {
            float float10 = (SystemUtil.getMeasuringTimeMs() - this.touchDropTime) / 100.0f;
            if (float10 >= 1.0f) {
                float10 = 1.0f;
                this.touchDropReturningStack = ItemStack.EMPTY;
            }
            final int integer10 = this.touchDropOriginSlot.xPosition - this.touchDropX;
            final int integer11 = this.touchDropOriginSlot.yPosition - this.touchDropY;
            final int integer12 = this.touchDropX + (int)(integer10 * float10);
            final int integer13 = this.touchDropY + (int)(integer11 * float10);
            this.drawItem(this.touchDropReturningStack, integer12, integer13, null);
        }
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
        GlStateManager.enableDepthTest();
        GuiLighting.enable();
    }
    
    protected void drawMouseoverTooltip(final int mouseX, final int mouseY) {
        if (this.minecraft.player.inventory.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
            this.renderTooltip(this.focusedSlot.getStack(), mouseX, mouseY);
        }
    }
    
    private void drawItem(final ItemStack stack, final int xPosition, final int yPosition, final String amountText) {
        GlStateManager.translatef(0.0f, 0.0f, 32.0f);
        this.blitOffset = 200;
        this.itemRenderer.zOffset = 200.0f;
        this.itemRenderer.renderGuiItem(stack, xPosition, yPosition);
        this.itemRenderer.renderGuiItemOverlay(this.font, stack, xPosition, yPosition - (this.touchDragStack.isEmpty() ? 0 : 8), amountText);
        this.blitOffset = 0;
        this.itemRenderer.zOffset = 0.0f;
    }
    
    protected void drawForeground(final int mouseX, final int mouseY) {
    }
    
    protected abstract void drawBackground(final float arg1, final int arg2, final int arg3);
    
    private void drawSlot(final Slot slot) {
        final int integer2 = slot.xPosition;
        final int integer3 = slot.yPosition;
        ItemStack itemStack4 = slot.getStack();
        boolean boolean5 = false;
        boolean boolean6 = slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && !this.touchIsRightClickDrag;
        final ItemStack itemStack5 = this.minecraft.player.inventory.getCursorStack();
        String string8 = null;
        if (slot == this.touchDragSlotStart && !this.touchDragStack.isEmpty() && this.touchIsRightClickDrag && !itemStack4.isEmpty()) {
            itemStack4 = itemStack4.copy();
            itemStack4.setAmount(itemStack4.getAmount() / 2);
        }
        else if (this.isCursorDragging && this.cursorDragSlots.contains(slot) && !itemStack5.isEmpty()) {
            if (this.cursorDragSlots.size() == 1) {
                return;
            }
            if (Container.canInsertItemIntoSlot(slot, itemStack5, true) && this.container.canInsertIntoSlot(slot)) {
                itemStack4 = itemStack5.copy();
                boolean5 = true;
                Container.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack4, slot.getStack().isEmpty() ? 0 : slot.getStack().getAmount());
                final int integer4 = Math.min(itemStack4.getMaxAmount(), slot.getMaxStackAmount(itemStack4));
                if (itemStack4.getAmount() > integer4) {
                    string8 = TextFormat.o.toString() + integer4;
                    itemStack4.setAmount(integer4);
                }
            }
            else {
                this.cursorDragSlots.remove(slot);
                this.calculateOffset();
            }
        }
        this.blitOffset = 100;
        this.itemRenderer.zOffset = 100.0f;
        if (itemStack4.isEmpty() && slot.doDrawHoveringEffect()) {
            final String string9 = slot.getBackgroundSprite();
            if (string9 != null) {
                final Sprite sprite10 = this.minecraft.getSpriteAtlas().getSprite(string9);
                GlStateManager.disableLighting();
                this.minecraft.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
                DrawableHelper.blit(integer2, integer3, this.blitOffset, 16, 16, sprite10);
                GlStateManager.enableLighting();
                boolean6 = true;
            }
        }
        if (!boolean6) {
            if (boolean5) {
                DrawableHelper.fill(integer2, integer3, integer2 + 16, integer3 + 16, -2130706433);
            }
            GlStateManager.enableDepthTest();
            this.itemRenderer.renderGuiItem(this.minecraft.player, itemStack4, integer2, integer3);
            this.itemRenderer.renderGuiItemOverlay(this.font, itemStack4, integer2, integer3, string8);
        }
        this.itemRenderer.zOffset = 0.0f;
        this.blitOffset = 0;
    }
    
    private void calculateOffset() {
        final ItemStack itemStack1 = this.minecraft.player.inventory.getCursorStack();
        if (itemStack1.isEmpty() || !this.isCursorDragging) {
            return;
        }
        if (this.heldButtonType == 2) {
            this.draggedStackRemainder = itemStack1.getMaxAmount();
            return;
        }
        this.draggedStackRemainder = itemStack1.getAmount();
        for (final Slot slot3 : this.cursorDragSlots) {
            final ItemStack itemStack2 = itemStack1.copy();
            final ItemStack itemStack3 = slot3.getStack();
            final int integer6 = itemStack3.isEmpty() ? 0 : itemStack3.getAmount();
            Container.calculateStackSize(this.cursorDragSlots, this.heldButtonType, itemStack2, integer6);
            final int integer7 = Math.min(itemStack2.getMaxAmount(), slot3.getMaxStackAmount(itemStack2));
            if (itemStack2.getAmount() > integer7) {
                itemStack2.setAmount(integer7);
            }
            this.draggedStackRemainder -= itemStack2.getAmount() - integer6;
        }
    }
    
    private Slot getSlotAt(final double xPosition, final double yPosition) {
        for (int integer5 = 0; integer5 < this.container.slotList.size(); ++integer5) {
            final Slot slot6 = this.container.slotList.get(integer5);
            if (this.isPointOverSlot(slot6, xPosition, yPosition) && slot6.doDrawHoveringEffect()) {
                return slot6;
            }
        }
        return null;
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        final boolean boolean6 = this.minecraft.options.keyPickItem.matchesMouse(button);
        final Slot slot7 = this.getSlotAt(mouseX, mouseY);
        final long long8 = SystemUtil.getMeasuringTimeMs();
        this.isDoubleClicking = (this.lastClickedSlot == slot7 && long8 - this.lastButtonClickTime < 250L && this.lastClickedButton == button);
        this.cancelNextRelease = false;
        if (button == 0 || button == 1 || boolean6) {
            final int integer10 = this.left;
            final int integer11 = this.top;
            final boolean boolean7 = this.isClickOutsideBounds(mouseX, mouseY, integer10, integer11, button);
            int integer12 = -1;
            if (slot7 != null) {
                integer12 = slot7.id;
            }
            if (boolean7) {
                integer12 = -999;
            }
            if (this.minecraft.options.touchscreen && boolean7 && this.minecraft.player.inventory.getCursorStack().isEmpty()) {
                this.minecraft.openScreen(null);
                return true;
            }
            if (integer12 != -1) {
                if (this.minecraft.options.touchscreen) {
                    if (slot7 != null && slot7.hasStack()) {
                        this.touchDragSlotStart = slot7;
                        this.touchDragStack = ItemStack.EMPTY;
                        this.touchIsRightClickDrag = (button == 1);
                    }
                    else {
                        this.touchDragSlotStart = null;
                    }
                }
                else if (!this.isCursorDragging) {
                    if (this.minecraft.player.inventory.getCursorStack().isEmpty()) {
                        if (this.minecraft.options.keyPickItem.matchesMouse(button)) {
                            this.onMouseClick(slot7, integer12, button, SlotActionType.d);
                        }
                        else {
                            final boolean boolean8 = integer12 != -999 && (InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 344));
                            SlotActionType slotActionType15 = SlotActionType.a;
                            if (boolean8) {
                                this.quickMovingStack = ((slot7 != null && slot7.hasStack()) ? slot7.getStack().copy() : ItemStack.EMPTY);
                                slotActionType15 = SlotActionType.b;
                            }
                            else if (integer12 == -999) {
                                slotActionType15 = SlotActionType.e;
                            }
                            this.onMouseClick(slot7, integer12, button, slotActionType15);
                        }
                        this.cancelNextRelease = true;
                    }
                    else {
                        this.isCursorDragging = true;
                        this.heldButtonCode = button;
                        this.cursorDragSlots.clear();
                        if (button == 0) {
                            this.heldButtonType = 0;
                        }
                        else if (button == 1) {
                            this.heldButtonType = 1;
                        }
                        else if (this.minecraft.options.keyPickItem.matchesMouse(button)) {
                            this.heldButtonType = 2;
                        }
                    }
                }
            }
        }
        this.lastClickedSlot = slot7;
        this.lastButtonClickTime = long8;
        this.lastClickedButton = button;
        return true;
    }
    
    protected boolean isClickOutsideBounds(final double mouseX, final double mouseY, final int left, final int top, final int button) {
        return mouseX < left || mouseY < top || mouseX >= left + this.containerWidth || mouseY >= top + this.containerHeight;
    }
    
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        final Slot slot10 = this.getSlotAt(mouseX, mouseY);
        final ItemStack itemStack11 = this.minecraft.player.inventory.getCursorStack();
        if (this.touchDragSlotStart != null && this.minecraft.options.touchscreen) {
            if (button == 0 || button == 1) {
                if (this.touchDragStack.isEmpty()) {
                    if (slot10 != this.touchDragSlotStart && !this.touchDragSlotStart.getStack().isEmpty()) {
                        this.touchDragStack = this.touchDragSlotStart.getStack().copy();
                    }
                }
                else if (this.touchDragStack.getAmount() > 1 && slot10 != null && Container.canInsertItemIntoSlot(slot10, this.touchDragStack, false)) {
                    final long long12 = SystemUtil.getMeasuringTimeMs();
                    if (this.touchHoveredSlot == slot10) {
                        if (long12 - this.touchDropTimer > 500L) {
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, SlotActionType.a);
                            this.onMouseClick(slot10, slot10.id, 1, SlotActionType.a);
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, 0, SlotActionType.a);
                            this.touchDropTimer = long12 + 750L;
                            this.touchDragStack.subtractAmount(1);
                        }
                    }
                    else {
                        this.touchHoveredSlot = slot10;
                        this.touchDropTimer = long12;
                    }
                }
            }
        }
        else if (this.isCursorDragging && slot10 != null && !itemStack11.isEmpty() && (itemStack11.getAmount() > this.cursorDragSlots.size() || this.heldButtonType == 2) && Container.canInsertItemIntoSlot(slot10, itemStack11, true) && slot10.canInsert(itemStack11) && this.container.canInsertIntoSlot(slot10)) {
            this.cursorDragSlots.add(slot10);
            this.calculateOffset();
        }
        return true;
    }
    
    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        final Slot slot6 = this.getSlotAt(mouseX, mouseY);
        final int integer7 = this.left;
        final int integer8 = this.top;
        final boolean boolean9 = this.isClickOutsideBounds(mouseX, mouseY, integer7, integer8, button);
        int integer9 = -1;
        if (slot6 != null) {
            integer9 = slot6.id;
        }
        if (boolean9) {
            integer9 = -999;
        }
        if (this.isDoubleClicking && slot6 != null && button == 0 && this.container.canInsertIntoSlot(ItemStack.EMPTY, slot6)) {
            if (hasShiftDown()) {
                if (!this.quickMovingStack.isEmpty()) {
                    for (final Slot slot7 : this.container.slotList) {
                        if (slot7 != null && slot7.canTakeItems(this.minecraft.player) && slot7.hasStack() && slot7.inventory == slot6.inventory && Container.canInsertItemIntoSlot(slot7, this.quickMovingStack, true)) {
                            this.onMouseClick(slot7, slot7.id, button, SlotActionType.b);
                        }
                    }
                }
            }
            else {
                this.onMouseClick(slot6, integer9, button, SlotActionType.g);
            }
            this.isDoubleClicking = false;
            this.lastButtonClickTime = 0L;
        }
        else {
            if (this.isCursorDragging && this.heldButtonCode != button) {
                this.isCursorDragging = false;
                this.cursorDragSlots.clear();
                return this.cancelNextRelease = true;
            }
            if (this.cancelNextRelease) {
                this.cancelNextRelease = false;
                return true;
            }
            if (this.touchDragSlotStart != null && this.minecraft.options.touchscreen) {
                if (button == 0 || button == 1) {
                    if (this.touchDragStack.isEmpty() && slot6 != this.touchDragSlotStart) {
                        this.touchDragStack = this.touchDragSlotStart.getStack();
                    }
                    final boolean boolean10 = Container.canInsertItemIntoSlot(slot6, this.touchDragStack, false);
                    if (integer9 != -1 && !this.touchDragStack.isEmpty() && boolean10) {
                        this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, button, SlotActionType.a);
                        this.onMouseClick(slot6, integer9, 0, SlotActionType.a);
                        if (this.minecraft.player.inventory.getCursorStack().isEmpty()) {
                            this.touchDropReturningStack = ItemStack.EMPTY;
                        }
                        else {
                            this.onMouseClick(this.touchDragSlotStart, this.touchDragSlotStart.id, button, SlotActionType.a);
                            this.touchDropX = MathHelper.floor(mouseX - integer7);
                            this.touchDropY = MathHelper.floor(mouseY - integer8);
                            this.touchDropOriginSlot = this.touchDragSlotStart;
                            this.touchDropReturningStack = this.touchDragStack;
                            this.touchDropTime = SystemUtil.getMeasuringTimeMs();
                        }
                    }
                    else if (!this.touchDragStack.isEmpty()) {
                        this.touchDropX = MathHelper.floor(mouseX - integer7);
                        this.touchDropY = MathHelper.floor(mouseY - integer8);
                        this.touchDropOriginSlot = this.touchDragSlotStart;
                        this.touchDropReturningStack = this.touchDragStack;
                        this.touchDropTime = SystemUtil.getMeasuringTimeMs();
                    }
                    this.touchDragStack = ItemStack.EMPTY;
                    this.touchDragSlotStart = null;
                }
            }
            else if (this.isCursorDragging && !this.cursorDragSlots.isEmpty()) {
                this.onMouseClick(null, -999, Container.packClickData(0, this.heldButtonType), SlotActionType.f);
                for (final Slot slot7 : this.cursorDragSlots) {
                    this.onMouseClick(slot7, slot7.id, Container.packClickData(1, this.heldButtonType), SlotActionType.f);
                }
                this.onMouseClick(null, -999, Container.packClickData(2, this.heldButtonType), SlotActionType.f);
            }
            else if (!this.minecraft.player.inventory.getCursorStack().isEmpty()) {
                if (this.minecraft.options.keyPickItem.matchesMouse(button)) {
                    this.onMouseClick(slot6, integer9, button, SlotActionType.d);
                }
                else {
                    final boolean boolean10 = integer9 != -999 && (InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 340) || InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 344));
                    if (boolean10) {
                        this.quickMovingStack = ((slot6 != null && slot6.hasStack()) ? slot6.getStack().copy() : ItemStack.EMPTY);
                    }
                    this.onMouseClick(slot6, integer9, button, boolean10 ? SlotActionType.b : SlotActionType.a);
                }
            }
        }
        if (this.minecraft.player.inventory.getCursorStack().isEmpty()) {
            this.lastButtonClickTime = 0L;
        }
        this.isCursorDragging = false;
        return true;
    }
    
    private boolean isPointOverSlot(final Slot slot, final double pointX, final double pointY) {
        return this.isPointWithinBounds(slot.xPosition, slot.yPosition, 16, 16, pointX, pointY);
    }
    
    protected boolean isPointWithinBounds(final int xPosition, final int yPosition, final int width, final int height, double pointX, double pointY) {
        final int integer9 = this.left;
        final int integer10 = this.top;
        pointX -= integer9;
        pointY -= integer10;
        return pointX >= xPosition - 1 && pointX < xPosition + width + 1 && pointY >= yPosition - 1 && pointY < yPosition + height + 1;
    }
    
    protected void onMouseClick(final Slot slot, int invSlot, final int button, final SlotActionType slotActionType) {
        if (slot != null) {
            invSlot = slot.id;
        }
        this.minecraft.interactionManager.a(this.container.syncId, invSlot, button, slotActionType, this.minecraft.player);
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == 256 || this.minecraft.options.keyInventory.matchesKey(keyCode, scanCode)) {
            this.minecraft.player.closeContainer();
        }
        this.handleHotbarKeyPressed(keyCode, scanCode);
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            if (this.minecraft.options.keyPickItem.matchesKey(keyCode, scanCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 0, SlotActionType.d);
            }
            else if (this.minecraft.options.keyDrop.matchesKey(keyCode, scanCode)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, Screen.hasControlDown() ? 1 : 0, SlotActionType.e);
            }
        }
        return true;
    }
    
    protected boolean handleHotbarKeyPressed(final int keyCode, final int scanCode) {
        if (this.minecraft.player.inventory.getCursorStack().isEmpty() && this.focusedSlot != null) {
            for (int integer3 = 0; integer3 < 9; ++integer3) {
                if (this.minecraft.options.keysHotbar[integer3].matchesKey(keyCode, scanCode)) {
                    this.onMouseClick(this.focusedSlot, this.focusedSlot.id, integer3, SlotActionType.c);
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void removed() {
        if (this.minecraft.player == null) {
            return;
        }
        this.container.close(this.minecraft.player);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (!this.minecraft.player.isAlive() || this.minecraft.player.removed) {
            this.minecraft.player.closeContainer();
        }
    }
    
    @Override
    public T getContainer() {
        return this.container;
    }
    
    static {
        BACKGROUND_TEXTURE = new Identifier("textures/gui/container/inventory.png");
    }
}
