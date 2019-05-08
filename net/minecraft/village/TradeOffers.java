package net.minecraft.village;

import net.minecraft.world.World;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import java.util.Locale;
import net.minecraft.item.map.MapState;
import net.minecraft.item.FilledMapItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import com.google.common.collect.Lists;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.potion.PotionUtil;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.potion.Potion;
import java.util.List;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.block.Block;
import javax.annotation.Nullable;
import net.minecraft.util.registry.Registry;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.Item;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Items;
import java.util.HashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Map;

public class TradeOffers
{
    public static final Map<VillagerProfession, Int2ObjectMap<Factory[]>> PROFESSION_TO_LEVELED_TRADE;
    public static final Int2ObjectMap<Factory[]> WANDERING_TRADER_TRADES;
    
    private static Int2ObjectMap<Factory[]> copyToFastUtilMap(final ImmutableMap<Integer, Factory[]> immutableMap) {
        return (Int2ObjectMap<Factory[]>)new Int2ObjectOpenHashMap((Map)immutableMap);
    }
    
    static {
        final VillagerProfession b;
        final Factory[] v1;
        final SellItemFactory sellItemFactory;
        final int n;
        final SellItemFactory sellItemFactory2;
        final int n2;
        final SellItemFactory sellItemFactory3;
        final int n3;
        final SellItemFactory sellItemFactory4;
        final int n4;
        final Factory[] v2;
        final SellItemFactory sellItemFactory5;
        final int n5;
        final SellItemFactory sellItemFactory6;
        final int n6;
        final SellItemFactory sellItemFactory7;
        final int n7;
        final Factory[] v3;
        final SellItemFactory sellItemFactory8;
        final int n8;
        final SellItemFactory sellItemFactory9;
        final int n9;
        final SellItemFactory sellItemFactory10;
        final int n10;
        final Integer k1;
        final Integer k2;
        final Integer k3;
        final VillagerProfession o;
        final Factory[] v4;
        final SellItemFactory sellItemFactory11;
        final int n11;
        final Factory[] v5;
        final SellItemFactory sellItemFactory12;
        final int n12;
        final Integer k4;
        final Integer k5;
        final VillagerProfession n13;
        final Factory[] v6;
        final SellItemFactory sellItemFactory13;
        final int n14;
        final SellItemFactory sellItemFactory14;
        final int n15;
        final SellItemFactory sellItemFactory15;
        final int n16;
        final SellItemFactory sellItemFactory16;
        final int n17;
        final Factory[] v7;
        final SellItemFactory sellItemFactory17;
        final int n18;
        final Integer k6;
        final Integer k7;
        final VillagerProfession i;
        final Factory[] v8;
        final Factory[] v9;
        final Factory[] v10;
        final Factory[] v11;
        final Factory[] v12;
        final SellItemFactory sellItemFactory18;
        final int n19;
        final Integer k8;
        final Integer k9;
        final Integer k10;
        final Integer k11;
        final Integer k12;
        PROFESSION_TO_LEVELED_TRADE = SystemUtil.<Map<VillagerProfession, Int2ObjectMap<Factory[]>>>consume(Maps.newHashMap(), hashMap -> {
            hashMap.put(VillagerProfession.f, copyToFastUtilMap(ImmutableMap.<Integer, Factory[]>of(1, new Factory[] { new BuyForOneEmeraldFactory(Items.jP, 20, 8, 2), new BuyForOneEmeraldFactory(Items.nJ, 26, 8, 2), new BuyForOneEmeraldFactory(Items.nI, 22, 8, 2), new BuyForOneEmeraldFactory(Items.oO, 15, 8, 2), new SellItemFactory(Items.jQ, 1, 6, 8, 1) }, 2, new Factory[] { new BuyForOneEmeraldFactory(Blocks.cI, 6, 6, 10), new SellItemFactory(Items.nW, 1, 4, 5), new SellItemFactory(Items.je, 1, 4, 8, 5) }, 3, new Factory[] { new SellItemFactory(Items.lU, 3, 18, 10), new BuyForOneEmeraldFactory(Blocks.dC, 4, 6, 20) }, 4, new Factory[] { new SellItemFactory(Blocks.cP, 1, 1, 6, 15), new SellSuspiciousStewFactory(StatusEffects.a, 160, 15), new SellSuspiciousStewFactory(StatusEffects.h, 160, 15), new SellSuspiciousStewFactory(StatusEffects.r, 140, 15), new SellSuspiciousStewFactory(StatusEffects.o, 120, 15), new SellSuspiciousStewFactory(StatusEffects.s, 280, 15), new SellSuspiciousStewFactory(StatusEffects.w, 7, 15) }, 5, new Factory[] { new SellItemFactory(Items.nN, 3, 3, 30), new SellItemFactory(Items.mu, 4, 3, 30) })));
            hashMap.put(VillagerProfession.g, copyToFastUtilMap(ImmutableMap.<Integer, Factory[]>of(1, new Factory[] { new BuyForOneEmeraldFactory(Items.jG, 20, 8, 2), new BuyForOneEmeraldFactory(Items.jh, 10, 8, 2), new ProcessItemFactory(Items.lb, 6, Items.lf, 6, 8, 1), new SellItemFactory(Items.kJ, 3, 1, 8, 1) }, 2, new Factory[] { new BuyForOneEmeraldFactory(Items.lb, 15, 8, 10), new ProcessItemFactory(Items.lc, 6, Items.lg, 6, 8, 5), new SellItemFactory(Items.CAMPFIRE, 2, 1, 5) }, 3, new Factory[] { new BuyForOneEmeraldFactory(Items.lc, 13, 8, 20), new SellEnchantedToolFactory(Items.kY, 3, 2, 10, 0.2f) }, 4, new Factory[] { new BuyForOneEmeraldFactory(Items.ld, 6, 6, 30) }, 5, new Factory[] { new BuyForOneEmeraldFactory(Items.le, 4, 6, 30), new TypeAwareBuyForOneEmeraldFactory(1, 6, 30, ImmutableMap.<VillagerType, Item>builder().put(VillagerType.PLAINS, Items.kE).put(VillagerType.TAIGA, Items.oY).put(VillagerType.SNOW, Items.oY).put(VillagerType.DESERT, Items.pa).put(VillagerType.JUNGLE, Items.pa).put(VillagerType.SAVANNA, Items.pb).put(VillagerType.SWAMP, Items.pc).build()) })));
            hashMap.put(VillagerProfession.m, copyToFastUtilMap(ImmutableMap.<Integer, Factory[]>of(1, new Factory[] { new BuyForOneEmeraldFactory(Blocks.aX, 18, 8, 2), new BuyForOneEmeraldFactory(Blocks.bj, 18, 8, 2), new BuyForOneEmeraldFactory(Blocks.bm, 18, 8, 2), new BuyForOneEmeraldFactory(Blocks.be, 18, 8, 2), new SellItemFactory(Items.lW, 2, 1, 1) }, 2, new Factory[] { new BuyForOneEmeraldFactory(Items.lA, 12, 8, 10), new BuyForOneEmeraldFactory(Items.lp, 12, 8, 10), new BuyForOneEmeraldFactory(Items.lz, 12, 8, 10), new BuyForOneEmeraldFactory(Items.lt, 12, 8, 10), new BuyForOneEmeraldFactory(Items.lr, 12, 8, 10), new SellItemFactory(Blocks.aX, 1, 1, 8, 5), new SellItemFactory(Blocks.aY, 1, 1, 8, 5), new SellItemFactory(Blocks.aZ, 1, 1, 8, 5), new SellItemFactory(Blocks.ba, 1, 1, 8, 5), new SellItemFactory(Blocks.bb, 1, 1, 8, 5), new SellItemFactory(Blocks.bc, 1, 1, 8, 5), new SellItemFactory(Blocks.bd, 1, 1, 8, 5), new SellItemFactory(Blocks.be, 1, 1, 8, 5), new SellItemFactory(Blocks.bf, 1, 1, 8, 5), new SellItemFactory(Blocks.bg, 1, 1, 8, 5), new SellItemFactory(Blocks.bh, 1, 1, 8, 5), new SellItemFactory(Blocks.bi, 1, 1, 8, 5), new SellItemFactory(Blocks.bj, 1, 1, 8, 5), new SellItemFactory(Blocks.bk, 1, 1, 8, 5), new SellItemFactory(Blocks.bl, 1, 1, 8, 5), new SellItemFactory(Blocks.bm, 1, 1, 8, 5), new SellItemFactory(Blocks.gt, 1, 4, 8, 5), new SellItemFactory(Blocks.gu, 1, 4, 8, 5), new SellItemFactory(Blocks.gv, 1, 4, 8, 5), new SellItemFactory(Blocks.gw, 1, 4, 8, 5), new SellItemFactory(Blocks.gx, 1, 4, 8, 5), new SellItemFactory(Blocks.gy, 1, 4, 8, 5), new SellItemFactory(Blocks.gz, 1, 4, 8, 5), new SellItemFactory(Blocks.gA, 1, 4, 8, 5), new SellItemFactory(Blocks.gB, 1, 4, 8, 5), new SellItemFactory(Blocks.gC, 1, 4, 8, 5), new SellItemFactory(Blocks.gD, 1, 4, 8, 5), new SellItemFactory(Blocks.gE, 1, 4, 8, 5), new SellItemFactory(Blocks.gF, 1, 4, 8, 5), new SellItemFactory(Blocks.gG, 1, 4, 8, 5), new SellItemFactory(Blocks.gH, 1, 4, 8, 5), new SellItemFactory(Blocks.gI, 1, 4, 8, 5) }, 3, new Factory[] { new BuyForOneEmeraldFactory(Items.ls, 12, 8, 20), new BuyForOneEmeraldFactory(Items.lo, 12, 8, 20), new BuyForOneEmeraldFactory(Items.lv, 12, 8, 20), new BuyForOneEmeraldFactory(Items.li, 12, 8, 20), new BuyForOneEmeraldFactory(Items.lq, 12, 8, 20), new SellItemFactory(Blocks.aw, 3, 1, 6, 10), new SellItemFactory(Blocks.aA, 3, 1, 6, 10), new SellItemFactory(Blocks.aK, 3, 1, 6, 10), new SellItemFactory(Blocks.aL, 3, 1, 6, 10), new SellItemFactory(Blocks.aH, 3, 1, 6, 10), new SellItemFactory(Blocks.aI, 3, 1, 6, 10), new SellItemFactory(Blocks.aF, 3, 1, 6, 10), new SellItemFactory(Blocks.aD, 3, 1, 6, 10), new SellItemFactory(Blocks.aJ, 3, 1, 6, 10), new SellItemFactory(Blocks.az, 3, 1, 6, 10), new SellItemFactory(Blocks.aE, 3, 1, 6, 10), new SellItemFactory(Blocks.aB, 3, 1, 6, 10), new SellItemFactory(Blocks.ay, 3, 1, 6, 10), new SellItemFactory(Blocks.ax, 3, 1, 6, 10), new SellItemFactory(Blocks.aC, 3, 1, 6, 10), new SellItemFactory(Blocks.aG, 3, 1, 6, 10) }, 4, new Factory[] { new BuyForOneEmeraldFactory(Items.ly, 12, 8, 30), new BuyForOneEmeraldFactory(Items.lm, 12, 8, 30), new BuyForOneEmeraldFactory(Items.lx, 12, 8, 30), new BuyForOneEmeraldFactory(Items.lj, 12, 8, 30), new BuyForOneEmeraldFactory(Items.lu, 12, 8, 30), new BuyForOneEmeraldFactory(Items.ln, 12, 8, 30), new SellItemFactory(Items.ov, 3, 1, 6, 15), new SellItemFactory(Items.oG, 3, 1, 6, 15), new SellItemFactory(Items.oy, 3, 1, 6, 15), new SellItemFactory(Items.oJ, 3, 1, 6, 15), new SellItemFactory(Items.oB, 3, 1, 6, 15), new SellItemFactory(Items.oI, 3, 1, 6, 15), new SellItemFactory(Items.oA, 3, 1, 6, 15), new SellItemFactory(Items.oC, 3, 1, 6, 15), new SellItemFactory(Items.oK, 3, 1, 6, 15), new SellItemFactory(Items.oF, 3, 1, 6, 15), new SellItemFactory(Items.ox, 3, 1, 6, 15), new SellItemFactory(Items.oE, 3, 1, 6, 15), new SellItemFactory(Items.oH, 3, 1, 6, 15), new SellItemFactory(Items.oz, 3, 1, 6, 15), new SellItemFactory(Items.ow, 3, 1, 6, 15), new SellItemFactory(Items.oD, 3, 1, 6, 15) }, 5, new Factory[] { new SellItemFactory(Items.ko, 2, 3, 30) })));
            hashMap.put(VillagerProfession.h, copyToFastUtilMap(ImmutableMap.<Integer, Factory[]>of(1, new Factory[] { new BuyForOneEmeraldFactory(Items.jz, 32, 8, 2), new SellItemFactory(Items.jg, 1, 16, 1), new ProcessItemFactory(Blocks.E, 10, Items.kl, 10, 6, 1) }, 2, new Factory[] { new BuyForOneEmeraldFactory(Items.kl, 26, 6, 10), new SellItemFactory(Items.jf, 2, 1, 5) }, 3, new Factory[] { new BuyForOneEmeraldFactory(Items.jG, 14, 8, 20), new SellItemFactory(Items.py, 3, 1, 10) }, 4, new Factory[] { new BuyForOneEmeraldFactory(Items.jH, 24, 8, 30), new SellEnchantedToolFactory(Items.jf, 2, 2, 15) }, 5, new Factory[] { new BuyForOneEmeraldFactory(Items.TRIPWIRE_HOOK, 8, 6, 30), new SellEnchantedToolFactory(Items.py, 3, 2, 15), new SellPotionHoldingItemFactory(Items.jg, 5, Items.oU, 5, 2, 6, 30) })));
            hashMap.put(VillagerProfession.j, copyToFastUtilMap(ImmutableMap.<Integer, Factory[]>builder().put(1, new Factory[] { new BuyForOneEmeraldFactory(Items.kR, 24, 8, 2), new EnchantBookFactory(1), new SellItemFactory(Blocks.bH, 6, 3, 6, 1) }).put(2, new Factory[] { new BuyForOneEmeraldFactory(Items.kS, 4, 6, 10), new EnchantBookFactory(5), new SellItemFactory(Items.LANTERN, 1, 1, 5) }).put(3, new Factory[] { new BuyForOneEmeraldFactory(Items.lh, 5, 6, 20), new EnchantBookFactory(10), new SellItemFactory(Items.GLASS, 1, 4, 10) }).put(4, new Factory[] { new BuyForOneEmeraldFactory(Items.nD, 2, 6, 30), new EnchantBookFactory(15), new SellItemFactory(Items.kZ, 5, 1, 15), new SellItemFactory(Items.kX, 4, 1, 15) }).put(5, new Factory[] { new SellItemFactory(Items.or, 20, 1, 30) }).build()));
            hashMap.put(VillagerProfession.d, copyToFastUtilMap(ImmutableMap.<Integer, Factory[]>of(1, new Factory[] { new BuyForOneEmeraldFactory(Items.kR, 24, 8, 2), new SellItemFactory(Items.nM, 7, 1, 1) }, 2, new Factory[] { new BuyForOneEmeraldFactory(Items.GLASS_PANE, 10, 8, 10), new SellMapFactory(13, "Monument", MapIcon.Type.j, 6, 5) }, 3, new Factory[] { new BuyForOneEmeraldFactory(Items.kX, 1, 6, 20), new SellMapFactory(14, "Mansion", MapIcon.Type.i, 6, 10) }, 4, new Factory[] { new SellItemFactory(Items.nG, 7, 1, 15), new SellItemFactory(Items.ov, 3, 1, 15), new SellItemFactory(Items.oG, 3, 1, 15), new SellItemFactory(Items.oy, 3, 1, 15), new SellItemFactory(Items.oJ, 3, 1, 15), new SellItemFactory(Items.oB, 3, 1, 15), new SellItemFactory(Items.oI, 3, 1, 15), new SellItemFactory(Items.oA, 3, 1, 15), new SellItemFactory(Items.oC, 3, 1, 15), new SellItemFactory(Items.oK, 3, 1, 15), new SellItemFactory(Items.oF, 3, 1, 15), new SellItemFactory(Items.ox, 3, 1, 15), new SellItemFactory(Items.oE, 3, 1, 15), new SellItemFactory(Items.oH, 3, 1, 15), new SellItemFactory(Items.oz, 3, 1, 15), new SellItemFactory(Items.ow, 3, 1, 15), new SellItemFactory(Items.oD, 3, 1, 15) }, 5, new Factory[] { new SellItemFactory(Items.pF, 8, 1, 30) })));
            hashMap.put(VillagerProfession.e, copyToFastUtilMap(ImmutableMap.<Integer, Factory[]>of(1, new Factory[] { new BuyForOneEmeraldFactory(Items.mf, 32, 8, 2), new SellItemFactory(Items.kC, 1, 2, 1) }, 2, new Factory[] { new BuyForOneEmeraldFactory(Items.jl, 3, 6, 10), new SellItemFactory(Items.ll, 1, 1, 5) }, 3, new Factory[] { new BuyForOneEmeraldFactory(Items.oj, 2, 6, 20), new SellItemFactory(Blocks.cL, 4, 1, 6, 10) }, 4, new Factory[] { new BuyForOneEmeraldFactory(Items.iZ, 4, 6, 30), new BuyForOneEmeraldFactory(Items.mm, 9, 6, 30), new SellItemFactory(Items.mg, 5, 1, 15) }, 5, new Factory[] { new BuyForOneEmeraldFactory(Items.mk, 22, 6, 30), new SellItemFactory(Items.nB, 3, 1, 30) })));
            b = VillagerProfession.b;
            1;
            v1 = new Factory[5];
            v1[0] = new BuyForOneEmeraldFactory(Items.jh, 15, 8, 2);
            new SellItemFactory(new ItemStack(Items.kb), 7, 1, 6, 1, 0.2f);
            v1[n] = sellItemFactory;
            new SellItemFactory(new ItemStack(Items.kc), 4, 1, 6, 1, 0.2f);
            v1[n2] = sellItemFactory2;
            new SellItemFactory(new ItemStack(Items.jZ), 5, 1, 6, 1, 0.2f);
            v1[n3] = sellItemFactory3;
            new SellItemFactory(new ItemStack(Items.ka), 9, 1, 6, 1, 0.2f);
            v1[n4] = sellItemFactory4;
            2;
            v2 = new Factory[4];
            v2[0] = new BuyForOneEmeraldFactory(Items.jk, 4, 6, 10);
            new SellItemFactory(new ItemStack(Items.BELL), 36, 1, 6, 5, 0.2f);
            v2[n5] = sellItemFactory5;
            new SellItemFactory(new ItemStack(Items.jY), 1, 1, 6, 5, 0.2f);
            v2[n6] = sellItemFactory6;
            new SellItemFactory(new ItemStack(Items.jX), 3, 1, 6, 5, 0.2f);
            v2[n7] = sellItemFactory7;
            3;
            v3 = new Factory[] { new BuyForOneEmeraldFactory(Items.kz, 1, 6, 20), new BuyForOneEmeraldFactory(Items.jj, 1, 6, 20), null, null, null };
            new SellItemFactory(new ItemStack(Items.jV), 1, 1, 6, 10, 0.2f);
            v3[n8] = sellItemFactory8;
            new SellItemFactory(new ItemStack(Items.jW), 4, 1, 6, 10, 0.2f);
            v3[n9] = sellItemFactory9;
            new SellItemFactory(new ItemStack(Items.oW), 5, 1, 6, 10, 0.2f);
            v3[n10] = sellItemFactory10;
            hashMap.put(b, copyToFastUtilMap(ImmutableMap.<Integer, Factory[]>of(k1, v1, k2, v2, k3, v3, 4, new Factory[] { new SellEnchantedToolFactory(Items.kf, 14, 2, 15, 0.2f), new SellEnchantedToolFactory(Items.kg, 8, 2, 15, 0.2f) }, 5, new Factory[] { new SellEnchantedToolFactory(Items.kd, 8, 2, 30, 0.2f), new SellEnchantedToolFactory(Items.ke, 16, 2, 30, 0.2f) })));
            o = VillagerProfession.o;
            1;
            v4 = new Factory[] { new BuyForOneEmeraldFactory(Items.jh, 15, 8, 2), null, null };
            new SellItemFactory(new ItemStack(Items.jc), 3, 1, 6, 1, 0.2f);
            v4[n11] = sellItemFactory11;
            v4[2] = new SellEnchantedToolFactory(Items.jm, 2, 2, 1);
            2;
            v5 = new Factory[] { new BuyForOneEmeraldFactory(Items.jk, 4, 6, 10), null };
            new SellItemFactory(new ItemStack(Items.BELL), 36, 1, 6, 5, 0.2f);
            v5[n12] = sellItemFactory12;
            hashMap.put(o, copyToFastUtilMap(ImmutableMap.<Integer, Factory[]>of(k4, v4, k5, v5, 3, new Factory[] { new BuyForOneEmeraldFactory(Items.kl, 24, 6, 20) }, 4, new Factory[] { new BuyForOneEmeraldFactory(Items.jj, 1, 6, 30), new SellEnchantedToolFactory(Items.jy, 12, 2, 15, 0.2f) }, 5, new Factory[] { new SellEnchantedToolFactory(Items.jv, 8, 2, 30, 0.2f) })));
            n13 = VillagerProfession.n;
            1;
            v6 = new Factory[5];
            v6[0] = new BuyForOneEmeraldFactory(Items.jh, 15, 8, 2);
            new SellItemFactory(new ItemStack(Items.ju), 1, 1, 6, 1, 0.2f);
            v6[n14] = sellItemFactory13;
            new SellItemFactory(new ItemStack(Items.js), 1, 1, 6, 1, 0.2f);
            v6[n15] = sellItemFactory14;
            new SellItemFactory(new ItemStack(Items.jt), 1, 1, 6, 1, 0.2f);
            v6[n16] = sellItemFactory15;
            new SellItemFactory(new ItemStack(Items.jK), 1, 1, 6, 1, 0.2f);
            v6[n17] = sellItemFactory16;
            2;
            v7 = new Factory[] { new BuyForOneEmeraldFactory(Items.jk, 4, 6, 10), null };
            new SellItemFactory(new ItemStack(Items.BELL), 36, 1, 6, 5, 0.2f);
            v7[n18] = sellItemFactory17;
            hashMap.put(n13, copyToFastUtilMap(ImmutableMap.<Integer, Factory[]>of(k6, v6, k7, v7, 3, new Factory[] { new BuyForOneEmeraldFactory(Items.kl, 30, 6, 20), new SellEnchantedToolFactory(Items.jc, 1, 2, 10, 0.2f), new SellEnchantedToolFactory(Items.ja, 2, 2, 10, 0.2f), new SellEnchantedToolFactory(Items.jb, 3, 2, 10, 0.2f), new SellEnchantedToolFactory(Items.jM, 9, 2, 10, 0.2f) }, 4, new Factory[] { new BuyForOneEmeraldFactory(Items.jj, 1, 6, 30), new SellEnchantedToolFactory(Items.jy, 12, 2, 15, 0.2f), new SellEnchantedToolFactory(Items.jw, 5, 2, 15, 0.2f) }, 5, new Factory[] { new SellEnchantedToolFactory(Items.jx, 13, 2, 30, 0.2f) })));
            hashMap.put(VillagerProfession.c, copyToFastUtilMap(ImmutableMap.<Integer, Factory[]>of(1, new Factory[] { new BuyForOneEmeraldFactory(Items.md, 14, 8, 2), new BuyForOneEmeraldFactory(Items.km, 7, 8, 2), new BuyForOneEmeraldFactory(Items.og, 4, 8, 2), new SellItemFactory(Items.oi, 1, 1, 1) }, 2, new Factory[] { new BuyForOneEmeraldFactory(Items.jh, 15, 8, 2), new SellItemFactory(Items.kn, 1, 5, 8, 5), new SellItemFactory(Items.me, 1, 8, 8, 5) }, 3, new Factory[] { new BuyForOneEmeraldFactory(Items.ot, 7, 8, 20), new BuyForOneEmeraldFactory(Items.mb, 10, 8, 20) }, 4, new Factory[] { new BuyForOneEmeraldFactory(Items.DRIED_KELP_BLOCK, 10, 6, 30) }, 5, new Factory[] { new BuyForOneEmeraldFactory(Items.pR, 10, 6, 30) })));
            i = VillagerProfession.i;
            1;
            v8 = new Factory[] { new BuyForOneEmeraldFactory(Items.kF, 6, 8, 2), new SellDyedArmorFactory(Items.jT, 3), new SellDyedArmorFactory(Items.jS, 7) };
            2;
            v9 = new Factory[] { new BuyForOneEmeraldFactory(Items.kl, 26, 6, 10), new SellDyedArmorFactory(Items.jR, 5, 6, 5), new SellDyedArmorFactory(Items.jU, 4, 6, 5) };
            3;
            v10 = new Factory[] { new BuyForOneEmeraldFactory(Items.ok, 9, 6, 20), new SellDyedArmorFactory(Items.jS, 7) };
            4;
            v11 = new Factory[] { new BuyForOneEmeraldFactory(Items.iZ, 4, 6, 30), new SellDyedArmorFactory(Items.op, 6, 6, 15) };
            5;
            v12 = new Factory[2];
            new SellItemFactory(new ItemStack(Items.kB), 6, 1, 6, 30, 0.2f);
            v12[n19] = sellItemFactory18;
            v12[1] = new SellDyedArmorFactory(Items.jR, 5, 6, 30);
            hashMap.put(i, copyToFastUtilMap(ImmutableMap.<Integer, Factory[]>of(k8, v8, k9, v9, k10, v10, k11, v11, k12, v12)));
            hashMap.put(VillagerProfession.k, copyToFastUtilMap(ImmutableMap.<Integer, Factory[]>of(1, new Factory[] { new BuyForOneEmeraldFactory(Items.kM, 10, 8, 2), new SellItemFactory(Items.kL, 1, 10, 8, 1) }, 2, new Factory[] { new BuyForOneEmeraldFactory(Blocks.b, 20, 8, 10), new SellItemFactory(Blocks.dq, 1, 4, 8, 5) }, 3, new Factory[] { new BuyForOneEmeraldFactory(Blocks.c, 16, 8, 20), new BuyForOneEmeraldFactory(Blocks.g, 16, 8, 20), new BuyForOneEmeraldFactory(Blocks.e, 16, 8, 20), new SellItemFactory(Blocks.h, 1, 4, 8, 10), new SellItemFactory(Blocks.f, 1, 4, 8, 10), new SellItemFactory(Blocks.d, 1, 4, 8, 10) }, 4, new Factory[] { new BuyForOneEmeraldFactory(Items.ob, 12, 6, 30), new SellItemFactory(Blocks.fy, 1, 1, 6, 15), new SellItemFactory(Blocks.fx, 1, 1, 6, 15), new SellItemFactory(Blocks.fI, 1, 1, 6, 15), new SellItemFactory(Blocks.fA, 1, 1, 6, 15), new SellItemFactory(Blocks.fE, 1, 1, 6, 15), new SellItemFactory(Blocks.fF, 1, 1, 6, 15), new SellItemFactory(Blocks.fM, 1, 1, 6, 15), new SellItemFactory(Blocks.fL, 1, 1, 6, 15), new SellItemFactory(Blocks.fD, 1, 1, 6, 15), new SellItemFactory(Blocks.fz, 1, 1, 6, 15), new SellItemFactory(Blocks.fC, 1, 1, 6, 15), new SellItemFactory(Blocks.fK, 1, 1, 6, 15), new SellItemFactory(Blocks.fG, 1, 1, 6, 15), new SellItemFactory(Blocks.fH, 1, 1, 6, 15), new SellItemFactory(Blocks.fB, 1, 1, 6, 15), new SellItemFactory(Blocks.fJ, 1, 1, 6, 15), new SellItemFactory(Blocks.iZ, 1, 1, 6, 15), new SellItemFactory(Blocks.iY, 1, 1, 6, 15), new SellItemFactory(Blocks.jj, 1, 1, 6, 15), new SellItemFactory(Blocks.jb, 1, 1, 6, 15), new SellItemFactory(Blocks.jf, 1, 1, 6, 15), new SellItemFactory(Blocks.jg, 1, 1, 6, 15), new SellItemFactory(Blocks.jn, 1, 1, 6, 15), new SellItemFactory(Blocks.jm, 1, 1, 6, 15), new SellItemFactory(Blocks.je, 1, 1, 6, 15), new SellItemFactory(Blocks.ja, 1, 1, 6, 15), new SellItemFactory(Blocks.jd, 1, 1, 6, 15), new SellItemFactory(Blocks.jl, 1, 1, 6, 15), new SellItemFactory(Blocks.jh, 1, 1, 6, 15), new SellItemFactory(Blocks.ji, 1, 1, 6, 15), new SellItemFactory(Blocks.jc, 1, 1, 6, 15), new SellItemFactory(Blocks.jk, 1, 1, 6, 15) }, 5, new Factory[] { new SellItemFactory(Blocks.ft, 1, 1, 6, 30), new SellItemFactory(Blocks.fr, 1, 1, 6, 30) })));
            return;
        });
        WANDERING_TRADER_TRADES = copyToFastUtilMap(ImmutableMap.<Integer, Factory[]>of(1, new Factory[] { new SellItemFactory(Items.SEA_PICKLE, 2, 1, 5, 1), new SellItemFactory(Items.kT, 4, 1, 5, 1), new SellItemFactory(Items.GLOWSTONE, 2, 1, 5, 1), new SellItemFactory(Items.pw, 5, 1, 5, 1), new SellItemFactory(Items.FERN, 1, 1, 12, 1), new SellItemFactory(Items.SUGAR_CANE, 1, 1, 8, 1), new SellItemFactory(Items.PUMPKIN, 1, 1, 4, 1), new SellItemFactory(Items.KELP, 3, 1, 12, 1), new SellItemFactory(Items.CACTUS, 3, 1, 8, 1), new SellItemFactory(Items.DANDELION, 1, 1, 12, 1), new SellItemFactory(Items.POPPY, 1, 1, 12, 1), new SellItemFactory(Items.BLUE_ORCHID, 1, 1, 8, 1), new SellItemFactory(Items.ALLIUM, 1, 1, 12, 1), new SellItemFactory(Items.AZURE_BLUET, 1, 1, 12, 1), new SellItemFactory(Items.RED_TULIP, 1, 1, 12, 1), new SellItemFactory(Items.ORANGE_TULIP, 1, 1, 12, 1), new SellItemFactory(Items.WHITE_TULIP, 1, 1, 12, 1), new SellItemFactory(Items.PINK_TULIP, 1, 1, 12, 1), new SellItemFactory(Items.OXEYE_DAISY, 1, 1, 12, 1), new SellItemFactory(Items.CORNFLOWER, 1, 1, 12, 1), new SellItemFactory(Items.LILY_OF_THE_VALLEY, 1, 1, 7, 1), new SellItemFactory(Items.jO, 1, 1, 12, 1), new SellItemFactory(Items.oP, 1, 1, 12, 1), new SellItemFactory(Items.lZ, 1, 1, 12, 1), new SellItemFactory(Items.ma, 1, 1, 12, 1), new SellItemFactory(Items.ACACIA_SAPLING, 5, 1, 8, 1), new SellItemFactory(Items.BIRCH_SAPLING, 5, 1, 8, 1), new SellItemFactory(Items.DARK_OAK_SAPLING, 5, 1, 8, 1), new SellItemFactory(Items.JUNGLE_SAPLING, 5, 1, 8, 1), new SellItemFactory(Items.OAK_SAPLING, 5, 1, 8, 1), new SellItemFactory(Items.SPRUCE_SAPLING, 5, 1, 8, 1), new SellItemFactory(Items.li, 1, 3, 12, 1), new SellItemFactory(Items.lA, 1, 3, 12, 1), new SellItemFactory(Items.lx, 1, 3, 12, 1), new SellItemFactory(Items.lq, 1, 3, 12, 1), new SellItemFactory(Items.lz, 1, 3, 12, 1), new SellItemFactory(Items.lj, 1, 3, 12, 1), new SellItemFactory(Items.lo, 1, 3, 12, 1), new SellItemFactory(Items.lu, 1, 3, 12, 1), new SellItemFactory(Items.ls, 1, 3, 12, 1), new SellItemFactory(Items.lp, 1, 3, 12, 1), new SellItemFactory(Items.lm, 1, 3, 12, 1), new SellItemFactory(Items.lt, 1, 3, 12, 1), new SellItemFactory(Items.lr, 1, 3, 12, 1), new SellItemFactory(Items.lv, 1, 3, 12, 1), new SellItemFactory(Items.ly, 1, 3, 12, 1), new SellItemFactory(Items.ln, 1, 3, 12, 1), new SellItemFactory(Items.BRAIN_CORAL_BLOCK, 3, 1, 8, 1), new SellItemFactory(Items.BUBBLE_CORAL_BLOCK, 3, 1, 8, 1), new SellItemFactory(Items.FIRE_CORAL_BLOCK, 3, 1, 8, 1), new SellItemFactory(Items.HORN_CORAL_BLOCK, 3, 1, 8, 1), new SellItemFactory(Items.TUBE_CORAL_BLOCK, 3, 1, 8, 1), new SellItemFactory(Items.VINE, 1, 1, 12, 1), new SellItemFactory(Items.BROWN_MUSHROOM, 1, 1, 12, 1), new SellItemFactory(Items.RED_MUSHROOM, 1, 1, 12, 1), new SellItemFactory(Items.LILY_PAD, 1, 2, 5, 1), new SellItemFactory(Items.SAND, 1, 8, 8, 1), new SellItemFactory(Items.RED_SAND, 1, 4, 6, 1) }, 2, new Factory[] { new SellItemFactory(Items.kK, 5, 1, 4, 1), new SellItemFactory(Items.kH, 5, 1, 4, 1), new SellItemFactory(Items.PACKED_ICE, 3, 1, 6, 1), new SellItemFactory(Items.BLUE_ICE, 6, 1, 6, 1), new SellItemFactory(Items.jI, 1, 1, 8, 1), new SellItemFactory(Items.PODZOL, 3, 3, 6, 1) }));
    }
    
