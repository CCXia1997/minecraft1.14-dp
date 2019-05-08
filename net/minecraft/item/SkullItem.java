package net.minecraft.item;

import net.minecraft.nbt.Tag;
import net.minecraft.util.TagHelper;
import net.minecraft.block.entity.SkullBlockEntity;
import java.util.UUID;
import com.mojang.authlib.GameProfile;
import org.apache.commons.lang3.StringUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.block.Block;

public class SkullItem extends WallStandingBlockItem
{
    public SkullItem(final Block standingBlock, final Block wallBlock, final Settings settings) {
        super(standingBlock, wallBlock, settings);
    }
    
    @Override
    public TextComponent getTranslatedNameTrimmed(final ItemStack stack) {
        if (stack.getItem() == Items.PLAYER_HEAD && stack.hasTag()) {
            String string2 = null;
            final CompoundTag compoundTag3 = stack.getTag();
            if (compoundTag3.containsKey("SkullOwner", 8)) {
                string2 = compoundTag3.getString("SkullOwner");
            }
            else if (compoundTag3.containsKey("SkullOwner", 10)) {
                final CompoundTag compoundTag4 = compoundTag3.getCompound("SkullOwner");
                if (compoundTag4.containsKey("Name", 8)) {
                    string2 = compoundTag4.getString("Name");
                }
            }
            if (string2 != null) {
                return new TranslatableTextComponent(this.getTranslationKey() + ".named", new Object[] { string2 });
            }
        }
        return super.getTranslatedNameTrimmed(stack);
    }
    
    @Override
    public boolean onTagDeserialized(final CompoundTag tag) {
        super.onTagDeserialized(tag);
        if (tag.containsKey("SkullOwner", 8) && !StringUtils.isBlank((CharSequence)tag.getString("SkullOwner"))) {
            GameProfile gameProfile2 = new GameProfile((UUID)null, tag.getString("SkullOwner"));
            gameProfile2 = SkullBlockEntity.loadProperties(gameProfile2);
            tag.put("SkullOwner", TagHelper.serializeProfile(new CompoundTag(), gameProfile2));
            return true;
        }
        return false;
    }
}
