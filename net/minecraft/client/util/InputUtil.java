package net.minecraft.client.util;

import com.google.common.collect.Maps;
import java.util.Objects;
import java.util.Map;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import javax.annotation.Nullable;
import org.lwjgl.glfw.GLFWScrollCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWCharModsCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class InputUtil
{
    public static final KeyCode UNKNOWN_KEYCODE;
    
    public static KeyCode getKeyCode(final int integer1, final int integer2) {
        if (integer1 == -1) {
            return Type.b.createFromCode(integer2);
        }
        return Type.a.createFromCode(integer1);
    }
    
    public static KeyCode fromName(final String s) {
        if (KeyCode.NAMES.containsKey(s)) {
            return KeyCode.NAMES.get(s);
        }
        for (final Type type5 : Type.values()) {
            if (s.startsWith(type5.name)) {
                final String string6 = s.substring(type5.name.length() + 1);
                return type5.createFromCode(Integer.parseInt(string6));
            }
        }
        throw new IllegalArgumentException("Unknown key name: " + s);
    }
    
    public static boolean isKeyPressed(final long handle, final int integer3) {
        return GLFW.glfwGetKey(handle, integer3) == 1;
    }
    
    public static void setKeyboardCallbacks(final long handle, final GLFWKeyCallbackI gLFWKeyCallbackI, final GLFWCharModsCallbackI gLFWCharModsCallbackI4) {
        GLFW.glfwSetKeyCallback(handle, gLFWKeyCallbackI);
        GLFW.glfwSetCharModsCallback(handle, gLFWCharModsCallbackI4);
    }
    
    public static void setMouseCallbacks(final long handle, final GLFWCursorPosCallbackI gLFWCursorPosCallbackI, final GLFWMouseButtonCallbackI gLFWMouseButtonCallbackI, final GLFWScrollCallbackI gLFWScrollCallbackI5) {
        GLFW.glfwSetCursorPosCallback(handle, gLFWCursorPosCallbackI);
        GLFW.glfwSetMouseButtonCallback(handle, gLFWMouseButtonCallbackI);
        GLFW.glfwSetScrollCallback(handle, gLFWScrollCallbackI5);
    }
    
    public static void setCursorParameters(final long long1, final int integer, final double double4, final double double6) {
        GLFW.glfwSetCursorPos(long1, double4, double6);
        GLFW.glfwSetInputMode(long1, 208897, integer);
    }
    
    @Nullable
    public static String getKeycodeName(final int integer) {
        return GLFW.glfwGetKeyName(integer, -1);
    }
    
    @Nullable
    public static String getScancodeName(final int integer) {
        return GLFW.glfwGetKeyName(-1, integer);
    }
    
    static {
        UNKNOWN_KEYCODE = Type.a.createFromCode(-1);
    }
    
    @Environment(EnvType.CLIENT)
    public enum Type
    {
        a("key.keyboard"), 
        b("scancode"), 
        c("key.mouse");
        
        private static final String[] mouseButtons;
        private final Int2ObjectMap<KeyCode> map;
        private final String name;
        
        private static void mapKey(final Type type, final String name, final int keyCode) {
            final KeyCode keyCode2 = new KeyCode(name, type, keyCode);
            type.map.put(keyCode, keyCode2);
        }
        
        private Type(final String string1) {
            this.map = (Int2ObjectMap<KeyCode>)new Int2ObjectOpenHashMap();
            this.name = string1;
        }
        
        public KeyCode createFromCode(final int integer) {
            if (this.map.containsKey(integer)) {
                return (KeyCode)this.map.get(integer);
            }
            String string2;
            if (this == Type.c) {
                if (integer <= 2) {
                    string2 = "." + Type.mouseButtons[integer];
                }
                else {
                    string2 = "." + (integer + 1);
                }
            }
            else {
                string2 = "." + integer;
            }
            final KeyCode keyCode3 = new KeyCode(this.name + string2, this, integer);
            this.map.put(integer, keyCode3);
            return keyCode3;
        }
        
        public String getName() {
            return this.name;
        }
        
        static {
            mapKey(Type.a, "key.keyboard.unknown", -1);
            mapKey(Type.c, "key.mouse.left", 0);
            mapKey(Type.c, "key.mouse.right", 1);
            mapKey(Type.c, "key.mouse.middle", 2);
            mapKey(Type.c, "key.mouse.4", 3);
            mapKey(Type.c, "key.mouse.5", 4);
            mapKey(Type.c, "key.mouse.6", 5);
            mapKey(Type.c, "key.mouse.7", 6);
            mapKey(Type.c, "key.mouse.8", 7);
            mapKey(Type.a, "key.keyboard.0", 48);
            mapKey(Type.a, "key.keyboard.1", 49);
            mapKey(Type.a, "key.keyboard.2", 50);
            mapKey(Type.a, "key.keyboard.3", 51);
            mapKey(Type.a, "key.keyboard.4", 52);
            mapKey(Type.a, "key.keyboard.5", 53);
            mapKey(Type.a, "key.keyboard.6", 54);
            mapKey(Type.a, "key.keyboard.7", 55);
            mapKey(Type.a, "key.keyboard.8", 56);
            mapKey(Type.a, "key.keyboard.9", 57);
            mapKey(Type.a, "key.keyboard.a", 65);
            mapKey(Type.a, "key.keyboard.b", 66);
            mapKey(Type.a, "key.keyboard.c", 67);
            mapKey(Type.a, "key.keyboard.d", 68);
            mapKey(Type.a, "key.keyboard.e", 69);
            mapKey(Type.a, "key.keyboard.f", 70);
            mapKey(Type.a, "key.keyboard.g", 71);
            mapKey(Type.a, "key.keyboard.h", 72);
            mapKey(Type.a, "key.keyboard.i", 73);
            mapKey(Type.a, "key.keyboard.j", 74);
            mapKey(Type.a, "key.keyboard.k", 75);
            mapKey(Type.a, "key.keyboard.l", 76);
            mapKey(Type.a, "key.keyboard.m", 77);
            mapKey(Type.a, "key.keyboard.n", 78);
            mapKey(Type.a, "key.keyboard.o", 79);
            mapKey(Type.a, "key.keyboard.p", 80);
            mapKey(Type.a, "key.keyboard.q", 81);
            mapKey(Type.a, "key.keyboard.r", 82);
            mapKey(Type.a, "key.keyboard.s", 83);
            mapKey(Type.a, "key.keyboard.t", 84);
            mapKey(Type.a, "key.keyboard.u", 85);
            mapKey(Type.a, "key.keyboard.v", 86);
            mapKey(Type.a, "key.keyboard.w", 87);
            mapKey(Type.a, "key.keyboard.x", 88);
            mapKey(Type.a, "key.keyboard.y", 89);
            mapKey(Type.a, "key.keyboard.z", 90);
            mapKey(Type.a, "key.keyboard.f1", 290);
            mapKey(Type.a, "key.keyboard.f2", 291);
            mapKey(Type.a, "key.keyboard.f3", 292);
            mapKey(Type.a, "key.keyboard.f4", 293);
            mapKey(Type.a, "key.keyboard.f5", 294);
            mapKey(Type.a, "key.keyboard.f6", 295);
            mapKey(Type.a, "key.keyboard.f7", 296);
            mapKey(Type.a, "key.keyboard.f8", 297);
            mapKey(Type.a, "key.keyboard.f9", 298);
            mapKey(Type.a, "key.keyboard.f10", 299);
            mapKey(Type.a, "key.keyboard.f11", 300);
            mapKey(Type.a, "key.keyboard.f12", 301);
            mapKey(Type.a, "key.keyboard.f13", 302);
            mapKey(Type.a, "key.keyboard.f14", 303);
            mapKey(Type.a, "key.keyboard.f15", 304);
            mapKey(Type.a, "key.keyboard.f16", 305);
            mapKey(Type.a, "key.keyboard.f17", 306);
            mapKey(Type.a, "key.keyboard.f18", 307);
            mapKey(Type.a, "key.keyboard.f19", 308);
            mapKey(Type.a, "key.keyboard.f20", 309);
            mapKey(Type.a, "key.keyboard.f21", 310);
            mapKey(Type.a, "key.keyboard.f22", 311);
            mapKey(Type.a, "key.keyboard.f23", 312);
            mapKey(Type.a, "key.keyboard.f24", 313);
            mapKey(Type.a, "key.keyboard.f25", 314);
            mapKey(Type.a, "key.keyboard.num.lock", 282);
            mapKey(Type.a, "key.keyboard.keypad.0", 320);
            mapKey(Type.a, "key.keyboard.keypad.1", 321);
            mapKey(Type.a, "key.keyboard.keypad.2", 322);
            mapKey(Type.a, "key.keyboard.keypad.3", 323);
            mapKey(Type.a, "key.keyboard.keypad.4", 324);
            mapKey(Type.a, "key.keyboard.keypad.5", 325);
            mapKey(Type.a, "key.keyboard.keypad.6", 326);
            mapKey(Type.a, "key.keyboard.keypad.7", 327);
            mapKey(Type.a, "key.keyboard.keypad.8", 328);
            mapKey(Type.a, "key.keyboard.keypad.9", 329);
            mapKey(Type.a, "key.keyboard.keypad.add", 334);
            mapKey(Type.a, "key.keyboard.keypad.decimal", 330);
            mapKey(Type.a, "key.keyboard.keypad.enter", 335);
            mapKey(Type.a, "key.keyboard.keypad.equal", 336);
            mapKey(Type.a, "key.keyboard.keypad.multiply", 332);
            mapKey(Type.a, "key.keyboard.keypad.divide", 331);
            mapKey(Type.a, "key.keyboard.keypad.subtract", 333);
            mapKey(Type.a, "key.keyboard.down", 264);
            mapKey(Type.a, "key.keyboard.left", 263);
            mapKey(Type.a, "key.keyboard.right", 262);
            mapKey(Type.a, "key.keyboard.up", 265);
            mapKey(Type.a, "key.keyboard.apostrophe", 39);
            mapKey(Type.a, "key.keyboard.backslash", 92);
            mapKey(Type.a, "key.keyboard.comma", 44);
            mapKey(Type.a, "key.keyboard.equal", 61);
            mapKey(Type.a, "key.keyboard.grave.accent", 96);
            mapKey(Type.a, "key.keyboard.left.bracket", 91);
            mapKey(Type.a, "key.keyboard.minus", 45);
            mapKey(Type.a, "key.keyboard.period", 46);
            mapKey(Type.a, "key.keyboard.right.bracket", 93);
            mapKey(Type.a, "key.keyboard.semicolon", 59);
            mapKey(Type.a, "key.keyboard.slash", 47);
            mapKey(Type.a, "key.keyboard.space", 32);
            mapKey(Type.a, "key.keyboard.tab", 258);
            mapKey(Type.a, "key.keyboard.left.alt", 342);
            mapKey(Type.a, "key.keyboard.left.control", 341);
            mapKey(Type.a, "key.keyboard.left.shift", 340);
            mapKey(Type.a, "key.keyboard.left.win", 343);
            mapKey(Type.a, "key.keyboard.right.alt", 346);
            mapKey(Type.a, "key.keyboard.right.control", 345);
            mapKey(Type.a, "key.keyboard.right.shift", 344);
            mapKey(Type.a, "key.keyboard.right.win", 347);
            mapKey(Type.a, "key.keyboard.enter", 257);
            mapKey(Type.a, "key.keyboard.escape", 256);
            mapKey(Type.a, "key.keyboard.backspace", 259);
            mapKey(Type.a, "key.keyboard.delete", 261);
            mapKey(Type.a, "key.keyboard.end", 269);
            mapKey(Type.a, "key.keyboard.home", 268);
            mapKey(Type.a, "key.keyboard.insert", 260);
            mapKey(Type.a, "key.keyboard.page.down", 267);
            mapKey(Type.a, "key.keyboard.page.up", 266);
            mapKey(Type.a, "key.keyboard.caps.lock", 280);
            mapKey(Type.a, "key.keyboard.pause", 284);
            mapKey(Type.a, "key.keyboard.scroll.lock", 281);
            mapKey(Type.a, "key.keyboard.menu", 348);
            mapKey(Type.a, "key.keyboard.print.screen", 283);
            mapKey(Type.a, "key.keyboard.world.1", 161);
            mapKey(Type.a, "key.keyboard.world.2", 162);
            mouseButtons = new String[] { "left", "middle", "right" };
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static final class KeyCode
    {
        private final String name;
        private final Type type;
        private final int keyCode;
        private static final Map<String, KeyCode> NAMES;
        
        private KeyCode(final String keyName, final Type type, final int integer) {
            this.name = keyName;
            this.type = type;
            this.keyCode = integer;
            KeyCode.NAMES.put(keyName, this);
        }
        
        public Type getCategory() {
            return this.type;
        }
        
        public int getKeyCode() {
            return this.keyCode;
        }
        
        public String getName() {
            return this.name;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final KeyCode keyCode2 = (KeyCode)o;
            return this.keyCode == keyCode2.keyCode && this.type == keyCode2.type;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.type, this.keyCode);
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        static {
            NAMES = Maps.newHashMap();
        }
    }
}
