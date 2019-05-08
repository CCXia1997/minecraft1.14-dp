package net.minecraft.village;

import java.util.stream.Collector;
import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import java.util.Map;

public enum VillageGossipType
{
    a("major_negative", -5, 100, 1, 10), 
    b("minor_negative", -1, 200, 2, 20), 
    c("minor_positive", 1, 200, 2, 20), 
    d("major_positive", 5, 100, 1, 10), 
    e("trading", 1, 25, 2, 20), 
    f("golem", 1, 100, 1, 1); //铁傀儡流言，倍率1，最大名声100，初始value为1？
    
    public final String key;
    public final int multiplier;
    public final int maxReputation;
    public final int j;
    public final int value;
    private static final Map<String, VillageGossipType> BY_KEY;
    
    private VillageGossipType(final String key, final int multiplier, final int maxReputation, final int integer4, final int value) {
        this.key = key;
        this.multiplier = multiplier;
        this.maxReputation = maxReputation;
        this.j = integer4;
        this.value = value;
    }
    
    @Nullable
    public static VillageGossipType byKey(final String key) {
        return VillageGossipType.BY_KEY.get(key);
    }
    
    static {
        BY_KEY = Stream.<VillageGossipType>of(values()).collect(ImmutableMap.toImmutableMap(villageGossipType -> villageGossipType.key, Function.identity()));
    }
}