    static class BuyForOneEmeraldFactory implements Factory
    {
        private final Item buy;
        private final int price;
        private final int maxUses;
        private final int experience;
        private final float multiplier;
        
        public BuyForOneEmeraldFactory(final ItemProvider itemProvider, final int price, final int maxUses, final int experience) {
            this.buy = itemProvider.getItem();
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = 0.05f;
        }
        
        @Override
        public TradeOffer create(final Entity entity, final Random random) {
            final ItemStack itemStack3 = new ItemStack(this.buy, this.price);
            return new TradeOffer(itemStack3, new ItemStack(Items.nF), this.maxUses, this.experience, this.multiplier);
        }
    }
    
    static class TypeAwareBuyForOneEmeraldFactory implements Factory
    {
        private final Map<VillagerType, Item> map;
        private final int count;
        private final int maxUses;
        private final int experience;
        
        public TypeAwareBuyForOneEmeraldFactory(final int integer1, final int integer2, final int experience, final Map<VillagerType, Item> map) {
            final IllegalStateException ex;
            Registry.VILLAGER_TYPE.stream().filter(villagerType -> !map.containsKey(villagerType)).findAny().ifPresent(villagerType -> {
                new IllegalStateException("Missing trade for villager type: " + Registry.VILLAGER_TYPE.getId(villagerType));
                throw ex;
            });
            this.map = map;
            this.count = integer1;
            this.maxUses = integer2;
            this.experience = experience;
        }
        
