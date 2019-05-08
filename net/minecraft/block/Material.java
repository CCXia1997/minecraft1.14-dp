package net.minecraft.block;

import net.minecraft.block.piston.PistonBehavior;

public final class Material
{
    public static final Material AIR;
    public static final Material STRUCTURE_VOID;
    public static final Material PORTAL;
    public static final Material CARPET;
    public static final Material PLANT;
    public static final Material UNDERWATER_PLANT;
    public static final Material REPLACEABLE_PLANT;
    public static final Material SEAGRASS;
    public static final Material WATER;
    public static final Material BUBBLE_COLUMN;
    public static final Material LAVA;
    public static final Material SNOW;
    public static final Material FIRE;
    public static final Material PART;
    public static final Material COBWEB;
    public static final Material REDSTONE_LAMP;
    public static final Material CLAY;
    public static final Material EARTH;
    public static final Material ORGANIC;
    public static final Material PACKED_ICE;
    public static final Material SAND;
    public static final Material SPONGE;
    public static final Material SHULKER_BOX;
    public static final Material WOOD;
    public static final Material BAMBOO_SAPLING;
    public static final Material BAMBOO;
    public static final Material WOOL;
    public static final Material TNT;
    public static final Material LEAVES;
    public static final Material GLASS;
    public static final Material ICE;
    public static final Material CACTUS;
    public static final Material STONE;
    public static final Material METAL;
    public static final Material SNOW_BLOCK;
    public static final Material ANVIL;
    public static final Material BARRIER;
    public static final Material PISTON;
    public static final Material UNUSED_PLANT;
    public static final Material PUMPKIN;
    public static final Material DRAGON_EGG;
    public static final Material CAKE;
    private final MaterialColor color;
    private final PistonBehavior pistonBehavior;
    private final boolean blocksMovement;
    private final boolean burnable;
    private final boolean breakByHand;
    private final boolean liquid;
    private final boolean blocksLight;
    private final boolean replaceable;
    private final boolean solid;
    
    public Material(final MaterialColor color, final boolean liquid, final boolean solid, final boolean blocksMovement, final boolean blocksLight, final boolean breakByHand, final boolean burnable, final boolean replaceable, final PistonBehavior pistonBehavior) {
        this.color = color;
        this.liquid = liquid;
        this.solid = solid;
        this.blocksMovement = blocksMovement;
        this.blocksLight = blocksLight;
        this.breakByHand = breakByHand;
        this.burnable = burnable;
        this.replaceable = replaceable;
        this.pistonBehavior = pistonBehavior;
    }
    
    public boolean isLiquid() {
        return this.liquid;
    }
    
    public boolean isSolid() {
        return this.solid;
    }
    
    public boolean blocksMovement() {
        return this.blocksMovement;
    }
    
    public boolean isBurnable() {
        return this.burnable;
    }
    
    public boolean isReplaceable() {
        return this.replaceable;
    }
    
    public boolean blocksLight() {
        return this.blocksLight;
    }
    
    public boolean canBreakByHand() {
        return this.breakByHand;
    }
    
    public PistonBehavior getPistonBehavior() {
        return this.pistonBehavior;
    }
    
    public MaterialColor getColor() {
        return this.color;
    }
    
