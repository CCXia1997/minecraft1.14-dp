package net.minecraft.entity.ai.brain.sensor;

import net.minecraft.util.registry.Registry;
import net.minecraft.util.Identifier;
import java.util.function.Supplier;

public class SensorType<U extends Sensor<?>>
{
    public static final SensorType<DummySensor> a;
    public static final SensorType<NearestLivingEntitiesSensor> b;
    public static final SensorType<NearestPlayersSensor> c;
    public static final SensorType<InteractableDoorsSensor> d;
    public static final SensorType<NearestBedSensor> e;
    public static final SensorType<HurtBySensor> f;
    public static final SensorType<VillagerHostilesSensor> g;
    public static final SensorType<VillagerBabiesSensor> h;
    public static final SensorType<SecondaryPointsOfInterestSensor> i;
    private final Supplier<U> factory;
    private final Identifier id;
    
    private SensorType(final Supplier<U> supplier, final String id) {
        this.factory = supplier;
        this.id = new Identifier(id);
    }
    
    public U create() {
        return this.factory.get();
    }
    
    private static <U extends Sensor<?>> SensorType<U> register(final String id, final Supplier<U> supplier) {
        return Registry.<SensorType<U>>register(Registry.SENSOR_TYPE, new Identifier(id), new SensorType<U>(supplier, id));
    }
    
    static {
        a = SensorType.<DummySensor>register("dummy", DummySensor::new);
        b = SensorType.<NearestLivingEntitiesSensor>register("nearest_living_entities", NearestLivingEntitiesSensor::new);
        c = SensorType.<NearestPlayersSensor>register("nearest_players", NearestPlayersSensor::new);
        d = SensorType.<InteractableDoorsSensor>register("interactable_doors", InteractableDoorsSensor::new);
        e = SensorType.<NearestBedSensor>register("nearest_bed", NearestBedSensor::new);
        f = SensorType.<HurtBySensor>register("hurt_by", HurtBySensor::new);
        g = SensorType.<VillagerHostilesSensor>register("villager_hostiles", VillagerHostilesSensor::new);
        h = SensorType.<VillagerBabiesSensor>register("villager_babies", VillagerBabiesSensor::new);
        i = SensorType.<SecondaryPointsOfInterestSensor>register("secondary_pois", SecondaryPointsOfInterestSensor::new);
    }
}