        @Nullable
        @Override
        public TradeOffer create(final Entity entity, final Random random) {
            if (entity instanceof VillagerDataContainer) {
                final ItemStack itemStack3 = new ItemStack(this.map.get(((VillagerDataContainer)entity).getVillagerData().getType()), this.count);
                return new TradeOffer(itemStack3, new ItemStack(Items.nF), this.maxUses, this.experience, 0.05f);
            }
            return null;
        }
    }
    
    static class SellItemFactory implements Factory
    {
        private final ItemStack sell;
        private final int price;
        private final int count;
        private final int maxUses;
        private final int experience;
        private final float multiplier;
        
        public SellItemFactory(final Block block, final int integer2, final int integer3, final int integer4, final int integer5) {
            this(new ItemStack(block), integer2, integer3, integer4, integer5);
        }
        
        public SellItemFactory(final Item item, final int integer2, final int integer3, final int integer4) {
            this(new ItemStack(item), integer2, integer3, 6, integer4);
        }
        
        public SellItemFactory(final Item item, final int integer2, final int integer3, final int integer4, final int integer5) {
            this(new ItemStack(item), integer2, integer3, integer4, integer5);
        }
        
        public SellItemFactory(final ItemStack itemStack, final int integer2, final int integer3, final int integer4, final int integer5) {
            this(itemStack, integer2, integer3, integer4, integer5, 0.05f);
        }
        
