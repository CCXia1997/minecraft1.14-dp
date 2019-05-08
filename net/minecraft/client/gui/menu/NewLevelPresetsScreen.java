package net.minecraft.client.gui.menu;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.GuiLighting;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.NarratorManager;
import javax.annotation.Nullable;
import java.util.Collections;
import net.minecraft.item.Items;
import java.util.Arrays;
import net.minecraft.world.biome.Biomes;
import net.minecraft.block.Blocks;
import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.Map;
import com.google.common.collect.Maps;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.biome.Biome;
import net.minecraft.item.ItemProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class NewLevelPresetsScreen extends Screen
{
    private static final List<SuperflatPreset> presets;
    private final CustomizeFlatLevelScreen parent;
    private String shareText;
    private String listText;
    private SuperflatPresetsListWidget e;
    private ButtonWidget f;
    private TextFieldWidget customPresetField;
    
    public NewLevelPresetsScreen(final CustomizeFlatLevelScreen parent) {
        super(new TranslatableTextComponent("createWorld.customize.presets.title", new Object[0]));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.shareText = I18n.translate("createWorld.customize.presets.share");
        this.listText = I18n.translate("createWorld.customize.presets.list");
        (this.customPresetField = new TextFieldWidget(this.font, 50, 40, this.width - 100, 20, this.shareText)).setMaxLength(1230);
        this.customPresetField.setText(this.parent.getConfigString());
        this.children.add(this.customPresetField);
        this.e = new SuperflatPresetsListWidget();
        this.children.add(this.e);
        this.f = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("createWorld.customize.presets.select"), buttonWidget -> {
            this.parent.a(this.customPresetField.getText());
            this.minecraft.openScreen(this.parent);
            return;
        }));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent)));
        this.a(this.e.getSelected() != null);
    }
    
    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amount) {
        return this.e.mouseScrolled(mouseX, mouseY, amount);
    }
    
    @Override
    public void resize(final MinecraftClient client, final int width, final int height) {
        final String string4 = this.customPresetField.getText();
        this.init(client, width, height);
        this.customPresetField.setText(string4);
    }
    
    @Override
    public void removed() {
        this.minecraft.keyboard.enableRepeatEvents(false);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.e.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
        this.drawString(this.font, this.shareText, 50, 30, 10526880);
        this.drawString(this.font, this.listText, 50, 70, 10526880);
        this.customPresetField.render(mouseX, mouseY, delta);
        super.render(mouseX, mouseY, delta);
    }
    
    @Override
    public void tick() {
        this.customPresetField.tick();
        super.tick();
    }
    
    public void a(final boolean boolean1) {
        this.f.active = (boolean1 || this.customPresetField.getText().length() > 1);
    }
    
    private static void addPreset(final String string, final ItemProvider itemProvider, final Biome biome, final List<String> list, final FlatChunkGeneratorLayer... arr) {
        final FlatChunkGeneratorConfig flatChunkGeneratorConfig6 = ChunkGeneratorType.e.createSettings();
        for (int integer7 = arr.length - 1; integer7 >= 0; --integer7) {
            flatChunkGeneratorConfig6.getLayers().add(arr[integer7]);
        }
        flatChunkGeneratorConfig6.setBiome(biome);
        flatChunkGeneratorConfig6.updateLayerBlocks();
        for (final String string2 : list) {
            flatChunkGeneratorConfig6.getStructures().put(string2, Maps.newHashMap());
        }
        NewLevelPresetsScreen.presets.add(new SuperflatPreset(itemProvider.getItem(), string, flatChunkGeneratorConfig6.toString()));
    }
    
    static {
        presets = Lists.newArrayList();
        addPreset(I18n.translate("createWorld.customize.preset.classic_flat"), Blocks.i, Biomes.c, Arrays.<String>asList("village"), new FlatChunkGeneratorLayer(1, Blocks.i), new FlatChunkGeneratorLayer(2, Blocks.j), new FlatChunkGeneratorLayer(1, Blocks.z));
        addPreset(I18n.translate("createWorld.customize.preset.tunnelers_dream"), Blocks.b, Biomes.e, Arrays.<String>asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"), new FlatChunkGeneratorLayer(1, Blocks.i), new FlatChunkGeneratorLayer(5, Blocks.j), new FlatChunkGeneratorLayer(230, Blocks.b), new FlatChunkGeneratorLayer(1, Blocks.z));
        addPreset(I18n.translate("createWorld.customize.preset.water_world"), Items.ky, Biomes.z, Arrays.<String>asList("biome_1", "oceanmonument"), new FlatChunkGeneratorLayer(90, Blocks.A), new FlatChunkGeneratorLayer(5, Blocks.C), new FlatChunkGeneratorLayer(5, Blocks.j), new FlatChunkGeneratorLayer(5, Blocks.b), new FlatChunkGeneratorLayer(1, Blocks.z));
        addPreset(I18n.translate("createWorld.customize.preset.overworld"), Blocks.aQ, Biomes.c, Arrays.<String>asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake", "pillager_outpost"), new FlatChunkGeneratorLayer(1, Blocks.i), new FlatChunkGeneratorLayer(3, Blocks.j), new FlatChunkGeneratorLayer(59, Blocks.b), new FlatChunkGeneratorLayer(1, Blocks.z));
        addPreset(I18n.translate("createWorld.customize.preset.snowy_kingdom"), Blocks.cA, Biomes.n, Arrays.<String>asList("village", "biome_1"), new FlatChunkGeneratorLayer(1, Blocks.cA), new FlatChunkGeneratorLayer(1, Blocks.i), new FlatChunkGeneratorLayer(3, Blocks.j), new FlatChunkGeneratorLayer(59, Blocks.b), new FlatChunkGeneratorLayer(1, Blocks.z));
        addPreset(I18n.translate("createWorld.customize.preset.bottomless_pit"), Items.jH, Biomes.c, Arrays.<String>asList("village", "biome_1"), new FlatChunkGeneratorLayer(1, Blocks.i), new FlatChunkGeneratorLayer(3, Blocks.j), new FlatChunkGeneratorLayer(2, Blocks.m));
        addPreset(I18n.translate("createWorld.customize.preset.desert"), Blocks.C, Biomes.d, Arrays.<String>asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"), new FlatChunkGeneratorLayer(8, Blocks.C), new FlatChunkGeneratorLayer(52, Blocks.as), new FlatChunkGeneratorLayer(3, Blocks.b), new FlatChunkGeneratorLayer(1, Blocks.z));
        addPreset(I18n.translate("createWorld.customize.preset.redstone_ready"), Items.kC, Biomes.d, Collections.<String>emptyList(), new FlatChunkGeneratorLayer(52, Blocks.as), new FlatChunkGeneratorLayer(3, Blocks.b), new FlatChunkGeneratorLayer(1, Blocks.z));
        addPreset(I18n.translate("createWorld.customize.preset.the_void"), Blocks.gg, Biomes.aa, Arrays.<String>asList("decoration"), new FlatChunkGeneratorLayer(1, Blocks.AIR));
    }
    
    @Environment(EnvType.CLIENT)
    class SuperflatPresetsListWidget extends AlwaysSelectedEntryListWidget<SuperflatPresetItem>
    {
        public SuperflatPresetsListWidget() {
            super(NewLevelPresetsScreen.this.minecraft, NewLevelPresetsScreen.this.width, NewLevelPresetsScreen.this.height, 80, NewLevelPresetsScreen.this.height - 37, 24);
            for (int integer2 = 0; integer2 < NewLevelPresetsScreen.presets.size(); ++integer2) {
                this.addEntry(new SuperflatPresetItem());
            }
        }
        
        public void a(@Nullable final SuperflatPresetItem entry) {
            super.setSelected(entry);
            if (entry != null) {
                NarratorManager.INSTANCE.a(new TranslatableTextComponent("narrator.select", new Object[] { NewLevelPresetsScreen.presets.get(this.children().indexOf(entry)).b }).getString());
            }
        }
        
        @Override
        protected void moveSelection(final int amount) {
            super.moveSelection(amount);
            NewLevelPresetsScreen.this.a(true);
        }
        
        @Override
        protected boolean isFocused() {
            return NewLevelPresetsScreen.this.getFocused() == this;
        }
        
        @Override
        public boolean keyPressed(final int keyCode, final int scanCode, final int modifiers) {
            if (super.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if ((keyCode == 257 || keyCode == 335) && this.getSelected() != null) {
                this.getSelected().a();
            }
            return false;
        }
        
        @Environment(EnvType.CLIENT)
        public class SuperflatPresetItem extends Entry<SuperflatPresetItem>
        {
            @Override
            public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
                final SuperflatPreset superflatPreset10 = NewLevelPresetsScreen.presets.get(index);
                this.a(integer3, integer2, superflatPreset10.a);
                NewLevelPresetsScreen.this.font.draw(superflatPreset10.b, (float)(integer3 + 18 + 5), (float)(integer2 + 6), 16777215);
            }
            
            @Override
            public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
                if (button == 0) {
                    this.a();
                }
                return false;
            }
            
            private void a() {
                SuperflatPresetsListWidget.this.a(this);
                NewLevelPresetsScreen.this.a(true);
                NewLevelPresetsScreen.this.customPresetField.setText(NewLevelPresetsScreen.presets.get(SuperflatPresetsListWidget.this.children().indexOf(this)).c);
                NewLevelPresetsScreen.this.customPresetField.d();
            }
            
            private void a(final int integer1, final int integer2, final Item item) {
                this.a(integer1 + 1, integer2 + 1);
                GlStateManager.enableRescaleNormal();
                GuiLighting.enableForItems();
                NewLevelPresetsScreen.this.itemRenderer.renderGuiItemIcon(new ItemStack(item), integer1 + 2, integer2 + 2);
                GuiLighting.disable();
                GlStateManager.disableRescaleNormal();
            }
            
            private void a(final int integer1, final int integer2) {
                GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                SuperflatPresetsListWidget.this.minecraft.getTextureManager().bindTexture(DrawableHelper.STATS_ICON_LOCATION);
                DrawableHelper.blit(integer1, integer2, NewLevelPresetsScreen.this.blitOffset, 0.0f, 0.0f, 18, 18, 128, 128);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    static class SuperflatPreset
    {
        public final Item a;
        public final String b;
        public final String c;
        
        public SuperflatPreset(final Item item, final String string2, final String string3) {
            this.a = item;
            this.b = string2;
            this.c = string3;
        }
    }
}
