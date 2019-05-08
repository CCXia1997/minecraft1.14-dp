package net.minecraft.sound;

import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Arrays;
import java.util.Map;

public enum SoundCategory
{
    a("master"), 
    b("music"), 
    c("record"), 
    d("weather"), 
    e("block"), 
    f("hostile"), 
    g("neutral"), 
    h("player"), 
    i("ambient"), 
    j("voice");
    
    private static final Map<String, SoundCategory> NAME_MAP;
    private final String name;
    
    private SoundCategory(final String string1) {
        this.name = string1;
    }
    
    public String getName() {
        return this.name;
    }
    
    static {
        NAME_MAP = Arrays.<SoundCategory>stream(values()).collect(Collectors.toMap(SoundCategory::getName, Function.<SoundCategory>identity()));
    }
}