        public SellItemFactory(final ItemStack itemStack, final int price, final int count, final int maxUses, final int experience, final float multiplier) {
            this.sell = itemStack;
            this.price = price;
            this.count = count;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }
        
        @Override
        public TradeOffer create(final Entity entity, final Random random) {
            return new TradeOffer(new ItemStack(Items.nF, this.price), new ItemStack(this.sell.getItem(), this.count), this.maxUses, this.experience, this.multiplier);
        }
    }
    
    static class SellSuspiciousStewFactory implements Factory
    {
        final StatusEffect effect;
        final int duration;
        final int experience;
        private final float multiplier;
        
        public SellSuspiciousStewFactory(final StatusEffect statusEffect, final int duration, final int experience) {
            this.effect = statusEffect;
            this.duration = duration;
            this.experience = experience;
            this.multiplier = 0.05f;
        }
        
        @Nullable
        @Override
        public TradeOffer create(final Entity entity, final Random random) {
            final ItemStack itemStack3 = new ItemStack(Items.pz, 1);
            SuspiciousStewItem.addEffectToStew(itemStack3, this.effect, this.duration);
            return new TradeOffer(new ItemStack(Items.nF, 1), itemStack3, 6, this.experience, this.multiplier);
        }
    }
    
    static class SellEnchantedToolFactory implements Factory
    {
        private final ItemStack tool;
        private final int basePrice;
        private final int maxUses;
        private final int experience;
        private final float multiplier;
        
