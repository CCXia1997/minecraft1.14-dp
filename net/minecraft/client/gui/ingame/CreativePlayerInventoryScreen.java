package net.minecraft.client.gui.ingame;

import net.minecraft.container.ContainerType;
import net.minecraft.util.DefaultedList;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.text.TextFormat;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import com.google.common.collect.Lists;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.options.HotbarStorageEntry;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.inventory.Inventory;
import com.google.common.collect.ImmutableList;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.tag.TagContainer;
import java.util.function.Predicate;
import net.minecraft.tag.ItemTags;
import net.minecraft.client.search.Searchable;
import java.util.Iterator;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.client.search.SearchManager;
import net.minecraft.util.registry.Registry;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.container.ContainerListener;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.container.SlotActionType;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Screen;
import com.google.common.collect.Maps;
import net.minecraft.text.TextComponent;
import net.minecraft.text.StringTextComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import java.util.Map;
import net.minecraft.container.Slot;
import java.util.List;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class CreativePlayerInventoryScreen extends AbstractPlayerInventoryScreen<CreativeContainer>
{
    private static final Identifier TEXTURE;
    private static final BasicInventory inventory;
    private static int selectedTab;
    private float scrollPosition;
    private boolean p;
    private TextFieldWidget searchBox;
    private List<Slot> slots;
    private Slot deleteItemSlot;
    private CreativePlayerInventoryContainerListener t;
    private boolean u;
    private boolean v;
    private final Map<Identifier, Tag<Item>> w;
    
    public CreativePlayerInventoryScreen(final PlayerEntity playerEntity) {
        super(new CreativeContainer(playerEntity), playerEntity.inventory, new StringTextComponent(""));
        this.w = Maps.newTreeMap();
        playerEntity.container = this.container;
        this.passEvents = true;
        this.containerHeight = 136;
        this.containerWidth = 195;
    }
    
    @Override
    public void tick() {
        if (!this.minecraft.interactionManager.hasCreativeInventory()) {
            this.minecraft.openScreen(new PlayerInventoryScreen(this.minecraft.player));
        }
        else if (this.searchBox != null) {
            this.searchBox.tick();
        }
    }
    
    @Override
    protected void onMouseClick(@Nullable final Slot slot, final int invSlot, final int button, SlotActionType slotActionType) {
        if (this.a(slot)) {
            this.searchBox.e();
            this.searchBox.j(0);
        }
        final boolean boolean5 = slotActionType == SlotActionType.b;
        slotActionType = ((invSlot == -999 && slotActionType == SlotActionType.a) ? SlotActionType.e : slotActionType);
        if (slot != null || CreativePlayerInventoryScreen.selectedTab == ItemGroup.INVENTORY.getIndex() || slotActionType == SlotActionType.f) {
            if (slot != null && !slot.canTakeItems(this.minecraft.player)) {
                return;
            }
            if (slot == this.deleteItemSlot && boolean5) {
                for (int integer6 = 0; integer6 < this.minecraft.player.playerContainer.getStacks().size(); ++integer6) {
                    this.minecraft.interactionManager.clickCreativeStack(ItemStack.EMPTY, integer6);
                }
            }
            else if (CreativePlayerInventoryScreen.selectedTab == ItemGroup.INVENTORY.getIndex()) {
                if (slot == this.deleteItemSlot) {
                    this.minecraft.player.inventory.setCursorStack(ItemStack.EMPTY);
                }
                else if (slotActionType == SlotActionType.e && slot != null && slot.hasStack()) {
                    final ItemStack itemStack6 = slot.takeStack((button == 0) ? 1 : slot.getStack().getMaxAmount());
                    final ItemStack itemStack7 = slot.getStack();
                    this.minecraft.player.dropItem(itemStack6, true);
                    this.minecraft.interactionManager.dropCreativeStack(itemStack6);
                    this.minecraft.interactionManager.clickCreativeStack(itemStack7, ((CreativeSlot)slot).slot.id);
                }
                else if (slotActionType == SlotActionType.e && !this.minecraft.player.inventory.getCursorStack().isEmpty()) {
                    this.minecraft.player.dropItem(this.minecraft.player.inventory.getCursorStack(), true);
                    this.minecraft.interactionManager.dropCreativeStack(this.minecraft.player.inventory.getCursorStack());
                    this.minecraft.player.inventory.setCursorStack(ItemStack.EMPTY);
                }
                else {
                    this.minecraft.player.playerContainer.onSlotClick((slot == null) ? invSlot : ((CreativeSlot)slot).slot.id, button, slotActionType, this.minecraft.player);
                    this.minecraft.player.playerContainer.sendContentUpdates();
                }
            }
            else if (slotActionType != SlotActionType.f && slot.inventory == CreativePlayerInventoryScreen.inventory) {
                final PlayerInventory playerInventory6 = this.minecraft.player.inventory;
                ItemStack itemStack7 = playerInventory6.getCursorStack();
                final ItemStack itemStack8 = slot.getStack();
                if (slotActionType == SlotActionType.c) {
                    if (!itemStack8.isEmpty() && button >= 0 && button < 9) {
                        final ItemStack itemStack9 = itemStack8.copy();
                        itemStack9.setAmount(itemStack9.getMaxAmount());
                        this.minecraft.player.inventory.setInvStack(button, itemStack9);
                        this.minecraft.player.playerContainer.sendContentUpdates();
                    }
                    return;
                }
                if (slotActionType == SlotActionType.d) {
                    if (playerInventory6.getCursorStack().isEmpty() && slot.hasStack()) {
                        final ItemStack itemStack9 = slot.getStack().copy();
                        itemStack9.setAmount(itemStack9.getMaxAmount());
                        playerInventory6.setCursorStack(itemStack9);
                    }
                    return;
                }
                if (slotActionType == SlotActionType.e) {
                    if (!itemStack8.isEmpty()) {
                        final ItemStack itemStack9 = itemStack8.copy();
                        itemStack9.setAmount((button == 0) ? 1 : itemStack9.getMaxAmount());
                        this.minecraft.player.dropItem(itemStack9, true);
                        this.minecraft.interactionManager.dropCreativeStack(itemStack9);
                    }
                    return;
                }
                if (!itemStack7.isEmpty() && !itemStack8.isEmpty() && itemStack7.isEqualIgnoreTags(itemStack8) && ItemStack.areTagsEqual(itemStack7, itemStack8)) {
                    if (button == 0) {
                        if (boolean5) {
                            itemStack7.setAmount(itemStack7.getMaxAmount());
                        }
                        else if (itemStack7.getAmount() < itemStack7.getMaxAmount()) {
                            itemStack7.addAmount(1);
                        }
                    }
                    else {
                        itemStack7.subtractAmount(1);
                    }
                }
                else if (itemStack8.isEmpty() || !itemStack7.isEmpty()) {
                    if (button == 0) {
                        playerInventory6.setCursorStack(ItemStack.EMPTY);
                    }
                    else {
                        playerInventory6.getCursorStack().subtractAmount(1);
                    }
                }
                else {
                    playerInventory6.setCursorStack(itemStack8.copy());
                    itemStack7 = playerInventory6.getCursorStack();
                    if (boolean5) {
                        itemStack7.setAmount(itemStack7.getMaxAmount());
                    }
                }
            }
            else if (this.container != null) {
                final ItemStack itemStack6 = (slot == null) ? ItemStack.EMPTY : ((CreativeContainer)this.container).getSlot(slot.id).getStack();
                ((CreativeContainer)this.container).onSlotClick((slot == null) ? invSlot : slot.id, button, slotActionType, this.minecraft.player);
                if (Container.unpackButtonId(button) == 2) {
                    for (int integer7 = 0; integer7 < 9; ++integer7) {
                        this.minecraft.interactionManager.clickCreativeStack(((CreativeContainer)this.container).getSlot(45 + integer7).getStack(), 36 + integer7);
                    }
                }
                else if (slot != null) {
                    final ItemStack itemStack7 = ((CreativeContainer)this.container).getSlot(slot.id).getStack();
                    this.minecraft.interactionManager.clickCreativeStack(itemStack7, slot.id - ((CreativeContainer)this.container).slotList.size() + 9 + 36);
                    final int integer8 = 45 + button;
                    if (slotActionType == SlotActionType.c) {
                        this.minecraft.interactionManager.clickCreativeStack(itemStack6, integer8 - ((CreativeContainer)this.container).slotList.size() + 9 + 36);
                    }
                    else if (slotActionType == SlotActionType.e && !itemStack6.isEmpty()) {
                        final ItemStack itemStack9 = itemStack6.copy();
                        itemStack9.setAmount((button == 0) ? 1 : itemStack9.getMaxAmount());
                        this.minecraft.player.dropItem(itemStack9, true);
                        this.minecraft.interactionManager.dropCreativeStack(itemStack9);
                    }
                    this.minecraft.player.playerContainer.sendContentUpdates();
                }
            }
        }
        else {
            final PlayerInventory playerInventory6 = this.minecraft.player.inventory;
            if (!playerInventory6.getCursorStack().isEmpty() && this.v) {
                if (button == 0) {
                    this.minecraft.player.dropItem(playerInventory6.getCursorStack(), true);
                    this.minecraft.interactionManager.dropCreativeStack(playerInventory6.getCursorStack());
                    playerInventory6.setCursorStack(ItemStack.EMPTY);
                }
                if (button == 1) {
                    final ItemStack itemStack7 = playerInventory6.getCursorStack().split(1);
                    this.minecraft.player.dropItem(itemStack7, true);
                    this.minecraft.interactionManager.dropCreativeStack(itemStack7);
                }
            }
        }
    }
    
    private boolean a(@Nullable final Slot slot) {
        return slot != null && slot.inventory == CreativePlayerInventoryScreen.inventory;
    }
    
    @Override
    protected void b() {
        final int integer1 = this.left;
        super.b();
        if (this.searchBox != null && this.left != integer1) {
            this.searchBox.setX(this.left + 82);
        }
    }
    
    @Override
    protected void init() {
        if (this.minecraft.interactionManager.hasCreativeInventory()) {
            super.init();
            this.minecraft.keyboard.enableRepeatEvents(true);
            final TextRenderer font = this.font;
            final int x = this.left + 82;
            final int y = this.top + 6;
            final int width = 80;
            this.font.getClass();
            (this.searchBox = new TextFieldWidget(font, x, y, width, 9, I18n.translate("itemGroup.search"))).setMaxLength(50);
            this.searchBox.setHasBorder(false);
            this.searchBox.setVisible(false);
            this.searchBox.h(16777215);
            this.children.add(this.searchBox);
            final int integer1 = CreativePlayerInventoryScreen.selectedTab;
            CreativePlayerInventoryScreen.selectedTab = -1;
            this.setSelectedTab(ItemGroup.GROUPS[integer1]);
            this.minecraft.player.playerContainer.removeListener(this.t);
            this.t = new CreativePlayerInventoryContainerListener(this.minecraft);
            this.minecraft.player.playerContainer.addListener(this.t);
        }
        else {
            this.minecraft.openScreen(new PlayerInventoryScreen(this.minecraft.player));
        }
    }
    
    @Override
    public void resize(final MinecraftClient client, final int width, final int height) {
        final String string4 = this.searchBox.getText();
        this.init(client, width, height);
        this.searchBox.setText(string4);
        if (!this.searchBox.getText().isEmpty()) {
            this.e();
        }
    }
    
    @Override
    public void removed() {
        super.removed();
        if (this.minecraft.player != null && this.minecraft.player.inventory != null) {
            this.minecraft.player.playerContainer.removeListener(this.t);
        }
        this.minecraft.keyboard.enableRepeatEvents(false);
    }
    
    @Override
    public boolean charTyped(final char chr, final int keyCode) {
        if (this.u) {
            return false;
        }
        if (CreativePlayerInventoryScreen.selectedTab != ItemGroup.SEARCH.getIndex()) {
            return false;
        }
        final String string3 = this.searchBox.getText();
        if (this.searchBox.charTyped(chr, keyCode)) {
            if (!Objects.equals(string3, this.searchBox.getText())) {
                this.e();
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
        this.u = false;
        if (CreativePlayerInventoryScreen.selectedTab != ItemGroup.SEARCH.getIndex()) {
            if (this.minecraft.options.keyChat.matchesKey(keyCode, scanCode)) {
                this.u = true;
                this.setSelectedTab(ItemGroup.SEARCH);
                return true;
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
        else {
            final boolean boolean4 = !this.a(this.focusedSlot) || (this.focusedSlot != null && this.focusedSlot.hasStack());
            if (boolean4 && this.handleHotbarKeyPressed(keyCode, scanCode)) {
                return this.u = true;
            }
            final String string5 = this.searchBox.getText();
            if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
                if (!Objects.equals(string5, this.searchBox.getText())) {
                    this.e();
                }
                return true;
            }
            return (this.searchBox.isFocused() && this.searchBox.isVisible() && keyCode != 256) || super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
    
    @Override
    public boolean keyReleased(final int keyCode, final int scanCode, final int modifiers) {
        this.u = false;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }
    
    private void e() {
        ((CreativeContainer)this.container).itemList.clear();
        this.w.clear();
        String string1 = this.searchBox.getText();
        if (string1.isEmpty()) {
            for (final Item item3 : Registry.ITEM) {
                item3.appendItemsForGroup(ItemGroup.SEARCH, ((CreativeContainer)this.container).itemList);
            }
        }
        else {
            Searchable<ItemStack> searchable2;
            if (string1.startsWith("#")) {
                string1 = string1.substring(1);
                searchable2 = this.minecraft.<ItemStack>getSearchableContainer(SearchManager.ITEM_TAG);
                this.a(string1);
            }
            else {
                searchable2 = this.minecraft.<ItemStack>getSearchableContainer(SearchManager.ITEM_TOOLTIP);
            }
            ((CreativeContainer)this.container).itemList.addAll(searchable2.findAll(string1.toLowerCase(Locale.ROOT)));
        }
        this.scrollPosition = 0.0f;
        ((CreativeContainer)this.container).a(0.0f);
    }
    
    private void a(final String string) {
        final int integer2 = string.indexOf(58);
        Predicate<Identifier> predicate3;
        if (integer2 == -1) {
            predicate3 = (identifier -> identifier.getPath().contains(string));
        }
        else {
            final String string2 = string.substring(0, integer2).trim();
            final String string3 = string.substring(integer2 + 1).trim();
            final CharSequence charSequence;
            final CharSequence charSequence2;
            predicate3 = (identifier -> identifier.getNamespace().contains(charSequence) && identifier.getPath().contains(charSequence2));
        }
        final TagContainer<Item> tagContainer4 = ItemTags.getContainer();
        final Tag<Item> tag;
        tagContainer4.getKeys().stream().filter(predicate3).forEach(identifier -> tag = this.w.put(identifier, tagContainer4.get(identifier)));
    }
    
    @Override
    protected void drawForeground(final int mouseX, final int mouseY) {
        final ItemGroup itemGroup3 = ItemGroup.GROUPS[CreativePlayerInventoryScreen.selectedTab];
        if (itemGroup3.hasTooltip()) {
            GlStateManager.disableBlend();
            this.font.draw(I18n.translate(itemGroup3.getTranslationKey()), 8.0f, 6.0f, 4210752);
        }
    }
    
    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        if (button == 0) {
            final double double6 = mouseX - this.left;
            final double double7 = mouseY - this.top;
            for (final ItemGroup itemGroup13 : ItemGroup.GROUPS) {
                if (this.isClickInTab(itemGroup13, double6, double7)) {
                    return true;
                }
            }
            if (CreativePlayerInventoryScreen.selectedTab != ItemGroup.INVENTORY.getIndex() && this.c(mouseX, mouseY)) {
                this.p = this.doRenderScrollBar();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        if (button == 0) {
            final double double6 = mouseX - this.left;
            final double double7 = mouseY - this.top;
            this.p = false;
            for (final ItemGroup itemGroup13 : ItemGroup.GROUPS) {
                if (this.isClickInTab(itemGroup13, double6, double7)) {
                    this.setSelectedTab(itemGroup13);
                    return true;
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    private boolean doRenderScrollBar() {
        return CreativePlayerInventoryScreen.selectedTab != ItemGroup.INVENTORY.getIndex() && ItemGroup.GROUPS[CreativePlayerInventoryScreen.selectedTab].hasScrollbar() && ((CreativeContainer)this.container).e();
    }
    
    private void setSelectedTab(final ItemGroup itemGroup) {
        final int integer2 = CreativePlayerInventoryScreen.selectedTab;
        CreativePlayerInventoryScreen.selectedTab = itemGroup.getIndex();
        this.cursorDragSlots.clear();
        ((CreativeContainer)this.container).itemList.clear();
        if (itemGroup == ItemGroup.HOTBAR) {
            final HotbarStorage hotbarStorage3 = this.minecraft.getCreativeHotbarStorage();
            for (int integer3 = 0; integer3 < 9; ++integer3) {
                final HotbarStorageEntry hotbarStorageEntry5 = hotbarStorage3.getSavedHotbar(integer3);
                if (hotbarStorageEntry5.isEmpty()) {
                    for (int integer4 = 0; integer4 < 9; ++integer4) {
                        if (integer4 == integer3) {
                            final ItemStack itemStack7 = new ItemStack(Items.kR);
                            itemStack7.getOrCreateSubCompoundTag("CustomCreativeLock");
                            final String string8 = this.minecraft.options.keysHotbar[integer3].getLocalizedName();
                            final String string9 = this.minecraft.options.keySaveToolbarActivator.getLocalizedName();
                            itemStack7.setDisplayName(new TranslatableTextComponent("inventory.hotbarInfo", new Object[] { string9, string8 }));
                            ((CreativeContainer)this.container).itemList.add(itemStack7);
                        }
                        else {
                            ((CreativeContainer)this.container).itemList.add(ItemStack.EMPTY);
                        }
                    }
                }
                else {
                    ((CreativeContainer)this.container).itemList.addAll(hotbarStorageEntry5);
                }
            }
        }
        else if (itemGroup != ItemGroup.SEARCH) {
            itemGroup.appendItems(((CreativeContainer)this.container).itemList);
        }
        if (itemGroup == ItemGroup.INVENTORY) {
            final Container container3 = this.minecraft.player.playerContainer;
            if (this.slots == null) {
                this.slots = ImmutableList.copyOf(((CreativeContainer)this.container).slotList);
            }
            ((CreativeContainer)this.container).slotList.clear();
            for (int integer3 = 0; integer3 < container3.slotList.size(); ++integer3) {
                final Slot slot5 = new CreativeSlot(container3.slotList.get(integer3), integer3);
                ((CreativeContainer)this.container).slotList.add(slot5);
                if (integer3 >= 5 && integer3 < 9) {
                    final int integer4 = integer3 - 5;
                    final int integer5 = integer4 / 2;
                    final int integer6 = integer4 % 2;
                    slot5.xPosition = 54 + integer5 * 54;
                    slot5.yPosition = 6 + integer6 * 27;
                }
                else if (integer3 >= 0 && integer3 < 5) {
                    slot5.xPosition = -2000;
                    slot5.yPosition = -2000;
                }
                else if (integer3 == 45) {
                    slot5.xPosition = 35;
                    slot5.yPosition = 20;
                }
                else if (integer3 < container3.slotList.size()) {
                    final int integer4 = integer3 - 9;
                    final int integer5 = integer4 % 9;
                    final int integer6 = integer4 / 9;
                    slot5.xPosition = 9 + integer5 * 18;
                    if (integer3 >= 36) {
                        slot5.yPosition = 112;
                    }
                    else {
                        slot5.yPosition = 54 + integer6 * 18;
                    }
                }
            }
            this.deleteItemSlot = new Slot(CreativePlayerInventoryScreen.inventory, 0, 173, 112);
            ((CreativeContainer)this.container).slotList.add(this.deleteItemSlot);
        }
        else if (integer2 == ItemGroup.INVENTORY.getIndex()) {
            ((CreativeContainer)this.container).slotList.clear();
            ((CreativeContainer)this.container).slotList.addAll(this.slots);
            this.slots = null;
        }
        if (this.searchBox != null) {
            if (itemGroup == ItemGroup.SEARCH) {
                this.searchBox.setVisible(true);
                this.searchBox.d(false);
                this.searchBox.a(true);
                if (integer2 != itemGroup.getIndex()) {
                    this.searchBox.setText("");
                }
                this.e();
            }
            else {
                this.searchBox.setVisible(false);
                this.searchBox.d(true);
                this.searchBox.a(false);
                this.searchBox.setText("");
            }
        }
        this.scrollPosition = 0.0f;
        ((CreativeContainer)this.container).a(0.0f);
    }
    
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        if (!this.doRenderScrollBar()) {
            return false;
        }
        final int integer7 = (((CreativeContainer)this.container).itemList.size() + 9 - 1) / 9 - 5;
        this.scrollPosition -= (float)(amount / integer7);
        this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
        ((CreativeContainer)this.container).a(this.scrollPosition);
        return true;
    }
    
    @Override
    protected boolean isClickOutsideBounds(final double mouseX, final double mouseY, final int left, final int top, final int button) {
        final boolean boolean8 = mouseX < left || mouseY < top || mouseX >= left + this.containerWidth || mouseY >= top + this.containerHeight;
        return this.v = (boolean8 && !this.isClickInTab(ItemGroup.GROUPS[CreativePlayerInventoryScreen.selectedTab], mouseX, mouseY));
    }
    
    protected boolean c(final double double1, final double double3) {
        final int integer5 = this.left;
        final int integer6 = this.top;
        final int integer7 = integer5 + 175;
        final int integer8 = integer6 + 18;
        final int integer9 = integer7 + 14;
        final int integer10 = integer8 + 112;
        return double1 >= integer7 && double3 >= integer8 && double1 < integer9 && double3 < integer10;
    }
    
    @Override
    public boolean mouseDragged(final double mouseX, final double mouseY, final int button, final double deltaX, final double deltaY) {
        if (this.p) {
            final int integer10 = this.top + 18;
            final int integer11 = integer10 + 112;
            this.scrollPosition = ((float)mouseY - integer10 - 7.5f) / (integer11 - integer10 - 15.0f);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            ((CreativeContainer)this.container).a(this.scrollPosition);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
        for (final ItemGroup itemGroup7 : ItemGroup.GROUPS) {
            if (this.a(itemGroup7, mouseX, mouseY)) {
                break;
            }
        }
        if (this.deleteItemSlot != null && CreativePlayerInventoryScreen.selectedTab == ItemGroup.INVENTORY.getIndex() && this.isPointWithinBounds(this.deleteItemSlot.xPosition, this.deleteItemSlot.yPosition, 16, 16, mouseX, mouseY)) {
            this.renderTooltip(I18n.translate("inventory.binSlot"), mouseX, mouseY);
        }
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();
        this.drawMouseoverTooltip(mouseX, mouseY);
    }
    
    @Override
    protected void renderTooltip(final ItemStack stack, final int x, final int y) {
        if (CreativePlayerInventoryScreen.selectedTab == ItemGroup.SEARCH.getIndex()) {
            final List<TextComponent> list4 = stack.getTooltipText(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.NORMAL);
            final List<String> list5 = Lists.newArrayListWithCapacity(list4.size());
            for (final TextComponent textComponent7 : list4) {
                list5.add(textComponent7.getFormattedText());
            }
            final Item item6 = stack.getItem();
            ItemGroup itemGroup7 = item6.getItemGroup();
            if (itemGroup7 == null && item6 == Items.nZ) {
                final Map<Enchantment, Integer> map8 = EnchantmentHelper.getEnchantments(stack);
                if (map8.size() == 1) {
                    final Enchantment enchantment9 = map8.keySet().iterator().next();
                    for (final ItemGroup itemGroup8 : ItemGroup.GROUPS) {
                        if (itemGroup8.containsEnchantments(enchantment9.type)) {
                            itemGroup7 = itemGroup8;
                            break;
                        }
                    }
                }
            }
            final Item object;
            final List<String> list6;
            this.w.forEach((identifier, tag) -> {
                if (tag.contains(object)) {
                    list6.add(1, "" + TextFormat.r + TextFormat.f + "#" + identifier);
                }
                return;
            });
            if (itemGroup7 != null) {
                list5.add(1, "" + TextFormat.r + TextFormat.j + I18n.translate(itemGroup7.getTranslationKey()));
            }
            for (int integer8 = 0; integer8 < list5.size(); ++integer8) {
                if (integer8 == 0) {
                    list5.set(integer8, stack.getRarity().formatting + list5.get(integer8));
                }
                else {
                    list5.set(integer8, TextFormat.h + list5.get(integer8));
                }
            }
            this.renderTooltip(list5, x, y);
        }
        else {
            super.renderTooltip(stack, x, y);
        }
    }
    
    @Override
    protected void drawBackground(final float delta, final int mouseX, final int mouseY) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GuiLighting.enableForItems();
        final ItemGroup itemGroup4 = ItemGroup.GROUPS[CreativePlayerInventoryScreen.selectedTab];
        for (final ItemGroup itemGroup5 : ItemGroup.GROUPS) {
            this.minecraft.getTextureManager().bindTexture(CreativePlayerInventoryScreen.TEXTURE);
            if (itemGroup5.getIndex() != CreativePlayerInventoryScreen.selectedTab) {
                this.a(itemGroup5);
            }
        }
        this.minecraft.getTextureManager().bindTexture(new Identifier("textures/gui/container/creative_inventory/tab_" + itemGroup4.getTexture()));
        this.blit(this.left, this.top, 0, 0, this.containerWidth, this.containerHeight);
        this.searchBox.render(mouseX, mouseY, delta);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int integer5 = this.left + 175;
        final int integer6 = this.top + 18;
        final int integer7 = integer6 + 112;
        this.minecraft.getTextureManager().bindTexture(CreativePlayerInventoryScreen.TEXTURE);
        if (itemGroup4.hasScrollbar()) {
            this.blit(integer5, integer6 + (int)((integer7 - integer6 - 17) * this.scrollPosition), 232 + (this.doRenderScrollBar() ? 0 : 12), 0, 12, 15);
        }
        this.a(itemGroup4);
        if (itemGroup4 == ItemGroup.INVENTORY) {
            PlayerInventoryScreen.drawEntity(this.left + 88, this.top + 45, 20, (float)(this.left + 88 - mouseX), (float)(this.top + 45 - 30 - mouseY), this.minecraft.player);
        }
    }
    
    protected boolean isClickInTab(final ItemGroup itemGroup, final double double2, final double double4) {
        final int integer6 = itemGroup.getColumn();
        int integer7 = 28 * integer6;
        int integer8 = 0;
        if (itemGroup.isSpecial()) {
            integer7 = this.containerWidth - 28 * (6 - integer6) + 2;
        }
        else if (integer6 > 0) {
            integer7 += integer6;
        }
        if (itemGroup.isTopRow()) {
            integer8 -= 32;
        }
        else {
            integer8 += this.containerHeight;
        }
        return double2 >= integer7 && double2 <= integer7 + 28 && double4 >= integer8 && double4 <= integer8 + 32;
    }
    
    protected boolean a(final ItemGroup itemGroup, final int integer2, final int integer3) {
        final int integer4 = itemGroup.getColumn();
        int integer5 = 28 * integer4;
        int integer6 = 0;
        if (itemGroup.isSpecial()) {
            integer5 = this.containerWidth - 28 * (6 - integer4) + 2;
        }
        else if (integer4 > 0) {
            integer5 += integer4;
        }
        if (itemGroup.isTopRow()) {
            integer6 -= 32;
        }
        else {
            integer6 += this.containerHeight;
        }
        if (this.isPointWithinBounds(integer5 + 3, integer6 + 3, 23, 27, integer2, integer3)) {
            this.renderTooltip(I18n.translate(itemGroup.getTranslationKey()), integer2, integer3);
            return true;
        }
        return false;
    }
    
    protected void a(final ItemGroup itemGroup) {
        final boolean boolean2 = itemGroup.getIndex() == CreativePlayerInventoryScreen.selectedTab;
        final boolean boolean3 = itemGroup.isTopRow();
        final int integer4 = itemGroup.getColumn();
        final int integer5 = integer4 * 28;
        int integer6 = 0;
        int integer7 = this.left + 28 * integer4;
        int integer8 = this.top;
        final int integer9 = 32;
        if (boolean2) {
            integer6 += 32;
        }
        if (itemGroup.isSpecial()) {
            integer7 = this.left + this.containerWidth - 28 * (6 - integer4);
        }
        else if (integer4 > 0) {
            integer7 += integer4;
        }
        if (boolean3) {
            integer8 -= 28;
        }
        else {
            integer6 += 64;
            integer8 += this.containerHeight - 4;
        }
        GlStateManager.disableLighting();
        this.blit(integer7, integer8, integer5, integer6, 28, 32);
        this.blitOffset = 100;
        this.itemRenderer.zOffset = 100.0f;
        integer7 += 6;
        integer8 += 8 + (boolean3 ? 1 : -1);
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        final ItemStack itemStack10 = itemGroup.getIcon();
        this.itemRenderer.renderGuiItem(itemStack10, integer7, integer8);
        this.itemRenderer.renderGuiItemOverlay(this.font, itemStack10, integer7, integer8);
        GlStateManager.disableLighting();
        this.itemRenderer.zOffset = 0.0f;
        this.blitOffset = 0;
    }
    
    public int c() {
        return CreativePlayerInventoryScreen.selectedTab;
    }
    
    public static void onHotbarKeyPress(final MinecraftClient minecraftClient, final int integer, final boolean boolean3, final boolean boolean4) {
        final ClientPlayerEntity clientPlayerEntity5 = minecraftClient.player;
        final HotbarStorage hotbarStorage6 = minecraftClient.getCreativeHotbarStorage();
        final HotbarStorageEntry hotbarStorageEntry7 = hotbarStorage6.getSavedHotbar(integer);
        if (boolean3) {
            for (int integer2 = 0; integer2 < PlayerInventory.getHotbarSize(); ++integer2) {
                final ItemStack itemStack9 = hotbarStorageEntry7.get(integer2).copy();
                clientPlayerEntity5.inventory.setInvStack(integer2, itemStack9);
                minecraftClient.interactionManager.clickCreativeStack(itemStack9, 36 + integer2);
            }
            clientPlayerEntity5.playerContainer.sendContentUpdates();
        }
        else if (boolean4) {
            for (int integer2 = 0; integer2 < PlayerInventory.getHotbarSize(); ++integer2) {
                hotbarStorageEntry7.set(integer2, clientPlayerEntity5.inventory.getInvStack(integer2).copy());
            }
            final String string8 = minecraftClient.options.keysHotbar[integer].getLocalizedName();
            final String string9 = minecraftClient.options.keyLoadToolbarActivator.getLocalizedName();
            minecraftClient.inGameHud.setOverlayMessage(new TranslatableTextComponent("inventory.hotbarSaved", new Object[] { string9, string8 }), false);
            hotbarStorage6.save();
        }
    }
    
    static {
        TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
        inventory = new BasicInventory(45);
        CreativePlayerInventoryScreen.selectedTab = ItemGroup.BUILDING_BLOCKS.getIndex();
    }
    
    @Environment(EnvType.CLIENT)
    public static class CreativeContainer extends Container
    {
        public final DefaultedList<ItemStack> itemList;
        
        public CreativeContainer(final PlayerEntity playerEntity) {
            super(null, 0);
            this.itemList = DefaultedList.<ItemStack>create();
            final PlayerInventory playerInventory2 = playerEntity.inventory;
            for (int integer3 = 0; integer3 < 5; ++integer3) {
                for (int integer4 = 0; integer4 < 9; ++integer4) {
                    this.addSlot(new a(CreativePlayerInventoryScreen.inventory, integer3 * 9 + integer4, 9 + integer4 * 18, 18 + integer3 * 18));
                }
            }
            for (int integer3 = 0; integer3 < 9; ++integer3) {
                this.addSlot(new Slot(playerInventory2, integer3, 9 + integer3 * 18, 112));
            }
            this.a(0.0f);
        }
        
        @Override
        public boolean canUse(final PlayerEntity player) {
            return true;
        }
        
        public void a(final float float1) {
            final int integer2 = (this.itemList.size() + 9 - 1) / 9 - 5;
            int integer3 = (int)(float1 * integer2 + 0.5);
            if (integer3 < 0) {
                integer3 = 0;
            }
            for (int integer4 = 0; integer4 < 5; ++integer4) {
                for (int integer5 = 0; integer5 < 9; ++integer5) {
                    final int integer6 = integer5 + (integer4 + integer3) * 9;
                    if (integer6 >= 0 && integer6 < this.itemList.size()) {
                        CreativePlayerInventoryScreen.inventory.setInvStack(integer5 + integer4 * 9, this.itemList.get(integer6));
                    }
                    else {
                        CreativePlayerInventoryScreen.inventory.setInvStack(integer5 + integer4 * 9, ItemStack.EMPTY);
                    }
                }
            }
        }
        
        public boolean e() {
            return this.itemList.size() > 45;
        }
        
        @Override
        public ItemStack transferSlot(final PlayerEntity player, final int invSlot) {
            if (invSlot >= this.slotList.size() - 9 && invSlot < this.slotList.size()) {
                final Slot slot3 = this.slotList.get(invSlot);
                if (slot3 != null && slot3.hasStack()) {
                    slot3.setStack(ItemStack.EMPTY);
                }
            }
            return ItemStack.EMPTY;
        }
        
        @Override
        public boolean canInsertIntoSlot(final ItemStack stack, final Slot slot) {
            return slot.inventory != CreativePlayerInventoryScreen.inventory;
        }
        
        @Override
        public boolean canInsertIntoSlot(final Slot slot) {
            return slot.inventory != CreativePlayerInventoryScreen.inventory;
        }
    }
    
    @Environment(EnvType.CLIENT)
    class CreativeSlot extends Slot
    {
        private final Slot slot;
        
        public CreativeSlot(final Slot slot, final int integer) {
            super(slot.inventory, integer, 0, 0);
            this.slot = slot;
        }
        
        @Override
        public ItemStack onTakeItem(final PlayerEntity player, final ItemStack stack) {
            this.slot.onTakeItem(player, stack);
            return stack;
        }
        
        @Override
        public boolean canInsert(final ItemStack stack) {
            return this.slot.canInsert(stack);
        }
        
        @Override
        public ItemStack getStack() {
            return this.slot.getStack();
        }
        
        @Override
        public boolean hasStack() {
            return this.slot.hasStack();
        }
        
        @Override
        public void setStack(final ItemStack itemStack) {
            this.slot.setStack(itemStack);
        }
        
        @Override
        public void markDirty() {
            this.slot.markDirty();
        }
        
        @Override
        public int getMaxStackAmount() {
            return this.slot.getMaxStackAmount();
        }
        
        @Override
        public int getMaxStackAmount(final ItemStack itemStack) {
            return this.slot.getMaxStackAmount(itemStack);
        }
        
        @Nullable
        @Override
        public String getBackgroundSprite() {
            return this.slot.getBackgroundSprite();
        }
        
        @Override
        public ItemStack takeStack(final int amount) {
            return this.slot.takeStack(amount);
        }
        
        @Override
        public boolean doDrawHoveringEffect() {
            return this.slot.doDrawHoveringEffect();
        }
        
        @Override
        public boolean canTakeItems(final PlayerEntity playerEntity) {
            return this.slot.canTakeItems(playerEntity);
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class a extends Slot
    {
        public a(final Inventory inventory, final int invSlot, final int xPosition, final int yPosition) {
            super(inventory, invSlot, xPosition, yPosition);
        }
        
        @Override
        public boolean canTakeItems(final PlayerEntity playerEntity) {
            if (super.canTakeItems(playerEntity) && this.hasStack()) {
                return this.getStack().getSubCompoundTag("CustomCreativeLock") == null;
            }
            return !this.hasStack();
        }
    }
}
