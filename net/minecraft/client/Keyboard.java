package net.minecraft.client;

import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.gui.menu.AccessibilityScreen;
import net.minecraft.client.gui.menu.options.ChatOptionsScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.client.gui.menu.options.ControlsOptionsScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.arguments.BlockArgumentParser;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.entity.Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.world.dimension.DimensionType;
import java.util.Locale;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.options.GameOption;
import net.minecraft.util.SystemUtil;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.StringTextComponent;
import net.minecraft.client.util.Clipboard;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class Keyboard
{
    private final MinecraftClient client;
    private boolean repeatEvents;
    private final Clipboard clipboard;
    private long debugCrashStartTime;
    private long debugCrashLastLogTime;
    private long debugCrashElapsedTime;
    private boolean switchF3State;
    
    public Keyboard(final MinecraftClient minecraftClient) {
        this.clipboard = new Clipboard();
        this.debugCrashStartTime = -1L;
        this.debugCrashLastLogTime = -1L;
        this.debugCrashElapsedTime = -1L;
        this.client = minecraftClient;
    }
    
    private void debugWarn(final String string, final Object... arr) {
        this.client.inGameHud.getChatHud().addMessage(new StringTextComponent("").append(new TranslatableTextComponent("debug.prefix", new Object[0]).applyFormat(TextFormat.o, TextFormat.r)).append(" ").append(new TranslatableTextComponent(string, arr)));
    }
    
    private void debugError(final String string, final Object... arr) {
        this.client.inGameHud.getChatHud().addMessage(new StringTextComponent("").append(new TranslatableTextComponent("debug.prefix", new Object[0]).applyFormat(TextFormat.m, TextFormat.r)).append(" ").append(new TranslatableTextComponent(string, arr)));
    }
    
    private boolean processF3(final int key) {
        if (this.debugCrashStartTime > 0L && this.debugCrashStartTime < SystemUtil.getMeasuringTimeMs() - 100L) {
            return true;
        }
        switch (key) {
            case 65: {
                this.client.worldRenderer.reload();
                this.debugWarn("debug.reload_chunks.message");
                return true;
            }
            case 66: {
                final boolean boolean2 = !this.client.getEntityRenderManager().shouldRenderHitboxes();
                this.client.getEntityRenderManager().setRenderHitboxes(boolean2);
                this.debugWarn(boolean2 ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
                return true;
            }
            case 68: {
                if (this.client.inGameHud != null) {
                    this.client.inGameHud.getChatHud().clear(false);
                }
                return true;
            }
            case 70: {
                GameOption.RENDER_DISTANCE.set(this.client.options, MathHelper.clamp(this.client.options.viewDistance + (Screen.hasShiftDown() ? -1 : 1), GameOption.RENDER_DISTANCE.getMin(), GameOption.RENDER_DISTANCE.getMax()));
                this.debugWarn("debug.cycle_renderdistance.message", this.client.options.viewDistance);
                return true;
            }
            case 71: {
                final boolean boolean3 = this.client.debugRenderer.toggleShowChunkBorder();
                this.debugWarn(boolean3 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
                return true;
            }
            case 72: {
                this.client.options.advancedItemTooltips = !this.client.options.advancedItemTooltips;
                this.debugWarn(this.client.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
                this.client.options.write();
                return true;
            }
            case 73: {
                if (!this.client.player.getReducedDebugInfo()) {
                    this.copyLookAt(this.client.player.allowsPermissionLevel(2), !Screen.hasShiftDown());
                }
                return true;
            }
            case 78: {
                if (!this.client.player.allowsPermissionLevel(2)) {
                    this.debugWarn("debug.creative_spectator.error");
                }
                else if (this.client.player.isCreative()) {
                    this.client.player.sendChatMessage("/gamemode spectator");
                }
                else if (this.client.player.isSpectator()) {
                    this.client.player.sendChatMessage("/gamemode creative");
                }
                return true;
            }
            case 80: {
                this.client.options.pauseOnLostFocus = !this.client.options.pauseOnLostFocus;
                this.client.options.write();
                this.debugWarn(this.client.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
                return true;
            }
            case 81: {
                this.debugWarn("debug.help.message");
                final ChatHud chatHud4 = this.client.inGameHud.getChatHud();
                chatHud4.addMessage(new TranslatableTextComponent("debug.reload_chunks.help", new Object[0]));
                chatHud4.addMessage(new TranslatableTextComponent("debug.show_hitboxes.help", new Object[0]));
                chatHud4.addMessage(new TranslatableTextComponent("debug.copy_location.help", new Object[0]));
                chatHud4.addMessage(new TranslatableTextComponent("debug.clear_chat.help", new Object[0]));
                chatHud4.addMessage(new TranslatableTextComponent("debug.cycle_renderdistance.help", new Object[0]));
                chatHud4.addMessage(new TranslatableTextComponent("debug.chunk_boundaries.help", new Object[0]));
                chatHud4.addMessage(new TranslatableTextComponent("debug.advanced_tooltips.help", new Object[0]));
                chatHud4.addMessage(new TranslatableTextComponent("debug.inspect.help", new Object[0]));
                chatHud4.addMessage(new TranslatableTextComponent("debug.creative_spectator.help", new Object[0]));
                chatHud4.addMessage(new TranslatableTextComponent("debug.pause_focus.help", new Object[0]));
                chatHud4.addMessage(new TranslatableTextComponent("debug.help.help", new Object[0]));
                chatHud4.addMessage(new TranslatableTextComponent("debug.reload_resourcepacks.help", new Object[0]));
                return true;
            }
            case 84: {
                this.debugWarn("debug.reload_resourcepacks.message");
                this.client.reloadResources();
                return true;
            }
            case 67: {
                if (this.client.player.getReducedDebugInfo()) {
                    return false;
                }
                this.debugWarn("debug.copy_location.message");
                this.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", DimensionType.getId(this.client.player.world.dimension.getType()), this.client.player.x, this.client.player.y, this.client.player.z, this.client.player.yaw, this.client.player.pitch));
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private void copyLookAt(final boolean boolean1, final boolean boolean2) {
        final HitResult hitResult3 = this.client.hitResult;
        if (hitResult3 == null) {
            return;
        }
        switch (hitResult3.getType()) {
            case BLOCK: {
                final BlockPos blockPos4 = ((BlockHitResult)hitResult3).getBlockPos();
                final BlockState blockState5 = this.client.player.world.getBlockState(blockPos4);
                if (!boolean1) {
                    this.copyBlock(blockState5, blockPos4, null);
                    this.debugWarn("debug.inspect.client.block");
                    break;
                }
                if (boolean2) {
                    this.client.player.networkHandler.getQueryHandler().queryBlockNbt(blockPos4, compoundTag -> {
                        this.copyBlock(blockState5, blockPos4, compoundTag);
                        this.debugWarn("debug.inspect.server.block");
                        return;
                    });
                    break;
                }
                final BlockEntity blockEntity6 = this.client.player.world.getBlockEntity(blockPos4);
                final CompoundTag compoundTag2 = (blockEntity6 != null) ? blockEntity6.toTag(new CompoundTag()) : null;
                this.copyBlock(blockState5, blockPos4, compoundTag2);
                this.debugWarn("debug.inspect.client.block");
                break;
            }
            case ENTITY: {
                final Entity entity4 = ((EntityHitResult)hitResult3).getEntity();
                final Identifier identifier5 = Registry.ENTITY_TYPE.getId(entity4.getType());
                final Vec3d vec3d6 = new Vec3d(entity4.x, entity4.y, entity4.z);
                if (!boolean1) {
                    this.copyEntity(identifier5, vec3d6, null);
                    this.debugWarn("debug.inspect.client.entity");
                    break;
                }
                if (boolean2) {
                    this.client.player.networkHandler.getQueryHandler().queryEntityNbt(entity4.getEntityId(), compoundTag -> {
                        this.copyEntity(identifier5, vec3d6, compoundTag);
                        this.debugWarn("debug.inspect.server.entity");
                        return;
                    });
                    break;
                }
                final CompoundTag compoundTag2 = entity4.toTag(new CompoundTag());
                this.copyEntity(identifier5, vec3d6, compoundTag2);
                this.debugWarn("debug.inspect.client.entity");
                break;
            }
        }
    }
    
    private void copyBlock(final BlockState blockState, final BlockPos blockPos, @Nullable final CompoundTag compoundTag) {
        if (compoundTag != null) {
            compoundTag.remove("x");
            compoundTag.remove("y");
            compoundTag.remove("z");
            compoundTag.remove("id");
        }
        final StringBuilder stringBuilder4 = new StringBuilder(BlockArgumentParser.stringifyBlockState(blockState));
        if (compoundTag != null) {
            stringBuilder4.append(compoundTag);
        }
        final String string5 = String.format(Locale.ROOT, "/setblock %d %d %d %s", blockPos.getX(), blockPos.getY(), blockPos.getZ(), stringBuilder4);
        this.setClipboard(string5);
    }
    
    private void copyEntity(final Identifier identifier, final Vec3d vec3d, @Nullable final CompoundTag compoundTag) {
        String string6;
        if (compoundTag != null) {
            compoundTag.remove("UUIDMost");
            compoundTag.remove("UUIDLeast");
            compoundTag.remove("Pos");
            compoundTag.remove("Dimension");
            final String string5 = compoundTag.toTextComponent().getString();
            string6 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", identifier.toString(), vec3d.x, vec3d.y, vec3d.z, string5);
        }
        else {
            string6 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", identifier.toString(), vec3d.x, vec3d.y, vec3d.z);
        }
        this.setClipboard(string6);
    }
    
    public void onKey(final long window, final int key, final int scancode, final int integer5, final int integer6) {
        if (window != this.client.window.getHandle()) {
            return;
        }
        if (this.debugCrashStartTime > 0L) {
            if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 67) || !InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 292)) {
                this.debugCrashStartTime = -1L;
            }
        }
        else if (InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 67) && InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 292)) {
            this.switchF3State = true;
            this.debugCrashStartTime = SystemUtil.getMeasuringTimeMs();
            this.debugCrashLastLogTime = SystemUtil.getMeasuringTimeMs();
            this.debugCrashElapsedTime = 0L;
        }
        final ParentElement parentElement7 = this.client.currentScreen;
        if (integer5 == 1 && (!(this.client.currentScreen instanceof ControlsOptionsScreen) || ((ControlsOptionsScreen)parentElement7).time <= SystemUtil.getMeasuringTimeMs() - 20L)) {
            if (this.client.options.keyFullscreen.matchesKey(key, scancode)) {
                this.client.window.toggleFullscreen();
                this.client.options.fullscreen = this.client.window.isFullscreen();
                return;
            }
            if (this.client.options.keyScreenshot.matchesKey(key, scancode)) {
                if (Screen.hasControlDown()) {}
                ScreenshotUtils.a(this.client.runDirectory, this.client.window.getFramebufferWidth(), this.client.window.getFramebufferHeight(), this.client.getFramebuffer(), textComponent -> this.client.execute(() -> this.client.inGameHud.getChatHud().addMessage(textComponent)));
                return;
            }
        }
        final boolean boolean8 = parentElement7 == null || !(parentElement7.getFocused() instanceof TextFieldWidget) || !((TextFieldWidget)parentElement7.getFocused()).f();
        if (integer5 != 0 && key == 66 && Screen.hasControlDown() && boolean8) {
            GameOption.NARRATOR.a(this.client.options, 1);
            if (parentElement7 instanceof ChatOptionsScreen) {
                ((ChatOptionsScreen)parentElement7).a();
            }
            if (parentElement7 instanceof AccessibilityScreen) {
                ((AccessibilityScreen)parentElement7).a();
            }
        }
        if (parentElement7 != null) {
            final boolean[] arr9 = { false };
            final Object o;
            final ParentElement parentElement8;
            Screen.wrapScreenError(() -> {
                if (integer5 == 1 || (integer5 == 2 && this.repeatEvents)) {
                    o[0] = parentElement8.keyPressed(key, scancode, integer6);
                }
                else if (integer5 == 0) {
                    o[0] = parentElement8.keyReleased(key, scancode, integer6);
                }
                return;
            }, "keyPressed event handler", parentElement7.getClass().getCanonicalName());
            if (arr9[0]) {
                return;
            }
        }
        if (this.client.currentScreen == null || this.client.currentScreen.passEvents) {
            final InputUtil.KeyCode keyCode9 = InputUtil.getKeyCode(key, scancode);
            if (integer5 == 0) {
                KeyBinding.setKeyPressed(keyCode9, false);
                if (key == 292) {
                    if (this.switchF3State) {
                        this.switchF3State = false;
                    }
                    else {
                        this.client.options.debugEnabled = !this.client.options.debugEnabled;
                        this.client.options.debugProfilerEnabled = (this.client.options.debugEnabled && Screen.hasShiftDown());
                        this.client.options.debugTpsEnabled = (this.client.options.debugEnabled && Screen.hasAltDown());
                    }
                }
            }
            else {
                if (key == 293 && this.client.gameRenderer != null) {
                    this.client.gameRenderer.toggleShadersEnabled();
                }
                boolean boolean9 = false;
                if (this.client.currentScreen == null) {
                    if (key == 256) {
                        this.client.openPauseMenu();
                    }
                    boolean9 = (InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), 292) && this.processF3(key));
                    this.switchF3State |= boolean9;
                    if (key == 290) {
                        this.client.options.hudHidden = !this.client.options.hudHidden;
                    }
                }
                if (boolean9) {
                    KeyBinding.setKeyPressed(keyCode9, false);
                }
                else {
                    KeyBinding.setKeyPressed(keyCode9, true);
                    KeyBinding.onKeyPressed(keyCode9);
                }
                if (this.client.options.debugProfilerEnabled) {
                    if (key == 48) {
                        this.client.handleProfilerKeyPress(0);
                    }
                    for (int integer7 = 0; integer7 < 9; ++integer7) {
                        if (key == 49 + integer7) {
                            this.client.handleProfilerKeyPress(integer7 + 1);
                        }
                    }
                }
            }
        }
    }
    
    private void onChar(final long window, final int integer3, final int integer4) {
        if (window != this.client.window.getHandle()) {
            return;
        }
        final Element element5 = this.client.currentScreen;
        if (element5 == null || this.client.getOverlay() != null) {
            return;
        }
        if (Character.charCount(integer3) == 1) {
            Screen.wrapScreenError(() -> element5.charTyped((char)integer3, integer4), "charTyped event handler", element5.getClass().getCanonicalName());
        }
        else {
            for (final char character9 : Character.toChars(integer3)) {
                Screen.wrapScreenError(() -> element5.charTyped(character9, integer4), "charTyped event handler", element5.getClass().getCanonicalName());
            }
        }
    }
    
    public void enableRepeatEvents(final boolean boolean1) {
        this.repeatEvents = boolean1;
    }
    
    public void setup(final long long1) {
        InputUtil.setKeyboardCallbacks(long1, this::onKey, this::onChar);
    }
    
    public String getClipboard() {
        return this.clipboard.getClipboard(this.client.window.getHandle(), (integer, long2) -> {
            if (integer != 65545) {
                this.client.window.logGlError(integer, long2);
            }
        });
    }
    
    public void setClipboard(final String string) {
        this.clipboard.setClipboard(this.client.window.getHandle(), string);
    }
    
    public void pollDebugCrash() {
        if (this.debugCrashStartTime > 0L) {
            final long long1 = SystemUtil.getMeasuringTimeMs();
            final long long2 = 10000L - (long1 - this.debugCrashStartTime);
            final long long3 = long1 - this.debugCrashLastLogTime;
            if (long2 < 0L) {
                if (Screen.hasControlDown()) {
                    GlfwUtil.a();
                }
                throw new CrashException(new CrashReport("Manually triggered debug crash", new Throwable()));
            }
            if (long3 >= 1000L) {
                if (this.debugCrashElapsedTime == 0L) {
                    this.debugWarn("debug.crash.message");
                }
                else {
                    this.debugError("debug.crash.warning", MathHelper.ceil(long2 / 1000.0f));
                }
                this.debugCrashLastLogTime = long1;
                ++this.debugCrashElapsedTime;
            }
        }
    }
}
