package net.minecraft.client.gui;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.world.chunk.ChunkStatus;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class WorldGenerationProgressScreen extends Screen
{
    private final WorldGenerationProgressTracker progressProvider;
    private long b;
    private static final Object2IntMap<ChunkStatus> STATUS_TO_COLOR;
    
    public WorldGenerationProgressScreen(final WorldGenerationProgressTracker worldGenerationProgressTracker) {
        super(NarratorManager.a);
        this.b = -1L;
        this.progressProvider = worldGenerationProgressTracker;
    }
    
    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
    
    @Override
    public void removed() {
        NarratorManager.INSTANCE.a(I18n.translate("narrator.loading.done"));
    }
    
    @Override
    public void render(final int mouseX, final int mouseY, final float delta) {
        this.renderBackground();
        final String string4 = MathHelper.clamp(this.progressProvider.getProgressPercentage(), 0, 100) + "%";
        final long long5 = SystemUtil.getMeasuringTimeMs();
        if (long5 - this.b > 2000L) {
            this.b = long5;
            NarratorManager.INSTANCE.a(new TranslatableTextComponent("narrator.loading", new Object[] { string4 }).getString());
        }
        final int integer7 = this.width / 2;
        final int integer8 = this.height / 2;
        final int integer9 = 30;
        drawChunkMap(this.progressProvider, integer7, integer8 + 30, 2, 0);
        final TextRenderer font = this.font;
        final String str = string4;
        final int centerX = integer7;
        final int n = integer8;
        this.font.getClass();
        this.drawCenteredString(font, str, centerX, n - 9 / 2 - 30, 16777215);
    }
    
    public static void drawChunkMap(final WorldGenerationProgressTracker progressProvider, final int centerX, final int centerY, final int chunkSize, final int integer5) {
        final int integer6 = chunkSize + integer5;
        final int integer7 = progressProvider.getCenterSize();
        final int integer8 = integer7 * integer6 - integer5;
        final int integer9 = progressProvider.getSize();
        final int integer10 = integer9 * integer6 - integer5;
        final int integer11 = centerX - integer10 / 2;
        final int integer12 = centerY - integer10 / 2;
        final int integer13 = integer8 / 2 + 1;
        final int integer14 = -16772609;
        if (integer5 != 0) {
            DrawableHelper.fill(centerX - integer13, centerY - integer13, centerX - integer13 + 1, centerY + integer13, -16772609);
            DrawableHelper.fill(centerX + integer13 - 1, centerY - integer13, centerX + integer13, centerY + integer13, -16772609);
            DrawableHelper.fill(centerX - integer13, centerY - integer13, centerX + integer13, centerY - integer13 + 1, -16772609);
            DrawableHelper.fill(centerX - integer13, centerY + integer13 - 1, centerX + integer13, centerY + integer13, -16772609);
        }
        for (int integer15 = 0; integer15 < integer9; ++integer15) {
            for (int integer16 = 0; integer16 < integer9; ++integer16) {
                final ChunkStatus chunkStatus17 = progressProvider.getChunkStatus(integer15, integer16);
                final int integer17 = integer11 + integer15 * integer6;
                final int integer18 = integer12 + integer16 * integer6;
                DrawableHelper.fill(integer17, integer18, integer17 + chunkSize, integer18 + chunkSize, WorldGenerationProgressScreen.STATUS_TO_COLOR.getInt(chunkStatus17) | 0xFF000000);
            }
        }
    }
    
    static {
        STATUS_TO_COLOR = SystemUtil.<Object2IntMap>consume((Object2IntMap)new Object2IntOpenHashMap(), object2IntOpenHashMap -> {
            object2IntOpenHashMap.defaultReturnValue(0);
            object2IntOpenHashMap.put(ChunkStatus.EMPTY, 5526612);
            object2IntOpenHashMap.put(ChunkStatus.STRUCTURE_STARTS, 10066329);
            object2IntOpenHashMap.put(ChunkStatus.STRUCTURE_REFERENCES, 6250897);
            object2IntOpenHashMap.put(ChunkStatus.BIOMES, 8434258);
            object2IntOpenHashMap.put(ChunkStatus.NOISE, 13750737);
            object2IntOpenHashMap.put(ChunkStatus.SURFACE, 7497737);
            object2IntOpenHashMap.put(ChunkStatus.CARVERS, 7169628);
            object2IntOpenHashMap.put(ChunkStatus.LIQUID_CARVERS, 3159410);
            object2IntOpenHashMap.put(ChunkStatus.FEATURES, 2213376);
            object2IntOpenHashMap.put(ChunkStatus.LIGHT, 13421772);
            object2IntOpenHashMap.put(ChunkStatus.SPAWN, 15884384);
            object2IntOpenHashMap.put(ChunkStatus.HEIGHTMAPS, 15658734);
            object2IntOpenHashMap.put(ChunkStatus.FULL, 16777215);
        });
    }
}
