package net.minecraft.block.enums;

import net.minecraft.sound.SoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.tag.BlockTags;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.StringRepresentable;

public enum Instrument implements StringRepresentable
{
    a("harp", SoundEvents.gU), 
    b("basedrum", SoundEvents.gO), 
    c("snare", SoundEvents.gX), 
    d("hat", SoundEvents.gV), 
    e("bass", SoundEvents.gP), 
    f("flute", SoundEvents.gS), 
    g("bell", SoundEvents.gQ), 
    h("guitar", SoundEvents.gT), 
    i("chime", SoundEvents.gR), 
    j("xylophone", SoundEvents.gY), 
    k("iron_xylophone", SoundEvents.gZ), 
    l("cow_bell", SoundEvents.ha), 
    m("didgeridoo", SoundEvents.hb), 
    n("bit", SoundEvents.hc), 
    o("banjo", SoundEvents.hd), 
    p("pling", SoundEvents.gW);
    
    private final String name;
    private final SoundEvent sound;
    
    private Instrument(final String string1, final SoundEvent soundEvent) {
        this.name = string1;
        this.sound = soundEvent;
    }
    
    @Override
    public String asString() {
        return this.name;
    }
    
    public SoundEvent getSound() {
        return this.sound;
    }
    
    public static Instrument fromBlockState(final BlockState state) {
        final Block block2 = state.getBlock();
        if (block2 == Blocks.cE) {
            return Instrument.f;
        }
        if (block2 == Blocks.bD) {
            return Instrument.g;
        }
        if (block2.matches(BlockTags.a)) {
            return Instrument.h;
        }
        if (block2 == Blocks.gL) {
            return Instrument.i;
        }
        if (block2 == Blocks.iE) {
            return Instrument.j;
        }
        if (block2 == Blocks.bE) {
            return Instrument.k;
        }
        if (block2 == Blocks.cK) {
            return Instrument.l;
        }
        if (block2 == Blocks.cI) {
            return Instrument.m;
        }
        if (block2 == Blocks.ef) {
            return Instrument.n;
        }
        if (block2 == Blocks.gs) {
            return Instrument.o;
        }
        if (block2 == Blocks.cL) {
            return Instrument.p;
        }
        final Material material3 = state.getMaterial();
        if (material3 == Material.STONE) {
            return Instrument.b;
        }
        if (material3 == Material.SAND) {
            return Instrument.c;
        }
        if (material3 == Material.GLASS) {
            return Instrument.d;
        }
        if (material3 == Material.WOOD) {
            return Instrument.e;
        }
        return Instrument.a;
    }
}
