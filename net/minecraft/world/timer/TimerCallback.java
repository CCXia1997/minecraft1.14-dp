package net.minecraft.world.timer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

@FunctionalInterface
public interface TimerCallback<T>
{
    void call(final T arg1, final Timer<T> arg2, final long arg3);
    
    public abstract static class Serializer<T, C extends TimerCallback<T>>
    {
        private final Identifier id;
        private final Class<?> callbackClass;
        
        public Serializer(final Identifier identifier, final Class<?> class2) {
            this.id = identifier;
            this.callbackClass = class2;
        }
        
        public Identifier getId() {
            return this.id;
        }
        
        public Class<?> getCallbackClass() {
            return this.callbackClass;
        }
        
        public abstract void serialize(final CompoundTag arg1, final C arg2);
        
        public abstract C deserialize(final CompoundTag arg1);
    }
}
