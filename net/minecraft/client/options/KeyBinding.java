package net.minecraft.client.options;

import net.minecraft.util.SystemUtil;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.client.resource.language.I18n;
import java.util.Iterator;
import net.minecraft.client.MinecraftClient;
import java.util.Set;
import net.minecraft.client.util.InputUtil;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class KeyBinding implements Comparable<KeyBinding>
{
    private static final Map<String, KeyBinding> keysById;
    private static final Map<InputUtil.KeyCode, KeyBinding> keysByCode;
    private static final Set<String> keyCategories;
    private static final Map<String, Integer> categoryOrderMap;
    private final String id;
    private final InputUtil.KeyCode defaultKeyCode;
    private final String category;
    private InputUtil.KeyCode keyCode;
    private boolean pressed;
    private int timesPressed;
    
    public static void onKeyPressed(final InputUtil.KeyCode keyCode) {
        final KeyBinding keyBinding2 = KeyBinding.keysByCode.get(keyCode);
        if (keyBinding2 != null) {
            final KeyBinding keyBinding3 = keyBinding2;
            ++keyBinding3.timesPressed;
        }
    }
    
    public static void setKeyPressed(final InputUtil.KeyCode key, final boolean pressed) {
        final KeyBinding keyBinding3 = KeyBinding.keysByCode.get(key);
        if (keyBinding3 != null) {
            keyBinding3.pressed = pressed;
        }
    }
    
    public static void updatePressedStates() {
        for (final KeyBinding keyBinding2 : KeyBinding.keysById.values()) {
            if (keyBinding2.keyCode.getCategory() == InputUtil.Type.a && keyBinding2.keyCode.getKeyCode() != InputUtil.UNKNOWN_KEYCODE.getKeyCode()) {
                keyBinding2.pressed = InputUtil.isKeyPressed(MinecraftClient.getInstance().window.getHandle(), keyBinding2.keyCode.getKeyCode());
            }
        }
    }
    
    public static void unpressAll() {
        for (final KeyBinding keyBinding2 : KeyBinding.keysById.values()) {
            keyBinding2.reset();
        }
    }
    
    public static void updateKeysByCode() {
        KeyBinding.keysByCode.clear();
        for (final KeyBinding keyBinding2 : KeyBinding.keysById.values()) {
            KeyBinding.keysByCode.put(keyBinding2.keyCode, keyBinding2);
        }
    }
    
    public KeyBinding(final String id, final int keyCode, final String category) {
        this(id, InputUtil.Type.a, keyCode, category);
    }
    
    public KeyBinding(final String id, final InputUtil.Type type, final int code, final String category) {
        this.id = id;
        this.keyCode = type.createFromCode(code);
        this.defaultKeyCode = this.keyCode;
        this.category = category;
        KeyBinding.keysById.put(id, this);
        KeyBinding.keysByCode.put(this.keyCode, this);
        KeyBinding.keyCategories.add(category);
    }
    
    public boolean isPressed() {
        return this.pressed;
    }
    
    public String getCategory() {
        return this.category;
    }
    
    public boolean wasPressed() {
        if (this.timesPressed == 0) {
            return false;
        }
        --this.timesPressed;
        return true;
    }
    
    private void reset() {
        this.timesPressed = 0;
        this.pressed = false;
    }
    
    public String getId() {
        return this.id;
    }
    
    public InputUtil.KeyCode getDefaultKeyCode() {
        return this.defaultKeyCode;
    }
    
    public void setKeyCode(final InputUtil.KeyCode keyCode) {
        this.keyCode = keyCode;
    }
    
    public int a(final KeyBinding keyBinding) {
        if (this.category.equals(keyBinding.category)) {
            return I18n.translate(this.id).compareTo(I18n.translate(keyBinding.id));
        }
        return KeyBinding.categoryOrderMap.get(this.category).compareTo(KeyBinding.categoryOrderMap.get(keyBinding.category));
    }
    
    public static Supplier<String> getLocalizedName(final String id) {
        final KeyBinding keyBinding2 = KeyBinding.keysById.get(id);
        if (keyBinding2 == null) {
            return () -> id;
        }
        return keyBinding2::getLocalizedName;
    }
    
    public boolean equals(final KeyBinding keyBinding) {
        return this.keyCode.equals(keyBinding.keyCode);
    }
    
    public boolean isNotBound() {
        return this.keyCode.equals(InputUtil.UNKNOWN_KEYCODE);
    }
    
    public boolean matchesKey(final int keyCode, final int scanCode) {
        if (keyCode == InputUtil.UNKNOWN_KEYCODE.getKeyCode()) {
            return this.keyCode.getCategory() == InputUtil.Type.b && this.keyCode.getKeyCode() == scanCode;
        }
        return this.keyCode.getCategory() == InputUtil.Type.a && this.keyCode.getKeyCode() == keyCode;
    }
    
    public boolean matchesMouse(final int code) {
        return this.keyCode.getCategory() == InputUtil.Type.c && this.keyCode.getKeyCode() == code;
    }
    
    public String getLocalizedName() {
        final String string1 = this.keyCode.getName();
        final int integer2 = this.keyCode.getKeyCode();
        String string2 = null;
        switch (this.keyCode.getCategory()) {
            case a: {
                string2 = InputUtil.getKeycodeName(integer2);
                break;
            }
            case b: {
                string2 = InputUtil.getScancodeName(integer2);
                break;
            }
            case c: {
                final String string3 = I18n.translate(string1);
                string2 = (Objects.equals(string3, string1) ? I18n.translate(InputUtil.Type.c.getName(), integer2 + 1) : string3);
                break;
            }
        }
        return (string2 == null) ? I18n.translate(string1) : string2;
    }
    
    public boolean isDefault() {
        return this.keyCode.equals(this.defaultKeyCode);
    }
    
    public String getName() {
        return this.keyCode.getName();
    }
    
    static {
        keysById = Maps.newHashMap();
        keysByCode = Maps.newHashMap();
        keyCategories = Sets.newHashSet();
        categoryOrderMap = SystemUtil.<Map<String, Integer>>consume(Maps.newHashMap(), hashMap -> {
            hashMap.put("key.categories.movement", 1);
            hashMap.put("key.categories.gameplay", 2);
            hashMap.put("key.categories.inventory", 3);
            hashMap.put("key.categories.creative", 4);
            hashMap.put("key.categories.multiplayer", 5);
            hashMap.put("key.categories.ui", 6);
            hashMap.put("key.categories.misc", 7);
        });
    }
}
