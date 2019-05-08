package net.minecraft.entity.ai.brain;

import java.util.Comparator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import com.google.common.collect.Maps;
import java.util.Map;

public class Schedule
{
    public static final Schedule EMPTY;
    public static final Schedule SIMPLE;
    public static final Schedule VILLAGER_BABY;
    public static final Schedule VILLAGER_DEFAULT;
    private final Map<Activity, ScheduleRule> scheduleRules;
    
    public Schedule() {
        this.scheduleRules = Maps.newHashMap();
    }
    
    protected static ScheduleBuilder register(final String id) {
        final Schedule schedule2 = Registry.SCHEDULE.<Schedule>add(new Identifier(id), new Schedule());
        return new ScheduleBuilder(schedule2);
    }
    
    protected void addActivity(final Activity activity) {
        if (!this.scheduleRules.containsKey(activity)) {
            this.scheduleRules.put(activity, new ScheduleRule());
        }
    }
    
    protected ScheduleRule getRule(final Activity activity) {
        return this.scheduleRules.get(activity);
    }
    
    protected List<ScheduleRule> getOtherRules(final Activity activity) {
        return this.scheduleRules.entrySet().stream().filter(entry -> entry.getKey() != activity).map(Map.Entry::getValue).collect(Collectors.toList());
    }
    
    public Activity getActivityForTime(final int time) {
        return this.scheduleRules.entrySet().stream().max(Comparator.comparingDouble(entry -> entry.getValue().getPriority(time))).<Activity>map(Map.Entry::getKey).orElse(Activity.b);
    }
    
    static {
        EMPTY = register("empty").withActivity(0, Activity.b).build();
        SIMPLE = register("simple").withActivity(5000, Activity.c).withActivity(11000, Activity.e).build();
        VILLAGER_BABY = register("villager_baby").withActivity(10, Activity.b).withActivity(3000, Activity.d).withActivity(6000, Activity.b).withActivity(10000, Activity.d).withActivity(12000, Activity.e).build();
        VILLAGER_DEFAULT = register("villager_default").withActivity(10, Activity.b).withActivity(2000, Activity.c).withActivity(9000, Activity.f).withActivity(11000, Activity.b).withActivity(12000, Activity.e).build();
    }
}
