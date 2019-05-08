package net.minecraft.client.gui.menu;

import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.client.audio.SoundInstance;
import net.minecraft.client.audio.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.render.Tessellator;
import java.util.Set;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import com.google.common.collect.Sets;
import com.google.common.collect.Lists;
import java.util.Comparator;
import net.minecraft.block.Block;
import net.minecraft.stat.StatType;
import java.util.List;
import net.minecraft.text.TextFormat;
import java.util.Iterator;
import net.minecraft.util.Identifier;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GuiLighting;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.SystemUtil;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.Packet;
import net.minecraft.server.network.packet.ClientStatusC2SPacket;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import javax.annotation.Nullable;
import net.minecraft.stat.StatHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.StatsListener;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class StatsScreen extends Screen implements StatsListener
{
    protected final Screen parent;
    private CustomStatsListWidget generalButton;
    private ItemStatsListWidget itemsButton;
    private EntityStatsListWidget mobsButton;
    private final StatHandler statHandler;
    @Nullable
    private AlwaysSelectedEntryListWidget<?> listWidget;
    private boolean h;
    
    public StatsScreen(final Screen parent, final StatHandler statHandler) {
        super(new TranslatableTextComponent("gui.stats", new Object[0]));
        this.h = true;
        this.parent = parent;
        this.statHandler = statHandler;
    }
    
    @Override
    protected void init() {
        this.h = true;
        this.minecraft.getNetworkHandler().sendPacket(new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.b));
    }
    
    public void a() {
        this.generalButton = new CustomStatsListWidget(this.minecraft);
        this.itemsButton = new ItemStatsListWidget(this.minecraft);
        this.mobsButton = new EntityStatsListWidget(this.minecraft);
    }
    
    public void b() {
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 120, this.height - 52, 80, 20, I18n.translate("stat.generalButton"), buttonWidget -> this.a(this.generalButton)));
        final ButtonWidget buttonWidget1 = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 40, this.height - 52, 80, 20, I18n.translate("stat.itemsButton"), buttonWidget -> this.a(this.itemsButton)));
        final ButtonWidget buttonWidget2 = this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 + 40, this.height - 52, 80, 20, I18n.translate("stat.mobsButton"), buttonWidget -> this.a(this.mobsButton)));
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height - 28, 200, 20, I18n.translate("gui.done"), buttonWidget -> this.minecraft.openScreen(this.parent)));
        if (this.itemsButton.children().isEmpty()) {
            buttonWidget1.active = false;
        }
        if (this.mobsButton.children().isEmpty()) {
            buttonWidget2.active = false;
        }
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        if (this.h) {
            this.renderBackground();
            this.drawCenteredString(this.font, I18n.translate("multiplayer.downloadingStats"), this.width / 2, this.height / 2, 16777215);
            final TextRenderer font = this.font;
            final String str = StatsScreen.PROGRESS_BAR_STAGES[(int)(SystemUtil.getMeasuringTimeMs() / 150L % StatsScreen.PROGRESS_BAR_STAGES.length)];
            final int centerX = this.width / 2;
            final int n = this.height / 2;
            this.font.getClass();
            this.drawCenteredString(font, str, centerX, n + 9 * 2, 16777215);
        }
        else {
            this.d().render(mouseX, mouseY, delta);
            this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
            super.render(mouseX, mouseY, delta);
        }
    }
    
    @Override
    public void onStatsReady() {
        if (this.h) {
            this.a();
            this.b();
            this.a(this.generalButton);
            this.h = false;
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return !this.h;
    }
    
    @Nullable
    public AlwaysSelectedEntryListWidget<?> d() {
        return this.listWidget;
    }
    
    public void a(@Nullable final AlwaysSelectedEntryListWidget<?> alwaysSelectedEntryListWidget) {
        this.children.remove(this.generalButton);
        this.children.remove(this.itemsButton);
        this.children.remove(this.mobsButton);
        if (alwaysSelectedEntryListWidget != null) {
            this.children.add(0, alwaysSelectedEntryListWidget);
            this.listWidget = alwaysSelectedEntryListWidget;
        }
    }
    
    private int a(final int integer) {
        return 115 + 40 * integer;
    }
    
    private void a(final int integer1, final int integer2, final Item item) {
        this.a(integer1 + 1, integer2 + 1, 0, 0);
        GlStateManager.enableRescaleNormal();
        GuiLighting.enableForItems();
        this.itemRenderer.renderGuiItemIcon(item.getDefaultStack(), integer1 + 2, integer2 + 2);
        GuiLighting.disable();
        GlStateManager.disableRescaleNormal();
    }
    
    private void a(final int integer1, final int integer2, final int integer3, final int integer4) {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.minecraft.getTextureManager().bindTexture(StatsScreen.STATS_ICON_LOCATION);
        DrawableHelper.blit(integer1, integer2, this.blitOffset, (float)integer3, (float)integer4, 18, 18, 128, 128);
    }
    
    @Environment(EnvType.CLIENT)
    class CustomStatsListWidget extends AlwaysSelectedEntryListWidget<CustomStatItem>
    {
        public CustomStatsListWidget(final MinecraftClient minecraftClient) {
            super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 10);
            for (final Stat<Identifier> stat4 : Stats.i) {
                this.addEntry(new CustomStatItem((Stat)stat4));
            }
        }
        
        @Override
        protected void renderBackground() {
            StatsScreen.this.renderBackground();
        }
        
        @Environment(EnvType.CLIENT)
        class CustomStatItem extends Entry<CustomStatItem>
        {
            private final Stat<Identifier> b;
            
            private CustomStatItem(final Stat<Identifier> stat) {
                this.b = stat;
            }
            
            @Override
            public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
                final TextComponent textComponent10 = new TranslatableTextComponent("stat." + this.b.getValue().toString().replace(':', '.'), new Object[0]).applyFormat(TextFormat.h);
                CustomStatsListWidget.this.drawString(StatsScreen.this.font, textComponent10.getString(), integer3 + 2, integer2 + 1, (index % 2 == 0) ? 16777215 : 9474192);
                final String string11 = this.b.format(StatsScreen.this.statHandler.getStat(this.b));
                CustomStatsListWidget.this.drawString(StatsScreen.this.font, string11, integer3 + 2 + 213 - StatsScreen.this.font.getStringWidth(string11), integer2 + 1, (index % 2 == 0) ? 16777215 : 9474192);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    class ItemStatsListWidget extends AlwaysSelectedEntryListWidget<ItemStatItem>
    {
        protected final List<StatType<Block>> a;
        protected final List<StatType<Item>> b;
        private final int[] i;
        protected int c;
        protected final List<Item> d;
        protected final Comparator<Item> e;
        @Nullable
        protected StatType<?> f;
        protected int g;
        
        public ItemStatsListWidget(final MinecraftClient minecraftClient) {
            super(minecraftClient, StatsScreen.this.width, StatsScreen.this.height, 32, StatsScreen.this.height - 64, 20);
            this.i = new int[] { 3, 4, 1, 2, 5, 6 };
            this.c = -1;
            this.e = new a();
            (this.a = Lists.newArrayList()).add(Stats.a);
            this.b = Lists.newArrayList(Stats.d, Stats.b, Stats.c, Stats.e, Stats.f);
            this.setRenderHeader(true, 20);
            final Set<Item> set3 = Sets.<Item>newIdentityHashSet();
            for (final Item item5 : Registry.ITEM) {
                boolean boolean6 = false;
                for (final StatType<Item> statType8 : this.b) {
                    if (statType8.hasStat(item5) && StatsScreen.this.statHandler.getStat(statType8.getOrCreateStat(item5)) > 0) {
                        boolean6 = true;
                    }
                }
                if (boolean6) {
                    set3.add(item5);
                }
            }
            for (final Block block5 : Registry.BLOCK) {
                boolean boolean6 = false;
                for (final StatType<Block> statType9 : this.a) {
                    if (statType9.hasStat(block5) && StatsScreen.this.statHandler.getStat(statType9.getOrCreateStat(block5)) > 0) {
                        boolean6 = true;
                    }
                }
                if (boolean6) {
                    set3.add(block5.getItem());
                }
            }
            set3.remove(Items.AIR);
            this.d = Lists.newArrayList(set3);
            for (int integer4 = 0; integer4 < this.d.size(); ++integer4) {
                this.addEntry(new ItemStatItem());
            }
        }
        
        @Override
        protected void renderHeader(final int x, final int y, final Tessellator tessellator) {
            if (!this.minecraft.mouse.wasLeftButtonClicked()) {
                this.c = -1;
            }
            for (int integer4 = 0; integer4 < this.i.length; ++integer4) {
                StatsScreen.this.a(x + StatsScreen.this.a(integer4) - 18, y + 1, 0, (this.c == integer4) ? 0 : 18);
            }
            if (this.f != null) {
                final int integer4 = StatsScreen.this.a(this.b(this.f)) - 36;
                final int integer5 = (this.g == 1) ? 2 : 1;
                StatsScreen.this.a(x + integer4, y + 1, 18 * integer5, 0);
            }
            for (int integer4 = 0; integer4 < this.i.length; ++integer4) {
                final int integer5 = (this.c == integer4) ? 1 : 0;
                StatsScreen.this.a(x + StatsScreen.this.a(integer4) - 18 + integer5, y + 1 + integer5, 18 * this.i[integer4], 18);
            }
        }
        
        @Override
        public int getRowWidth() {
            return 375;
        }
        
        @Override
        protected int getScrollbarPosition() {
            return this.width / 2 + 140;
        }
        
        @Override
        protected void renderBackground() {
            StatsScreen.this.renderBackground();
        }
        
        @Override
        protected void clickedHeader(final int x, final int y) {
            this.c = -1;
            for (int integer3 = 0; integer3 < this.i.length; ++integer3) {
                final int integer4 = x - StatsScreen.this.a(integer3);
                if (integer4 >= -36 && integer4 <= 0) {
                    this.c = integer3;
                    break;
                }
            }
            if (this.c >= 0) {
                this.a(this.a(this.c));
                this.minecraft.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.mh, 1.0f));
            }
        }
        
        private StatType<?> a(final int integer) {
            return ((integer < this.a.size()) ? ((StatType<Block>)this.a.get(integer)) : ((StatType<Item>)this.b.get(integer - this.a.size())));
        }
        
        private int b(final StatType<?> statType) {
            final int integer2 = this.a.indexOf(statType);
            if (integer2 >= 0) {
                return integer2;
            }
            final int integer3 = this.b.indexOf(statType);
            if (integer3 >= 0) {
                return integer3 + this.a.size();
            }
            return -1;
        }
        
        @Override
        protected void renderDecorations(final int mouseX, final int mouseY) {
            if (mouseY < this.top || mouseY > this.bottom) {
                return;
            }
            final ItemStatItem itemStatItem3 = this.getEntryAtPosition(mouseX, mouseY);
            final int integer4 = (this.width - this.getRowWidth()) / 2;
            if (itemStatItem3 != null) {
                if (mouseX < integer4 + 40 || mouseX > integer4 + 40 + 20) {
                    return;
                }
                final Item item5 = this.d.get(this.children().indexOf(itemStatItem3));
                this.a(this.a(item5), mouseX, mouseY);
            }
            else {
                TextComponent textComponent5 = null;
                final int integer5 = mouseX - integer4;
                for (int integer6 = 0; integer6 < this.i.length; ++integer6) {
                    final int integer7 = StatsScreen.this.a(integer6);
                    if (integer5 >= integer7 - 18 && integer5 <= integer7) {
                        textComponent5 = new TranslatableTextComponent(this.a(integer6).getTranslationKey(), new Object[0]);
                        break;
                    }
                }
                this.a(textComponent5, mouseX, mouseY);
            }
        }
        
        protected void a(@Nullable final TextComponent textComponent, final int integer2, final int integer3) {
            if (textComponent == null) {
                return;
            }
            final String string4 = textComponent.getFormattedText();
            final int integer4 = integer2 + 12;
            final int integer5 = integer3 - 12;
            final int integer6 = StatsScreen.this.font.getStringWidth(string4);
            this.fillGradient(integer4 - 3, integer5 - 3, integer4 + integer6 + 3, integer5 + 8 + 3, -1073741824, -1073741824);
            StatsScreen.this.font.drawWithShadow(string4, (float)integer4, (float)integer5, -1);
        }
        
        protected TextComponent a(final Item item) {
            return item.getTextComponent();
        }
        
        protected void a(final StatType<?> statType) {
            if (statType != this.f) {
                this.f = statType;
                this.g = -1;
            }
            else if (this.g == -1) {
                this.g = 1;
            }
            else {
                this.f = null;
                this.g = 0;
            }
            this.d.sort(this.e);
        }
        
        @Environment(EnvType.CLIENT)
        class a implements Comparator<Item>
        {
            private a() {
            }
            
            public int a(final Item item1, final Item item2) {
                int integer3;
                int integer4;
                if (ItemStatsListWidget.this.f == null) {
                    integer3 = 0;
                    integer4 = 0;
                }
                else if (ItemStatsListWidget.this.a.contains(ItemStatsListWidget.this.f)) {
                    final StatType<Block> statType5 = (StatType<Block>)ItemStatsListWidget.this.f;
                    integer3 = ((item1 instanceof BlockItem) ? StatsScreen.this.statHandler.<Block>getStat(statType5, ((BlockItem)item1).getBlock()) : -1);
                    integer4 = ((item2 instanceof BlockItem) ? StatsScreen.this.statHandler.<Block>getStat(statType5, ((BlockItem)item2).getBlock()) : -1);
                }
                else {
                    final StatType<Item> statType6 = (StatType<Item>)ItemStatsListWidget.this.f;
                    integer3 = StatsScreen.this.statHandler.<Item>getStat(statType6, item1);
                    integer4 = StatsScreen.this.statHandler.<Item>getStat(statType6, item2);
                }
                if (integer3 == integer4) {
                    return ItemStatsListWidget.this.g * Integer.compare(Item.getRawIdByItem(item1), Item.getRawIdByItem(item2));
                }
                return ItemStatsListWidget.this.g * Integer.compare(integer3, integer4);
            }
        }
        
        @Environment(EnvType.CLIENT)
        class ItemStatItem extends Entry<ItemStatItem>
        {
            private ItemStatItem() {
            }
            
            @Override
            public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
                final Item item10 = StatsScreen.this.itemsButton.d.get(index);
                StatsScreen.this.a(integer3 + 40, integer2, item10);
                for (int integer4 = 0; integer4 < StatsScreen.this.itemsButton.a.size(); ++integer4) {
                    Stat<Block> stat12;
                    if (item10 instanceof BlockItem) {
                        stat12 = StatsScreen.this.itemsButton.a.get(integer4).getOrCreateStat(((BlockItem)item10).getBlock());
                    }
                    else {
                        stat12 = null;
                    }
                    this.a(stat12, integer3 + StatsScreen.this.a(integer4), integer2, index % 2 == 0);
                }
                for (int integer4 = 0; integer4 < StatsScreen.this.itemsButton.b.size(); ++integer4) {
                    this.a(StatsScreen.this.itemsButton.b.get(integer4).getOrCreateStat(item10), integer3 + StatsScreen.this.a(integer4 + StatsScreen.this.itemsButton.a.size()), integer2, index % 2 == 0);
                }
            }
            
            protected void a(@Nullable final Stat<?> stat, final int integer2, final int integer3, final boolean boolean4) {
                final String string5 = (stat == null) ? "-" : stat.format(StatsScreen.this.statHandler.getStat(stat));
                ItemStatsListWidget.this.drawString(StatsScreen.this.font, string5, integer2 - StatsScreen.this.font.getStringWidth(string5), integer3 + 5, boolean4 ? 16777215 : 9474192);
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    class EntityStatsListWidget extends AlwaysSelectedEntryListWidget<EntityStatItem>
    {
        public EntityStatsListWidget(final MinecraftClient minecraftClient) {
            final int width = StatsScreen.this.width;
            final int height = StatsScreen.this.height;
            final int top = 32;
            final int bottom = StatsScreen.this.height - 64;
            StatsScreen.this.font.getClass();
            super(minecraftClient, width, height, top, bottom, 9 * 4);
            for (final EntityType<?> entityType4 : Registry.ENTITY_TYPE) {
                if (StatsScreen.this.statHandler.getStat(Stats.g.getOrCreateStat(entityType4)) > 0 || StatsScreen.this.statHandler.getStat(Stats.h.getOrCreateStat(entityType4)) > 0) {
                    this.addEntry(new EntityStatItem(entityType4));
                }
            }
        }
        
        @Override
        protected void renderBackground() {
            StatsScreen.this.renderBackground();
        }
        
        @Environment(EnvType.CLIENT)
        class EntityStatItem extends Entry<EntityStatItem>
        {
            private final EntityType<?> b;
            
            public EntityStatItem(final EntityType<?> entityType) {
                this.b = entityType;
            }
            
            @Override
            public void render(final int index, final int integer2, final int integer3, final int width, final int height, final int mouseX, final int mouseY, final boolean hovering, final float delta) {
                final String string10 = I18n.translate(SystemUtil.createTranslationKey("entity", EntityType.getId(this.b)));
                final int integer4 = StatsScreen.this.statHandler.getStat(Stats.g.getOrCreateStat(this.b));
                final int integer5 = StatsScreen.this.statHandler.getStat(Stats.h.getOrCreateStat(this.b));
                EntityStatsListWidget.this.drawString(StatsScreen.this.font, string10, integer3 + 2, integer2 + 1, 16777215);
                final EntityStatsListWidget a = EntityStatsListWidget.this;
                final TextRenderer l = StatsScreen.this.font;
                final String a2 = this.a(string10, integer4);
                final int x = integer3 + 2 + 10;
                final int n = integer2 + 1;
                StatsScreen.this.font.getClass();
                a.drawString(l, a2, x, n + 9, (integer4 == 0) ? 6316128 : 9474192);
                final EntityStatsListWidget a3 = EntityStatsListWidget.this;
                final TextRenderer n2 = StatsScreen.this.font;
                final String b = this.b(string10, integer5);
                final int x2 = integer3 + 2 + 10;
                final int n3 = integer2 + 1;
                StatsScreen.this.font.getClass();
                a3.drawString(n2, b, x2, n3 + 9 * 2, (integer5 == 0) ? 6316128 : 9474192);
            }
            
            private String a(final String string, final int integer) {
                final String string2 = Stats.g.getTranslationKey();
                if (integer == 0) {
                    return I18n.translate(string2 + ".none", string);
                }
                return I18n.translate(string2, integer, string);
            }
            
            private String b(final String string, final int integer) {
                final String string2 = Stats.h.getTranslationKey();
                if (integer == 0) {
                    return I18n.translate(string2 + ".none", string);
                }
                return I18n.translate(string2, string, integer);
            }
        }
    }
}
