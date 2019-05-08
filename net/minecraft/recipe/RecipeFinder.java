package net.minecraft.recipe;

import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import java.util.Iterator;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import java.util.Collection;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.Item;
import javax.annotation.Nullable;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.ItemStack;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

public class RecipeFinder
{
    public final Int2IntMap idToAmountMap;
    
    public RecipeFinder() {
        this.idToAmountMap = (Int2IntMap)new Int2IntOpenHashMap();
    }
    
    public void addNormalItem(final ItemStack stack) {
        if (!stack.isDamaged() && !stack.hasEnchantments() && !stack.hasDisplayName()) {
            this.addItem(stack);
        }
    }
    
    public void addItem(final ItemStack itemStack) {
        this.a(itemStack, 64);
    }
    
    public void a(final ItemStack itemStack, final int integer) {
        if (!itemStack.isEmpty()) {
            final int integer2 = getItemId(itemStack);
            final int integer3 = Math.min(integer, itemStack.getAmount());
            this.addItem(integer2, integer3);
        }
    }
    
    public static int getItemId(final ItemStack itemStack) {
        return Registry.ITEM.getRawId(itemStack.getItem());
    }
    
    private boolean contains(final int integer) {
        return this.idToAmountMap.get(integer) > 0;
    }
    
    private int take(final int id, final int amount) {
        final int integer3 = this.idToAmountMap.get(id);
        if (integer3 >= amount) {
            this.idToAmountMap.put(id, integer3 - amount);
            return id;
        }
        return 0;
    }
    
    private void addItem(final int id, final int amount) {
        this.idToAmountMap.put(id, this.idToAmountMap.get(id) + amount);
    }
    
    public boolean findRecipe(final Recipe<?> recipe, @Nullable final IntList outMatchingInputIds) {
        return this.findRecipe(recipe, outMatchingInputIds, 1);
    }
    
    public boolean findRecipe(final Recipe<?> recipe, @Nullable final IntList outMatchingInputIds, final int amount) {
        return new Filter(recipe).find(amount, outMatchingInputIds);
    }
    
    public int countRecipeCrafts(final Recipe<?> recipe, @Nullable final IntList outMatchingInputIds) {
        return this.countRecipeCrafts(recipe, Integer.MAX_VALUE, outMatchingInputIds);
    }
    
    public int countRecipeCrafts(final Recipe<?> recipe, final int limit, @Nullable final IntList outMatchingInputIds) {
        return new Filter(recipe).countCrafts(limit, outMatchingInputIds);
    }
    
    public static ItemStack getStackFromId(final int integer) {
        if (integer == 0) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(Item.byRawId(integer));
    }
    
    public void clear() {
        this.idToAmountMap.clear();
    }
    
    class Filter
    {
        private final Recipe<?> recipe;
        private final List<Ingredient> ingredients;
        private final int ingredientCount;
        private final int[] e;
        private final int f;
        private final BitSet g;
        private final IntList h;
        
        public Filter(final Recipe<?> recipe) {
            this.ingredients = Lists.newArrayList();
            this.h = (IntList)new IntArrayList();
            this.recipe = recipe;
            this.ingredients.addAll(recipe.getPreviewInputs());
            this.ingredients.removeIf(Ingredient::isEmpty);
            this.ingredientCount = this.ingredients.size();
            this.e = this.a();
            this.f = this.e.length;
            this.g = new BitSet(this.ingredientCount + this.f + this.ingredientCount + this.ingredientCount * this.f);
            for (int integer3 = 0; integer3 < this.ingredients.size(); ++integer3) {
                final IntList intList4 = this.ingredients.get(integer3).getIds();
                for (int integer4 = 0; integer4 < this.f; ++integer4) {
                    if (intList4.contains(this.e[integer4])) {
                        this.g.set(this.d(true, integer4, integer3));
                    }
                }
            }
        }
        
        public boolean find(final int amount, @Nullable final IntList outMatchingInputIds) {
            if (amount <= 0) {
                return true;
            }
            int integer3 = 0;
            while (this.a(amount)) {
                RecipeFinder.this.take(this.e[this.h.getInt(0)], amount);
                final int integer4 = this.h.size() - 1;
                this.c(this.h.getInt(integer4));
                for (int integer5 = 0; integer5 < integer4; ++integer5) {
                    this.c((integer5 & 0x1) == 0x0, this.h.get(integer5), this.h.get(integer5 + 1));
                }
                this.h.clear();
                this.g.clear(0, this.ingredientCount + this.f);
                ++integer3;
            }
            final boolean boolean4 = integer3 == this.ingredientCount;
            final boolean boolean5 = boolean4 && outMatchingInputIds != null;
            if (boolean5) {
                outMatchingInputIds.clear();
            }
            this.g.clear(0, this.ingredientCount + this.f + this.ingredientCount);
            int integer6 = 0;
            final List<Ingredient> list7 = this.recipe.getPreviewInputs();
            for (int integer7 = 0; integer7 < list7.size(); ++integer7) {
                if (boolean5 && list7.get(integer7).isEmpty()) {
                    outMatchingInputIds.add(0);
                }
                else {
                    for (int integer8 = 0; integer8 < this.f; ++integer8) {
                        if (this.b(false, integer6, integer8)) {
                            this.c(true, integer8, integer6);
                            RecipeFinder.this.addItem(this.e[integer8], amount);
                            if (boolean5) {
                                outMatchingInputIds.add(this.e[integer8]);
                            }
                        }
                    }
                    ++integer6;
                }
            }
            return boolean4;
        }
        
