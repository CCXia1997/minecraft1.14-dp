package net.minecraft.tag;

import java.util.function.Function;
import net.minecraft.util.Identifier;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.concurrent.CompletionStage;
import com.mojang.datafixers.util.Pair;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.resource.ResourceReloadListener;

public class TagManager implements ResourceReloadListener
{
    private final RegistryTagContainer<Block> blocks;
    private final RegistryTagContainer<Item> items;
    private final RegistryTagContainer<Fluid> fluids;
    private final RegistryTagContainer<EntityType<?>> entities;
    
    public TagManager() {
        this.blocks = new RegistryTagContainer<Block>(Registry.BLOCK, "tags/blocks", "block");
        this.items = new RegistryTagContainer<Item>(Registry.ITEM, "tags/items", "item");
        this.fluids = new RegistryTagContainer<Fluid>(Registry.FLUID, "tags/fluids", "fluid");
        this.entities = new RegistryTagContainer<EntityType<?>>(Registry.ENTITY_TYPE, "tags/entity_types", "entity_type");
    }
    
    public RegistryTagContainer<Block> blocks() {
        return this.blocks;
    }
    
    public RegistryTagContainer<Item> items() {
        return this.items;
    }
    
    public RegistryTagContainer<Fluid> fluids() {
        return this.fluids;
    }
    
    public RegistryTagContainer<EntityType<?>> entities() {
        return this.entities;
    }
    
    public void clear() {
        this.blocks.clear();
        this.items.clear();
        this.fluids.clear();
        this.entities.clear();
    }
    
    public void toPacket(final PacketByteBuf buf) {
        this.blocks.toPacket(buf);
        this.items.toPacket(buf);
        this.fluids.toPacket(buf);
        this.entities.toPacket(buf);
    }
    
    public static TagManager fromPacket(final PacketByteBuf buf) {
        final TagManager tagManager2 = new TagManager();
        tagManager2.blocks().fromPacket(buf);
        tagManager2.items().fromPacket(buf);
        tagManager2.fluids().fromPacket(buf);
        tagManager2.entities().fromPacket(buf);
        return tagManager2;
    }
    
    @Override
    public CompletableFuture<Void> a(final Helper helper, final ResourceManager resourceManager, final Profiler prepareProfiler, final Profiler applyProfiler, final Executor prepareExecutor, final Executor applyExecutor) {
        final CompletableFuture<Map<Identifier, Tag.Builder<Block>>> completableFuture7 = this.blocks.prepareReload(resourceManager, prepareExecutor);
        final CompletableFuture<Map<Identifier, Tag.Builder<Item>>> completableFuture8 = this.items.prepareReload(resourceManager, prepareExecutor);
        final CompletableFuture<Map<Identifier, Tag.Builder<Fluid>>> completableFuture9 = this.fluids.prepareReload(resourceManager, prepareExecutor);
        final CompletableFuture<Map<Identifier, Tag.Builder<EntityType<?>>>> completableFuture10 = this.entities.prepareReload(resourceManager, prepareExecutor);
        return completableFuture7.thenCombine(completableFuture8, Pair::of).thenCombine(completableFuture9.thenCombine(completableFuture10, Pair::of), (pair1, pair2) -> new a((Map<Identifier, Tag.Builder<Block>>)pair1.getFirst(), (Map<Identifier, Tag.Builder<Item>>)pair1.getSecond(), (Map<Identifier, Tag.Builder<Fluid>>)pair2.getFirst(), (Map<Identifier, Tag.Builder<EntityType<?>>>)pair2.getSecond())).thenCompose(helper::waitForAll).thenAcceptAsync(a -> {
            this.clear();
            this.blocks.applyReload(a.a);
            this.items.applyReload(a.b);
            this.fluids.applyReload(a.c);
            this.entities.applyReload(a.d);
            BlockTags.setContainer(this.blocks);
            ItemTags.setContainer(this.items);
            FluidTags.setContainer(this.fluids);
            EntityTags.setContainer(this.entities);
        }, applyExecutor);
    }
    
    public static class a
    {
        final Map<Identifier, Tag.Builder<Block>> a;
        final Map<Identifier, Tag.Builder<Item>> b;
        final Map<Identifier, Tag.Builder<Fluid>> c;
        final Map<Identifier, Tag.Builder<EntityType<?>>> d;
        
        public a(final Map<Identifier, Tag.Builder<Block>> map1, final Map<Identifier, Tag.Builder<Item>> map2, final Map<Identifier, Tag.Builder<Fluid>> map3, final Map<Identifier, Tag.Builder<EntityType<?>>> map4) {
            this.a = map1;
            this.b = map2;
            this.c = map3;
            this.d = map4;
        }
    }
}
