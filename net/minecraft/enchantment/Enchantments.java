package net.minecraft.enchantment;

import net.minecraft.util.registry.Registry;
import net.minecraft.entity.EquipmentSlot;

public class Enchantments
{
    private static final EquipmentSlot[] ALL_ARMOR;
    public static final Enchantment a;
    public static final Enchantment b;
    public static final Enchantment c;
    public static final Enchantment d;
    public static final Enchantment e;
    public static final Enchantment f;
    public static final Enchantment g;
    public static final Enchantment h;
    public static final Enchantment i;
    public static final Enchantment j;
    public static final Enchantment k;
    public static final Enchantment l;
    public static final Enchantment m;
    public static final Enchantment n;
    public static final Enchantment o;
    public static final Enchantment p;
    public static final Enchantment q;
    public static final Enchantment r;
    public static final Enchantment s;
    public static final Enchantment t;
    public static final Enchantment u;
    public static final Enchantment v;
    public static final Enchantment w;
    public static final Enchantment x;
    public static final Enchantment y;
    public static final Enchantment z;
    public static final Enchantment A;
    public static final Enchantment B;
    public static final Enchantment C;
    public static final Enchantment D;
    public static final Enchantment E;
    public static final Enchantment F;
    public static final Enchantment G;
    public static final Enchantment H;
    public static final Enchantment I;
    public static final Enchantment J;
    public static final Enchantment K;
    
    private static Enchantment register(final String string, final Enchantment enchantment) {
        return Registry.<Enchantment>register(Registry.ENCHANTMENT, string, enchantment);
    }
    
    static {
        ALL_ARMOR = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
        a = register("protection", new ProtectionEnchantment(Enchantment.Weight.COMMON, ProtectionEnchantment.Type.ALL, Enchantments.ALL_ARMOR));
        b = register("fire_protection", new ProtectionEnchantment(Enchantment.Weight.UNCOMMON, ProtectionEnchantment.Type.FIRE, Enchantments.ALL_ARMOR));
        c = register("feather_falling", new ProtectionEnchantment(Enchantment.Weight.UNCOMMON, ProtectionEnchantment.Type.FALL, Enchantments.ALL_ARMOR));
        d = register("blast_protection", new ProtectionEnchantment(Enchantment.Weight.RARE, ProtectionEnchantment.Type.EXPLOSION, Enchantments.ALL_ARMOR));
        e = register("projectile_protection", new ProtectionEnchantment(Enchantment.Weight.UNCOMMON, ProtectionEnchantment.Type.PROJECTILE, Enchantments.ALL_ARMOR));
        f = register("respiration", new RespirationEnchantment(Enchantment.Weight.RARE, Enchantments.ALL_ARMOR));
        g = register("aqua_affinity", new AquaAffinityEnchantment(Enchantment.Weight.RARE, Enchantments.ALL_ARMOR));
        h = register("thorns", new ThornsEnchantment(Enchantment.Weight.LEGENDARY, Enchantments.ALL_ARMOR));
        i = register("depth_strider", new DepthStriderEnchantment(Enchantment.Weight.RARE, Enchantments.ALL_ARMOR));
        j = register("frost_walker", new FrostWalkerEnchantment(Enchantment.Weight.RARE, new EquipmentSlot[] { EquipmentSlot.FEET }));
        k = register("binding_curse", new BindingCurseEnchantment(Enchantment.Weight.LEGENDARY, Enchantments.ALL_ARMOR));
        l = register("sharpness", new DamageEnchantment(Enchantment.Weight.COMMON, 0, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        m = register("smite", new DamageEnchantment(Enchantment.Weight.UNCOMMON, 1, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        n = register("bane_of_arthropods", new DamageEnchantment(Enchantment.Weight.UNCOMMON, 2, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        o = register("knockback", new KnockbackEnchantment(Enchantment.Weight.UNCOMMON, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        p = register("fire_aspect", new FireAspectEnchantment(Enchantment.Weight.RARE, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        q = register("looting", new LuckEnchantment(Enchantment.Weight.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        r = register("sweeping", new SweepingEnchantment(Enchantment.Weight.RARE, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        s = register("efficiency", new EfficiencyEnchantment(Enchantment.Weight.COMMON, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        t = register("silk_touch", new SilkTouchEnchantment(Enchantment.Weight.LEGENDARY, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        u = register("unbreaking", new UnbreakingEnchantment(Enchantment.Weight.UNCOMMON, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        v = register("fortune", new LuckEnchantment(Enchantment.Weight.RARE, EnchantmentTarget.BREAKER, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        w = register("power", new PowerEnchantment(Enchantment.Weight.COMMON, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        x = register("punch", new PunchEnchantment(Enchantment.Weight.RARE, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        y = register("flame", new FlameEnchantment(Enchantment.Weight.RARE, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        z = register("infinity", new InfinityEnchantment(Enchantment.Weight.LEGENDARY, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        A = register("luck_of_the_sea", new LuckEnchantment(Enchantment.Weight.RARE, EnchantmentTarget.FISHING, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        B = register("lure", new LureEnchantment(Enchantment.Weight.RARE, EnchantmentTarget.FISHING, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        C = register("loyalty", new LoyaltyEnchantment(Enchantment.Weight.UNCOMMON, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        D = register("impaling", new ImpalingEnchantment(Enchantment.Weight.RARE, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        E = register("riptide", new RiptideEnchantment(Enchantment.Weight.RARE, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        F = register("channeling", new ChannelingEnchantment(Enchantment.Weight.LEGENDARY, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        G = register("multishot", new MultishotEnchantment(Enchantment.Weight.RARE, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        H = register("quick_charge", new QuickChargeEnchantment(Enchantment.Weight.UNCOMMON, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        I = register("piercing", new PiercingEnchantment(Enchantment.Weight.COMMON, new EquipmentSlot[] { EquipmentSlot.HAND_MAIN }));
        J = register("mending", new MendingEnchantment(Enchantment.Weight.RARE, EquipmentSlot.values()));
        K = register("vanishing_curse", new VanishingCurseEnchantment(Enchantment.Weight.LEGENDARY, EquipmentSlot.values()));
    }
}
