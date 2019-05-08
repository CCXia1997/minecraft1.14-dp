package net.minecraft.stat;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.scoreboard.ScoreboardCriterion;

public class Stat<T> extends ScoreboardCriterion
{
    private final StatFormatter formatter;
    private final T value;
    private final StatType<T> type;
    
    protected Stat(final StatType<T> type, final T value, final StatFormatter formatter) {
        super(Stat.<T>getName(type, value));
        this.type = type;
        this.formatter = formatter;
        this.value = value;
    }
    
    public static <T> String getName(final StatType<T> type, final T value) {
        return Stat.getName(Registry.STAT_TYPE.getId(type)) + ":" + Stat.getName(type.getRegistry().getId(value));
    }
    
    private static <T> String getName(@Nullable final Identifier id) {
        return id.toString().replace(':', '.');
    }
    
    public StatType<T> getType() {
        return this.type;
    }
    
    public T getValue() {
        return this.value;
    }
    
    @Environment(EnvType.CLIENT)
    public String format(final int integer) {
        return this.formatter.format(integer);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof Stat && Objects.equals(this.getName(), ((Stat)o).getName()));
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    @Override
    public String toString() {
        return "Stat{name=" + this.getName() + ", formatter=" + this.formatter + '}';
    }
}
