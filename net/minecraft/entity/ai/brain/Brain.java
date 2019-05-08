package net.minecraft.entity.ai.brain;

import com.google.common.collect.ImmutableMap;
import java.util.stream.Collectors;
import net.minecraft.server.world.ServerWorld;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.datafixers.NbtOps;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.Iterator;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.util.function.Function;
import com.google.common.collect.Sets;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import java.util.Collection;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.ai.brain.task.Task;
import java.util.Set;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import java.util.Optional;
import java.util.Map;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.entity.LivingEntity;

public class Brain<E extends LivingEntity> implements DynamicSerializable
{
    private final Map<MemoryModuleType<?>, Optional<?>> memories;
    private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors;
    private final Map<Integer, Map<Activity, Set<Task<? super E>>>> tasks;
    private Schedule schedule;
    private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryModuleState>>> requiredActivityMemories;
    private Set<Activity> coreActivities;
    private final Set<Activity> possibleActivities;
    private Activity defaultActivity;
    private long activityStartTime;
    
    public <T> Brain(final Collection<MemoryModuleType<?>> collection1, final Collection<SensorType<? extends Sensor<? super E>>> sensors, final Dynamic<T> dynamic) {
        this.memories = Maps.newHashMap();
        this.sensors = Maps.newLinkedHashMap();
        this.tasks = Maps.newTreeMap();
        this.schedule = Schedule.EMPTY;
        this.requiredActivityMemories = Maps.newHashMap();
        this.coreActivities = Sets.newHashSet();
        this.possibleActivities = Sets.newHashSet();
        this.defaultActivity = Activity.b;
        this.activityStartTime = -9999L;
        final Optional<?> optional;
        collection1.forEach(memoryModuleType -> optional = this.memories.put(memoryModuleType, Optional.empty()));
        final Sensor<? super E> sensor2;
        sensors.forEach(sensorType -> sensor2 = this.sensors.put(sensorType, sensorType.create()));
        final Iterator<MemoryModuleType<?>> iterator;
        MemoryModuleType<?> memoryModuleType2;
        this.sensors.values().forEach(sensor -> {
            sensor.getOutputMemoryModules().iterator();
            while (iterator.hasNext()) {
                memoryModuleType2 = iterator.next();
                this.memories.put(memoryModuleType2, Optional.empty());
            }
            return;
        });
        for (final Map.Entry<Dynamic<T>, Dynamic<T>> entry5 : dynamic.get("memories").asMap((Function)Function.identity(), (Function)Function.identity()).entrySet()) {
            this.<T, Object>readMemory(Registry.MEMORY_MODULE_TYPE.get(new Identifier(entry5.getKey().asString(""))), entry5.getValue());
        }
    }
    
    public boolean hasMemoryModule(final MemoryModuleType<?> memoryModuleType) {
        return this.isMemoryInState(memoryModuleType, MemoryModuleState.a);
    }
    
    private <T, U> void readMemory(final MemoryModuleType<U> memoryModuleType, final Dynamic<T> dynamic) {
        this.<U>putMemory(memoryModuleType, memoryModuleType.getFactory().<Throwable>orElseThrow(RuntimeException::new).apply(dynamic));
    }
    
    public void forget(final MemoryModuleType<?> memoryModuleType) {
        this.setMemory(memoryModuleType, Optional.empty());
    }
    
    public <U> void putMemory(final MemoryModuleType<U> memoryModuleType, @Nullable final U value) {
        this.<U>setMemory(memoryModuleType, Optional.ofNullable((U)value));
    }
    
    public <U> void setMemory(final MemoryModuleType<U> memoryModuleType, final Optional<U> value) {
        if (this.memories.containsKey(memoryModuleType)) {
            if (value.isPresent() && this.isEmptyCollection(value.get())) {
                this.forget(memoryModuleType);
            }
            else {
                this.memories.put(memoryModuleType, value);
            }
        }
    }
    
    public <U> Optional<U> getOptionalMemory(final MemoryModuleType<U> memoryModuleType) {
        return (Optional<U>)this.memories.get(memoryModuleType);
    }
    
    public boolean isMemoryInState(final MemoryModuleType<?> memoryModuleType, final MemoryModuleState state) {
        final Optional<?> optional3 = this.memories.get(memoryModuleType);
        return optional3 != null && (state == MemoryModuleState.c || (state == MemoryModuleState.a && optional3.isPresent()) || (state == MemoryModuleState.b && !optional3.isPresent()));
    }
    
    public Schedule getSchedule() {
        return this.schedule;
    }
    
    public void setSchedule(final Schedule schedule) {
        this.schedule = schedule;
    }
    
    public void setCoreActivities(final Set<Activity> set) {
        this.coreActivities = set;
    }
    
    @Deprecated
    public Stream<Task<? super E>> streamRunningTasks() {
        return this.tasks.values().stream().flatMap(map -> map.values().stream()).<Task<? super E>>flatMap(Collection::stream).filter(task -> task.getStatus() == Task.Status.b);
    }
    