        public SellEnchantedToolFactory(final Item item, final int basePrice, final int maxUses, final int experience) {
            this(item, basePrice, maxUses, experience, 0.05f);
        }
        
        public SellEnchantedToolFactory(final Item item, final int basePrice, final int maxUses, final int experience, final float multiplier) {
            this.tool = new ItemStack(item);
            this.basePrice = basePrice;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = multiplier;
        }
        
        @Override
        public TradeOffer create(final Entity entity, final Random random) {
            final int integer3 = 5 + random.nextInt(15);
            final ItemStack itemStack4 = EnchantmentHelper.enchant(random, new ItemStack(this.tool.getItem()), integer3, false);
            final int integer4 = Math.min(this.basePrice + integer3, 64);
            final ItemStack itemStack5 = new ItemStack(Items.nF, integer4);
            return new TradeOffer(itemStack5, itemStack4, this.maxUses, this.experience, this.multiplier);
        }
    }
    
    static class SellPotionHoldingItemFactory implements Factory
    {
        private final ItemStack sell;
        private final int sellCount;
        private final int price;
        private final int maxUses;
        private final int experience;
        private final Item secondBuy;
        private final int secondCount;
        private final float priceMultiplier;
        
        public SellPotionHoldingItemFactory(final Item arrow, final int secondCount, final Item tippedArrow, final int sellCount, final int price, final int maxUses, final int experience) {
            this.sell = new ItemStack(tippedArrow);
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
            this.secondBuy = arrow;
            this.secondCount = secondCount;
            this.sellCount = sellCount;
            this.priceMultiplier = 0.05f;
        }
        
