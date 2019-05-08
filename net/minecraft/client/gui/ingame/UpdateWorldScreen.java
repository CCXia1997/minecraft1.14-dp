package net.minecraft.client.gui.ingame;

import it.unimi.dsi.fastutil.Hash;
import net.minecraft.util.SystemUtil;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.util.Iterator;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.updater.WorldUpdater;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.world.dimension.DimensionType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;

@Environment(EnvType.CLIENT)
public class UpdateWorldScreen extends Screen
{
    private static final Object2IntMap<DimensionType> a;
    private final BooleanConsumer callback;
    private final WorldUpdater updater;
    
    public UpdateWorldScreen(final BooleanConsumer booleanConsumer, final String string, final LevelStorage levelStorage, final boolean boolean4) {
        super(new TranslatableTextComponent("optimizeWorld.title", new Object[] { levelStorage.getLevelProperties(string).getLevelName() }));
        this.callback = booleanConsumer;
        this.updater = new WorldUpdater(string, levelStorage, levelStorage.getLevelProperties(string), boolean4);
    }
    
    @Override
    protected void init() {
        super.init();
        this.<ButtonWidget>addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 150, 200, 20, I18n.translate("gui.cancel"), buttonWidget -> {
            this.updater.cancel();
            this.callback.accept(false);
        }));
    }
    
    @Override
    public void tick() {
        if (this.updater.isDone()) {
            this.callback.accept(true);
        }
    }
    
    @Override
    public void removed() {
        this.updater.cancel();
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 20, 16777215);
        final int integer4 = this.width / 2 - 150;
        final int integer5 = this.width / 2 + 150;
        final int integer6 = this.height / 4 + 100;
        final int integer7 = integer6 + 10;
        final TextRenderer font = this.font;
        final String formattedText = this.updater.getStatus().getFormattedText();
        final int centerX = this.width / 2;
        final int n = integer6;
        this.font.getClass();
        this.drawCenteredString(font, formattedText, centerX, n - 9 - 2, 10526880);
        if (this.updater.getTotalChunkCount() > 0) {
            DrawableHelper.fill(integer4 - 1, integer6 - 1, integer5 + 1, integer7 + 1, -16777216);
            this.drawString(this.font, I18n.translate("optimizeWorld.info.converted", this.updater.getUpgradedChunkCount()), integer4, 40, 10526880);
            final TextRenderer font2 = this.font;
            final String translate = I18n.translate("optimizeWorld.info.skipped", this.updater.getSkippedChunkCount());
            final int x = integer4;
            final int n2 = 40;
            this.font.getClass();
            this.drawString(font2, translate, x, n2 + 9 + 3, 10526880);
            final TextRenderer font3 = this.font;
            final String translate2 = I18n.translate("optimizeWorld.info.total", this.updater.getTotalChunkCount());
            final int x2 = integer4;
            final int n3 = 40;
            this.font.getClass();
            this.drawString(font3, translate2, x2, n3 + (9 + 3) * 2, 10526880);
            int integer8 = 0;
            for (final DimensionType dimensionType10 : DimensionType.getAll()) {
                final int integer9 = MathHelper.floor(this.updater.getProgress(dimensionType10) * (integer5 - integer4));
                DrawableHelper.fill(integer4 + integer8, integer6, integer4 + integer8 + integer9, integer7, UpdateWorldScreen.a.getInt(dimensionType10));
                integer8 += integer9;
            }
            final int integer10 = this.updater.getUpgradedChunkCount() + this.updater.getSkippedChunkCount();
            final TextRenderer font4 = this.font;
            final String string = integer10 + " / " + this.updater.getTotalChunkCount();
            final int centerX2 = this.width / 2;
            final int n4 = integer6;
            final int n5 = 2;
            this.font.getClass();
            this.drawCenteredString(font4, string, centerX2, n4 + n5 * 9 + 2, 10526880);
            final TextRenderer font5 = this.font;
            final String string2 = MathHelper.floor(this.updater.getProgress() * 100.0f) + "%";
            final int centerX3 = this.width / 2;
            final int n6 = integer6 + (integer7 - integer6) / 2;
            this.font.getClass();
            this.drawCenteredString(font5, string2, centerX3, n6 - 9 / 2, 10526880);
        }
        super.render(mouseX, mouseY, delta);
    }
    
    static {
        a = SystemUtil.<Object2IntMap>consume((Object2IntMap)new Object2IntOpenCustomHashMap((Hash.Strategy)SystemUtil.identityHashStrategy()), object2IntOpenCustomHashMap -> {
            object2IntOpenCustomHashMap.put(DimensionType.a, -13408734);
            object2IntOpenCustomHashMap.put(DimensionType.b, -10075085);
            object2IntOpenCustomHashMap.put(DimensionType.c, -8943531);
            object2IntOpenCustomHashMap.defaultReturnValue(-2236963);
        });
    }
}