        private int[] a() {
            final IntCollection intCollection1 = (IntCollection)new IntAVLTreeSet();
            for (final Ingredient ingredient3 : this.ingredients) {
                intCollection1.addAll((IntCollection)ingredient3.getIds());
            }
            final IntIterator intIterator2 = intCollection1.iterator();
            while (intIterator2.hasNext()) {
                if (!RecipeFinder.this.contains(intIterator2.nextInt())) {
                    intIterator2.remove();
                }
            }
            return intCollection1.toIntArray();
        }
        
        private boolean a(final int integer) {
            for (int integer2 = this.f, integer3 = 0; integer3 < integer2; ++integer3) {
                if (RecipeFinder.this.idToAmountMap.get(this.e[integer3]) >= integer) {
                    this.a(false, integer3);
                    while (!this.h.isEmpty()) {
                        final int integer4 = this.h.size();
                        final boolean boolean5 = (integer4 & 0x1) == 0x1;
                        final int integer5 = this.h.getInt(integer4 - 1);
                        if (!boolean5 && !this.b(integer5)) {
                            break;
                        }
                        for (int integer6 = boolean5 ? this.ingredientCount : integer2, integer7 = 0; integer7 < integer6; ++integer7) {
                            if (!this.b(boolean5, integer7) && this.a(boolean5, integer5, integer7) && this.b(boolean5, integer5, integer7)) {
                                this.a(boolean5, integer7);
                                break;
                            }
                        }
                        int integer7 = this.h.size();
                        if (integer7 != integer4) {
                            continue;
                        }
                        this.h.removeInt(integer7 - 1);
                    }
                    if (!this.h.isEmpty()) {
                        return true;
                    }
                }
            }
            return false;
        }
        
        private boolean b(final int integer) {
            return this.g.get(this.d(integer));
        }
        
        private void c(final int integer) {
            this.g.set(this.d(integer));
        }
        
        private int d(final int integer) {
            return this.ingredientCount + this.f + integer;
        }
        
        private boolean a(final boolean boolean1, final int integer2, final int integer3) {
            return this.g.get(this.d(boolean1, integer2, integer3));
        }
        
        private boolean b(final boolean boolean1, final int integer2, final int integer3) {
            return boolean1 != this.g.get(1 + this.d(boolean1, integer2, integer3));
        }
        
        private void c(final boolean boolean1, final int integer2, final int integer3) {
            this.g.flip(1 + this.d(boolean1, integer2, integer3));
        }
        
        private int d(final boolean boolean1, final int integer2, final int integer3) {
            final int integer4 = boolean1 ? (integer2 * this.ingredientCount + integer3) : (integer3 * this.ingredientCount + integer2);
            return this.ingredientCount + this.f + this.ingredientCount + 2 * integer4;
        }
        
        private void a(final boolean boolean1, final int integer) {
            this.g.set(this.c(boolean1, integer));
            this.h.add(integer);
        }
        
        private boolean b(final boolean boolean1, final int integer) {
            return this.g.get(this.c(boolean1, integer));
        }
        
        private int c(final boolean boolean1, final int integer) {
            return (boolean1 ? 0 : this.ingredientCount) + integer;
        }
        
        public int countCrafts(final int limit, @Nullable final IntList outMatchingInputIds) {
            int integer3 = 0;
            int integer4 = Math.min(limit, this.b()) + 1;
            int integer5;
            while (true) {
                integer5 = (integer3 + integer4) / 2;
                if (this.find(integer5, null)) {
                    if (integer4 - integer3 <= 1) {
                        break;
                    }
                    integer3 = integer5;
                }
                else {
                    integer4 = integer5;
                }
            }
            if (integer5 > 0) {
                this.find(integer5, outMatchingInputIds);
            }
            return integer5;
        }
        
        private int b() {
            int integer1 = Integer.MAX_VALUE;
            for (final Ingredient ingredient3 : this.ingredients) {
                int integer2 = 0;
                for (final int integer3 : ingredient3.getIds()) {
                    integer2 = Math.max(integer2, RecipeFinder.this.idToAmountMap.get(integer3));
                }
                if (integer1 > 0) {
                    integer1 = Math.min(integer1, integer2);
                }
            }
            return integer1;
        }
    }
}