        @Override
        public TradeOffer create(final Entity entity, final Random random) {
            final ItemStack itemStack3 = new ItemStack(Items.nF, this.price);
            final List<Potion> list4 = Registry.POTION.stream().filter(potion -> !potion.getEffects().isEmpty() && BrewingRecipeRegistry.isBrewable(potion)).collect(Collectors.toList());
            final Potion potion2 = list4.get(random.nextInt(list4.size()));
            final ItemStack itemStack4 = PotionUtil.setPotion(new ItemStack(this.sell.getItem(), this.sellCount), potion2);
            return new TradeOffer(itemStack3, new ItemStack(this.secondBuy, this.secondCount), itemStack4, this.maxUses, this.experience, this.priceMultiplier);
        }
    }
    
    static class SellDyedArmorFactory implements Factory
    {
        private final Item sell;
        private final int price;
        private final int maxUses;
        private final int experience;
        
        public SellDyedArmorFactory(final Item item, final int price) {
            this(item, price, 6, 1);
        }
        
        public SellDyedArmorFactory(final Item item, final int price, final int maxUses, final int experience) {
            this.sell = item;
            this.price = price;
            this.maxUses = maxUses;
            this.experience = experience;
        }
        
        @Override
        public TradeOffer create(final Entity entity, final Random random) {
            final ItemStack itemStack3 = new ItemStack(Items.nF, this.price);
            ItemStack itemStack4 = new ItemStack(this.sell);
            if (this.sell instanceof DyeableArmorItem) {
                final List<DyeItem> list5 = Lists.newArrayList();
                list5.add(getDye(random));
                if (random.nextFloat() > 0.7f) {
                    list5.add(getDye(random));
                }
                if (random.nextFloat() > 0.8f) {
                    list5.add(getDye(random));
                }
                itemStack4 = DyeableItem.applyDyes(itemStack4, list5);
            }
            return new TradeOffer(itemStack3, itemStack4, this.maxUses, this.experience, 0.2f);
        }
        
