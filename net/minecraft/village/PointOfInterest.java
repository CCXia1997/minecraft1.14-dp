package net.minecraft.village;

import java.util.Objects;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.util.function.Function;
import com.mojang.datafixers.Dynamic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DynamicSerializable;

public class PointOfInterest implements DynamicSerializable
{
    private final BlockPos pos;
    private final PointOfInterestType type;
    private int freeTickets;
    private final Runnable updateListener;
    
    private PointOfInterest(final BlockPos pos, final PointOfInterestType type, final int freeTickets, final Runnable updateListener) {
        this.pos = pos.toImmutable();
        this.type = type;
        this.freeTickets = freeTickets;
        this.updateListener = updateListener;
    }
    
    public PointOfInterest(final BlockPos pos, final PointOfInterestType type, final Runnable updateListener) {
        this(pos, type, type.getTicketCount(), updateListener);
    }
    
    public <T> PointOfInterest(final Dynamic<T> dynamic, final Runnable updateListener) {
        this(dynamic.get("pos").map((Function)BlockPos::deserialize).orElse(new BlockPos(0, 0, 0)), Registry.POINT_OF_INTEREST_TYPE.get(new Identifier(dynamic.get("type").asString(""))), dynamic.get("free_tickets").asInt(0), updateListener);
    }
    
    @Override
    public <T> T serialize(final DynamicOps<T> ops) {
        return (T)ops.createMap((Map)ImmutableMap.of(ops.createString("pos"), this.pos.<V>serialize((com.mojang.datafixers.types.DynamicOps<V>)ops), ops.createString("type"), ops.createString(Registry.POINT_OF_INTEREST_TYPE.getId(this.type).toString()), ops.createString("free_tickets"), ops.createInt(this.freeTickets)));
    }
    
    protected boolean reserveTicket() {
        if (this.freeTickets <= 0) {
            return false;
        }
        --this.freeTickets;
        this.updateListener.run();
        return true;
    }
    
    protected boolean releaseTicket() {
        if (this.freeTickets >= this.type.getTicketCount()) {
            return false;
        }
        ++this.freeTickets;
        this.updateListener.run();
        return true;
    }
    
    public boolean hasSpace() {
        return this.freeTickets > 0;
    }
    
    public boolean isOccupied() {
        return this.freeTickets != this.type.getTicketCount();
    }
    
    public BlockPos getPos() {
        return this.pos;
    }
    
    public PointOfInterestType getType() {
        return this.type;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj != null && this.getClass() == obj.getClass() && Objects.equals(this.pos, ((PointOfInterest)obj).pos));
    }
    
    @Override
    public int hashCode() {
        return this.pos.hashCode();
    }
}
