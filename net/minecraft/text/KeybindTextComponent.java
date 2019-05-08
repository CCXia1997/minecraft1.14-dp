package net.minecraft.text;

import java.util.function.Supplier;
import java.util.function.Function;

public class KeybindTextComponent extends AbstractTextComponent
{
    public static Function<String, Supplier<String>> b;
    private final String keybind;
    private Supplier<String> d;
    
    public KeybindTextComponent(final String string) {
        this.keybind = string;
    }
    
    @Override
    public String getText() {
        if (this.d == null) {
            this.d = KeybindTextComponent.b.apply(this.keybind);
        }
        return this.d.get();
    }
    
    @Override
    public KeybindTextComponent copyShallow() {
        return new KeybindTextComponent(this.keybind);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof KeybindTextComponent) {
            final KeybindTextComponent keybindTextComponent2 = (KeybindTextComponent)o;
            return this.keybind.equals(keybindTextComponent2.keybind) && super.equals(o);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "KeybindComponent{keybind='" + this.keybind + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
    }
    
    public String getKeybind() {
        return this.keybind;
    }
    
    static {
        KeybindTextComponent.b = (string -> () -> string);
    }
}
