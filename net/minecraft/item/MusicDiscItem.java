package net.minecraft.item;

import com.google.common.collect.Maps;
import net.minecraft.text.TranslatableTextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextFormat;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.text.TextComponent;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.stat.Stats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.IWorld;
import net.minecraft.state.property.Property;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;
import net.minecraft.sound.SoundEvent;
import java.util.Map;

public class MusicDiscItem extends Item
{
    private static final Map<SoundEvent, MusicDiscItem> SOUND_ITEM_MAP;
    private final int comparatorOutput;
    private final SoundEvent sound;
    
    protected MusicDiscItem(final int comparatorOutput, final SoundEvent soundEvent, final Settings settings) {
        super(settings);
        this.comparatorOutput = comparatorOutput;
        this.sound = soundEvent;
        MusicDiscItem.SOUND_ITEM_MAP.put(this.sound, this);
    }
    
    @Override
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        final World world2 = usageContext.getWorld();
        final BlockPos blockPos3 = usageContext.getBlockPos();
        final BlockState blockState4 = world2.getBlockState(blockPos3);
        if (blockState4.getBlock() != Blocks.cG || blockState4.<Boolean>get((Property<Boolean>)JukeboxBlock.HAS_RECORD)) {
            return ActionResult.PASS;
        }
        final ItemStack itemStack5 = usageContext.getItemStack();
        if (!world2.isClient) {
            ((JukeboxBlock)Blocks.cG).setRecord(world2, blockPos3, blockState4, itemStack5);
            world2.playLevelEvent(null, 1010, blockPos3, Item.getRawIdByItem(this));
            itemStack5.subtractAmount(1);
            final PlayerEntity playerEntity6 = usageContext.getPlayer();
            if (playerEntity6 != null) {
                playerEntity6.incrementStat(Stats.aj);
            }
        }
        return ActionResult.a;
    }
    
    public int getComparatorOutput() {
        return this.comparatorOutput;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
        tooltip.add(this.getDescription().applyFormat(TextFormat.h));
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getDescription() {
        return new TranslatableTextComponent(this.getTranslationKey() + ".desc", new Object[0]);
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static MusicDiscItem bySound(final SoundEvent sound) {
        return MusicDiscItem.SOUND_ITEM_MAP.get(sound);
    }
    
    @Environment(EnvType.CLIENT)
    public SoundEvent getSound() {
        return this.sound;
    }
    
    static {
        SOUND_ITEM_MAP = Maps.newHashMap();
    }
}
