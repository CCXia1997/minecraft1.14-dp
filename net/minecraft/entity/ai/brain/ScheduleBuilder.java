package net.minecraft.entity.ai.brain;

import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Set;
import com.google.common.collect.Lists;
import java.util.List;

public class ScheduleBuilder
{
    private final Schedule schedule;
    private final List<ActivityEntry> activities;
    
    public ScheduleBuilder(final Schedule schedule) {
        this.activities = Lists.newArrayList();
        this.schedule = schedule;
    }
    
    public ScheduleBuilder withActivity(final int startTime, final Activity activity) {
        this.activities.add(new ActivityEntry(startTime, activity));
        return this;
    }
    
    public Schedule build() {
        this.activities.stream().map(ActivityEntry::getActivity).collect(Collectors.toSet()).forEach(this.schedule::addActivity);
        final Activity activity2;
        this.activities.forEach(activityEntry -> {
            activity2 = activityEntry.getActivity();
            this.schedule.getOtherRules(activity2).forEach(scheduleRule -> scheduleRule.withEntry(activityEntry.getStartTime(), 0.0f));
            this.schedule.getRule(activity2).withEntry(activityEntry.getStartTime(), 1.0f);
            return;
        });
        return this.schedule;
    }
    
    static class ActivityEntry
    {
        private final int startTime;
        private final Activity activity;
        
        public ActivityEntry(final int startTime, final Activity activity) {
            this.startTime = startTime;
            this.activity = activity;
        }
        
        public int getStartTime() {
            return this.startTime;
        }
        
        public Activity getActivity() {
            return this.activity;
        }
    }
}
