package net.minecraft.entity.ai.brain;

import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import java.util.Collection;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import com.google.common.collect.Lists;
import java.util.List;

public class ScheduleRule
{
    private final List<ScheduleRuleEntry> entries;
    private int b;
    
    public ScheduleRule() {
        this.entries = Lists.newArrayList();
    }
    
    public ScheduleRule withEntry(final int startTime, final float priority) {
        this.entries.add(new ScheduleRuleEntry(startTime, priority));
        this.sort();
        return this;
    }
    
    private void sort() {
        final Int2ObjectSortedMap<ScheduleRuleEntry> int2ObjectSortedMap1 = (Int2ObjectSortedMap<ScheduleRuleEntry>)new Int2ObjectAVLTreeMap();
        final ScheduleRuleEntry scheduleRuleEntry2;
        this.entries.forEach(scheduleRuleEntry -> scheduleRuleEntry2 = (ScheduleRuleEntry)int2ObjectSortedMap1.put(scheduleRuleEntry.getStartTime(), scheduleRuleEntry));
        this.entries.clear();
        this.entries.addAll(int2ObjectSortedMap1.values());
        this.b = 0;
    }
    
    public float getPriority(final int time) {
        if (this.entries.size() <= 0) {
            return 0.0f;
        }
        final ScheduleRuleEntry scheduleRuleEntry2 = this.entries.get(this.b);
        final ScheduleRuleEntry scheduleRuleEntry3 = this.entries.get(this.entries.size() - 1);
        final boolean boolean4 = time < scheduleRuleEntry2.getStartTime();
        final int integer5 = boolean4 ? 0 : this.b;
        float float6 = boolean4 ? scheduleRuleEntry3.getPriority() : scheduleRuleEntry2.getPriority();
        for (int integer6 = integer5; integer6 < this.entries.size(); ++integer6) {
            final ScheduleRuleEntry scheduleRuleEntry4 = this.entries.get(integer6);
            if (scheduleRuleEntry4.getStartTime() > time) {
                break;
            }
            this.b = integer6;
            float6 = scheduleRuleEntry4.getPriority();
        }
        return float6;
    }
}
