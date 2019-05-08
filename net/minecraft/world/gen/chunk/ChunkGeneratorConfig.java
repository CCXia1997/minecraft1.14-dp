package net.minecraft.world.gen.chunk;

import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;

public class ChunkGeneratorConfig
{
    protected int villageDistance;
    protected final int villageSeparation = 8;
    protected int oceanMonumentSpacing;
    protected int oceanMonumentSeparation;
    protected int strongholdDistance;
    protected int strongholdCount;
    protected int strongholdSpread;
    protected int templeDistance;
    protected final int templeSeparation = 8;
    protected final int shipwreckSpacing = 16;
    protected final int shipwreckSeparation = 8;
    protected int endCityDistance;
    protected final int endCitySeparation = 11;
    protected final int oceanRuinSpacing = 16;
    protected final int oceanRuinSeparation = 8;
    protected int mansionDistance;
    protected final int mansionSeparation = 20;
    protected BlockState defaultBlock;
    protected BlockState defaultFluid;
    
    public ChunkGeneratorConfig() {
        this.villageDistance = 32;
        this.oceanMonumentSpacing = 32;
        this.oceanMonumentSeparation = 5;
        this.strongholdDistance = 32;
        this.strongholdCount = 128;
        this.strongholdSpread = 3;
        this.templeDistance = 32;
        this.endCityDistance = 20;
        this.mansionDistance = 80;
        this.defaultBlock = Blocks.b.getDefaultState();
        this.defaultFluid = Blocks.A.getDefaultState();
    }
    
    public int getVillageDistance() {
        return this.villageDistance;
    }
    
    public int getVillageSeparation() {
        return 8;
    }
    
    public int getOceanMonumentSpacing() {
        return this.oceanMonumentSpacing;
    }
    
    public int getOceanMonumentSeparation() {
        return this.oceanMonumentSeparation;
    }
    
    public int getStrongholdDistance() {
        return this.strongholdDistance;
    }
    
    public int getStrongholdCount() {
        return this.strongholdCount;
    }
    
    public int getStrongholdSpread() {
        return this.strongholdSpread;
    }
    
    public int getTempleDistance() {
        return this.templeDistance;
    }
    
    public int getTempleSeparation() {
        return 8;
    }
    
    public int getShipwreckSpacing() {
        return 16;
    }
    
    public int getShipwreckSeparation() {
        return 8;
    }
    
    public int getOceanRuinSpacing() {
        return 16;
    }
    
    public int getOceanRuinSeparation() {
        return 8;
    }
    
    public int getEndCityDistance() {
        return this.endCityDistance;
    }
    
    public int getEndCitySeparation() {
        return 11;
    }
    
    public int getMansionDistance() {
        return this.mansionDistance;
    }
    
    public int getMansionSeparation() {
        return 20;
    }
    
    public BlockState getDefaultBlock() {
        return this.defaultBlock;
    }
    
    public BlockState getDefaultFluid() {
        return this.defaultFluid;
    }
    
    public void setDefaultBlock(final BlockState state) {
        this.defaultBlock = state;
    }
    
    public void setDefaultFluid(final BlockState state) {
        this.defaultFluid = state;
    }
    
    public int getMaxY() {
        return 0;
    }
    
    public int getMinY() {
        return 256;
    }
}
