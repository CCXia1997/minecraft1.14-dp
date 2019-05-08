package net.minecraft.world.loot;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.HashMultimap;
import java.util.function.Supplier;
import com.google.common.collect.Multimap;

public class LootTableReporter
{
    private final Multimap<String, String> messages;
    private final Supplier<String> nameFactory;
    private String name;
    
    public LootTableReporter() {
        this(HashMultimap.create(), () -> "");
    }
    
    public LootTableReporter(final Multimap<String, String> messages, final Supplier<String> nameFactory) {
        this.messages = messages;
        this.nameFactory = nameFactory;
    }
    
    private String getContext() {
        if (this.name == null) {
            this.name = this.nameFactory.get();
        }
        return this.name;
    }
    
    public void report(final String message) {
        this.messages.put(this.getContext(), message);
    }
    
    public LootTableReporter makeChild(final String name) {
        return new LootTableReporter(this.messages, () -> this.getContext() + name);
    }
    
    public Multimap<String, String> getMessages() {
        return ImmutableMultimap.copyOf(this.messages);
    }
}
