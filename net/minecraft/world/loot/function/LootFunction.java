package net.minecraft.world.loot.function;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import java.util.function.Consumer;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.item.ItemStack;
import java.util.function.BiFunction;
import net.minecraft.world.loot.context.ParameterConsumer;

public interface LootFunction extends ParameterConsumer, BiFunction<ItemStack, LootContext, ItemStack>
{
    default Consumer<ItemStack> apply(final BiFunction<ItemStack, LootContext, ItemStack> itemApplier, final Consumer<ItemStack> itemDropper, final LootContext context) {
        return stack -> itemDropper.accept(itemApplier.apply(stack, context));
    }
    
    public abstract static class Factory<T extends LootFunction>
    {
        private final Identifier id;
        private final Class<T> functionClass;
        
        protected Factory(final Identifier id, final Class<T> clazz) {
            this.id = id;
            this.functionClass = clazz;
        }
        
        public Identifier getId() {
            return this.id;
        }
        
        public Class<T> getFunctionClass() {
            return this.functionClass;
        }
        
        public abstract void toJson(final JsonObject arg1, final T arg2, final JsonSerializationContext arg3);
        
        public abstract T fromJson(final JsonObject arg1, final JsonDeserializationContext arg2);
    }
    
    public interface Builder
    {
        LootFunction build();
    }
}
