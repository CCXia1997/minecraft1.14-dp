package net.minecraft.world.loot.context;

import net.minecraft.util.Identifier;
import net.minecraft.item.ItemStack;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;

public class LootContextParameters
{
    public static final LootContextParameter<Entity> a;
    public static final LootContextParameter<PlayerEntity> b;
    public static final LootContextParameter<DamageSource> c;
    public static final LootContextParameter<Entity> d;
    public static final LootContextParameter<Entity> e;
    public static final LootContextParameter<BlockPos> f;
    public static final LootContextParameter<BlockState> g;
    public static final LootContextParameter<BlockEntity> h;
    public static final LootContextParameter<ItemStack> i;
    public static final LootContextParameter<Float> j;
    
    private static <T> LootContextParameter<T> register(final String name) {
        return new LootContextParameter<T>(new Identifier(name));
    }
    
    static {
        a = LootContextParameters.<Entity>register("this_entity");
        b = LootContextParameters.<PlayerEntity>register("last_damage_player");
        c = LootContextParameters.<DamageSource>register("damage_source");
        d = LootContextParameters.<Entity>register("killer_entity");
        e = LootContextParameters.<Entity>register("direct_killer_entity");
        f = LootContextParameters.<BlockPos>register("position");
        g = LootContextParameters.<BlockState>register("block_state");
        h = LootContextParameters.<BlockEntity>register("block_entity");
        i = LootContextParameters.<ItemStack>register("tool");
        j = LootContextParameters.<Float>register("explosion_radius");
    }
}
