package net.minecraft.server.world;

import java.util.Objects;

final class ChunkTicket<T> implements Comparable<ChunkTicket<?>>
{
    private final ChunkTicketType<T> type;
    private final int level;
    private final T argument;
    private final long location;
    
    ChunkTicket(final ChunkTicketType<T> type, final int level, final T argument, final long location) {
        this.type = type;
        this.level = level;
        this.argument = argument;
        this.location = location;
    }
    
    public int a(final ChunkTicket<?> chunkTicket) {
        final int integer2 = Integer.compare(this.level, chunkTicket.level);
        if (integer2 != 0) {
            return integer2;
        }
        final int integer3 = Integer.compare(System.identityHashCode(this.type), System.identityHashCode(chunkTicket.type));
        if (integer3 != 0) {
            return integer3;
        }
        return this.type.getArgumentComparator().compare(this.argument, (T)chunkTicket.argument);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ChunkTicket)) {
            return false;
        }
        final ChunkTicket<?> chunkTicket2 = obj;
        return this.level == chunkTicket2.level && Objects.equals(this.type, chunkTicket2.type) && Objects.equals(this.argument, chunkTicket2.argument);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.level, this.argument);
    }
    
    @Override
    public String toString() {
        return "Ticket[" + this.type + " " + this.level + " (" + this.argument + ")] at " + this.location;
    }
    
    public ChunkTicketType<T> getType() {
        return this.type;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public long getLocation() {
        return this.location;
    }
}
