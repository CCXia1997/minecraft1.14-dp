package net.minecraft.world.timer;

import org.apache.logging.log4j.LogManager;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.Iterator;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.CompoundTag;
import com.google.common.collect.Maps;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Map;
import com.google.common.primitives.UnsignedLong;
import java.util.Queue;
import org.apache.logging.log4j.Logger;

public class Timer<T>
{
    private static final Logger LOGGER;
    private final TimerCallbackSerializer<T> callback;
    private final Queue<Event<T>> events;
    private UnsignedLong eventCounter;
    private final Map<String, Event<T>> eventsByName;
    
    private static <T> Comparator<Event<T>> createEventComparator() {
        final int integer3;
        return (event1, event2) -> {
            integer3 = Long.compare(event1.triggerTime, event2.triggerTime);
            if (integer3 != 0) {
                return integer3;
            }
            else {
                return event1.id.compareTo(event2.id);
            }
        };
    }
    
    public Timer(final TimerCallbackSerializer<T> timerCallbackSerializer) {
        this.events = new PriorityQueue<Event<T>>(Timer.createEventComparator());
        this.eventCounter = UnsignedLong.ZERO;
        this.eventsByName = Maps.newHashMap();
        this.callback = timerCallbackSerializer;
    }
    
    public void processEvents(final T server, final long time) {
        while (true) {
            final Event<T> event4 = this.events.peek();
            if (event4 == null || event4.triggerTime > time) {
                break;
            }
            this.events.remove();
            this.eventsByName.remove(event4.name);
            event4.callback.call(server, this, time);
        }
    }
    
    private void setEvent(final String name, final long triggerTime, final TimerCallback<T> callback) {
        this.eventCounter = this.eventCounter.plus(UnsignedLong.ONE);
        final Event<T> event5 = new Event<T>(triggerTime, this.eventCounter, name, (TimerCallback)callback);
        this.eventsByName.put(name, event5);
        this.events.add(event5);
    }
    
    public boolean addEvent(final String string, final long long2, final TimerCallback<T> timerCallback4) {
        if (this.eventsByName.containsKey(string)) {
            return false;
        }
        this.setEvent(string, long2, timerCallback4);
        return true;
    }
    
    public void replaceEvent(final String name, final long triggerTime, final TimerCallback<T> callback) {
        final Event<T> event5 = this.eventsByName.remove(name);
        if (event5 != null) {
            this.events.remove(event5);
        }
        this.setEvent(name, triggerTime, callback);
    }
    
    private void addEvent(final CompoundTag tag) {
        final CompoundTag compoundTag2 = tag.getCompound("Callback");
        final TimerCallback<T> timerCallback3 = this.callback.deserialize(compoundTag2);
        if (timerCallback3 != null) {
            final String string4 = tag.getString("Name");
            final long long5 = tag.getLong("TriggerTime");
            this.addEvent(string4, long5, timerCallback3);
        }
    }
    
    public void fromTag(final ListTag tag) {
        this.events.clear();
        this.eventsByName.clear();
        this.eventCounter = UnsignedLong.ZERO;
        if (tag.isEmpty()) {
            return;
        }
        if (tag.getListType() != 10) {
            Timer.LOGGER.warn("Invalid format of events: " + tag);
            return;
        }
        for (final Tag tag2 : tag) {
            this.addEvent((CompoundTag)tag2);
        }
    }
    
    private CompoundTag serialize(final Event<T> event) {
        final CompoundTag compoundTag2 = new CompoundTag();
        compoundTag2.putString("Name", event.name);
        compoundTag2.putLong("TriggerTime", event.triggerTime);
        compoundTag2.put("Callback", this.callback.<TimerCallback<T>>serialize(event.callback));
        return compoundTag2;
    }
    
    public ListTag toTag() {
        final ListTag listTag1 = new ListTag();
        this.events.stream().sorted(Timer.createEventComparator()).map(this::serialize).forEach(listTag1::add);
        return listTag1;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    public static class Event<T>
    {
        public final long triggerTime;
        public final UnsignedLong id;
        public final String name;
        public final TimerCallback<T> callback;
        
        private Event(final long triggerTime, final UnsignedLong id, final String name, final TimerCallback<T> callback) {
            this.triggerTime = triggerTime;
            this.id = id;
            this.name = name;
            this.callback = callback;
        }
    }
}