    static {
        AIR = new Builder(MaterialColor.AIR).allowsMovement().lightPassesThrough().notSolid().replaceable().build();
        STRUCTURE_VOID = new Builder(MaterialColor.AIR).allowsMovement().lightPassesThrough().notSolid().replaceable().build();
        PORTAL = new Builder(MaterialColor.AIR).allowsMovement().lightPassesThrough().notSolid().blocksPistons().build();
        CARPET = new Builder(MaterialColor.WEB).allowsMovement().lightPassesThrough().notSolid().burnable().build();
        PLANT = new Builder(MaterialColor.FOLIAGE).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().build();
        UNDERWATER_PLANT = new Builder(MaterialColor.WATER).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().build();
        REPLACEABLE_PLANT = new Builder(MaterialColor.FOLIAGE).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().replaceable().burnable().build();
        SEAGRASS = new Builder(MaterialColor.WATER).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().replaceable().build();
        WATER = new Builder(MaterialColor.WATER).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().replaceable().liquid().build();
        BUBBLE_COLUMN = new Builder(MaterialColor.WATER).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().replaceable().liquid().build();
        LAVA = new Builder(MaterialColor.LAVA).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().replaceable().liquid().build();
        SNOW = new Builder(MaterialColor.WHITE).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().replaceable().requiresTool().build();
        FIRE = new Builder(MaterialColor.AIR).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().replaceable().build();
        PART = new Builder(MaterialColor.AIR).allowsMovement().lightPassesThrough().notSolid().destroyedByPiston().build();
        COBWEB = new Builder(MaterialColor.WEB).allowsMovement().lightPassesThrough().destroyedByPiston().requiresTool().build();
        REDSTONE_LAMP = new Builder(MaterialColor.AIR).build();
        CLAY = new Builder(MaterialColor.CLAY).build();
        EARTH = new Builder(MaterialColor.DIRT).build();
        ORGANIC = new Builder(MaterialColor.GRASS).build();
        PACKED_ICE = new Builder(MaterialColor.ICE).build();
        SAND = new Builder(MaterialColor.SAND).build();
        SPONGE = new Builder(MaterialColor.YELLOW).build();
        SHULKER_BOX = new Builder(MaterialColor.PURPLE).build();
        WOOD = new Builder(MaterialColor.WOOD).burnable().build();
        BAMBOO_SAPLING = new Builder(MaterialColor.WOOD).burnable().destroyedByPiston().allowsMovement().build();
        BAMBOO = new Builder(MaterialColor.WOOD).burnable().destroyedByPiston().build();
        WOOL = new Builder(MaterialColor.WEB).burnable().build();
        TNT = new Builder(MaterialColor.LAVA).burnable().lightPassesThrough().build();
        LEAVES = new Builder(MaterialColor.FOLIAGE).burnable().lightPassesThrough().destroyedByPiston().build();
        GLASS = new Builder(MaterialColor.AIR).lightPassesThrough().build();
        ICE = new Builder(MaterialColor.ICE).lightPassesThrough().build();
        CACTUS = new Builder(MaterialColor.FOLIAGE).lightPassesThrough().destroyedByPiston().build();
        STONE = new Builder(MaterialColor.STONE).requiresTool().build();
        METAL = new Builder(MaterialColor.IRON).requiresTool().build();
        SNOW_BLOCK = new Builder(MaterialColor.WHITE).requiresTool().build();
        ANVIL = new Builder(MaterialColor.IRON).requiresTool().blocksPistons().build();
        BARRIER = new Builder(MaterialColor.AIR).requiresTool().blocksPistons().build();
        PISTON = new Builder(MaterialColor.STONE).blocksPistons().build();
        UNUSED_PLANT = new Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
        PUMPKIN = new Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
        DRAGON_EGG = new Builder(MaterialColor.FOLIAGE).destroyedByPiston().build();
        CAKE = new Builder(MaterialColor.AIR).destroyedByPiston().build();
    }
    
    public static class Builder
    {
        private PistonBehavior pistonBehavior;
        private boolean blocksMovement;
        private boolean burnable;
        private boolean breakByHand;
        private boolean liquid;
        private boolean replaceable;
        private boolean solid;
        private final MaterialColor color;
        private boolean blocksLight;
        
        public Builder(final MaterialColor color) {
            this.pistonBehavior = PistonBehavior.a;
            this.blocksMovement = true;
            this.breakByHand = true;
            this.solid = true;
            this.blocksLight = true;
            this.color = color;
        }
        
        public Builder liquid() {
            this.liquid = true;
            return this;
        }
        
        public Builder notSolid() {
            this.solid = false;
            return this;
        }
        
        public Builder allowsMovement() {
            this.blocksMovement = false;
            return this;
        }
        
        private Builder lightPassesThrough() {
            this.blocksLight = false;
            return this;
        }
        
        protected Builder requiresTool() {
            this.breakByHand = false;
            return this;
        }
        
        protected Builder burnable() {
            this.burnable = true;
            return this;
        }
        
        public Builder replaceable() {
            this.replaceable = true;
            return this;
        }
        
        protected Builder destroyedByPiston() {
            this.pistonBehavior = PistonBehavior.b;
            return this;
        }
        
        protected Builder blocksPistons() {
            this.pistonBehavior = PistonBehavior.c;
            return this;
        }
        
        public Material build() {
            return new Material(this.color, this.liquid, this.solid, this.blocksMovement, this.blocksLight, this.breakByHand, this.burnable, this.replaceable, this.pistonBehavior);
        }
    }
}
