package net.minecraft.client.gui.menu;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GuiLighting;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.item.Items;
import javax.annotation.Nullable;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import java.util.List;
import net.minecraft.client.resource.language.I18n;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class CustomizeFlatLevelScreen extends Screen
{
    private final NewLevelScreen parent;
    private FlatChunkGeneratorConfig config;
    private String tileText;
    private String heightText;
    private SuperflatLayersListWidget layers;
    private ButtonWidget widgetButtonRemoveLayer;
    
    public CustomizeFlatLevelScreen(final NewLevelScreen parent, final CompoundTag generatorOptions) {
        super(new TranslatableTextComponent("createWorld.customize.flat.title", new Object[0]));
        this.config = FlatChunkGeneratorConfig.getDefaultConfig();
        this.parent = parent;
        this.a(generatorOptions);
    }
    
    public String getConfigString() {
        return this.config.toString();
    }
    
    public CompoundTag b() {
        return (CompoundTag)this.config.toDynamic((com.mojang.datafixers.types.DynamicOps<Object>)NbtOps.INSTANCE).getValue();
    }
    
    public void a(final String string) {
        this.config = FlatChunkGeneratorConfig.fromString(string);
    }
    
    public void a(final CompoundTag compoundTag) {
        this.config = FlatChunkGeneratorConfig.fromDynamic(new Dynamic((DynamicOps)NbtOps.INSTANCE, compoundTag));
    }
    
    @Override
    protected void init() {
        this.tileText = I18n.translate("createWorld.customize.flat.tile");
        this.heightText = I18n.translate("createWorld.customize.flat.height");
        this.layers = new SuperflatLayersListWidget();
        this.children.add(this.layers);
        List<FlatChunkGeneratorLayer> list2;
        int integer3;
        int integer4;
        this.widgetButtonRemoveLayer = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, this.height - 52, 150, 20, I18n.translate("createWorld.customize.flat.removeLayer"), buttonWidget -> {
            if (!this.d()) {
                return;
            }
            else {
                list2 = this.config.getLayers();
                integer3 = this.layers.children().indexOf(((EntryListWidget<Object>)this.layers).getSelected());
                integer4 = list2.size() - integer3 - 1;
                list2.remove(integer4);
                this.layers.a(list2.isEmpty() ? null : this.layers.children().get(Math.min(integer3, list2.size() - 1)));
                this.config.updateLayerBlocks();
                this.c();
                return;
            }
        }));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 5, this.height - 52, 150, 20, I18n.translate("createWorld.customize.presets"), buttonWidget -> {
            this.minecraft.openScreen(new NewLevelPresetsScreen(this));
            this.config.updateLayerBlocks();
            this.c();
            return;
        }));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done"), buttonWidget -> {
            this.parent.generatorOptionsTag = this.b();
            this.minecraft.openScreen(this.parent);
            this.config.updateLayerBlocks();
            this.c();
            return;
        }));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> {
            this.minecraft.openScreen(this.parent);
            this.config.updateLayerBlocks();
            this.c();
            return;
        }));
        this.config.updateLayerBlocks();
        this.c();
    }
    
    public void c() {
        this.widgetButtonRemoveLayer.active = this.d();
        this.layers.a();
    }
    
    private boolean d() {
        return this.layers.getSelected() != null;
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.layers.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
        final int integer4 = this.width / 2 - 92 - 16;
        this.drawString(this.font, this.tileText, integer4, 32, 16777215);
        this.drawString(this.font, this.heightText, integer4 + 2 + 213 - this.font.getStringWidth(this.heightText), 32, 16777215);
        super.render(mouseX, mouseY, delta);
    }
    
    @Environment(EnvType.CLIENT)
    class SuperflatLayersListWidget extends AlwaysSelectedEntryListWidget<SuperflatLayerItem>
    {
        public SuperflatLayersListWidget() {
            super(CustomizeFlatLevelScreen.this.minecraft, CustomizeFlatLevelScreen.this.width, CustomizeFlatLevelScreen.this.height, 43, CustomizeFlatLevelScreen.this.height - 60, 24);
            for (int integer2 = 0; integer2 < CustomizeFlatLevelScreen.this.config.getLayers().size(); ++integer2) {
                this.addEntry(new SuperflatLayerItem());
            }
        }
        
        public void a(@Nullable final SuperflatLayerItem entry) {
            super.setSelected(entry);
            if (entry != null) {
                final FlatChunkGeneratorLayer flatChunkGeneratorLayer2 = CustomizeFlatLevelScreen.this.config.getLayers().get(CustomizeFlatLevelScreen.this.config.getLayers().size() - this.children().indexOf(entry) - 1);
                final Item item3 = flatChunkGeneratorLayer2.getBlockState().getBlock().getItem();
                if (item3 != Items.AIR) {
                    NarratorManager.INSTANCE.a(new TranslatableTextComponent("narrator.select", new Object[] { item3.getTranslatedNameTrimmed(new ItemStack(item3)) }).getString());
                }
            }
        }
        
        @Override
        protected void moveSelection(final int amount) {
            super.moveSelection(amount);
            CustomizeFlatLevelScreen.this.c();
        }
        
        @Override
        protected boolean isFocused() {
            return CustomizeFlatLevelScreen.this.getFocused() == this;
        }
        
        @Override
        protected int getScrollbarPosition() {
            return this.width - 70;
        }
        
        public void a() {
            final int integer1 = this.children().indexOf(((EntryListWidget<Object>)this).getSelected());
            this.clearEntries();
            for (int integer2 = 0; integer2 < CustomizeFlatLevelScreen.this.config.getLayers().size(); ++integer2) {
                this.addEntry(new SuperflatLayerItem());
            }
            final List<SuperflatLayerItem> list2 = this.children();
            if (integer1 >= 0 && integer1 < list2.size()) {
                this.a(list2.get(integer1));
            }
        }
        
        @Environment(EnvType.CLIENT)
        class SuperflatLayerItem extends Entry<SuperflatLayerItem>
        {
            private SuperflatLayerItem() {
            }
            
            @Override
            public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
                final FlatChunkGeneratorLayer flatChunkGeneratorLayer10 = CustomizeFlatLevelScreen.this.config.getLayers().get(CustomizeFlatLevelScreen.this.config.getLayers().size() - index - 1);
                final BlockState blockState11 = flatChunkGeneratorLayer10.getBlockState();
                final Block block12 = blockState11.getBlock();
                Item item13 = block12.getItem();
                if (item13 == Items.AIR) {
                    if (block12 == Blocks.A) {
                        item13 = Items.ky;
                    }
                    else if (block12 == Blocks.B) {
                        item13 = Items.kz;
                    }
                }
                final ItemStack itemStack14 = new ItemStack(item13);
                final String string15 = item13.getTranslatedNameTrimmed(itemStack14).getFormattedText();
                this.a(integer3, integer2, itemStack14);
                CustomizeFlatLevelScreen.this.font.draw(string15, (float)(integer3 + 18 + 5), (float)(integer2 + 3), 16777215);
                String string16;
                if (index == 0) {
                    string16 = I18n.translate("createWorld.customize.flat.layer.top", flatChunkGeneratorLayer10.getThickness());
                }
                else if (index == CustomizeFlatLevelScreen.this.config.getLayers().size() - 1) {
                    string16 = I18n.translate("createWorld.customize.flat.layer.bottom", flatChunkGeneratorLayer10.getThickness());
                }
                else {
                    string16 = I18n.translate("createWorld.customize.flat.layer", flatChunkGeneratorLayer10.getThickness());
                }
                CustomizeFlatLevelScreen.this.font.draw(string16, (float)(integer3 + 2 + 213 - CustomizeFlatLevelScreen.this.font.getStringWidth(string16)), (float)(integer2 + 3), 16777215);
            }
            
            @Override
            public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
                if (button == 0) {
                    SuperflatLayersListWidget.this.a(this);
                    CustomizeFlatLevelScreen.this.c();
                    return true;
                }
                return false;
            }
            
            private void a(final int integer1, final int integer2, final ItemStack itemStack) {
                this.a(integer1 + 1, integer2 + 1);
                GlStateManager.enableRescaleNormal();
                if (!itemStack.isEmpty()) {
                    GuiLighting.enableForItems();
                    CustomizeFlatLevelScreen.this.itemRenderer.renderGuiItemIcon(itemStack, integer1 + 2, integer2 + 2);
                    GuiLighting.disable();
                }
                GlStateManager.disableRescaleNormal();
            }
            
            private void a(final int integer1, final int integer2) {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                SuperflatLayersListWidget.this.minecraft.getTextureManager().bindTexture(DrawableHelper.STATS_ICON_LOCATION);
                DrawableHelper.blit(integer1, integer2, CustomizeFlatLevelScreen.this.blitOffset, 0.0f, 0.0f, 18, 18, 128, 128);
            }
        }
    }
}
