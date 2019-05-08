package net.minecraft.block;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.block.entity.BlockEntity;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.util.TagHelper;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.item.ItemStack;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlayerSkullBlock extends SkullBlock
{
    protected PlayerSkullBlock(final Settings settings) {
        super(Type.PLAYER, settings);
    }
    
    @Override
    public void onPlaced(final World world, final BlockPos pos, final BlockState state, @Nullable final LivingEntity placer, final ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        final BlockEntity blockEntity6 = world.getBlockEntity(pos);
        if (blockEntity6 instanceof SkullBlockEntity) {
            final SkullBlockEntity skullBlockEntity7 = (SkullBlockEntity)blockEntity6;
            GameProfile gameProfile8 = null;
            if (itemStack.hasTag()) {
                final CompoundTag compoundTag9 = itemStack.getTag();
                if (compoundTag9.containsKey("SkullOwner", 10)) {
                    gameProfile8 = TagHelper.deserializeProfile(compoundTag9.getCompound("SkullOwner"));
                }
                else if (compoundTag9.containsKey("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)compoundTag9.getString("SkullOwner"))) {
                    gameProfile8 = new GameProfile((UUID)null, compoundTag9.getString("SkullOwner"));
                }
            }
            skullBlockEntity7.setOwnerAndType(gameProfile8);
        }
    }
}
