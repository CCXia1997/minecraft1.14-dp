package net.minecraft.data.server;

import net.minecraft.tag.TagContainer;
import java.nio.file.Path;
import net.minecraft.util.Identifier;
import net.minecraft.fluid.Fluids;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.registry.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.fluid.Fluid;

public class FluidTagsProvider extends AbstractTagProvider<Fluid>
{
    public FluidTagsProvider(final DataGenerator dataGenerator) {
        super(dataGenerator, Registry.FLUID);
    }
    
    @Override
    protected void configure() {
        this.a(FluidTags.a).add(Fluids.WATER, Fluids.FLOWING_WATER);
        this.a(FluidTags.b).add(Fluids.LAVA, Fluids.FLOWING_LAVA);
    }
    
    @Override
    protected Path getOutput(final Identifier identifier) {
        return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/fluids/" + identifier.getPath() + ".json");
    }
    
    @Override
    public String getName() {
        return "Fluid Tags";
    }
    
    @Override
    protected void a(final TagContainer<Fluid> tagContainer) {
        FluidTags.setContainer(tagContainer);
    }
}
