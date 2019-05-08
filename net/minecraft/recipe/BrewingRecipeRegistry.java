package net.minecraft.recipe;

import com.google.common.collect.Lists;
import java.util.Iterator;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potions;
import net.minecraft.item.Items;
import net.minecraft.item.ItemProvider;
import net.minecraft.potion.PotionUtil;
import net.minecraft.item.ItemStack;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import java.util.List;

public class BrewingRecipeRegistry
{
    private static final List<Recipe<Potion>> POTION_RECIPES;
    private static final List<Recipe<Item>> ITEM_RECIPES;
    private static final List<Ingredient> POTION_TYPES;
    private static final Predicate<ItemStack> POTION_TYPE_PREDICATE;
    
    public static boolean isValidIngredient(final ItemStack stack) {
        return isItemRecipeIngredient(stack) || isPotionRecipeIngredient(stack);
    }
    
    protected static boolean isItemRecipeIngredient(final ItemStack stack) {
        for (int integer2 = 0, integer3 = BrewingRecipeRegistry.ITEM_RECIPES.size(); integer2 < integer3; ++integer2) {
            if (((Recipe<Object>)BrewingRecipeRegistry.ITEM_RECIPES.get(integer2)).ingredient.a(stack)) {
                return true;
            }
        }
        return false;
    }
    
