package net.minecraft.client.render.item;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockState;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.FilledMapItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;
import net.minecraft.item.BlockItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.potion.PotionUtil;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.GrassColorHandler;
import net.minecraft.item.Items;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.DyeableItem;
import net.minecraft.client.render.block.BlockColorMap;
import net.minecraft.util.IdList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ItemColorMap
{
    private final IdList<ItemColorMapper> mappers;
    
    public ItemColorMap() {
        this.mappers = new IdList<ItemColorMapper>(32);
    }
    
    public static ItemColorMap create(final BlockColorMap blockColorMap) {
        final ItemColorMap itemColorMap2 = new ItemColorMap();
        itemColorMap2.register((itemStack, integer) -> (integer > 0) ? -1 : ((DyeableItem)itemStack.getItem()).getColor(itemStack), Items.jR, Items.jS, Items.jT, Items.jU, Items.op);
        itemColorMap2.register((itemStack, integer) -> GrassColorHandler.getColor(0.5, 1.0), Blocks.gQ, Blocks.gR);
        CompoundTag compoundTag3;
        int[] arr4;
        int integer2;
        int integer3;
        int integer4;
        final int[] array;
        int length;
        int i = 0;
        int integer5;
        int integer6;
        int integer7;
        int integer8;
        itemColorMap2.register((itemStack, integer) -> {
            if (integer != 1) {
                return -1;
            }
            else {
                compoundTag3 = itemStack.getSubCompoundTag("Explosion");
                arr4 = (int[])((compoundTag3 != null && compoundTag3.containsKey("Colors", 11)) ? compoundTag3.getIntArray("Colors") : null);
                if (arr4 == null) {
                    return 9079434;
                }
                else if (arr4.length == 1) {
                    return arr4[0];
                }
                else {
                    integer2 = 0;
                    integer3 = 0;
                    integer4 = 0;
                    for (length = array.length; i < length; ++i) {
                        integer5 = array[i];
                        integer2 += (integer5 & 0xFF0000) >> 16;
                        integer3 += (integer5 & 0xFF00) >> 8;
                        integer4 += (integer5 & 0xFF) >> 0;
                    }
                    integer6 = integer2 / arr4.length;
                    integer7 = integer3 / arr4.length;
                    integer8 = integer4 / arr4.length;
                    return integer6 << 16 | integer7 << 8 | integer8;
                }
            }
        }, Items.nY);
        itemColorMap2.register((itemStack, integer) -> (integer > 0) ? -1 : PotionUtil.getColor(itemStack), Items.ml, Items.oS, Items.oV);
        for (final SpawnEggItem spawnEggItem4 : SpawnEggItem.iterator()) {
            itemColorMap2.register((itemStack, integer) -> spawnEggItem4.getColor(integer), spawnEggItem4);
        }
        final BlockState blockState4;
        itemColorMap2.register((itemStack, integer) -> {
            blockState4 = ((BlockItem)itemStack.getItem()).getBlock().getDefaultState();
            return blockColorMap.getRenderColor(blockState4, null, null, integer);
        }, Blocks.i, Blocks.aQ, Blocks.aR, Blocks.dH, Blocks.ag, Blocks.ah, Blocks.ai, Blocks.aj, Blocks.ak, Blocks.al, Blocks.dM);
        itemColorMap2.register((itemStack, integer) -> (integer == 0) ? PotionUtil.getColor(itemStack) : -1, Items.oU);
        itemColorMap2.register((itemStack, integer) -> (integer == 0) ? -1 : FilledMapItem.j(itemStack), Items.lV);
        return itemColorMap2;
    }
    
    public int getRenderColor(final ItemStack item, final int integer) {
        final ItemColorMapper itemColorMapper3 = this.mappers.get(Registry.ITEM.getRawId(item.getItem()));
        return (itemColorMapper3 == null) ? -1 : itemColorMapper3.getColor(item, integer);
    }
    
    public void register(final ItemColorMapper mapper, final ItemProvider... arr) {
        for (final ItemProvider itemProvider6 : arr) {
            this.mappers.set(mapper, Item.getRawIdByItem(itemProvider6.getItem()));
        }
    }
}
