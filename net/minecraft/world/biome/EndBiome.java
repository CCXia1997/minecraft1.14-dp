package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityCategory;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import java.util.List;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class EndBiome extends Biome
{
    public EndBiome() {
        super(new Settings().<TernarySurfaceConfig>configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.END_CONFIG).precipitation(Precipitation.NONE).category(Category.THE_END).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f).waterColor(4159204).waterFogColor(329011).parent(null));
        this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, Biome.<EndSpikeFeatureConfig, NopeDecoratorConfig>configureFeature(Feature.ay, new EndSpikeFeatureConfig(false, ImmutableList.of(), null), Decorator.NOPE, DecoratorConfig.DEFAULT));
        this.addSpawn(EntityCategory.a, new SpawnEntry(EntityType.ENDERMAN, 10, 4, 4));
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getSkyColor(final float temperature) {
        return 0;
    }
}