    protected static boolean isPotionRecipeIngredient(final ItemStack stack) {
        for (int integer2 = 0, integer3 = BrewingRecipeRegistry.POTION_RECIPES.size(); integer2 < integer3; ++integer2) {
            if (((Recipe<Object>)BrewingRecipeRegistry.POTION_RECIPES.get(integer2)).ingredient.a(stack)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isBrewable(final Potion potion) {
        for (int integer2 = 0, integer3 = BrewingRecipeRegistry.POTION_RECIPES.size(); integer2 < integer3; ++integer2) {
            if (((Recipe<Object>)BrewingRecipeRegistry.POTION_RECIPES.get(integer2)).output == potion) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean hasRecipe(final ItemStack input, final ItemStack ingredient) {
        return BrewingRecipeRegistry.POTION_TYPE_PREDICATE.test(input) && (hasItemRecipe(input, ingredient) || hasPotionRecipe(input, ingredient));
    }
    
    protected static boolean hasItemRecipe(final ItemStack input, final ItemStack ingredien) {
        final Item item3 = input.getItem();
        for (int integer4 = 0, integer5 = BrewingRecipeRegistry.ITEM_RECIPES.size(); integer4 < integer5; ++integer4) {
            final Recipe<Item> recipe6 = BrewingRecipeRegistry.ITEM_RECIPES.get(integer4);
            if (((Recipe<Object>)recipe6).input == item3 && ((Recipe<Object>)recipe6).ingredient.a(ingredien)) {
                return true;
            }
        }
        return false;
    }
    
    protected static boolean hasPotionRecipe(final ItemStack input, final ItemStack ingredient) {
        final Potion potion3 = PotionUtil.getPotion(input);
        for (int integer4 = 0, integer5 = BrewingRecipeRegistry.POTION_RECIPES.size(); integer4 < integer5; ++integer4) {
            final Recipe<Potion> recipe6 = BrewingRecipeRegistry.POTION_RECIPES.get(integer4);
            if (((Recipe<Object>)recipe6).input == potion3 && ((Recipe<Object>)recipe6).ingredient.a(ingredient)) {
                return true;
            }
        }
        return false;
    }
    
    public static ItemStack craft(final ItemStack input, final ItemStack ingredient) {
        if (!ingredient.isEmpty()) {
            final Potion potion3 = PotionUtil.getPotion(ingredient);
            final Item item4 = ingredient.getItem();
            for (int integer5 = 0, integer6 = BrewingRecipeRegistry.ITEM_RECIPES.size(); integer5 < integer6; ++integer5) {
                final Recipe<Item> recipe7 = BrewingRecipeRegistry.ITEM_RECIPES.get(integer5);
                if (((Recipe<Object>)recipe7).input == item4 && ((Recipe<Object>)recipe7).ingredient.a(input)) {
                    return PotionUtil.setPotion(new ItemStack((ItemProvider)((Recipe<Object>)recipe7).output), potion3);
                }
            }
            for (int integer5 = 0, integer6 = BrewingRecipeRegistry.POTION_RECIPES.size(); integer5 < integer6; ++integer5) {
                final Recipe<Potion> recipe8 = BrewingRecipeRegistry.POTION_RECIPES.get(integer5);
                if (((Recipe<Object>)recipe8).input == potion3 && ((Recipe<Object>)recipe8).ingredient.a(input)) {
                    return PotionUtil.setPotion(new ItemStack(item4), (Potion)((Recipe<Object>)recipe8).output);
                }
            }
        }
        return ingredient;
    }
    
    public static void registerDefaults() {
        registerPotionType(Items.ml);
        registerPotionType(Items.oS);
        registerPotionType(Items.oV);
        registerItemRecipe(Items.ml, Items.jI, Items.oS);
        registerItemRecipe(Items.oS, Items.oR, Items.oV);
        registerPotionRecipe(Potions.b, Items.mu, Potions.c);
        registerPotionRecipe(Potions.b, Items.mi, Potions.c);
        registerPotionRecipe(Potions.b, Items.oj, Potions.c);
        registerPotionRecipe(Potions.b, Items.mp, Potions.c);
        registerPotionRecipe(Potions.b, Items.mn, Potions.c);
        registerPotionRecipe(Potions.b, Items.lC, Potions.c);
        registerPotionRecipe(Potions.b, Items.mq, Potions.c);
        registerPotionRecipe(Potions.b, Items.la, Potions.d);
        registerPotionRecipe(Potions.b, Items.kC, Potions.c);
        registerPotionRecipe(Potions.b, Items.mk, Potions.e);
        registerPotionRecipe(Potions.e, Items.nN, Potions.f);
        registerPotionRecipe(Potions.f, Items.kC, Potions.g);
        registerPotionRecipe(Potions.f, Items.mo, Potions.h);
        registerPotionRecipe(Potions.g, Items.mo, Potions.i);
        registerPotionRecipe(Potions.h, Items.kC, Potions.i);
        registerPotionRecipe(Potions.e, Items.mq, Potions.m);
        registerPotionRecipe(Potions.m, Items.kC, Potions.n);
        registerPotionRecipe(Potions.e, Items.oj, Potions.j);
        registerPotionRecipe(Potions.j, Items.kC, Potions.k);
        registerPotionRecipe(Potions.j, Items.la, Potions.l);
        registerPotionRecipe(Potions.j, Items.mo, Potions.r);
        registerPotionRecipe(Potions.k, Items.mo, Potions.s);
        registerPotionRecipe(Potions.r, Items.kC, Potions.s);
        registerPotionRecipe(Potions.r, Items.la, Potions.t);
        registerPotionRecipe(Potions.e, Items.iY, Potions.u);
        registerPotionRecipe(Potions.u, Items.kC, Potions.v);
        registerPotionRecipe(Potions.u, Items.la, Potions.w);
        registerPotionRecipe(Potions.o, Items.mo, Potions.r);
        registerPotionRecipe(Potions.p, Items.mo, Potions.s);
        registerPotionRecipe(Potions.e, Items.lC, Potions.o);
        registerPotionRecipe(Potions.o, Items.kC, Potions.p);
        registerPotionRecipe(Potions.o, Items.la, Potions.q);
        registerPotionRecipe(Potions.e, Items.le, Potions.x);
        registerPotionRecipe(Potions.x, Items.kC, Potions.y);
        registerPotionRecipe(Potions.e, Items.mu, Potions.z);
        registerPotionRecipe(Potions.z, Items.la, Potions.A);
        registerPotionRecipe(Potions.z, Items.mo, Potions.B);
        registerPotionRecipe(Potions.A, Items.mo, Potions.C);
        registerPotionRecipe(Potions.B, Items.la, Potions.C);
        registerPotionRecipe(Potions.D, Items.mo, Potions.B);
        registerPotionRecipe(Potions.E, Items.mo, Potions.B);
        registerPotionRecipe(Potions.F, Items.mo, Potions.C);
        registerPotionRecipe(Potions.e, Items.mn, Potions.D);
        registerPotionRecipe(Potions.D, Items.kC, Potions.E);
        registerPotionRecipe(Potions.D, Items.la, Potions.F);
        registerPotionRecipe(Potions.e, Items.mi, Potions.G);
        registerPotionRecipe(Potions.G, Items.kC, Potions.H);
        registerPotionRecipe(Potions.G, Items.la, Potions.I);
        registerPotionRecipe(Potions.e, Items.mp, Potions.J);
        registerPotionRecipe(Potions.J, Items.kC, Potions.K);
        registerPotionRecipe(Potions.J, Items.la, Potions.L);
        registerPotionRecipe(Potions.b, Items.mo, Potions.M);
        registerPotionRecipe(Potions.M, Items.kC, Potions.N);
        registerPotionRecipe(Potions.e, Items.pv, Potions.P);
        registerPotionRecipe(Potions.P, Items.kC, Potions.Q);
    }
    
    private static void registerItemRecipe(final Item input, final Item ingredient, final Item output) {
        if (!(input instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(input));
        }
        if (!(output instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(output));
        }
        BrewingRecipeRegistry.ITEM_RECIPES.add(new Recipe<Item>(input, Ingredient.ofItems(ingredient), output));
    }
    
    private static void registerPotionType(final Item item) {
        if (!(item instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + Registry.ITEM.getId(item));
        }
        BrewingRecipeRegistry.POTION_TYPES.add(Ingredient.ofItems(item));
    }
    
    private static void registerPotionRecipe(final Potion input, final Item item, final Potion output) {
        BrewingRecipeRegistry.POTION_RECIPES.add(new Recipe<Potion>(input, Ingredient.ofItems(item), output));
    }
    
    static {
        POTION_RECIPES = Lists.newArrayList();
        ITEM_RECIPES = Lists.newArrayList();
        POTION_TYPES = Lists.newArrayList();
        final Iterator<Ingredient> iterator;
        Ingredient ingredient3;
        POTION_TYPE_PREDICATE = (itemStack -> {
            BrewingRecipeRegistry.POTION_TYPES.iterator();
            while (iterator.hasNext()) {
                ingredient3 = iterator.next();
                if (ingredient3.a(itemStack)) {
                    return true;
                }
            }
            return false;
        });
    }
    
    static class Recipe<T>
    {
        private final T input;
        private final Ingredient ingredient;
        private final T output;
        
        public Recipe(final T object1, final Ingredient ingredient, final T object3) {
            this.input = object1;
            this.ingredient = ingredient;
            this.output = object3;
        }
    }
}
