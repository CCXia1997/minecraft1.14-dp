package net.minecraft.village;

import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import com.google.common.collect.ImmutableSet;

public class VillagerProfession
{
    public static final VillagerProfession a;
    public static final VillagerProfession b;
    public static final VillagerProfession c;
    public static final VillagerProfession d;
    public static final VillagerProfession e;
    public static final VillagerProfession f;
    public static final VillagerProfession g;
    public static final VillagerProfession h;
    public static final VillagerProfession i;
    public static final VillagerProfession j;
    public static final VillagerProfession k;
    public static final VillagerProfession l;
    public static final VillagerProfession m;
    public static final VillagerProfession n;
    public static final VillagerProfession o;
    private final String id;
    private final PointOfInterestType workStation;
    private final ImmutableSet<Item> gatherableItems;
    private final ImmutableSet<Block> secondaryJobSites;
    
    private VillagerProfession(final String id, final PointOfInterestType workStation, final ImmutableSet<Item> gatherableItems, final ImmutableSet<Block> secondaryJobSites) {
        this.id = id;
        this.workStation = workStation;
        this.gatherableItems = gatherableItems;
        this.secondaryJobSites = secondaryJobSites;
    }
    
    public PointOfInterestType getWorkStation() {
        return this.workStation;
    }
    
    public ImmutableSet<Item> getGatherableItems() {
        return this.gatherableItems;
    }
    
    public ImmutableSet<Block> getSecondaryJobSites() {
        return this.secondaryJobSites;
    }
    
    @Override
    public String toString() {
        return this.id;
    }
    
    static VillagerProfession register(final String key, final PointOfInterestType pointOfInterestType) {
        return register(key, pointOfInterestType, ImmutableSet.<Item>of(), ImmutableSet.<Block>of());
    }
    
    static VillagerProfession register(final String string, final PointOfInterestType pointOfInterestType, final ImmutableSet<Item> immutableSet3, final ImmutableSet<Block> immutableSet4) {
        return Registry.<VillagerProfession>register(Registry.VILLAGER_PROFESSION, new Identifier(string), new VillagerProfession(string, pointOfInterestType, immutableSet3, immutableSet4));
    }
    
    static {
        a = register("none", PointOfInterestType.b); //ÎÞÒµ
        b = register("armorer", PointOfInterestType.c);
        c = register("butcher", PointOfInterestType.d);
        d = register("cartographer", PointOfInterestType.e);
        e = register("cleric", PointOfInterestType.f);
        f = register("farmer", PointOfInterestType.g, ImmutableSet.<Item>of(Items.jP, Items.jO, Items.oP), ImmutableSet.<Block>of(Blocks.bV));
        g = register("fisherman", PointOfInterestType.h);
        h = register("fletcher", PointOfInterestType.i);
        i = register("leatherworker", PointOfInterestType.j);
        j = register("librarian", PointOfInterestType.k);
        k = register("mason", PointOfInterestType.l);
        l = register("nitwit", PointOfInterestType.m); //Éµ×Ó
        m = register("shepherd", PointOfInterestType.n);
        n = register("toolsmith", PointOfInterestType.o);
        o = register("weaponsmith", PointOfInterestType.p);
    }
}
