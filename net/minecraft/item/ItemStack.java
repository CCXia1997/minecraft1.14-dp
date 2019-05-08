package net.minecraft.item;

import java.util.AbstractList;
import org.apache.logging.log4j.LogManager;
import java.util.function.Predicate;
import net.minecraft.command.arguments.BlockPredicateArgumentType;
import net.minecraft.tag.TagManager;
import java.util.Objects;
import net.minecraft.text.event.HoverEvent;
import com.google.common.collect.HashMultimap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Rarity;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.command.arguments.BlockArgumentParser;
import com.mojang.brigadier.StringReader;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Iterator;
import com.google.common.collect.Multimap;
import java.util.Collection;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import java.util.Map;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.text.TextFormatter;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextFormat;
import net.minecraft.text.StringTextComponent;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.item.TooltipContext;
import com.google.gson.JsonParseException;
import net.minecraft.text.TextComponent;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.UseAction;
import net.minecraft.entity.Entity;
import java.util.function.Consumer;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.enchantment.UnbreakingEnchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import javax.annotation.Nullable;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.Random;
import net.minecraft.nbt.Tag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.world.ViewableWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.nbt.CompoundTag;
import java.text.DecimalFormat;
import org.apache.logging.log4j.Logger;

public final class ItemStack
{
    private static final Logger LOGGER;
    public static final ItemStack EMPTY;
    public static final DecimalFormat MODIFIER_FORMAT;
    private int amount;
    private int updateCooldown;
    @Deprecated
    private final Item item;
    private CompoundTag tag;
    private boolean empty;
    private ItemFrameEntity holdingItemFrame;
    private CachedBlockPosition lastCheckedCanHarvestBlock;
    private boolean lastCheckedCanHarvestResult;
    private CachedBlockPosition lastCheckedCanPlaceBlock;
    private boolean lastCheckedCanPlaceResult;
    
    private static DecimalFormat createModifierFormat() {
        final DecimalFormat decimalFormat1 = new DecimalFormat("#.##");
        decimalFormat1.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        return decimalFormat1;
    }
    
    public ItemStack(final ItemProvider itemProvider) {
        this(itemProvider, 1);
    }
    
    public ItemStack(final ItemProvider container, final int integer) {
        this.item = ((container == null) ? null : container.getItem());
        this.amount = integer;
        this.updateEmptyFlag();
    }
    
    private void updateEmptyFlag() {
        this.empty = false;
        this.empty = this.isEmpty();
    }
    
    private ItemStack(final CompoundTag compoundTag) {
        this.item = Registry.ITEM.get(new Identifier(compoundTag.getString("id")));
        this.amount = compoundTag.getByte("Count");
        if (compoundTag.containsKey("tag", 10)) {
            this.tag = compoundTag.getCompound("tag");
            this.getItem().onTagDeserialized(compoundTag);
        }
        if (this.getItem().canDamage()) {
            this.setDamage(this.getDamage());
        }
        this.updateEmptyFlag();
    }
    
    public static ItemStack fromTag(final CompoundTag tag) {
        try {
            return new ItemStack(tag);
        }
        catch (RuntimeException runtimeException2) {
            ItemStack.LOGGER.debug("Tried to load invalid item: {}", tag, runtimeException2);
            return ItemStack.EMPTY;
        }
    }
    
    public boolean isEmpty() {
        return this == ItemStack.EMPTY || (this.getItem() == null || this.getItem() == Items.AIR) || this.amount <= 0;
    }
    
    public ItemStack split(final int amount) {
        final int integer2 = Math.min(amount, this.amount);
        final ItemStack itemStack3 = this.copy();
        itemStack3.setAmount(integer2);
        this.subtractAmount(integer2);
        return itemStack3;
    }
    
    public Item getItem() {
        return this.empty ? Items.AIR : this.item;
    }
    