        private static DyeItem getDye(final Random random) {
            return DyeItem.fromColor(DyeColor.byId(random.nextInt(16)));
        }
    }
    
    static class EnchantBookFactory implements Factory
    {
        private final int experience;
        
        public EnchantBookFactory(final int experience) {
            this.experience = experience;
        }
        
        @Override
        public TradeOffer create(final Entity entity, final Random random) {
            final Enchantment enchantment3 = Registry.ENCHANTMENT.getRandom(random);
            final int integer4 = MathHelper.nextInt(random, enchantment3.getMinimumLevel(), enchantment3.getMaximumLevel());
            final ItemStack itemStack5 = EnchantedBookItem.makeStack(new InfoEnchantment(enchantment3, integer4));
            int integer5 = 2 + random.nextInt(5 + integer4 * 10) + 3 * integer4;
            if (enchantment3.isTreasure()) {
                integer5 *= 2;
            }
            if (integer5 > 64) {
                integer5 = 64;
            }
            return new TradeOffer(new ItemStack(Items.nF, integer5), new ItemStack(Items.kS), itemStack5, 6, this.experience, 0.2f);
        }
    }
    
    static class SellMapFactory implements Factory
    {
        private final int price;
        private final String structure;
        private final MapIcon.Type iconType;
        private final int maxUses;
        private final int experience;
        
