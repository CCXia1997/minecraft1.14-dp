package net.minecraft.world.timer;

import org.apache.logging.log4j.LogManager;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.Logger;

public class TimerCallbackSerializer<C>
{
    private static final Logger LOGGER;
    public static final TimerCallbackSerializer<MinecraftServer> INSTANCE;
    private final Map<Identifier, TimerCallback.Serializer<C, ?>> serializersByType;
    private final Map<Class<?>, TimerCallback.Serializer<C, ?>> serializersByClass;
    
    @VisibleForTesting
    public TimerCallbackSerializer() {
        this.serializersByType = Maps.newHashMap();
        this.serializersByClass = Maps.newHashMap();
    }
    
    public TimerCallbackSerializer<C> registerSerializer(final TimerCallback.Serializer<C, ?> serializer) {
        this.serializersByType.put(serializer.getId(), serializer);
        this.serializersByClass.put(serializer.getCallbackClass(), serializer);
        return this;
    }
    
    private <T extends TimerCallback<C>> TimerCallback.Serializer<C, T> getSerializer(final Class<?> class1) {
        return (TimerCallback.Serializer<C, T>)this.serializersByClass.get(class1);
    }
    
    public <T extends TimerCallback<C>> CompoundTag serialize(final T callback) {
        final TimerCallback.Serializer<C, T> serializer2 = this.<T>getSerializer(callback.getClass());
        final CompoundTag compoundTag3 = new CompoundTag();
        serializer2.serialize(compoundTag3, callback);
        compoundTag3.putString("Type", serializer2.getId().toString());
        return compoundTag3;
    }
    
    @Nullable
    public TimerCallback<C> deserialize(final CompoundTag tag) {
        final Identifier identifier2 = Identifier.create(tag.getString("Type"));
        final TimerCallback.Serializer<C, ?> serializer3 = this.serializersByType.get(identifier2);
        if (serializer3 == null) {
            TimerCallbackSerializer.LOGGER.error("Failed to deserialize timer callback: " + tag);
            return null;
        }
        try {
            return (TimerCallback<C>)serializer3.deserialize(tag);
        }
        catch (Exception exception4) {
            TimerCallbackSerializer.LOGGER.error("Failed to deserialize timer callback: " + tag, (Throwable)exception4);
            return null;
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
        INSTANCE = new TimerCallbackSerializer<MinecraftServer>().registerSerializer(new FunctionTimerCallback.Serializer()).registerSerializer(new FunctionTagTimerCallback.Serializer());
    }
}