    public ActionResult useOnBlock(final ItemUsageContext itemUsageContext) {
        final PlayerEntity playerEntity2 = itemUsageContext.getPlayer();
        final BlockPos blockPos3 = itemUsageContext.getBlockPos();
        final CachedBlockPosition cachedBlockPosition4 = new CachedBlockPosition(itemUsageContext.getWorld(), blockPos3, false);
        if (playerEntity2 != null && !playerEntity2.abilities.allowModifyWorld && !this.getCustomCanPlace(itemUsageContext.getWorld().getTagManager(), cachedBlockPosition4)) {
            return ActionResult.PASS;
        }
        final Item item5 = this.getItem();
        final ActionResult actionResult6 = item5.useOnBlock(itemUsageContext);
        if (playerEntity2 != null && actionResult6 == ActionResult.a) {
            playerEntity2.incrementStat(Stats.c.getOrCreateStat(item5));
        }
        return actionResult6;
    }
    
    public float getBlockBreakingSpeed(final BlockState blockState) {
        return this.getItem().getBlockBreakingSpeed(this, blockState);
    }
    
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        return this.getItem().use(world, player, hand);
    }
    
    public ItemStack onItemFinishedUsing(final World world, final LivingEntity livingEntity) {
        return this.getItem().onItemFinishedUsing(this, world, livingEntity);
    }
    
    public CompoundTag toTag(final CompoundTag compoundTag) {
        final Identifier identifier2 = Registry.ITEM.getId(this.getItem());
        compoundTag.putString("id", (identifier2 == null) ? "minecraft:air" : identifier2.toString());
        compoundTag.putByte("Count", (byte)this.amount);
        if (this.tag != null) {
            compoundTag.put("tag", this.tag);
        }
        return compoundTag;
    }
    
    public int getMaxAmount() {
        return this.getItem().getMaxAmount();
    }
    
    public boolean canStack() {
        return this.getMaxAmount() > 1 && (!this.hasDurability() || !this.isDamaged());
    }
    
    public boolean hasDurability() {
        if (this.empty || this.getItem().getDurability() <= 0) {
            return false;
        }
        final CompoundTag compoundTag1 = this.getTag();
        return compoundTag1 == null || !compoundTag1.getBoolean("Unbreakable");
    }
    
    public boolean isDamaged() {
        return this.hasDurability() && this.getDamage() > 0;
    }
    
    public int getDamage() {
        return (this.tag == null) ? 0 : this.tag.getInt("Damage");
    }
    
    public void setDamage(final int integer) {
        this.getOrCreateTag().putInt("Damage", Math.max(0, integer));
    }
    
    public int getDurability() {
        return this.getItem().getDurability();
    }
    
    public boolean applyDamage(int amount, final Random random, @Nullable final ServerPlayerEntity serverPlayerEntity) {
        if (!this.hasDurability()) {
            return false;
        }
        if (amount > 0) {
            final int integer4 = EnchantmentHelper.getLevel(Enchantments.u, this);
            int integer5 = 0;
            for (int integer6 = 0; integer4 > 0 && integer6 < amount; ++integer6) {
                if (UnbreakingEnchantment.shouldPreventDamage(this, integer4, random)) {
                    ++integer5;
                }
            }
            amount -= integer5;
            if (amount <= 0) {
                return false;
            }
        }
        if (serverPlayerEntity != null && amount != 0) {
            Criterions.ITEM_DURABILITY_CHANGED.handle(serverPlayerEntity, this, this.getDamage() + amount);
        }
        final int integer4 = this.getDamage() + amount;
        this.setDamage(integer4);
        return integer4 >= this.getDurability();
    }
    
    public <T extends LivingEntity> void applyDamage(final int integer, final T livingEntity, final Consumer<T> consumer) {
        if (livingEntity.world.isClient || (livingEntity instanceof PlayerEntity && ((PlayerEntity)livingEntity).abilities.creativeMode)) {
            return;
        }
        if (!this.hasDurability()) {
            return;
        }
        if (this.applyDamage(integer, livingEntity.getRand(), (ServerPlayerEntity)((livingEntity instanceof ServerPlayerEntity) ? livingEntity : null))) {
            consumer.accept(livingEntity);
            final Item item4 = this.getItem();
            this.subtractAmount(1);
            if (livingEntity instanceof PlayerEntity) {
                ((PlayerEntity)livingEntity).incrementStat(Stats.d.getOrCreateStat(item4));
            }
            this.setDamage(0);
        }
    }
    
    public void onEntityDamaged(final LivingEntity attacker, final PlayerEntity playerEntity) {
        final Item item3 = this.getItem();
        if (item3.onEntityDamaged(this, attacker, playerEntity)) {
            playerEntity.incrementStat(Stats.c.getOrCreateStat(item3));
        }
    }
    
    public void onBlockBroken(final World world, final BlockState state, final BlockPos pos, final PlayerEntity playerEntity) {
        final Item item5 = this.getItem();
        if (item5.onBlockBroken(this, world, state, pos, playerEntity)) {
            playerEntity.incrementStat(Stats.c.getOrCreateStat(item5));
        }
    }
    
    public boolean isEffectiveOn(final BlockState blockState) {
        return this.getItem().isEffectiveOn(blockState);
    }
    
    public boolean interactWithEntity(final PlayerEntity user, final LivingEntity target, final Hand hand) {
        return this.getItem().interactWithEntity(this, user, target, hand);
    }
    
    public ItemStack copy() {
        final ItemStack itemStack1 = new ItemStack(this.getItem(), this.amount);
        itemStack1.setUpdateCooldown(this.getUpdateCooldown());
        if (this.tag != null) {
            itemStack1.tag = this.tag.copy();
        }
        return itemStack1;
    }
    
    public static boolean areTagsEqual(final ItemStack a, final ItemStack b) {
        return (a.isEmpty() && b.isEmpty()) || (!a.isEmpty() && !b.isEmpty() && (a.tag != null || b.tag == null) && (a.tag == null || a.tag.equals(b.tag)));
    }
    
    public static boolean areEqual(final ItemStack a, final ItemStack b) {
        return (a.isEmpty() && b.isEmpty()) || (!a.isEmpty() && !b.isEmpty() && a.isEqual(b));
    }
    
    private boolean isEqual(final ItemStack itemStack) {
        return this.amount == itemStack.amount && this.getItem() == itemStack.getItem() && (this.tag != null || itemStack.tag == null) && (this.tag == null || this.tag.equals(itemStack.tag));
    }
    
    public static boolean areEqualIgnoreTags(final ItemStack a, final ItemStack b) {
        return a == b || (!a.isEmpty() && !b.isEmpty() && a.isEqualIgnoreTags(b));
    }
    
    public static boolean areEqualIgnoreDurability(final ItemStack a, final ItemStack b) {
        return a == b || (!a.isEmpty() && !b.isEmpty() && a.isEqualIgnoreDurability(b));
    }
    
    public boolean isEqualIgnoreTags(final ItemStack itemStack) {
        return !itemStack.isEmpty() && this.getItem() == itemStack.getItem();
    }
    
    public boolean isEqualIgnoreDurability(final ItemStack itemStack) {
        if (this.hasDurability()) {
            return !itemStack.isEmpty() && this.getItem() == itemStack.getItem();
        }
        return this.isEqualIgnoreTags(itemStack);
    }
    
    public String getTranslationKey() {
        return this.getItem().getTranslationKey(this);
    }
    
    @Override
    public String toString() {
        return this.amount + "x" + this.getItem().getTranslationKey();
    }
    
    public void update(final World world, final Entity owner, final int invSlot, final boolean boolean4) {
        if (this.updateCooldown > 0) {
            --this.updateCooldown;
        }
        if (this.getItem() != null) {
            this.getItem().onEntityTick(this, world, owner, invSlot, boolean4);
        }
    }
    
    public void onCrafted(final World world, final PlayerEntity player, final int amount) {
        player.increaseStat(Stats.b.getOrCreateStat(this.getItem()), amount);
        this.getItem().onCrafted(this, world, player);
    }
    
    public int getMaxUseTime() {
        return this.getItem().getMaxUseTime(this);
    }
    
    public UseAction getUseAction() {
        return this.getItem().getUseAction(this);
    }
    
    public void onItemStopUsing(final World world, final LivingEntity user, final int integer) {
        this.getItem().onItemStopUsing(this, world, user, integer);
    }
    
    public boolean m() {
        return this.getItem().i(this);
    }
    
    public boolean hasTag() {
        return !this.empty && this.tag != null && !this.tag.isEmpty();
    }
    
    @Nullable
    public CompoundTag getTag() {
        return this.tag;
    }
    
    public CompoundTag getOrCreateTag() {
        if (this.tag == null) {
            this.setTag(new CompoundTag());
        }
        return this.tag;
    }
    
    public CompoundTag getOrCreateSubCompoundTag(final String key) {
        if (this.tag == null || !this.tag.containsKey(key, 10)) {
            final CompoundTag compoundTag2 = new CompoundTag();
            this.setChildTag(key, compoundTag2);
            return compoundTag2;
        }
        return this.tag.getCompound(key);
    }
    
    @Nullable
    public CompoundTag getSubCompoundTag(final String key) {
        if (this.tag == null || !this.tag.containsKey(key, 10)) {
            return null;
        }
        return this.tag.getCompound(key);
    }
    
    public void removeSubTag(final String key) {
        if (this.tag != null && this.tag.containsKey(key)) {
            this.tag.remove(key);
            if (this.tag.isEmpty()) {
                this.tag = null;
            }
        }
    }
    
    public ListTag getEnchantmentList() {
        if (this.tag != null) {
            return this.tag.getList("Enchantments", 10);
        }
        return new ListTag();
    }
    
    public void setTag(@Nullable final CompoundTag compoundTag) {
        this.tag = compoundTag;
    }
    
    public TextComponent getDisplayName() {
        final CompoundTag compoundTag1 = this.getSubCompoundTag("display");
        if (compoundTag1 != null && compoundTag1.containsKey("Name", 8)) {
            try {
                final TextComponent textComponent2 = TextComponent.Serializer.fromJsonString(compoundTag1.getString("Name"));
                if (textComponent2 != null) {
                    return textComponent2;
                }
                compoundTag1.remove("Name");
            }
            catch (JsonParseException jsonParseException2) {
                compoundTag1.remove("Name");
            }
        }
        return this.getItem().getTranslatedNameTrimmed(this);
    }
    
    public ItemStack setDisplayName(@Nullable final TextComponent textComponent) {
        final CompoundTag compoundTag2 = this.getOrCreateSubCompoundTag("display");
        if (textComponent != null) {
            compoundTag2.putString("Name", TextComponent.Serializer.toJsonString(textComponent));
        }
        else {
            compoundTag2.remove("Name");
        }
        return this;
    }
    
    public void removeDisplayName() {
        final CompoundTag compoundTag1 = this.getSubCompoundTag("display");
        if (compoundTag1 != null) {
            compoundTag1.remove("Name");
            if (compoundTag1.isEmpty()) {
                this.removeSubTag("display");
            }
        }
        if (this.tag != null && this.tag.isEmpty()) {
            this.tag = null;
        }
    }
    
    public boolean hasDisplayName() {
        final CompoundTag compoundTag1 = this.getSubCompoundTag("display");
        return compoundTag1 != null && compoundTag1.containsKey("Name", 8);
    }
    
    @Environment(EnvType.CLIENT)
    public List<TextComponent> getTooltipText(@Nullable final PlayerEntity player, final TooltipContext tooltipContext) {
        final List<TextComponent> list3 = Lists.newArrayList();
        final TextComponent textComponent4 = new StringTextComponent("").append(this.getDisplayName()).applyFormat(this.getRarity().formatting);
        if (this.hasDisplayName()) {
            textComponent4.applyFormat(TextFormat.u);
        }
        list3.add(textComponent4);
        if (!tooltipContext.isAdvanced() && !this.hasDisplayName() && this.getItem() == Items.lV) {
            list3.add(new StringTextComponent("#" + FilledMapItem.getMapId(this)).applyFormat(TextFormat.h));
        }
        int integer5 = 0;
        if (this.hasTag() && this.tag.containsKey("HideFlags", 99)) {
            integer5 = this.tag.getInt("HideFlags");
        }
        if ((integer5 & 0x20) == 0x0) {
            this.getItem().buildTooltip(this, (player == null) ? null : player.world, list3, tooltipContext);
        }
        if (this.hasTag()) {
            if ((integer5 & 0x1) == 0x0) {
                appendEnchantmentComponents(list3, this.getEnchantmentList());
            }
            if (this.tag.containsKey("display", 10)) {
                final CompoundTag compoundTag6 = this.tag.getCompound("display");
                if (compoundTag6.containsKey("color", 3)) {
                    if (tooltipContext.isAdvanced()) {
                        list3.add(new TranslatableTextComponent("item.color", new Object[] { String.format("#%06X", compoundTag6.getInt("color")) }).applyFormat(TextFormat.h));
                    }
                    else {
                        list3.add(new TranslatableTextComponent("item.dyed", new Object[0]).applyFormat(TextFormat.h, TextFormat.u));
                    }
                }
                if (compoundTag6.getType("Lore") == 9) {
                    final ListTag listTag7 = compoundTag6.getList("Lore", 8);
                    for (int integer6 = 0; integer6 < listTag7.size(); ++integer6) {
                        final String string9 = listTag7.getString(integer6);
                        try {
                            final TextComponent textComponent5 = TextComponent.Serializer.fromJsonString(string9);
                            if (textComponent5 != null) {
                                list3.add(TextFormatter.style(textComponent5, new Style().setColor(TextFormat.f).setItalic(true)));
                            }
                        }
                        catch (JsonParseException jsonParseException10) {
                            compoundTag6.remove("Lore");
                        }
                    }
                }
            }
        }
        for (final EquipmentSlot equipmentSlot9 : EquipmentSlot.values()) {
            final Multimap<String, EntityAttributeModifier> multimap10 = this.getAttributeModifiers(equipmentSlot9);
            if (!multimap10.isEmpty() && (integer5 & 0x2) == 0x0) {
                list3.add(new StringTextComponent(""));
                list3.add(new TranslatableTextComponent("item.modifiers." + equipmentSlot9.getName(), new Object[0]).applyFormat(TextFormat.h));
                for (final Map.Entry<String, EntityAttributeModifier> entry12 : multimap10.entries()) {
                    final EntityAttributeModifier entityAttributeModifier13 = entry12.getValue();
                    double double14 = entityAttributeModifier13.getAmount();
                    boolean boolean18 = false;
                    if (player != null) {
                        if (entityAttributeModifier13.getId() == Item.MODIFIER_DAMAGE) {
                            double14 += player.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getBaseValue();
                            double14 += EnchantmentHelper.getAttackDamage(this, EntityGroup.DEFAULT);
                            boolean18 = true;
                        }
                        else if (entityAttributeModifier13.getId() == Item.MODIFIER_SWING_SPEED) {
                            double14 += player.getAttributeInstance(EntityAttributes.ATTACK_SPEED).getBaseValue();
                            boolean18 = true;
                        }
                    }
                    double double15;
                    if (entityAttributeModifier13.getOperation() == EntityAttributeModifier.Operation.b || entityAttributeModifier13.getOperation() == EntityAttributeModifier.Operation.c) {
                        double15 = double14 * 100.0;
                    }
                    else {
                        double15 = double14;
                    }
                    if (boolean18) {
                        list3.add(new StringTextComponent(" ").append(new TranslatableTextComponent("attribute.modifier.equals." + entityAttributeModifier13.getOperation().getId(), new Object[] { ItemStack.MODIFIER_FORMAT.format(double15), new TranslatableTextComponent("attribute.name." + entry12.getKey(), new Object[0]) })).applyFormat(TextFormat.c));
                    }
                    else if (double14 > 0.0) {
                        list3.add(new TranslatableTextComponent("attribute.modifier.plus." + entityAttributeModifier13.getOperation().getId(), new Object[] { ItemStack.MODIFIER_FORMAT.format(double15), new TranslatableTextComponent("attribute.name." + entry12.getKey(), new Object[0]) }).applyFormat(TextFormat.j));
                    }
                    else {
                        if (double14 >= 0.0) {
                            continue;
                        }
                        double15 *= -1.0;
                        list3.add(new TranslatableTextComponent("attribute.modifier.take." + entityAttributeModifier13.getOperation().getId(), new Object[] { ItemStack.MODIFIER_FORMAT.format(double15), new TranslatableTextComponent("attribute.name." + entry12.getKey(), new Object[0]) }).applyFormat(TextFormat.m));
                    }
                }
            }
        }
        if (this.hasTag() && this.getTag().getBoolean("Unbreakable") && (integer5 & 0x4) == 0x0) {
            list3.add(new TranslatableTextComponent("item.unbreakable", new Object[0]).applyFormat(TextFormat.j));
        }
        if (this.hasTag() && this.tag.containsKey("CanDestroy", 9) && (integer5 & 0x8) == 0x0) {
            final ListTag listTag8 = this.tag.getList("CanDestroy", 8);
            if (!listTag8.isEmpty()) {
                list3.add(new StringTextComponent(""));
                list3.add(new TranslatableTextComponent("item.canBreak", new Object[0]).applyFormat(TextFormat.h));
                for (int integer7 = 0; integer7 < listTag8.size(); ++integer7) {
                    list3.addAll(d(listTag8.getString(integer7)));
                }
            }
        }
        if (this.hasTag() && this.tag.containsKey("CanPlaceOn", 9) && (integer5 & 0x10) == 0x0) {
            final ListTag listTag8 = this.tag.getList("CanPlaceOn", 8);
            if (!listTag8.isEmpty()) {
                list3.add(new StringTextComponent(""));
                list3.add(new TranslatableTextComponent("item.canPlace", new Object[0]).applyFormat(TextFormat.h));
                for (int integer7 = 0; integer7 < listTag8.size(); ++integer7) {
                    list3.addAll(d(listTag8.getString(integer7)));
                }
            }
        }
        if (tooltipContext.isAdvanced()) {
            if (this.isDamaged()) {
                list3.add(new TranslatableTextComponent("item.durability", new Object[] { this.getDurability() - this.getDamage(), this.getDurability() }));
            }
            list3.add(new StringTextComponent(Registry.ITEM.getId(this.getItem()).toString()).applyFormat(TextFormat.i));
            if (this.hasTag()) {
                list3.add(new TranslatableTextComponent("item.nbt_tags", new Object[] { this.getTag().getKeys().size() }).applyFormat(TextFormat.i));
            }
        }
        return list3;
    }
    
    @Environment(EnvType.CLIENT)
    public static void appendEnchantmentComponents(final List<TextComponent> list, final ListTag enchantments) {
        for (int integer3 = 0; integer3 < enchantments.size(); ++integer3) {
            final CompoundTag compoundTag4 = enchantments.getCompoundTag(integer3);
            Registry.ENCHANTMENT.getOrEmpty(Identifier.create(compoundTag4.getString("id"))).ifPresent(enchantment -> list.add(enchantment.getTextComponent(compoundTag4.getInt("lvl"))));
        }
    }
    
    @Environment(EnvType.CLIENT)
    private static Collection<TextComponent> d(final String string) {
        try {
            final BlockArgumentParser blockArgumentParser2 = new BlockArgumentParser(new StringReader(string), true).parse(true);
            final BlockState blockState3 = blockArgumentParser2.getBlockState();
            final Identifier identifier4 = blockArgumentParser2.getTagId();
            final boolean boolean5 = blockState3 != null;
            final boolean boolean6 = identifier4 != null;
            if (boolean5 || boolean6) {
                if (boolean5) {
                    return Lists.newArrayList(blockState3.getBlock().getTextComponent().applyFormat(TextFormat.i));
                }
                final net.minecraft.tag.Tag<Block> tag7 = BlockTags.getContainer().get(identifier4);
                if (tag7 != null) {
                    final Collection<Block> collection8 = tag7.values();
                    if (!collection8.isEmpty()) {
                        return collection8.stream().map(Block::getTextComponent).map(textComponent -> textComponent.applyFormat(TextFormat.i)).collect(Collectors.toList());
                    }
                }
            }
        }
        catch (CommandSyntaxException ex) {}
        return Lists.newArrayList(new StringTextComponent("missingno").applyFormat(TextFormat.i));
    }
    
    @Environment(EnvType.CLIENT)
    public boolean hasEnchantmentGlint() {
        return this.getItem().hasEnchantmentGlint(this);
    }
    
    public Rarity getRarity() {
        return this.getItem().getRarity(this);
    }
    
    public boolean isEnchantable() {
        return this.getItem().isTool(this) && !this.hasEnchantments();
    }
    
    public void addEnchantment(final Enchantment enchantment, final int level) {
        this.getOrCreateTag();
        if (!this.tag.containsKey("Enchantments", 9)) {
            this.tag.put("Enchantments", new ListTag());
        }
        final ListTag listTag3 = this.tag.getList("Enchantments", 10);
        final CompoundTag compoundTag4 = new CompoundTag();
        compoundTag4.putString("id", String.valueOf(Registry.ENCHANTMENT.getId(enchantment)));
        compoundTag4.putShort("lvl", (byte)level);
        ((AbstractList<CompoundTag>)listTag3).add(compoundTag4);
    }
    
    public boolean hasEnchantments() {
        return this.tag != null && this.tag.containsKey("Enchantments", 9) && !this.tag.getList("Enchantments", 10).isEmpty();
    }
    
    public void setChildTag(final String tagName, final Tag tag) {
        this.getOrCreateTag().put(tagName, tag);
    }
    
    public boolean isHeldInItemFrame() {
        return this.holdingItemFrame != null;
    }
    
    public void setHoldingItemFrame(@Nullable final ItemFrameEntity itemFrameEntity) {
        this.holdingItemFrame = itemFrameEntity;
    }
    
    @Nullable
    public ItemFrameEntity getHoldingItemFrame() {
        return this.empty ? null : this.holdingItemFrame;
    }
    
    public int getRepairCost() {
        if (this.hasTag() && this.tag.containsKey("RepairCost", 3)) {
            return this.tag.getInt("RepairCost");
        }
        return 0;
    }
    
    public void setRepairCost(final int integer) {
        this.getOrCreateTag().putInt("RepairCost", integer);
    }
    
    public Multimap<String, EntityAttributeModifier> getAttributeModifiers(final EquipmentSlot equipmentSlot) {
        Multimap<String, EntityAttributeModifier> multimap2;
        if (this.hasTag() && this.tag.containsKey("AttributeModifiers", 9)) {
            multimap2 = HashMultimap.create();
            final ListTag listTag3 = this.tag.getList("AttributeModifiers", 10);
            for (int integer4 = 0; integer4 < listTag3.size(); ++integer4) {
                final CompoundTag compoundTag5 = listTag3.getCompoundTag(integer4);
                final EntityAttributeModifier entityAttributeModifier6 = EntityAttributes.createFromTag(compoundTag5);
                if (entityAttributeModifier6 != null) {
                    if (!compoundTag5.containsKey("Slot", 8) || compoundTag5.getString("Slot").equals(equipmentSlot.getName())) {
                        if (entityAttributeModifier6.getId().getLeastSignificantBits() != 0L && entityAttributeModifier6.getId().getMostSignificantBits() != 0L) {
                            multimap2.put(compoundTag5.getString("AttributeName"), entityAttributeModifier6);
                        }
                    }
                }
            }
        }
        else {
            multimap2 = this.getItem().getAttributeModifiers(equipmentSlot);
        }
        return multimap2;
    }
    
    public void addAttributeModifier(final String attributeName, final EntityAttributeModifier modifier, @Nullable final EquipmentSlot equipmentSlot) {
        this.getOrCreateTag();
        if (!this.tag.containsKey("AttributeModifiers", 9)) {
            this.tag.put("AttributeModifiers", new ListTag());
        }
        final ListTag listTag4 = this.tag.getList("AttributeModifiers", 10);
        final CompoundTag compoundTag5 = EntityAttributes.toTag(modifier);
        compoundTag5.putString("AttributeName", attributeName);
        if (equipmentSlot != null) {
            compoundTag5.putString("Slot", equipmentSlot.getName());
        }
        ((AbstractList<CompoundTag>)listTag4).add(compoundTag5);
    }
    
    public TextComponent toTextComponent() {
        final TextComponent textComponent1 = new StringTextComponent("").append(this.getDisplayName());
        if (this.hasDisplayName()) {
            textComponent1.applyFormat(TextFormat.u);
        }
        final TextComponent textComponent2 = TextFormatter.bracketed(textComponent1);
        if (!this.empty) {
            final CompoundTag compoundTag3 = this.toTag(new CompoundTag());
            final HoverEvent hoverEvent;
            final CompoundTag compoundTag4;
            textComponent2.applyFormat(this.getRarity().formatting).modifyStyle(style -> {
                new HoverEvent(HoverEvent.Action.SHOW_ITEM, new StringTextComponent(compoundTag4.toString()));
                style.setHoverEvent(hoverEvent);
                return;
            });
        }
        return textComponent2;
    }
    
    private static boolean areBlocksEqual(final CachedBlockPosition first, @Nullable final CachedBlockPosition second) {
        return second != null && first.getBlockState() == second.getBlockState() && ((first.getBlockEntity() == null && second.getBlockEntity() == null) || (first.getBlockEntity() != null && second.getBlockEntity() != null && Objects.equals(first.getBlockEntity().toTag(new CompoundTag()), second.getBlockEntity().toTag(new CompoundTag()))));
    }
    
    public boolean getCustomCanHarvest(final TagManager tagManager, final CachedBlockPosition cachedBlockPosition) {
        if (areBlocksEqual(cachedBlockPosition, this.lastCheckedCanHarvestBlock)) {
            return this.lastCheckedCanHarvestResult;
        }
        this.lastCheckedCanHarvestBlock = cachedBlockPosition;
        if (this.hasTag() && this.tag.containsKey("CanDestroy", 9)) {
            final ListTag listTag3 = this.tag.getList("CanDestroy", 8);
            for (int integer4 = 0; integer4 < listTag3.size(); ++integer4) {
                final String string5 = listTag3.getString(integer4);
                try {
                    final Predicate<CachedBlockPosition> predicate6 = BlockPredicateArgumentType.create().a(new StringReader(string5)).create(tagManager);
                    if (predicate6.test(cachedBlockPosition)) {
                        return this.lastCheckedCanHarvestResult = true;
                    }
                }
                catch (CommandSyntaxException ex) {}
            }
        }
        return this.lastCheckedCanHarvestResult = false;
    }
    
    public boolean getCustomCanPlace(final TagManager tagManager, final CachedBlockPosition cachedBlockPosition) {
        if (areBlocksEqual(cachedBlockPosition, this.lastCheckedCanPlaceBlock)) {
            return this.lastCheckedCanPlaceResult;
        }
        this.lastCheckedCanPlaceBlock = cachedBlockPosition;
        if (this.hasTag() && this.tag.containsKey("CanPlaceOn", 9)) {
            final ListTag listTag3 = this.tag.getList("CanPlaceOn", 8);
            for (int integer4 = 0; integer4 < listTag3.size(); ++integer4) {
                final String string5 = listTag3.getString(integer4);
                try {
                    final Predicate<CachedBlockPosition> predicate6 = BlockPredicateArgumentType.create().a(new StringReader(string5)).create(tagManager);
                    if (predicate6.test(cachedBlockPosition)) {
                        return this.lastCheckedCanPlaceResult = true;
                    }
                }
                catch (CommandSyntaxException ex) {}
            }
        }
        return this.lastCheckedCanPlaceResult = false;
    }
    
    public int getUpdateCooldown() {
        return this.updateCooldown;
    }
    
    public void setUpdateCooldown(final int integer) {
        this.updateCooldown = integer;
    }
    
    public int getAmount() {
        return this.empty ? 0 : this.amount;
    }
    
    public void setAmount(final int integer) {
        this.amount = integer;
        this.updateEmptyFlag();
    }
    
    public void addAmount(final int integer) {
        this.setAmount(this.amount + integer);
    }
    
    public void subtractAmount(final int integer) {
        this.addAmount(-integer);
    }
    
    public void b(final World world, final LivingEntity livingEntity, final int integer) {
        this.getItem().onUsingTick(world, livingEntity, this, integer);
    }
    
    public boolean isFood() {
        return this.getItem().isFood();
    }
    
    static {
        LOGGER = LogManager.getLogger();
        EMPTY = new ItemStack((ItemProvider)null);
        MODIFIER_FORMAT = createModifierFormat();
    }
}
