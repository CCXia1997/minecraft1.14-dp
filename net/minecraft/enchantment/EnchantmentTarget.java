package net.minecraft.enchantment;

import net.minecraft.item.CrossbowItem;
import net.minecraft.block.PumpkinBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.item.ElytraItem;
import net.minecraft.block.Block;
import net.minecraft.item.BowItem;
import net.minecraft.item.TridentItem;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;

public enum EnchantmentTarget
{
    ALL {
        @Override
        public boolean isAcceptableItem(final Item item) {
            for (final EnchantmentTarget enchantmentTarget5 : EnchantmentTarget.values()) {
                if (enchantmentTarget5 != EnchantmentTarget.ALL) {
                    if (enchantmentTarget5.isAcceptableItem(item)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }, 
    ARMOR {
        @Override
        public boolean isAcceptableItem(final Item item) {
            return item instanceof ArmorItem;
        }
    }, 
    FEET {
        @Override
        public boolean isAcceptableItem(final Item item) {
            return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.FEET;
        }
    }, 
    LEGS {
        @Override
        public boolean isAcceptableItem(final Item item) {
            return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.LEGS;
        }
    }, 
    CHEST {
        @Override
        public boolean isAcceptableItem(final Item item) {
            return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.CHEST;
        }
    }, 
    HELM {
        @Override
        public boolean isAcceptableItem(final Item item) {
            return item instanceof ArmorItem && ((ArmorItem)item).getSlotType() == EquipmentSlot.HEAD;
        }
    }, 
    WEAPON {
        @Override
        public boolean isAcceptableItem(final Item item) {
            return item instanceof SwordItem;
        }
    }, 
    BREAKER {
        @Override
        public boolean isAcceptableItem(final Item item) {
            return item instanceof MiningToolItem;
        }
    }, 
    FISHING {
        @Override
        public boolean isAcceptableItem(final Item item) {
            return item instanceof FishingRodItem;
        }
    }, 
    TRIDENT {
        @Override
        public boolean isAcceptableItem(final Item item) {
            return item instanceof TridentItem;
        }
    }, 
    TOOL {
        @Override
        public boolean isAcceptableItem(final Item item) {
            return item.canDamage();
        }
    }, 
    BOW {
        @Override
        public boolean isAcceptableItem(final Item item) {
            return item instanceof BowItem;
        }
    }, 
    WEARABLE {
        @Override
        public boolean isAcceptableItem(final Item item) {
            final Block block2 = Block.getBlockFromItem(item);
            return item instanceof ArmorItem || item instanceof ElytraItem || block2 instanceof AbstractSkullBlock || block2 instanceof PumpkinBlock;
        }
    }, 
    CROSSBOW {
        @Override
        public boolean isAcceptableItem(final Item item) {
            return item instanceof CrossbowItem;
        }
    };
    
    public abstract boolean isAcceptableItem(final Item arg1);
}
