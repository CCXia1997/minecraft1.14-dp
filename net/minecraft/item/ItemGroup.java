package net.minecraft.item;

import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.block.Blocks;
import java.util.Iterator;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.DefaultedList;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentTarget;

public abstract class ItemGroup
{
    public static final ItemGroup[] GROUPS;
    public static final ItemGroup BUILDING_BLOCKS;
    public static final ItemGroup DECORATIONS;
    public static final ItemGroup REDSTONE;
    public static final ItemGroup TRANSPORTATION;
    public static final ItemGroup MISC;
    public static final ItemGroup SEARCH;
    public static final ItemGroup FOOD;
    public static final ItemGroup TOOLS;
    public static final ItemGroup COMBAT;
    public static final ItemGroup BREWING;
    public static final ItemGroup MATERIALS;
    public static final ItemGroup HOTBAR;
    public static final ItemGroup INVENTORY;
    private final int index;
    private final String id;
    private String name;
    private String texture;
    private boolean scrollbar;
    private boolean tooltip;
    private EnchantmentTarget[] enchantments;
    private ItemStack icon;
    
    public ItemGroup(final int index, final String id) {
        this.texture = "items.png";
        this.scrollbar = true;
        this.tooltip = true;
        this.enchantments = new EnchantmentTarget[0];
        this.index = index;
        this.id = id;
        this.icon = ItemStack.EMPTY;
        ItemGroup.GROUPS[index] = this;
    }
    
    @Environment(EnvType.CLIENT)
    public int getIndex() {
        return this.index;
    }
    
    @Environment(EnvType.CLIENT)
    public String getId() {
        return this.id;
    }
    
    public String getName() {
        return (this.name == null) ? this.id : this.name;
    }
    
    @Environment(EnvType.CLIENT)
    public String getTranslationKey() {
        return "itemGroup." + this.getId();
    }
    
    @Environment(EnvType.CLIENT)
    public ItemStack getIcon() {
        if (this.icon.isEmpty()) {
            this.icon = this.createIcon();
        }
        return this.icon;
    }
    
    @Environment(EnvType.CLIENT)
    public abstract ItemStack createIcon();
    
    @Environment(EnvType.CLIENT)
    public String getTexture() {
        return this.texture;
    }
    
    public ItemGroup setTexture(final String texture) {
        this.texture = texture;
        return this;
    }
    
    public ItemGroup setName(final String name) {
        this.name = name;
        return this;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean hasTooltip() {
        return this.tooltip;
    }
    
    public ItemGroup setNoTooltip() {
        this.tooltip = false;
        return this;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean hasScrollbar() {
        return this.scrollbar;
    }
    
    public ItemGroup setNoScrollbar() {
        this.scrollbar = false;
        return this;
    }
    
    @Environment(EnvType.CLIENT)
    public int getColumn() {
        return this.index % 6;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isTopRow() {
        return this.index < 6;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean isSpecial() {
        return this.getColumn() == 5;
    }
    
    public EnchantmentTarget[] getEnchantments() {
        return this.enchantments;
    }
    
    public ItemGroup setEnchantments(final EnchantmentTarget... targets) {
        this.enchantments = targets;
        return this;
    }
    
    public boolean containsEnchantments(@Nullable final EnchantmentTarget target) {
        if (target != null) {
            for (final EnchantmentTarget enchantmentTarget5 : this.enchantments) {
                if (enchantmentTarget5 == target) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    public void appendItems(final DefaultedList<ItemStack> stacks) {
        for (final Item item3 : Registry.ITEM) {
            item3.appendItemsForGroup(this, stacks);
        }
    }
    
    static {
        GROUPS = new ItemGroup[12];
        BUILDING_BLOCKS = new ItemGroup(0, "buildingBlocks") {
            @Environment(EnvType.CLIENT)
            @Override
            public ItemStack createIcon() {
                return new ItemStack(Blocks.bF);
            }
        }.setName("building_blocks");
        DECORATIONS = new ItemGroup(1, "decorations") {
            @Environment(EnvType.CLIENT)
            @Override
            public ItemStack createIcon() {
                return new ItemStack(Blocks.gP);
            }
        };
        REDSTONE = new ItemGroup(2, "redstone") {
            @Environment(EnvType.CLIENT)
            @Override
            public ItemStack createIcon() {
                return new ItemStack(Items.kC);
            }
        };
        TRANSPORTATION = new ItemGroup(3, "transportation") {
            @Environment(EnvType.CLIENT)
            @Override
            public ItemStack createIcon() {
                return new ItemStack(Blocks.aM);
            }
        };
        MISC = new ItemGroup(6, "misc") {
            @Environment(EnvType.CLIENT)
            @Override
            public ItemStack createIcon() {
                return new ItemStack(Items.kz);
            }
        };
        SEARCH = new ItemGroup(5, "search") {
            @Environment(EnvType.CLIENT)
            @Override
            public ItemStack createIcon() {
                return new ItemStack(Items.kX);
            }
        }.setTexture("item_search.png");
        FOOD = new ItemGroup(7, "food") {
            @Environment(EnvType.CLIENT)
            @Override
            public ItemStack createIcon() {
                return new ItemStack(Items.je);
            }
        };
        TOOLS = new ItemGroup(8, "tools") {
            @Environment(EnvType.CLIENT)
            @Override
            public ItemStack createIcon() {
                return new ItemStack(Items.jc);
            }
        }.setEnchantments(EnchantmentTarget.ALL, EnchantmentTarget.BREAKER, EnchantmentTarget.FISHING, EnchantmentTarget.TOOL);
        COMBAT = new ItemGroup(9, "combat") {
            @Environment(EnvType.CLIENT)
            @Override
            public ItemStack createIcon() {
                return new ItemStack(Items.jC);
            }
        }.setEnchantments(EnchantmentTarget.ALL, EnchantmentTarget.ARMOR, EnchantmentTarget.FEET, EnchantmentTarget.HELM, EnchantmentTarget.LEGS, EnchantmentTarget.CHEST, EnchantmentTarget.BOW, EnchantmentTarget.WEAPON, EnchantmentTarget.WEARABLE, EnchantmentTarget.TOOL, EnchantmentTarget.TRIDENT, EnchantmentTarget.CROSSBOW);
        BREWING = new ItemGroup(10, "brewing") {
            @Environment(EnvType.CLIENT)
            @Override
            public ItemStack createIcon() {
                return PotionUtil.setPotion(new ItemStack(Items.ml), Potions.b);
            }
        };
        MATERIALS = ItemGroup.MISC;
        HOTBAR = new ItemGroup(4, "hotbar") {
            @Environment(EnvType.CLIENT)
            @Override
            public ItemStack createIcon() {
                return new ItemStack(Blocks.bH);
            }
            
            @Environment(EnvType.CLIENT)
            @Override
            public void appendItems(final DefaultedList<ItemStack> stacks) {
                throw new RuntimeException("Implement exception client-side.");
            }
            
            @Environment(EnvType.CLIENT)
            @Override
            public boolean isSpecial() {
                return true;
            }
        };
        INVENTORY = new ItemGroup(11, "inventory") {
            @Environment(EnvType.CLIENT)
            @Override
            public ItemStack createIcon() {
                return new ItemStack(Blocks.bP);
            }
        }.setTexture("inventory.png").setNoScrollbar().setNoTooltip();
    }
}
