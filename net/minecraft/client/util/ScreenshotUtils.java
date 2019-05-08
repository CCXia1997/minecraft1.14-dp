package net.minecraft.client.util;

import java.text.SimpleDateFormat;
import org.apache.logging.log4j.LogManager;
import net.minecraft.text.Style;
import java.util.Date;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.GLX;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.event.ClickEvent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.StringTextComponent;
import net.minecraft.resource.ResourceImpl;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;
import java.util.function.Consumer;
import net.minecraft.client.gl.GlFramebuffer;
import java.io.File;
import java.text.DateFormat;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ScreenshotUtils
{
    private static final Logger LOGGER;
    private static final DateFormat DATE_FORMAT;
    
    public static void a(final File file, final int integer2, final int integer3, final GlFramebuffer glFramebuffer, final Consumer<TextComponent> consumer) {
        a(file, null, integer2, integer3, glFramebuffer, consumer);
    }
    
    public static void a(final File file, @Nullable final String string, final int integer3, final int integer4, final GlFramebuffer glFramebuffer, final Consumer<TextComponent> consumer) {
        final NativeImage nativeImage7 = a(integer3, integer4, glFramebuffer);
        final File file2 = new File(file, "screenshots");
        file2.mkdir();
        if (string == null) {
            final File file3 = getScreenshotFilename(file2);
        }
        else {
            final File file3 = new File(file2, string);
        }
        final NativeImage nativeImage8;
        final File file4;
        TextComponent textComponent4;
        final TranslatableTextComponent translatableTextComponent;
        final TranslatableTextComponent translatableTextComponent2;
        ResourceImpl.RESOURCE_IO_EXECUTOR.execute(() -> {
            try {
                nativeImage8.writeFile(file4);
                textComponent4 = new StringTextComponent(file4.getName()).applyFormat(TextFormat.t).modifyStyle(style -> style.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file4.getAbsolutePath())));
                new TranslatableTextComponent("screenshot.success", new Object[] { textComponent4 });
                consumer.accept(translatableTextComponent);
            }
            catch (Exception exception4) {
                ScreenshotUtils.LOGGER.warn("Couldn't save screenshot", (Throwable)exception4);
                new TranslatableTextComponent("screenshot.failure", new Object[] { exception4.getMessage() });
                consumer.accept(translatableTextComponent2);
            }
            finally {
                nativeImage8.close();
            }
        });
    }
    
    public static NativeImage a(int integer1, int integer2, final GlFramebuffer glFramebuffer) {
        if (GLX.isUsingFBOs()) {
            integer1 = glFramebuffer.texWidth;
            integer2 = glFramebuffer.texHeight;
        }
        final NativeImage nativeImage4 = new NativeImage(integer1, integer2, false);
        if (GLX.isUsingFBOs()) {
            GlStateManager.bindTexture(glFramebuffer.colorAttachment);
            nativeImage4.loadFromTextureImage(0, true);
        }
        else {
            nativeImage4.a(true);
        }
        nativeImage4.e();
        return nativeImage4;
    }
    
    private static File getScreenshotFilename(final File directory) {
        final String string2 = ScreenshotUtils.DATE_FORMAT.format(new Date());
        int integer3 = 1;
        File file4;
        while (true) {
            file4 = new File(directory, string2 + ((integer3 == 1) ? "" : ("_" + integer3)) + ".png");
            if (!file4.exists()) {
                break;
            }
            ++integer3;
        }
        return file4;
    }
    
    static {
        LOGGER = LogManager.getLogger();
        DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
    }
}
