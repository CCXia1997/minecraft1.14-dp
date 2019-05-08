package net.minecraft.data.server;

import org.apache.logging.log4j.LogManager;
import net.minecraft.tag.TagContainer;
import java.nio.file.Path;
import net.minecraft.util.Identifier;
import java.util.List;
import java.util.Collection;
import com.google.common.collect.Lists;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.tag.Tag;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.registry.Registry;
import net.minecraft.data.DataGenerator;
import org.apache.logging.log4j.Logger;
import net.minecraft.item.Item;

public class ItemTagsProvider extends AbstractTagProvider<Item>
{
    private static final Logger LOG;
    
    public ItemTagsProvider(final DataGenerator dataGenerator) {
        super(dataGenerator, Registry.ITEM);
    }
    
    @Override
    protected void configure() {
        this.copy(BlockTags.a, ItemTags.a);
        this.copy(BlockTags.b, ItemTags.b);
        this.copy(BlockTags.c, ItemTags.c);
        this.copy(BlockTags.d, ItemTags.d);
        this.copy(BlockTags.e, ItemTags.e);
        this.copy(BlockTags.f, ItemTags.f);
        this.copy(BlockTags.g, ItemTags.g);
        this.copy(BlockTags.h, ItemTags.h);
        this.copy(BlockTags.i, ItemTags.i);
        this.copy(BlockTags.j, ItemTags.j);
        this.copy(BlockTags.k, ItemTags.k);
        this.copy(BlockTags.m, ItemTags.m);
        this.copy(BlockTags.n, ItemTags.n);
        this.copy(BlockTags.q, ItemTags.q);
        this.copy(BlockTags.p, ItemTags.p);
        this.copy(BlockTags.r, ItemTags.r);
        this.copy(BlockTags.s, ItemTags.s);
        this.copy(BlockTags.u, ItemTags.u);
        this.copy(BlockTags.t, ItemTags.t);
        this.copy(BlockTags.o, ItemTags.o);
        this.copy(BlockTags.w, ItemTags.w);
        this.copy(BlockTags.y, ItemTags.y);
        this.copy(BlockTags.z, ItemTags.z);
        this.copy(BlockTags.x, ItemTags.x);
        this.copy(BlockTags.A, ItemTags.A);
        this.copy(BlockTags.B, ItemTags.B);
        this.copy(BlockTags.C, ItemTags.C);
        this.copy(BlockTags.l, ItemTags.l);
        this.copy(BlockTags.D, ItemTags.D);
        this.copy(BlockTags.E, ItemTags.E);
        this.copy(BlockTags.F, ItemTags.F);
        this.copy(BlockTags.G, ItemTags.G);
        this.a(ItemTags.v).add(Items.ov, Items.ow, Items.ox, Items.oy, Items.oz, Items.oA, Items.oB, Items.oC, Items.oD, Items.oE, Items.oF, Items.oG, Items.oH, Items.oI, Items.oJ, Items.oK);
        this.a(ItemTags.H).add(Items.kE, Items.oY, Items.oZ, Items.pa, Items.pb, Items.pc);
        this.a(ItemTags.I).add(Items.lb, Items.lf, Items.lc, Items.lg, Items.le, Items.ld);
        this.copy(BlockTags.T, ItemTags.J);
        this.a(ItemTags.K).add(Items.pi, Items.pj, Items.pk, Items.pl, Items.pm, Items.pn, Items.po, Items.pp, Items.pq, Items.pr, Items.ps, Items.pt);
        this.a(ItemTags.L).add(Items.jh, Items.ji);
        this.a(ItemTags.M).add(Items.jg, Items.oU, Items.oT);
    }
    
    protected void copy(final Tag<Block> tag1, final Tag<Item> tag2) {
        final Tag.Builder<Item> builder3 = this.a(tag2);
        for (final Tag.Entry<Block> entry5 : tag1.entries()) {
            final Tag.Entry<Item> entry6 = this.convert(entry5);
            builder3.add(entry6);
        }
    }
    
    private Tag.Entry<Item> convert(final Tag.Entry<Block> entry) {
        if (entry instanceof Tag.TagEntry) {
            return new Tag.TagEntry<Item>(((Tag.TagEntry)entry).getId());
        }
        if (entry instanceof Tag.CollectionEntry) {
            final List<Item> list2 = Lists.newArrayList();
            for (final Block block4 : ((Tag.CollectionEntry)entry).getValues()) {
                final Item item5 = block4.getItem();
                if (item5 == Items.AIR) {
                    ItemTagsProvider.LOG.warn("Itemless block copied to item tag: {}", Registry.BLOCK.getId(block4));
                }
                else {
                    list2.add(item5);
                }
            }
            return new Tag.CollectionEntry<Item>(list2);
        }
        throw new UnsupportedOperationException("Unknown tag entry " + entry);
    }
    
    @Override
    protected Path getOutput(final Identifier identifier) {
        return this.root.getOutput().resolve("data/" + identifier.getNamespace() + "/tags/items/" + identifier.getPath() + ".json");
    }
    
    @Override
    public String getName() {
        return "Item Tags";
    }
    
    @Override
    protected void a(final TagContainer<Item> tagContainer) {
        ItemTags.setContainer(tagContainer);
    }
    
    static {
        LOG = LogManager.getLogger();
    }
}