    public void resetPossibleActivities(final Activity activity) {
        this.possibleActivities.clear();
        this.possibleActivities.addAll(this.coreActivities);
        final boolean boolean2 = this.requiredActivityMemories.keySet().contains(activity) && this.canDoActivity(activity);
        this.possibleActivities.add(boolean2 ? activity : this.defaultActivity);
    }
    
    public void refreshActivities(final long timeOfDay, final long time) {
        if (time - this.activityStartTime > 20L) {
            this.activityStartTime = time;
            final Activity activity5 = this.getSchedule().getActivityForTime((int)(timeOfDay % 24000L));
            if (!this.possibleActivities.contains(activity5)) {
                this.resetPossibleActivities(activity5);
            }
        }
    }
    
    public void setDefaultActivity(final Activity activity) {
        this.defaultActivity = activity;
    }
    
    public void setTaskList(final Activity activity, final ImmutableList<Pair<Integer, ? extends Task<? super E>>> immutableList) {
        this.setTaskList(activity, immutableList, ImmutableSet.of());
    }
    
    public void setTaskList(final Activity activity, final ImmutableList<Pair<Integer, ? extends Task<? super E>>> immutableList, final Set<Pair<MemoryModuleType<?>, MemoryModuleState>> set) {
        this.requiredActivityMemories.put(activity, set);
        immutableList.forEach(pair -> this.tasks.computeIfAbsent((Integer)pair.getFirst(), integer -> Maps.newHashMap()).computeIfAbsent(activity, activity -> Sets.newLinkedHashSet()).add(pair.getSecond()));
    }
    
    public boolean hasActivity(final Activity activity) {
        return this.possibleActivities.contains(activity);
    }
    
    public Brain<E> copy() {
        final Brain<E> brain1 = new Brain<E>(this.memories.keySet(), this.sensors.keySet(), (Dynamic<T>)new Dynamic((DynamicOps)NbtOps.INSTANCE, new CompoundTag()));
        final Optional<?> optional2;
        this.memories.forEach((memoryModuleType, optional) -> optional.ifPresent(object -> optional2 = brain1.memories.put(memoryModuleType, Optional.of(object))));
        return brain1;
    }
    
    public void tick(final ServerWorld serverWorld, final E livingEntity) {
        this.updateSensors(serverWorld, livingEntity);
        this.startTasks(serverWorld, livingEntity);
        this.updateTasks(serverWorld, livingEntity);
    }
    
    public void stopAllTasks(final ServerWorld serverWorld, final E livingEntity) {
        final long long3 = livingEntity.world.getTime();
        this.streamRunningTasks().forEach(task5 -> task5.stop(serverWorld, livingEntity, long3));
    }
    
    @Override
    public <T> T serialize(final DynamicOps<T> ops) {
        final T object2 = (T)ops.createMap((Map)this.memories.entrySet().stream().filter(entry -> entry.getKey().getFactory().isPresent() && ((Optional)entry.getValue()).isPresent()).map(entry -> Pair.of(ops.createString(entry.getKey().getId().toString()), ((DynamicSerializable)((Optional)entry.getValue()).get()).<T>serialize(ops))).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
        return (T)ops.createMap((Map)ImmutableMap.<Object, T>of(ops.createString("memories"), object2));
    }
    
    private void updateSensors(final ServerWorld serverWorld, final E livingEntity) {
        this.sensors.values().forEach(sensor -> sensor.canSense(serverWorld, livingEntity));
    }
    
    private void startTasks(final ServerWorld serverWorld, final E livingEntity) {
        final long long3 = serverWorld.getTime();
        this.tasks.values().stream().flatMap(map -> map.entrySet().stream()).filter(entry -> this.possibleActivities.contains(entry.getKey())).map(Map.Entry::getValue).flatMap(Collection::stream).filter(task -> task.getStatus() == Task.Status.a).forEach(task5 -> task5.tryStarting(serverWorld, livingEntity, long3));
    }
    
    private void updateTasks(final ServerWorld serverWorld, final E livingEntity) {
        final long long3 = serverWorld.getTime();
        this.streamRunningTasks().forEach(task5 -> task5.tick(serverWorld, livingEntity, long3));
    }
    
    private boolean canDoActivity(final Activity activity) {
        final MemoryModuleType<?> memoryModuleType2;
        final MemoryModuleState memoryModuleState3;
        return this.requiredActivityMemories.get(activity).stream().allMatch(pair -> {
            memoryModuleType2 = pair.getFirst();
            memoryModuleState3 = (MemoryModuleState)pair.getSecond();
            return this.isMemoryInState(memoryModuleType2, memoryModuleState3);
        });
    }
    
    private boolean isEmptyCollection(final Object value) {
        return value instanceof Collection && ((Collection)value).isEmpty();
    }
}
