package net.minecraft.block.entity;

import java.util.function.Supplier;
import net.minecraft.item.ItemProvider;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import com.google.common.collect.Lists;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.nbt.Tag;
import javax.annotation.Nullable;
import net.minecraft.text.TranslatableTextComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.item.ItemStack;
import java.util.List;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DyeColor;
import net.minecraft.text.TextComponent;
import net.minecraft.util.Nameable;

public class BannerBlockEntity extends BlockEntity implements Nameable
{
    private TextComponent customName;
    private DyeColor baseColor;
    private ListTag patternListTag;
    private boolean patternListTagRead;
    private List<BannerPattern> patterns;
    private List<DyeColor> patternColors;
    private String patternCacheKey;
    
    public BannerBlockEntity() {
        super(BlockEntityType.BANNER);
        this.baseColor = DyeColor.a;
    }
    
    public BannerBlockEntity(final DyeColor baseColor) {
        this();
        this.baseColor = baseColor;
    }
    
    @Environment(EnvType.CLIENT)
    public void deserialize(final ItemStack stack, final DyeColor dyeColor) {
        this.patternListTag = null;
        final CompoundTag compoundTag3 = stack.getSubCompoundTag("BlockEntityTag");
        if (compoundTag3 != null && compoundTag3.containsKey("Patterns", 9)) {
            this.patternListTag = compoundTag3.getList("Patterns", 10).copy();
        }
        this.baseColor = dyeColor;
        this.patterns = null;
        this.patternColors = null;
        this.patternCacheKey = "";
        this.patternListTagRead = true;
        this.customName = (stack.hasDisplayName() ? stack.getDisplayName() : null);
    }
    
    @Override
    public TextComponent getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return new TranslatableTextComponent("block.minecraft.banner", new Object[0]);
    }
    
    @Nullable
    @Override
    public TextComponent getCustomName() {
        return this.customName;
    }
    
    public void setCustomName(final TextComponent textComponent) {
        this.customName = textComponent;
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        if (this.patternListTag != null) {
            compoundTag.put("Patterns", this.patternListTag);
        }
        if (this.customName != null) {
            compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.customName));
        }
        return compoundTag;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        if (compoundTag.containsKey("CustomName", 8)) {
            this.customName = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
        }
        if (this.hasWorld()) {
            this.baseColor = ((AbstractBannerBlock)this.getCachedState().getBlock()).getColor();
        }
        else {
            this.baseColor = null;
        }
        this.patternListTag = compoundTag.getList("Patterns", 10);
        this.patterns = null;
        this.patternColors = null;
        this.patternCacheKey = null;
        this.patternListTagRead = true;
    }
    
    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 6, this.toInitialChunkDataTag());
    }
    
    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
    }
    
    public static int getPatternCount(final ItemStack stack) {
        final CompoundTag compoundTag2 = stack.getSubCompoundTag("BlockEntityTag");
        if (compoundTag2 != null && compoundTag2.containsKey("Patterns")) {
            return compoundTag2.getList("Patterns", 10).size();
        }
        return 0;
    }
    
    @Environment(EnvType.CLIENT)
    public List<BannerPattern> getPatterns() {
        this.readPattern();
        return this.patterns;
    }
    
    @Environment(EnvType.CLIENT)
    public List<DyeColor> getPatternColors() {
        this.readPattern();
        return this.patternColors;
    }
    
    @Environment(EnvType.CLIENT)
    public String getPatternCacheKey() {
        this.readPattern();
        return this.patternCacheKey;
    }
    
    @Environment(EnvType.CLIENT)
    private void readPattern() {
        if (this.patterns != null && this.patternColors != null && this.patternCacheKey != null) {
            return;
        }
        if (!this.patternListTagRead) {
            this.patternCacheKey = "";
            return;
        }
        this.patterns = Lists.newArrayList();
        this.patternColors = Lists.newArrayList();
        final DyeColor dyeColor1 = this.getColorForState(this::getCachedState);
        if (dyeColor1 == null) {
            this.patternCacheKey = "banner_missing";
        }
        else {
            this.patterns.add(BannerPattern.BASE);
            this.patternColors.add(dyeColor1);
            this.patternCacheKey = "b" + dyeColor1.getId();
            if (this.patternListTag != null) {
                for (int integer2 = 0; integer2 < this.patternListTag.size(); ++integer2) {
                    final CompoundTag compoundTag3 = this.patternListTag.getCompoundTag(integer2);
                    final BannerPattern bannerPattern4 = BannerPattern.byId(compoundTag3.getString("Pattern"));
                    if (bannerPattern4 != null) {
                        this.patterns.add(bannerPattern4);
                        final int integer3 = compoundTag3.getInt("Color");
                        this.patternColors.add(DyeColor.byId(integer3));
                        this.patternCacheKey = this.patternCacheKey + bannerPattern4.getId() + integer3;
                    }
                }
            }
        }
    }
    
    public static void loadFromItemStack(final ItemStack stack) {
        final CompoundTag compoundTag2 = stack.getSubCompoundTag("BlockEntityTag");
        if (compoundTag2 == null || !compoundTag2.containsKey("Patterns", 9)) {
            return;
        }
        final ListTag listTag3 = compoundTag2.getList("Patterns", 10);
        if (listTag3.isEmpty()) {
            return;
        }
        listTag3.c(listTag3.size() - 1);
        if (listTag3.isEmpty()) {
            stack.removeSubTag("BlockEntityTag");
        }
    }
    
    @Environment(EnvType.CLIENT)
    public ItemStack getPickStack(final BlockState blockState) {
        final ItemStack itemStack2 = new ItemStack(BannerBlock.getForColor(this.getColorForState(() -> blockState)));
        if (this.patternListTag != null && !this.patternListTag.isEmpty()) {
            itemStack2.getOrCreateSubCompoundTag("BlockEntityTag").put("Patterns", this.patternListTag.copy());
        }
        if (this.customName != null) {
            itemStack2.setDisplayName(this.customName);
        }
        return itemStack2;
    }
    
    public DyeColor getColorForState(final Supplier<BlockState> supplier) {
        if (this.baseColor == null) {
            this.baseColor = ((AbstractBannerBlock)supplier.get().getBlock()).getColor();
        }
        return this.baseColor;
    }
}