        public SellMapFactory(final int price, final String structure, final MapIcon.Type iconType, final int maxUses, final int experience) {
            this.price = price;
            this.structure = structure;
            this.iconType = iconType;
            this.maxUses = maxUses;
            this.experience = experience;
        }
        
        @Nullable
        @Override
        public TradeOffer create(final Entity entity, final Random random) {
            final World world3 = entity.world;
            final BlockPos blockPos4 = world3.locateStructure(this.structure, new BlockPos(entity), 100, true);
            if (blockPos4 != null) {
                final ItemStack itemStack5 = FilledMapItem.createMap(world3, blockPos4.getX(), blockPos4.getZ(), (byte)2, true, true);
                FilledMapItem.fillExplorationMap(world3, itemStack5);
                MapState.addDecorationsTag(itemStack5, blockPos4, "+", this.iconType);
                itemStack5.setDisplayName(new TranslatableTextComponent("filled_map." + this.structure.toLowerCase(Locale.ROOT), new Object[0]));
                return new TradeOffer(new ItemStack(Items.nF, this.price), new ItemStack(Items.kX), itemStack5, this.maxUses, this.experience, 0.2f);
            }
            return null;
        }
    }
    
    static class ProcessItemFactory implements Factory
    {
        private final ItemStack secondBuy;
        private final int secondCount;
        private final int price;
        private final ItemStack sell;
        private final int sellCount;
        private final int maxUses;
        private final int experience;
        private final float multiplier;
        
        public ProcessItemFactory(final ItemProvider itemProvider, final int secondCount, final Item sellItem, final int sellCount, final int maxUses, final int experience) {
            this(itemProvider, secondCount, 1, sellItem, sellCount, maxUses, experience);
        }
        
        public ProcessItemFactory(final ItemProvider itemProvider, final int secondCount, final int price, final Item sellItem, final int sellCount, final int maxUses, final int experience) {
            this.secondBuy = new ItemStack(itemProvider);
            this.secondCount = secondCount;
            this.price = price;
            this.sell = new ItemStack(sellItem);
            this.sellCount = sellCount;
            this.maxUses = maxUses;
            this.experience = experience;
            this.multiplier = 0.05f;
        }
        
        @Nullable
        @Override
        public TradeOffer create(final Entity entity, final Random random) {
            return new TradeOffer(new ItemStack(Items.nF, this.price), new ItemStack(this.secondBuy.getItem(), this.secondCount), new ItemStack(this.sell.getItem(), this.sellCount), this.maxUses, this.experience, this.multiplier);
        }
    }
    
    public interface Factory
    {
        @Nullable
        TradeOffer create(final Entity arg1, final Random arg2);
    }
}
