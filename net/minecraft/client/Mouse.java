package net.minecraft.client;

import net.minecraft.client.gui.Element;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.SmoothUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Mouse
{
    private final MinecraftClient client;
    private boolean leftButtonClicked;
    private boolean middleButtonClicked;
    private boolean rightButtonClicked;
    private double x;
    private double y;
    private int controlLeftTicks;
    private int activeButton;
    private boolean hasResolutionChanged;
    private int j;
    private double glfwTime;
    private final SmoothUtil cursorXSmoother;
    private final SmoothUtil cursorYSmoother;
    private double cursorDeltaX;
    private double cursorDeltaY;
    private double eventDeltaWheel;
    private double q;
    private boolean isCursorLocked;
    
    public Mouse(final MinecraftClient client) {
        this.activeButton = -1;
        this.hasResolutionChanged = true;
        this.cursorXSmoother = new SmoothUtil();
        this.cursorYSmoother = new SmoothUtil();
        this.q = Double.MIN_VALUE;
        this.client = client;
    }
    
    private void onMouseButton(final long window, int button, final int action, final int mods) {
        if (window != this.client.window.getHandle()) {
            return;
        }
        final boolean boolean6 = action == 1;
        if (MinecraftClient.IS_SYSTEM_MAC && button == 0) {
            if (boolean6) {
                if ((mods & 0x2) == 0x2) {
                    button = 1;
                    ++this.controlLeftTicks;
                }
            }
            else if (this.controlLeftTicks > 0) {
                button = 1;
                --this.controlLeftTicks;
            }
        }
        final int integer7 = button;
        if (boolean6) {
            if (this.client.options.touchscreen && this.j++ > 0) {
                return;
            }
            this.activeButton = integer7;
            this.glfwTime = GlfwUtil.getTime();
        }
        else if (this.activeButton != -1) {
            if (this.client.options.touchscreen && --this.j > 0) {
                return;
            }
            this.activeButton = -1;
        }
        final boolean[] arr8 = { false };
        if (this.client.overlay == null) {
            if (this.client.currentScreen == null) {
                if (!this.isCursorLocked && boolean6) {
                    this.lockCursor();
                }
            }
            else {
                final double double9 = this.x * this.client.window.getScaledWidth() / this.client.window.getWidth();
                final double double10 = this.y * this.client.window.getScaledHeight() / this.client.window.getHeight();
                if (boolean6) {
                    Screen.wrapScreenError(() -> arr8[0] = this.client.currentScreen.mouseClicked(double9, double10, integer7), "mouseClicked event handler", this.client.currentScreen.getClass().getCanonicalName());
                }
                else {
                    Screen.wrapScreenError(() -> arr8[0] = this.client.currentScreen.mouseReleased(double9, double10, integer7), "mouseReleased event handler", this.client.currentScreen.getClass().getCanonicalName());
                }
            }
        }
        if (!arr8[0] && (this.client.currentScreen == null || this.client.currentScreen.passEvents) && this.client.overlay == null) {
            if (integer7 == 0) {
                this.leftButtonClicked = boolean6;
            }
            else if (integer7 == 2) {
                this.middleButtonClicked = boolean6;
            }
            else if (integer7 == 1) {
                this.rightButtonClicked = boolean6;
            }
            KeyBinding.setKeyPressed(InputUtil.Type.c.createFromCode(integer7), boolean6);
            if (boolean6) {
                if (this.client.player.isSpectator() && integer7 == 2) {
                    this.client.inGameHud.getSpectatorWidget().c();
                }
                else {
                    KeyBinding.onKeyPressed(InputUtil.Type.c.createFromCode(integer7));
                }
            }
        }
    }
    
    private void onMouseScroll(final long window, final double double3, final double double5) {
        if (window == MinecraftClient.getInstance().window.getHandle()) {
            final double double6 = (this.client.options.discreteMouseScroll ? Math.signum(double5) : double5) * this.client.options.mouseWheelSensitivity;
            if (this.client.overlay == null) {
                if (this.client.currentScreen != null) {
                    final double double7 = this.x * this.client.window.getScaledWidth() / this.client.window.getWidth();
                    final double double8 = this.y * this.client.window.getScaledHeight() / this.client.window.getHeight();
                    this.client.currentScreen.mouseScrolled(double7, double8, double6);
                }
                else if (this.client.player != null) {
                    if (this.eventDeltaWheel != 0.0 && Math.signum(double6) != Math.signum(this.eventDeltaWheel)) {
                        this.eventDeltaWheel = 0.0;
                    }
                    this.eventDeltaWheel += double6;
                    final float float9 = (float)(int)this.eventDeltaWheel;
                    if (float9 == 0.0f) {
                        return;
                    }
                    this.eventDeltaWheel -= float9;
                    if (this.client.player.isSpectator()) {
                        if (this.client.inGameHud.getSpectatorWidget().b()) {
                            this.client.inGameHud.getSpectatorWidget().a(-float9);
                        }
                        else {
                            final float float10 = MathHelper.clamp(this.client.player.abilities.getFlySpeed() + float9 * 0.005f, 0.0f, 0.2f);
                            this.client.player.abilities.setFlySpeed(float10);
                        }
                    }
                    else {
                        this.client.player.inventory.a(float9);
                    }
                }
            }
        }
    }
    
    public void setup(final long long1) {
        InputUtil.setMouseCallbacks(long1, this::onCursorPos, this::onMouseButton, this::onMouseScroll);
    }
    
    private void onCursorPos(final long window, final double double3, final double double5) {
        if (window != MinecraftClient.getInstance().window.getHandle()) {
            return;
        }
        if (this.hasResolutionChanged) {
            this.x = double3;
            this.y = double5;
            this.hasResolutionChanged = false;
        }
        final Element element7 = this.client.currentScreen;
        if (element7 != null && this.client.overlay == null) {
            final double double6 = double3 * this.client.window.getScaledWidth() / this.client.window.getWidth();
            final double double7 = double5 * this.client.window.getScaledHeight() / this.client.window.getHeight();
            Screen.wrapScreenError(() -> element7.mouseMoved(double6, double7), "mouseMoved event handler", element7.getClass().getCanonicalName());
            if (this.activeButton != -1 && this.glfwTime > 0.0) {
                final double double8 = (double3 - this.x) * this.client.window.getScaledWidth() / this.client.window.getWidth();
                final double double9 = (double5 - this.y) * this.client.window.getScaledHeight() / this.client.window.getHeight();
                Screen.wrapScreenError(() -> element7.mouseDragged(double6, double7, this.activeButton, double8, double9), "mouseDragged event handler", element7.getClass().getCanonicalName());
            }
        }
        this.client.getProfiler().push("mouse");
        if (this.isCursorLocked() && this.client.isWindowFocused()) {
            this.cursorDeltaX += double3 - this.x;
            this.cursorDeltaY += double5 - this.y;
        }
        this.updateMouse();
        this.x = double3;
        this.y = double5;
        this.client.getProfiler().pop();
    }
    
    public void updateMouse() {
        final double double1 = GlfwUtil.getTime();
        final double double2 = double1 - this.q;
        this.q = double1;
        if (!this.isCursorLocked() || !this.client.isWindowFocused()) {
            this.cursorDeltaX = 0.0;
            this.cursorDeltaY = 0.0;
            return;
        }
        final double double3 = this.client.options.mouseSensitivity * 0.6000000238418579 + 0.20000000298023224;
        final double double4 = double3 * double3 * double3 * 8.0;
        double double7;
        double double8;
        if (this.client.options.smoothCameraEnabled) {
            final double double5 = this.cursorXSmoother.smooth(this.cursorDeltaX * double4, double2 * double4);
            final double double6 = this.cursorYSmoother.smooth(this.cursorDeltaY * double4, double2 * double4);
            double7 = double5;
            double8 = double6;
        }
        else {
            this.cursorXSmoother.clear();
            this.cursorYSmoother.clear();
            double7 = this.cursorDeltaX * double4;
            double8 = this.cursorDeltaY * double4;
        }
        this.cursorDeltaX = 0.0;
        this.cursorDeltaY = 0.0;
        int integer13 = 1;
        if (this.client.options.invertYMouse) {
            integer13 = -1;
        }
        this.client.getTutorialManager().onUpdateMouse(double7, double8);
        if (this.client.player != null) {
            this.client.player.changeLookDirection(double7, double8 * integer13);
        }
    }
    
    public boolean wasLeftButtonClicked() {
        return this.leftButtonClicked;
    }
    
    public boolean wasRightButtonClicked() {
        return this.rightButtonClicked;
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public void onResolutionChanged() {
        this.hasResolutionChanged = true;
    }
    
    public boolean isCursorLocked() {
        return this.isCursorLocked;
    }
    
    public void lockCursor() {
        if (!this.client.isWindowFocused()) {
            return;
        }
        if (this.isCursorLocked) {
            return;
        }
        if (!MinecraftClient.IS_SYSTEM_MAC) {
            KeyBinding.updatePressedStates();
        }
        this.isCursorLocked = true;
        this.x = this.client.window.getWidth() / 2;
        this.y = this.client.window.getHeight() / 2;
        InputUtil.setCursorParameters(this.client.window.getHandle(), 212995, this.x, this.y);
        this.client.openScreen(null);
        this.client.attackCooldown = 10000;
    }
    
    public void unlockCursor() {
        if (!this.isCursorLocked) {
            return;
        }
        this.isCursorLocked = false;
        this.x = this.client.window.getWidth() / 2;
        this.y = this.client.window.getHeight() / 2;
        InputUtil.setCursorParameters(this.client.window.getHandle(), 212993, this.x, this.y);
    }
}
