package net.minecraft.client.texture;

import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import net.minecraft.client.resource.metadata.AnimationFrameResourceMetadata;
import com.google.common.collect.Lists;
import java.util.Arrays;
import net.minecraft.resource.Resource;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.util.PngFile;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Sprite
{
    private final Identifier id;
    protected final int width;
    protected final int height;
    protected NativeImage[] images;
    @Nullable
    protected int[] frameOffsets;
    @Nullable
    protected int[] e;
    protected NativeImage[] interpolatedImages;
    private AnimationResourceMetadata animationMetadata;
    protected int x;
    protected int y;
    private float uMin;
    private float uMax;
    private float vMin;
    private float vMax;
    protected int framePos;
    protected int ticks;
    private static final int[] blendedPixelCache;
    private static final float[] srgbLinearMap;
    
    protected Sprite(final Identifier identifier, final int integer2, final int integer3) {
        this.id = identifier;
        this.width = integer2;
        this.height = integer3;
    }
    
    protected Sprite(final Identifier identifier, final PngFile pngFile, @Nullable final AnimationResourceMetadata animationResourceMetadata) {
        this.id = identifier;
        if (animationResourceMetadata != null) {
            final Pair<Integer, Integer> pair4 = getDimensions(animationResourceMetadata.getWidth(), animationResourceMetadata.getHeight(), pngFile.width, pngFile.height);
            this.width = (int)pair4.getFirst();
            this.height = (int)pair4.getSecond();
            if (!isDivisibleBy(pngFile.width, this.width) || !isDivisibleBy(pngFile.height, this.height)) {
                throw new IllegalArgumentException(String.format("Image size %s,%s is not multiply of frame size %s,%s", this.width, this.height, pngFile.width, pngFile.height));
            }
        }
        else {
            this.width = pngFile.width;
            this.height = pngFile.height;
        }
        this.animationMetadata = animationResourceMetadata;
    }
    
    private static Pair<Integer, Integer> getDimensions(final int animationMetadataWidth, final int animationMetadataHeight, final int width, final int height) {
        if (animationMetadataWidth != -1) {
            if (animationMetadataHeight != -1) {
                return (Pair<Integer, Integer>)Pair.of(animationMetadataWidth, animationMetadataHeight);
            }
            return (Pair<Integer, Integer>)Pair.of(animationMetadataWidth, height);
        }
        else {
            if (animationMetadataHeight != -1) {
                return (Pair<Integer, Integer>)Pair.of(width, animationMetadataHeight);
            }
            final int integer5 = Math.min(width, height);
            return (Pair<Integer, Integer>)Pair.of(integer5, integer5);
        }
    }
    
    private static boolean isDivisibleBy(final int number, final int divisor) {
        return number / divisor * divisor == number;
    }
    
    private void generateMipmapsInternal(final int mipLevels) {
        final NativeImage[] arr2 = new NativeImage[mipLevels + 1];
        arr2[0] = this.images[0];
        if (mipLevels > 0) {
            boolean boolean3 = false;
        Label_0092:
            for (int integer4 = 0; integer4 < this.images[0].getWidth(); ++integer4) {
                for (int integer5 = 0; integer5 < this.images[0].getHeight(); ++integer5) {
                    if (this.images[0].getPixelRGBA(integer4, integer5) >> 24 == 0) {
                        boolean3 = true;
                        break Label_0092;
                    }
                }
            }
            for (int integer4 = 1; integer4 <= mipLevels; ++integer4) {
                if (this.images.length > integer4 && this.images[integer4] != null) {
                    arr2[integer4] = this.images[integer4];
                }
                else {
                    final NativeImage nativeImage5 = arr2[integer4 - 1];
                    final NativeImage nativeImage6 = new NativeImage(nativeImage5.getWidth() >> 1, nativeImage5.getHeight() >> 1, false);
                    final int integer6 = nativeImage6.getWidth();
                    final int integer7 = nativeImage6.getHeight();
                    for (int integer8 = 0; integer8 < integer6; ++integer8) {
                        for (int integer9 = 0; integer9 < integer7; ++integer9) {
                            nativeImage6.setPixelRGBA(integer8, integer9, blendPixels(nativeImage5.getPixelRGBA(integer8 * 2 + 0, integer9 * 2 + 0), nativeImage5.getPixelRGBA(integer8 * 2 + 1, integer9 * 2 + 0), nativeImage5.getPixelRGBA(integer8 * 2 + 0, integer9 * 2 + 1), nativeImage5.getPixelRGBA(integer8 * 2 + 1, integer9 * 2 + 1), boolean3));
                        }
                    }
                    arr2[integer4] = nativeImage6;
                }
            }
            for (int integer4 = mipLevels + 1; integer4 < this.images.length; ++integer4) {
                if (this.images[integer4] != null) {
                    this.images[integer4].close();
                }
            }
        }
        this.images = arr2;
    }
    
    private static int blendPixels(final int colorTopLeft, final int colorTopRight, final int colorBottomLeft, final int colorBottomRight, final boolean hasTransparency) {
        if (hasTransparency) {
            Sprite.blendedPixelCache[0] = colorTopLeft;
            Sprite.blendedPixelCache[1] = colorTopRight;
            Sprite.blendedPixelCache[2] = colorBottomLeft;
            Sprite.blendedPixelCache[3] = colorBottomRight;
            float float6 = 0.0f;
            float float7 = 0.0f;
            float float8 = 0.0f;
            float float9 = 0.0f;
            for (int integer10 = 0; integer10 < 4; ++integer10) {
                if (Sprite.blendedPixelCache[integer10] >> 24 != 0) {
                    float6 += srgbToLinear(Sprite.blendedPixelCache[integer10] >> 24);
                    float7 += srgbToLinear(Sprite.blendedPixelCache[integer10] >> 16);
                    float8 += srgbToLinear(Sprite.blendedPixelCache[integer10] >> 8);
                    float9 += srgbToLinear(Sprite.blendedPixelCache[integer10] >> 0);
                }
            }
            float6 /= 4.0f;
            float7 /= 4.0f;
            float8 /= 4.0f;
            float9 /= 4.0f;
            int integer10 = (int)(Math.pow(float6, 0.45454545454545453) * 255.0);
            final int integer11 = (int)(Math.pow(float7, 0.45454545454545453) * 255.0);
            final int integer12 = (int)(Math.pow(float8, 0.45454545454545453) * 255.0);
            final int integer13 = (int)(Math.pow(float9, 0.45454545454545453) * 255.0);
            if (integer10 < 96) {
                integer10 = 0;
            }
            return integer10 << 24 | integer11 << 16 | integer12 << 8 | integer13;
        }
        final int integer14 = blendPixelsComponent(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 24);
        final int integer15 = blendPixelsComponent(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 16);
        final int integer16 = blendPixelsComponent(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 8);
        final int integer17 = blendPixelsComponent(colorTopLeft, colorTopRight, colorBottomLeft, colorBottomRight, 0);
        return integer14 << 24 | integer15 << 16 | integer16 << 8 | integer17;
    }
    
    private static int blendPixelsComponent(final int colorTopLeft, final int colorTopRight, final int colorBottomLeft, final int colorBottomRight, final int componentShift) {
        final float float6 = srgbToLinear(colorTopLeft >> componentShift);
        final float float7 = srgbToLinear(colorTopRight >> componentShift);
        final float float8 = srgbToLinear(colorBottomLeft >> componentShift);
        final float float9 = srgbToLinear(colorBottomRight >> componentShift);
        final float float10 = (float)Math.pow((float6 + float7 + float8 + float9) * 0.25, 0.45454545454545453);
        return (int)(float10 * 255.0);
    }
    
    private static float srgbToLinear(final int integer) {
        return Sprite.srgbLinearMap[integer & 0xFF];
    }
    
    private void upload(final int frame) {
        int integer2 = 0;
        int integer3 = 0;
        if (this.frameOffsets != null) {
            integer2 = this.frameOffsets[frame] * this.width;
            integer3 = this.e[frame] * this.height;
        }
        this.upload(integer2, integer3, this.images);
    }
    
    private void upload(final int integer1, final int integer2, final NativeImage[] arr) {
        for (int integer3 = 0; integer3 < this.images.length; ++integer3) {
            arr[integer3].upload(integer3, this.x >> integer3, this.y >> integer3, integer1 >> integer3, integer2 >> integer3, this.width >> integer3, this.height >> integer3, this.images.length > 1);
        }
    }
    
    public void init(final int width, final int height, final int x, final int y) {
        this.x = x;
        this.y = y;
        this.uMin = x / (float)width;
        this.uMax = (x + this.width) / (float)width;
        this.vMin = y / (float)height;
        this.vMax = (y + this.height) / (float)height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public float getMinU() {
        return this.uMin;
    }
    
    public float getMaxU() {
        return this.uMax;
    }
    
    public float getU(final double double1) {
        final float float3 = this.uMax - this.uMin;
        return this.uMin + float3 * (float)double1 / 16.0f;
    }
    
    public float getXFromU(final float float1) {
        final float float2 = this.uMax - this.uMin;
        return (float1 - this.uMin) / float2 * 16.0f;
    }
    
    public float getMinV() {
        return this.vMin;
    }
    
    public float getMaxV() {
        return this.vMax;
    }
    
    public float getV(final double double1) {
        final float float3 = this.vMax - this.vMin;
        return this.vMin + float3 * (float)double1 / 16.0f;
    }
    
    public float getYFromV(final float float1) {
        final float float2 = this.vMax - this.vMin;
        return (float1 - this.vMin) / float2 * 16.0f;
    }
    
    public Identifier getId() {
        return this.id;
    }
    
    public void tick() {
        ++this.ticks;
        if (this.ticks >= this.animationMetadata.getFrameTime(this.framePos)) {
            final int integer1 = this.animationMetadata.getFrameIndex(this.framePos);
            final int integer2 = (this.animationMetadata.getFrameCount() == 0) ? this.getFrameCount() : this.animationMetadata.getFrameCount();
            this.framePos = (this.framePos + 1) % integer2;
            this.ticks = 0;
            final int integer3 = this.animationMetadata.getFrameIndex(this.framePos);
            if (integer1 != integer3 && integer3 >= 0 && integer3 < this.getFrameCount()) {
                this.upload(integer3);
            }
        }
        else if (this.animationMetadata.shouldInterpolate()) {
            this.interpolateFrames();
        }
    }
    
    private void interpolateFrames() {
        final double double1 = 1.0 - this.ticks / (double)this.animationMetadata.getFrameTime(this.framePos);
        final int integer3 = this.animationMetadata.getFrameIndex(this.framePos);
        final int integer4 = (this.animationMetadata.getFrameCount() == 0) ? this.getFrameCount() : this.animationMetadata.getFrameCount();
        final int integer5 = this.animationMetadata.getFrameIndex((this.framePos + 1) % integer4);
        if (integer3 != integer5 && integer5 >= 0 && integer5 < this.getFrameCount()) {
            if (this.interpolatedImages == null || this.interpolatedImages.length != this.images.length) {
                if (this.interpolatedImages != null) {
                    for (final NativeImage nativeImage9 : this.interpolatedImages) {
                        if (nativeImage9 != null) {
                            nativeImage9.close();
                        }
                    }
                }
                this.interpolatedImages = new NativeImage[this.images.length];
            }
            for (int integer6 = 0; integer6 < this.images.length; ++integer6) {
                final int integer7 = this.width >> integer6;
                final int integer8 = this.height >> integer6;
                if (this.interpolatedImages[integer6] == null) {
                    this.interpolatedImages[integer6] = new NativeImage(integer7, integer8, false);
                }
                for (int integer9 = 0; integer9 < integer8; ++integer9) {
                    for (int integer10 = 0; integer10 < integer7; ++integer10) {
                        final int integer11 = this.getFramePixel(integer3, integer6, integer10, integer9);
                        final int integer12 = this.getFramePixel(integer5, integer6, integer10, integer9);
                        final int integer13 = this.lerp(double1, integer11 >> 16 & 0xFF, integer12 >> 16 & 0xFF);
                        final int integer14 = this.lerp(double1, integer11 >> 8 & 0xFF, integer12 >> 8 & 0xFF);
                        final int integer15 = this.lerp(double1, integer11 & 0xFF, integer12 & 0xFF);
                        this.interpolatedImages[integer6].setPixelRGBA(integer10, integer9, (integer11 & 0xFF000000) | integer13 << 16 | integer14 << 8 | integer15);
                    }
                }
            }
            this.upload(0, 0, this.interpolatedImages);
        }
    }
    
    private int lerp(final double multiplier, final int first, final int second) {
        return (int)(multiplier * first + (1.0 - multiplier) * second);
    }
    
    public int getFrameCount() {
        return (this.frameOffsets == null) ? 0 : this.frameOffsets.length;
    }
    
    public void load(final Resource resource, final int integer) throws IOException {
        final NativeImage nativeImage3 = NativeImage.fromInputStream(resource.getInputStream());
        (this.images = new NativeImage[integer])[0] = nativeImage3;
        int integer2;
        if (this.animationMetadata != null && this.animationMetadata.getWidth() != -1) {
            integer2 = nativeImage3.getWidth() / this.animationMetadata.getWidth();
        }
        else {
            integer2 = nativeImage3.getWidth() / this.width;
        }
        int integer3;
        if (this.animationMetadata != null && this.animationMetadata.getHeight() != -1) {
            integer3 = nativeImage3.getHeight() / this.animationMetadata.getHeight();
        }
        else {
            integer3 = nativeImage3.getHeight() / this.height;
        }
        if (this.animationMetadata != null && this.animationMetadata.getFrameCount() > 0) {
            final int integer4 = this.animationMetadata.getFrameIndexSet().stream().max(Integer::compareTo).get() + 1;
            this.frameOffsets = new int[integer4];
            this.e = new int[integer4];
            Arrays.fill(this.frameOffsets, -1);
            Arrays.fill(this.e, -1);
            for (final int integer5 : this.animationMetadata.getFrameIndexSet()) {
                if (integer5 >= integer2 * integer3) {
                    throw new RuntimeException("invalid frameindex " + integer5);
                }
                final int integer6 = integer5 / integer2;
                final int integer7 = integer5 % integer2;
                this.frameOffsets[integer5] = integer7;
                this.e[integer5] = integer6;
            }
        }
        else {
            final List<AnimationFrameResourceMetadata> list6 = Lists.newArrayList();
            final int integer8 = integer2 * integer3;
            this.frameOffsets = new int[integer8];
            this.e = new int[integer8];
            for (int integer5 = 0; integer5 < integer3; ++integer5) {
                for (int integer6 = 0; integer6 < integer2; ++integer6) {
                    final int integer7 = integer5 * integer2 + integer6;
                    this.frameOffsets[integer7] = integer6;
                    this.e[integer7] = integer5;
                    list6.add(new AnimationFrameResourceMetadata(integer7, -1));
                }
            }
            int integer5 = 1;
            boolean boolean9 = false;
            if (this.animationMetadata != null) {
                integer5 = this.animationMetadata.getDefaultFrameTime();
                boolean9 = this.animationMetadata.shouldInterpolate();
            }
            this.animationMetadata = new AnimationResourceMetadata(list6, this.width, this.height, integer5, boolean9);
        }
    }
    
    public void generateMipmaps(final int integer) {
        try {
            this.generateMipmapsInternal(integer);
        }
        catch (Throwable throwable2) {
            final CrashReport crashReport3 = CrashReport.create(throwable2, "Generating mipmaps for frame");
            final CrashReportSection crashReportSection4 = crashReport3.addElement("Frame being iterated");
            final StringBuilder stringBuilder1;
            final NativeImage[] images;
            int length;
            int i = 0;
            NativeImage nativeImage5;
            String string;
            final StringBuilder sb;
            crashReportSection4.add("Frame sizes", () -> {
                stringBuilder1 = new StringBuilder();
                images = this.images;
                for (length = images.length; i < length; ++i) {
                    nativeImage5 = images[i];
                    if (stringBuilder1.length() > 0) {
                        stringBuilder1.append(", ");
                    }
                    if (nativeImage5 == null) {
                        string = "null";
                    }
                    else {
                        string = nativeImage5.getWidth() + "x" + nativeImage5.getHeight();
                    }
                    sb.append(string);
                }
                return stringBuilder1.toString();
            });
            throw new CrashException(crashReport3);
        }
    }
    
    public void destroy() {
        if (this.images != null) {
            for (final NativeImage nativeImage4 : this.images) {
                if (nativeImage4 != null) {
                    nativeImage4.close();
                }
            }
        }
        this.images = null;
        if (this.interpolatedImages != null) {
            for (final NativeImage nativeImage4 : this.interpolatedImages) {
                if (nativeImage4 != null) {
                    nativeImage4.close();
                }
            }
        }
        this.interpolatedImages = null;
    }
    
    public boolean isAnimated() {
        return this.animationMetadata != null && this.animationMetadata.getFrameCount() > 1;
    }
    
    @Override
    public String toString() {
        final int integer1 = (this.frameOffsets == null) ? 0 : this.frameOffsets.length;
        return "TextureAtlasSprite{name='" + this.id + '\'' + ", frameCount=" + integer1 + ", x=" + this.x + ", y=" + this.y + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.uMin + ", u1=" + this.uMax + ", v0=" + this.vMin + ", v1=" + this.vMax + '}';
    }
    
    private int getFramePixel(final int frame, final int integer2, final int x, final int y) {
        return this.images[integer2].getPixelRGBA(x + (this.frameOffsets[frame] * this.width >> integer2), y + (this.e[frame] * this.height >> integer2));
    }
    
    public boolean a(final int integer1, final int integer2, final int integer3) {
        return (this.images[0].getPixelRGBA(integer2 + this.frameOffsets[integer1] * this.width, integer3 + this.e[integer1] * this.height) >> 24 & 0xFF) == 0x0;
    }
    
    public void upload() {
        this.upload(0);
    }
    
    static {
        blendedPixelCache = new int[4];
        int integer2;
        srgbLinearMap = SystemUtil.<float[]>consume(new float[256], arr -> {
            for (integer2 = 0; integer2 < arr.length; ++integer2) {
                arr[integer2] = (float)Math.pow(integer2 / 255.0f, 2.2);
            }
        });
    }
}
