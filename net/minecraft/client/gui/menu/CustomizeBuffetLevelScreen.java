package net.minecraft.client.gui.menu;

import java.util.AbstractList;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.util.NarratorManager;
import javax.annotation.Nullable;
import java.util.Comparator;
import net.minecraft.world.biome.Biome;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.biome.source.BiomeSourceType;
import net.minecraft.util.registry.Registry;
import net.minecraft.nbt.ListTag;
import java.util.Objects;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class CustomizeBuffetLevelScreen extends Screen
{
    private static final List<Identifier> CHUNK_GENERATOR_TYPES;
    private final NewLevelScreen parent;
    private final CompoundTag generatorOptionsTag;
    private BuffetBiomesListWidget biomSelectionList;
    private int biomsListLength;
    private ButtonWidget confirmButton;
    
    public CustomizeBuffetLevelScreen(final NewLevelScreen parent, final CompoundTag generatorOptionsTag) {
        super(new TranslatableTextComponent("createWorld.customize.buffet.title", new Object[0]));
        this.parent = parent;
        this.generatorOptionsTag = generatorOptionsTag;
    }
    
    @Override
    protected void init() {
        this.minecraft.keyboard.enableRepeatEvents(true);
        this.<ButtonWidget>addButton(new ButtonWidget((this.width - 200) / 2, 40, 200, 20, I18n.translate("createWorld.customize.buffet.generatortype") + " " + I18n.translate(SystemUtil.createTranslationKey("generator", CustomizeBuffetLevelScreen.CHUNK_GENERATOR_TYPES.get(this.biomsListLength))), buttonWidget -> {
            ++this.biomsListLength;
            if (this.biomsListLength >= CustomizeBuffetLevelScreen.CHUNK_GENERATOR_TYPES.size()) {
                this.biomsListLength = 0;
            }
            buttonWidget.setMessage(I18n.translate("createWorld.customize.buffet.generatortype") + " " + I18n.translate(SystemUtil.createTranslationKey("generator", CustomizeBuffetLevelScreen.CHUNK_GENERATOR_TYPES.get(this.biomsListLength))));
            return;
        }));
        this.biomSelectionList = new BuffetBiomesListWidget();
        this.children.add(this.biomSelectionList);
        this.confirmButton = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 155, this.height - 28, 150, 20, I18n.translate("gui.done"), buttonWidget -> {
            this.parent.generatorOptionsTag = this.getGeneratorTag();
            this.minecraft.openScreen(this.parent);
            return;
        }));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 5, this.height - 28, 150, 20, I18n.translate("gui.cancel"), buttonWidget -> this.minecraft.openScreen(this.parent)));
        this.initListSelectLogic();
        this.refreshConfirmButton();
    }
    
    private void initListSelectLogic() {
        if (this.generatorOptionsTag.containsKey("chunk_generator", 10) && this.generatorOptionsTag.getCompound("chunk_generator").containsKey("type", 8)) {
            final Identifier identifier1 = new Identifier(this.generatorOptionsTag.getCompound("chunk_generator").getString("type"));
            for (int integer2 = 0; integer2 < CustomizeBuffetLevelScreen.CHUNK_GENERATOR_TYPES.size(); ++integer2) {
                if (CustomizeBuffetLevelScreen.CHUNK_GENERATOR_TYPES.get(integer2).equals(identifier1)) {
                    this.biomsListLength = integer2;
                    break;
                }
            }
        }
        if (this.generatorOptionsTag.containsKey("biome_source", 10) && this.generatorOptionsTag.getCompound("biome_source").containsKey("biomes", 9)) {
            final ListTag listTag1 = this.generatorOptionsTag.getCompound("biome_source").getList("biomes", 8);
            for (int integer2 = 0; integer2 < listTag1.size(); ++integer2) {
                final Identifier identifier2 = new Identifier(listTag1.getString(integer2));
                this.biomSelectionList.a(this.biomSelectionList.children().stream().filter(buffetBiomeItem -> Objects.equals(buffetBiomeItem.biome, identifier2)).findFirst().orElse(null));
            }
        }
        this.generatorOptionsTag.remove("chunk_generator");
        this.generatorOptionsTag.remove("biome_source");
    }
    
    private CompoundTag getGeneratorTag() {
        final CompoundTag compoundTag1 = new CompoundTag();
        final CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.putString("type", Registry.BIOME_SOURCE_TYPE.getId(BiomeSourceType.FIXED).toString());
        final CompoundTag compoundTag3 = new CompoundTag();
        final ListTag listTag4 = new ListTag();
        ((AbstractList<StringTag>)listTag4).add(new StringTag(this.biomSelectionList.getSelected().biome.toString()));
        compoundTag3.put("biomes", listTag4);
        compoundTag2.put("options", compoundTag3);
        final CompoundTag compoundTag4 = new CompoundTag();
        final CompoundTag compoundTag5 = new CompoundTag();
        compoundTag4.putString("type", CustomizeBuffetLevelScreen.CHUNK_GENERATOR_TYPES.get(this.biomsListLength).toString());
        compoundTag5.putString("default_block", "minecraft:stone");
        compoundTag5.putString("default_fluid", "minecraft:water");
        compoundTag4.put("options", compoundTag5);
        compoundTag1.put("biome_source", compoundTag2);
        compoundTag1.put("chunk_generator", compoundTag4);
        return compoundTag1;
    }
    
    public void refreshConfirmButton() {
        this.confirmButton.active = (this.biomSelectionList.getSelected() != null);
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderDirtBackground(0);
        this.biomSelectionList.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 8, 16777215);
        this.drawCenteredString(this.font, I18n.translate("createWorld.customize.buffet.generator"), this.width / 2, 30, 10526880);
        this.drawCenteredString(this.font, I18n.translate("createWorld.customize.buffet.biome"), this.width / 2, 68, 10526880);
        super.render(mouseX, mouseY, delta);
    }
    
    static {
        CHUNK_GENERATOR_TYPES = Registry.CHUNK_GENERATOR_TYPE.getIds().stream().filter(identifier -> Registry.CHUNK_GENERATOR_TYPE.get(identifier).isBuffetScreenOption()).collect(Collectors.toList());
    }
    
    @Environment(EnvType.CLIENT)
    class BuffetBiomesListWidget extends AlwaysSelectedEntryListWidget<BuffetBiomeItem>
    {
        private BuffetBiomesListWidget() {
            super(CustomizeBuffetLevelScreen.this.minecraft, CustomizeBuffetLevelScreen.this.width, CustomizeBuffetLevelScreen.this.height, 80, CustomizeBuffetLevelScreen.this.height - 37, 16);
            Registry.BIOME.getIds().stream().sorted(Comparator.comparing(identifier -> Registry.BIOME.get(identifier).getTextComponent().getString())).forEach(identifier -> this.addEntry(new BuffetBiomeItem(identifier)));
        }
        
        @Override
        protected boolean isFocused() {
            return CustomizeBuffetLevelScreen.this.getFocused() == this;
        }
        
        public void a(@Nullable final BuffetBiomeItem entry) {
            super.setSelected(entry);
            if (entry != null) {
                NarratorManager.INSTANCE.a(new TranslatableTextComponent("narrator.select", new Object[] { Registry.BIOME.get(entry.biome).getTextComponent().getString() }).getString());
            }
        }
        
        @Override
        protected void moveSelection(final int amount) {
            super.moveSelection(amount);
            CustomizeBuffetLevelScreen.this.refreshConfirmButton();
        }
        
        @Environment(EnvType.CLIENT)
        class BuffetBiomeItem extends Entry<BuffetBiomeItem>
        {
            private final Identifier biome;
            
            public BuffetBiomeItem(final Identifier biome) {
                this.biome = biome;
            }
            
            @Override
            public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
                BuffetBiomesListWidget.this.drawString(CustomizeBuffetLevelScreen.this.font, Registry.BIOME.get(this.biome).getTextComponent().getString(), integer3 + 5, integer2 + 2, 16777215);
            }
            
            @Override
            public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
                if (button == 0) {
                    BuffetBiomesListWidget.this.a(this);
                    CustomizeBuffetLevelScreen.this.refreshConfirmButton();
                    return true;
                }
                return false;
            }
        }
    }
}
