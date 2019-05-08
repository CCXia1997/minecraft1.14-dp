package net.minecraft.tag;

import java.util.List;
import java.util.Collection;
import com.google.common.collect.Lists;
import java.util.Iterator;
import net.minecraft.util.Identifier;
import java.util.Map;
import net.minecraft.util.PacketByteBuf;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class RegistryTagContainer<T> extends TagContainer<T>
{
    private final Registry<T> registry;
    
    public RegistryTagContainer(final Registry<T> registry, final String path, final String type) {
        super(registry::getOrEmpty, path, false, type);
        this.registry = registry;
    }
    
    public void toPacket(final PacketByteBuf buf) {
        buf.writeVarInt(this.getEntries().size());
        for (final Map.Entry<Identifier, Tag<T>> entry3 : this.getEntries().entrySet()) {
            buf.writeIdentifier(entry3.getKey());
            buf.writeVarInt(entry3.getValue().values().size());
            for (final T object5 : entry3.getValue().values()) {
                buf.writeVarInt(this.registry.getRawId(object5));
            }
        }
    }
    
    public void fromPacket(final PacketByteBuf buf) {
        for (int integer2 = buf.readVarInt(), integer3 = 0; integer3 < integer2; ++integer3) {
            final Identifier identifier4 = buf.readIdentifier();
            final int integer4 = buf.readVarInt();
            final List<T> list6 = Lists.newArrayList();
            for (int integer5 = 0; integer5 < integer4; ++integer5) {
                list6.add(this.registry.get(buf.readVarInt()));
            }
            this.getEntries().put(identifier4, Tag.Builder.<T>create().add(list6).build(identifier4));
        }
    }
}
