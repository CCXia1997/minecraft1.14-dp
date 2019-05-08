package net.minecraft.world.gen.feature;

import java.util.Map;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.math.BlockPos;
import java.util.Optional;

public class EndGatewayFeatureConfig implements FeatureConfig
{
    private final Optional<BlockPos> exitPos;
    private final boolean exact;
    
    private EndGatewayFeatureConfig(final Optional<BlockPos> exitPos, final boolean exact) {
        this.exitPos = exitPos;
        this.exact = exact;
    }
    
    public static EndGatewayFeatureConfig createConfig(final BlockPos exitPortalPosition, final boolean exitsAtSpawn) {
        return new EndGatewayFeatureConfig(Optional.<BlockPos>of(exitPortalPosition), exitsAtSpawn);
    }
    
    public static EndGatewayFeatureConfig createConfig() {
        return new EndGatewayFeatureConfig(Optional.<BlockPos>empty(), false);
    }
    
    public Optional<BlockPos> getExitPos() {
        return this.exitPos;
    }
    
    public boolean isExact() {
        return this.exact;
    }
    
    @Override
    public <T> Dynamic<T> serialize(final DynamicOps<T> ops) {
        return (Dynamic<T>)new Dynamic((DynamicOps)ops, this.exitPos.map(blockPos -> ops.createMap((Map)ImmutableMap.of(ops.createString("exit_x"), ops.createInt(blockPos.getX()), ops.createString("exit_y"), ops.createInt(blockPos.getY()), ops.createString("exit_z"), ops.createInt(blockPos.getZ()), ops.createString("exact"), ops.createBoolean(this.exact)))).orElse(ops.emptyMap()));
    }
    
    public static <T> EndGatewayFeatureConfig deserialize(final Dynamic<T> dynamic) {
        final Optional<BlockPos> optional2 = dynamic.get("exit_x").asNumber().<BlockPos>flatMap(number -> dynamic.get("exit_y").asNumber().flatMap(number3 -> dynamic.get("exit_z").asNumber().map(number3 -> new BlockPos(number.intValue(), number3.intValue(), number3.intValue()))));
        final boolean boolean3 = dynamic.get("exact").asBoolean(false);
        return new EndGatewayFeatureConfig(optional2, boolean3);
    }
}
