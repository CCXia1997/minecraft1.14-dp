package net.minecraft.data.server;

import net.minecraft.tag.TagContainer;
import java.nio.file.Path;
import net.minecraft.util.Identifier;
import net.minecraft.tag.EntityTags;
import net.minecraft.util.registry.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.EntityType;

public class EntityTypeTagsProvider extends AbstractTagProvider<EntityType<?>>
{
    public EntityTypeTagsProvider(final DataGenerator dataGenerator) {
        super(dataGenerator, Registry.ENTITY_TYPE);
    }
    
    @Override
    protected void configure() {
        this.a(EntityTags.a).add(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
        this.a(EntityTags.b).add(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
    }
    
    @Override
    protected Path getOutput(final Identifier identifier) {
        return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/entity_types/" + identifier.getPath() + ".json");
    }
    
    @Override
    public String getName() {
        return "Entity Type Tags";
    }
    
    @Override
    protected void a(final TagContainer<EntityType<?>> tagContainer) {
        EntityTags.setContainer(tagContainer);
    }
}
