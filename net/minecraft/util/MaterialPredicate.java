package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.block.Material;
import net.minecraft.block.BlockState;
import java.util.function.Predicate;

public class MaterialPredicate implements Predicate<BlockState>
{
    private static final MaterialPredicate IS_AIR;
    private final Material material;
    
    private MaterialPredicate(final Material material) {
        this.material = material;
    }
    
    public static MaterialPredicate create(final Material material) {
        return (material == Material.AIR) ? MaterialPredicate.IS_AIR : new MaterialPredicate(material);
    }
    
    public boolean a(@Nullable final BlockState blockState) {
        return blockState != null && blockState.getMaterial() == this.material;
    }
    
    static {
        IS_AIR = new MaterialPredicate(Material.AIR) {
            @Override
            public boolean a(@Nullable final BlockState blockState) {
                return blockState != null && blockState.isAir();
            }
        };
    }
}
