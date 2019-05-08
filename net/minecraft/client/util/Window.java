package net.minecraft.client.util;

import org.apache.logging.log4j.LogManager;
import net.minecraft.client.options.GameOption;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallbackI;
import javax.annotation.Nullable;
import java.nio.Buffer;
import com.mojang.blaze3d.platform.TextureUtil;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.io.IOException;
import org.lwjgl.stb.STBImage;
import org.lwjgl.glfw.GLFWImage;
import java.io.FileNotFoundException;
import java.io.InputStream;
import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.MemoryStack;
import java.util.function.BiConsumer;
import org.lwjgl.opengl.GL;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.WindowSettings;
import java.util.Optional;
import net.minecraft.client.WindowEventHandler;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class Window implements AutoCloseable
{
    private static final Logger LOGGER;
    private final GLFWErrorCallback errorCallback;
    private final WindowEventHandler windowEventHandler;
    private final MonitorTracker monitorTracker;
    private Monitor monitor;
    private final long handle;
    private int g;
    private int h;
    private int i;
    private int j;
    private Optional<VideoMode> videoMode;
    private boolean fullscreen;
    private boolean m;
    private int positionX;
    private int positionY;
    private int width;
    private int height;
    private int framebufferWidth;
    private int framebufferHeight;
    private int scaledWidth;
    private int scaledHeight;
    private double scaleFactor;
    private String phase;
    private boolean x;
    private double y;
    private int framerateLimit;
    private boolean A;
    
    public Window(final WindowEventHandler windowEventHandler, final MonitorTracker monitorTracker, final WindowSettings windowSettings, final String string4, final String string5) {
        this.errorCallback = GLFWErrorCallback.create(this::logGlError);
        this.phase = "";
        this.y = Double.MIN_VALUE;
        this.monitorTracker = monitorTracker;
        this.throwExceptionOnGlError();
        this.setPhase("Pre startup");
        this.windowEventHandler = windowEventHandler;
        final Optional<VideoMode> optional6 = VideoMode.fromString(string4);
        if (optional6.isPresent()) {
            this.videoMode = optional6;
        }
        else if (windowSettings.fullscreenWidth.isPresent() && windowSettings.fullscreenHeight.isPresent()) {
            this.videoMode = Optional.<VideoMode>of(new VideoMode(windowSettings.fullscreenWidth.get(), windowSettings.fullscreenHeight.get(), 8, 8, 8, 60));
        }
        else {
            this.videoMode = Optional.<VideoMode>empty();
        }
        final boolean fullscreen = windowSettings.fullscreen;
        this.fullscreen = fullscreen;
        this.m = fullscreen;
        this.monitor = monitorTracker.getMonitor(GLFW.glfwGetPrimaryMonitor());
        final VideoMode videoMode7 = this.monitor.findClosestVideoMode(this.fullscreen ? this.videoMode : Optional.<VideoMode>empty());
        final int n = (windowSettings.width > 0) ? windowSettings.width : 1;
        this.width = n;
        this.i = n;
        final int n2 = (windowSettings.height > 0) ? windowSettings.height : 1;
        this.height = n2;
        this.j = n2;
        final int n3 = this.monitor.getViewportX() + videoMode7.getWidth() / 2 - this.width / 2;
        this.positionX = n3;
        this.g = n3;
        final int n4 = this.monitor.getViewportY() + videoMode7.getHeight() / 2 - this.height / 2;
        this.positionY = n4;
        this.h = n4;
        GLFW.glfwDefaultWindowHints();
        this.handle = GLFW.glfwCreateWindow(this.width, this.height, (CharSequence)string5, this.fullscreen ? this.monitor.getHandle() : 0L, 0L);
        this.updateMonitor();
        GLFW.glfwMakeContextCurrent(this.handle);
        GL.createCapabilities();
        this.w();
        this.v();
        GLFW.glfwSetFramebufferSizeCallback(this.handle, this::onFramebufferSizeChanged);
        GLFW.glfwSetWindowPosCallback(this.handle, this::onWindowPosChanged);
        GLFW.glfwSetWindowSizeCallback(this.handle, this::onWindowSizeChanged);
        GLFW.glfwSetWindowFocusCallback(this.handle, this::onWindowFocusChanged);
    }
    
    public static void a(final BiConsumer<Integer, String> biConsumer) {
        try (final MemoryStack memoryStack2 = MemoryStack.stackPush()) {
            final PointerBuffer pointerBuffer4 = memoryStack2.mallocPointer(1);
            final int integer5 = GLFW.glfwGetError(pointerBuffer4);
            if (integer5 != 0) {
                final long long6 = pointerBuffer4.get();
                final String string8 = (long6 == 0L) ? "" : MemoryUtil.memUTF8(long6);
                biConsumer.accept(integer5, string8);
            }
        }
    }
    
    public void a(final boolean boolean1) {
        GlStateManager.clear(256, boolean1);
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0, this.getFramebufferWidth() / this.getScaleFactor(), this.getFramebufferHeight() / this.getScaleFactor(), 0.0, 1000.0, 3000.0);
        GlStateManager.matrixMode(5888);
        GlStateManager.loadIdentity();
        GlStateManager.translatef(0.0f, 0.0f, -2000.0f);
    }
    
    public void setIcon(final InputStream icon16, final InputStream icon32) {
        try (final MemoryStack memoryStack3 = MemoryStack.stackPush()) {
            if (icon16 == null) {
                throw new FileNotFoundException("icons/icon_16x16.png");
            }
            if (icon32 == null) {
                throw new FileNotFoundException("icons/icon_32x32.png");
            }
            final IntBuffer intBuffer5 = memoryStack3.mallocInt(1);
            final IntBuffer intBuffer6 = memoryStack3.mallocInt(1);
            final IntBuffer intBuffer7 = memoryStack3.mallocInt(1);
            final GLFWImage.Buffer buffer8 = GLFWImage.mallocStack(2, memoryStack3);
            final ByteBuffer byteBuffer9 = this.a(icon16, intBuffer5, intBuffer6, intBuffer7);
            if (byteBuffer9 == null) {
                throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }
            buffer8.position(0);
            buffer8.width(intBuffer5.get(0));
            buffer8.height(intBuffer6.get(0));
            buffer8.pixels(byteBuffer9);
            final ByteBuffer byteBuffer10 = this.a(icon32, intBuffer5, intBuffer6, intBuffer7);
            if (byteBuffer10 == null) {
                throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
            }
            buffer8.position(1);
            buffer8.width(intBuffer5.get(0));
            buffer8.height(intBuffer6.get(0));
            buffer8.pixels(byteBuffer10);
            buffer8.position(0);
            GLFW.glfwSetWindowIcon(this.handle, buffer8);
            STBImage.stbi_image_free(byteBuffer9);
            STBImage.stbi_image_free(byteBuffer10);
        }
        catch (IOException iOException3) {
            Window.LOGGER.error("Couldn't set icon", (Throwable)iOException3);
        }
    }
    
    @Nullable
    private ByteBuffer a(final InputStream inputStream, final IntBuffer intBuffer2, final IntBuffer intBuffer3, final IntBuffer intBuffer4) throws IOException {
        ByteBuffer byteBuffer5 = null;
        try {
            byteBuffer5 = TextureUtil.readResource(inputStream);
            byteBuffer5.rewind();
            return STBImage.stbi_load_from_memory(byteBuffer5, intBuffer2, intBuffer3, intBuffer4, 0);
        }
        finally {
            if (byteBuffer5 != null) {
                MemoryUtil.memFree((Buffer)byteBuffer5);
            }
        }
    }
    
    public void setPhase(final String phase) {
        this.phase = phase;
    }
    
    private void throwExceptionOnGlError() {
        GLFW.glfwSetErrorCallback(Window::throwExceptionForGlError);
    }
    
    private static void throwExceptionForGlError(final int integer, final long long2) {
        throw new IllegalStateException("GLFW error " + integer + ": " + MemoryUtil.memUTF8(long2));
    }
    
    public void logGlError(final int error, final long description) {
        final String string4 = MemoryUtil.memUTF8(description);
        Window.LOGGER.error("########## GL ERROR ##########");
        Window.LOGGER.error("@ {}", this.phase);
        Window.LOGGER.error("{}: {}", error, string4);
    }
    
    public void logOnGlError() {
        GLFW.glfwSetErrorCallback((GLFWErrorCallbackI)this.errorCallback).free();
    }
    
    public void setVsync(final boolean boolean1) {
        GLFW.glfwSwapInterval((int)((this.A = boolean1) ? 1 : 0));
    }
    
    @Override
    public void close() {
        Callbacks.glfwFreeCallbacks(this.handle);
        this.errorCallback.close();
        GLFW.glfwDestroyWindow(this.handle);
        GLFW.glfwTerminate();
    }
    
    private void updateMonitor() {
        final Monitor monitor1 = this.monitor;
        this.monitor = this.monitorTracker.getMonitor(this);
        GameOption.FULLSCREEN_RESOLUTION.setMax((float)this.monitor.getVideoModeCount());
    }
    
    private void onWindowPosChanged(final long window, final int xpos, final int ypos) {
        this.positionX = xpos;
        this.positionY = ypos;
        this.updateMonitor();
    }
    
    private void onFramebufferSizeChanged(final long window, final int width, final int height) {
        if (window != this.handle) {
            return;
        }
        final int integer5 = this.getFramebufferWidth();
        final int integer6 = this.getFramebufferHeight();
        if (width == 0 || height == 0) {
            return;
        }
        this.framebufferWidth = width;
        this.framebufferHeight = height;
        if (this.getFramebufferWidth() != integer5 || this.getFramebufferHeight() != integer6) {
            this.windowEventHandler.onResolutionChanged();
        }
    }
    
    private void v() {
        final int[] arr1 = { 0 };
        final int[] arr2 = { 0 };
        GLFW.glfwGetFramebufferSize(this.handle, arr1, arr2);
        this.framebufferWidth = arr1[0];
        this.framebufferHeight = arr2[0];
    }
    
    private void onWindowSizeChanged(final long window, final int width, final int height) {
        this.width = width;
        this.height = height;
        this.updateMonitor();
    }
    
    private void onWindowFocusChanged(final long window, final boolean focused) {
        if (window == this.handle) {
            this.windowEventHandler.onWindowFocusChanged(focused);
        }
    }
    
    public void setFramerateLimit(final int integer) {
        this.framerateLimit = integer;
    }
    
    public int getFramerateLimit() {
        return this.framerateLimit;
    }
    
    public void setFullscreen(final boolean boolean1) {
        GLFW.glfwSwapBuffers(this.handle);
        pollEvents();
        if (this.fullscreen != this.m) {
            this.m = this.fullscreen;
            this.d(this.A);
        }
    }
    
    public void waitForFramerateLimit() {
        double double1;
        double double2;
        for (double1 = this.y + 1.0 / this.getFramerateLimit(), double2 = GLFW.glfwGetTime(); double2 < double1; double2 = GLFW.glfwGetTime()) {
            GLFW.glfwWaitEventsTimeout(double1 - double2);
        }
        this.y = double2;
    }
    
    public Optional<VideoMode> getVideoMode() {
        return this.videoMode;
    }
    
    public int e() {
        if (this.videoMode.isPresent()) {
            return this.monitor.findClosestVideoModeIndex(this.videoMode) + 1;
        }
        return 0;
    }
    
    public String b(int integer) {
        if (this.monitor.getVideoModeCount() <= integer) {
            integer = this.monitor.getVideoModeCount() - 1;
        }
        return this.monitor.getVideoMode(integer).toString();
    }
    
    public void c(final int integer) {
        final Optional<VideoMode> optional2 = this.videoMode;
        if (integer == 0) {
            this.videoMode = Optional.<VideoMode>empty();
        }
        else {
            this.videoMode = Optional.<VideoMode>of(this.monitor.getVideoMode(integer - 1));
        }
        if (!this.videoMode.equals(optional2)) {
            this.x = true;
        }
    }
    
    public void f() {
        if (this.fullscreen && this.x) {
            this.x = false;
            this.w();
            this.windowEventHandler.onResolutionChanged();
        }
    }
    
    private void w() {
        final boolean boolean1 = GLFW.glfwGetWindowMonitor(this.handle) != 0L;
        if (this.fullscreen) {
            final VideoMode videoMode2 = this.monitor.findClosestVideoMode(this.videoMode);
            if (!boolean1) {
                this.g = this.positionX;
                this.h = this.positionY;
                this.i = this.width;
                this.j = this.height;
            }
            this.positionX = 0;
            this.positionY = 0;
            this.width = videoMode2.getWidth();
            this.height = videoMode2.getHeight();
            GLFW.glfwSetWindowMonitor(this.handle, this.monitor.getHandle(), this.positionX, this.positionY, this.width, this.height, videoMode2.getRefreshRate());
        }
        else {
            final VideoMode videoMode2 = this.monitor.getCurrentVideoMode();
            this.positionX = this.g;
            this.positionY = this.h;
            this.width = this.i;
            this.height = this.j;
            GLFW.glfwSetWindowMonitor(this.handle, 0L, this.positionX, this.positionY, this.width, this.height, -1);
        }
    }
    
    public void toggleFullscreen() {
        this.fullscreen = !this.fullscreen;
    }
    
    private void d(final boolean boolean1) {
        try {
            this.w();
            this.windowEventHandler.onResolutionChanged();
            this.setVsync(boolean1);
            this.windowEventHandler.updateDisplay(false);
        }
        catch (Exception exception2) {
            Window.LOGGER.error("Couldn't toggle fullscreen", (Throwable)exception2);
        }
    }
    
    public int calculateScaleFactor(final int guiScale, final boolean forceUnicodeFont) {
        int integer3;
        for (integer3 = 1; integer3 != guiScale && integer3 < this.framebufferWidth && integer3 < this.framebufferHeight && this.framebufferWidth / (integer3 + 1) >= 320 && this.framebufferHeight / (integer3 + 1) >= 240; ++integer3) {}
        if (forceUnicodeFont && integer3 % 2 != 0) {
            ++integer3;
        }
        return integer3;
    }
    
    public void setScaleFactor(final double scaleFactor) {
        this.scaleFactor = scaleFactor;
        final int integer3 = (int)(this.framebufferWidth / scaleFactor);
        this.scaledWidth = ((this.framebufferWidth / scaleFactor > integer3) ? (integer3 + 1) : integer3);
        final int integer4 = (int)(this.framebufferHeight / scaleFactor);
        this.scaledHeight = ((this.framebufferHeight / scaleFactor > integer4) ? (integer4 + 1) : integer4);
    }
    
    public long getHandle() {
        return this.handle;
    }
    
    public boolean isFullscreen() {
        return this.fullscreen;
    }
    
    public int getFramebufferWidth() {
        return this.framebufferWidth;
    }
    
    public int getFramebufferHeight() {
        return this.framebufferHeight;
    }
    
    public static void pollEvents() {
        GLFW.glfwPollEvents();
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public int getScaledWidth() {
        return this.scaledWidth;
    }
    
    public int getScaledHeight() {
        return this.scaledHeight;
    }
    
    public int getPositionX() {
        return this.positionX;
    }
    
    public int getPositionY() {
        return this.positionY;
    }
    
    public double getScaleFactor() {
        return this.scaleFactor;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
