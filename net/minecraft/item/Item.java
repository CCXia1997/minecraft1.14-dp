package net.minecraft.item;

import net.minecraft.util.AbsoluteHand;
import net.minecraft.tag.Tag;
import com.google.common.collect.HashMultimap;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import com.google.common.collect.Multimap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.client.item.TooltipContext;
import java.util.List;
import net.minecraft.util.UseAction;
import net.minecraft.entity.Entity;
import net.minecraft.util.SystemUtil;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import com.google.common.collect.Maps;
import net.minecraft.util.registry.Registry;
import javax.annotation.Nullable;
import net.minecraft.util.Rarity;
import net.minecraft.util.Identifier;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import java.util.Map;

public class Item implements ItemProvider
{
    public static final Map<Block, Item> BLOCK_ITEM_MAP;
    private static final ItemPropertyGetter GETTER_DAMAGED;
    private static final ItemPropertyGetter GETTER_DAMAGE;
    private static final ItemPropertyGetter GETTER_HAND;
    private static final ItemPropertyGetter GETTER_COOLDOWN;
    private static final ItemPropertyGetter GETTER_CUSTOM_MODEL_DATA;
    protected static final UUID MODIFIER_DAMAGE;
    protected static final UUID MODIFIER_SWING_SPEED;
    protected static final Random random;
    private final Map<Identifier, ItemPropertyGetter> PROPERTIES;
    protected final ItemGroup itemGroup;
    private final Rarity rarity;
    private final int fullStackSize;
    private final int durability;
    private final Item recipeRemainder;
    @Nullable
    private String translationKey;
    @Nullable
    private final FoodItemSetting foodSetting;
    
    public static int getRawIdByItem(final Item item) {
        return (item == null) ? 0 : Registry.ITEM.getRawId(item);
    }
    
    public static Item byRawId(final int id) {
        return Registry.ITEM.get(id);
    }
    
    @Deprecated
    public static Item getItemFromBlock(final Block block) {
        return Item.BLOCK_ITEM_MAP.getOrDefault(block, Items.AIR);
    }
    
    public Item(final Settings settings) {
        this.PROPERTIES = Maps.newHashMap();
        this.addProperty(new Identifier("lefthanded"), Item.GETTER_HAND);
        this.addProperty(new Identifier("cooldown"), Item.GETTER_COOLDOWN);
        this.addProperty(new Identifier("custom_model_data"), Item.GETTER_CUSTOM_MODEL_DATA);
        this.itemGroup = settings.itemGroup;
        this.rarity = settings.rarity;
        this.recipeRemainder = settings.recipeRemainder;
        this.durability = settings.durability;
        this.fullStackSize = settings.fullStackSize;
        this.foodSetting = settings.foodSetting;
        if (this.durability > 0) {
            this.addProperty(new Identifier("damaged"), Item.GETTER_DAMAGED);
            this.addProperty(new Identifier("damage"), Item.GETTER_DAMAGE);
        }
    }
    
    public void onUsingTick(final World world, final LivingEntity entity, final ItemStack stack, final int timeLeft) {
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public ItemPropertyGetter getProperty(final Identifier identifier) {
        return this.PROPERTIES.get(identifier);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean hasProperties() {
        return !this.PROPERTIES.isEmpty();
    }
    
    public boolean onTagDeserialized(final CompoundTag tag) {
        return false;
    }
    
    public boolean beforeBlockBreak(final BlockState blockState, final World world, final BlockPos position, final PlayerEntity player) {
        return true;
    }
    
    @Override
    public Item getItem() {
        return this;
    }
    
    public final void addProperty(final Identifier id, final ItemPropertyGetter itemPropertyGetter) {
        this.PROPERTIES.put(id, itemPropertyGetter);
    }
    
    public ActionResult useOnBlock(final ItemUsageContext usageContext) {
        return ActionResult.PASS;
    }
    
    public float getBlockBreakingSpeed(final ItemStack stack, final BlockState blockState) {
        return 1.0f;
    }
    
    public TypedActionResult<ItemStack> use(final World world, final PlayerEntity player, final Hand hand) {
        if (!this.isFood()) {
            return new TypedActionResult<ItemStack>(ActionResult.PASS, player.getStackInHand(hand));
        }
        final ItemStack itemStack4 = player.getStackInHand(hand);
        if (player.canConsume(this.getFoodSetting().isAlwaysEdible())) {
            player.setCurrentHand(hand);
            return new TypedActionResult<ItemStack>(ActionResult.a, itemStack4);
        }
        return new TypedActionResult<ItemStack>(ActionResult.c, itemStack4);
    }
    
    public ItemStack onItemFinishedUsing(final ItemStack stack, final World world, final LivingEntity livingEntity) {
        if (this.isFood()) {
            return livingEntity.eatFood(world, stack);
        }
        return stack;
    }
    
    public final int getMaxAmount() {
        return this.fullStackSize;
    }
    
    public final int getDurability() {
        return this.durability;
    }
    
    public boolean canDamage() {
        return this.durability > 0;
    }
    
    public boolean onEntityDamaged(final ItemStack stack, final LivingEntity target, final LivingEntity livingEntity) {
        return false;
    }
    
    public boolean onBlockBroken(final ItemStack stack, final World world, final BlockState state, final BlockPos pos, final LivingEntity livingEntity) {
        return false;
    }
    
    public boolean isEffectiveOn(final BlockState blockState) {
        return false;
    }
    
    public boolean interactWithEntity(final ItemStack stack, final PlayerEntity player, final LivingEntity target, final Hand hand) {
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    public TextComponent getTextComponent() {
        return new TranslatableTextComponent(this.getTranslationKey(), new Object[0]);
    }
    
    protected String getOrCreateTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = SystemUtil.createTranslationKey("item", Registry.ITEM.getId(this));
        }
        return this.translationKey;
    }
    
    public String getTranslationKey() {
        return this.getOrCreateTranslationKey();
    }
    
    public String getTranslationKey(final ItemStack stack) {
        return this.getTranslationKey();
    }
    
    public boolean requiresClientSync() {
        return true;
    }
    
    @Nullable
    public final Item getRecipeRemainder() {
        return this.recipeRemainder;
    }
    
    public boolean hasRecipeRemainder() {
        return this.recipeRemainder != null;
    }
    
    public void onEntityTick(final ItemStack stack, final World world, final Entity entity, final int invSlot, final boolean selected) {
    }
    
    public void onCrafted(final ItemStack stack, final World world, final PlayerEntity player) {
    }
    
    public boolean isMap() {
        return false;
    }
    
    public UseAction getUseAction(final ItemStack stack) {
        return stack.getItem().isFood() ? UseAction.b : UseAction.a;
    }
    
    public int getMaxUseTime(final ItemStack stack) {
        if (stack.getItem().isFood()) {
            return this.getFoodSetting().isEatenFast() ? 16 : 32;
        }
        return 0;
    }
    
    public void onItemStopUsing(final ItemStack stack, final World world, final LivingEntity player, final int integer) {
    }
    
    @Environment(EnvType.CLIENT)
    public void buildTooltip(final ItemStack stack, @Nullable final World world, final List<TextComponent> tooltip, final TooltipContext options) {
    }
    
    public TextComponent getTranslatedNameTrimmed(final ItemStack stack) {
        return new TranslatableTextComponent(this.getTranslationKey(stack), new Object[0]);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean hasEnchantmentGlint(final ItemStack stack) {
        return stack.hasEnchantments();
    }
    
    public Rarity getRarity(final ItemStack stack) {
        if (!stack.hasEnchantments()) {
            return this.rarity;
        }
        switch (this.rarity) {
            case a:
            case b: {
                return Rarity.c;
            }
            case c: {
                return Rarity.d;
            }
            default: {
                return this.rarity;
            }
        }
    }
    
    public boolean isTool(final ItemStack stack) {
        return this.getMaxAmount() == 1 && this.canDamage();
    }
    
    protected static HitResult getHitResult(final World world, final PlayerEntity playerEntity, final RayTraceContext.FluidHandling fluidHandling) {
        final float float4 = playerEntity.pitch;
        final float float5 = playerEntity.yaw;
        final Vec3d vec3d6 = playerEntity.getCameraPosVec(1.0f);
        final float float6 = MathHelper.cos(-float5 * 0.017453292f - 3.1415927f);
        final float float7 = MathHelper.sin(-float5 * 0.017453292f - 3.1415927f);
        final float float8 = -MathHelper.cos(-float4 * 0.017453292f);
        final float float9 = MathHelper.sin(-float4 * 0.017453292f);
        final float float10 = float7 * float8;
        final float float11 = float9;
        final float float12 = float6 * float8;
        final double double14 = 5.0;
        final Vec3d vec3d7 = vec3d6.add(float10 * 5.0, float11 * 5.0, float12 * 5.0);
        return world.rayTrace(new RayTraceContext(vec3d6, vec3d7, RayTraceContext.ShapeType.b, fluidHandling, playerEntity));
    }
    
    public int getEnchantability() {
        return 0;
    }
    
    public void appendItemsForGroup(final ItemGroup itemGroup, final DefaultedList<ItemStack> defaultedList) {
        if (this.isInItemGroup(itemGroup)) {
            defaultedList.add(new ItemStack(this));
        }
    }
    
    protected boolean isInItemGroup(final ItemGroup itemGroup) {
        final ItemGroup itemGroup2 = this.getItemGroup();
        return itemGroup2 != null && (itemGroup == ItemGroup.SEARCH || itemGroup == itemGroup2);
    }
    
    @Nullable
    public final ItemGroup getItemGroup() {
        return this.itemGroup;
    }
    
    public boolean canRepair(final ItemStack tool, final ItemStack ingredient) {
        return false;
    }
    
    public Multimap<String, EntityAttributeModifier> getAttributeModifiers(final EquipmentSlot equiptmentSlot) {
        return HashMultimap.create();
    }
    
    public boolean i(final ItemStack itemStack) {
        return itemStack.getItem() == Items.py;
    }
    
    @Environment(EnvType.CLIENT)
    public ItemStack getDefaultStack() {
        return new ItemStack(this);
    }
    
    public boolean matches(final Tag<Item> tag) {
        return tag.contains(this);
    }
    
    public boolean isFood() {
        return this.foodSetting != null;
    }
    
    @Nullable
    public FoodItemSetting getFoodSetting() {
        return this.foodSetting;
    }
    
    static {
        BLOCK_ITEM_MAP = Maps.newHashMap();
        GETTER_DAMAGED = ((itemStack, world, livingEntity) -> itemStack.isDamaged() ? 1.0f : 0.0f);
        GETTER_DAMAGE = ((itemStack, world, livingEntity) -> MathHelper.clamp(itemStack.getDamage() / (float)itemStack.getDurability(), 0.0f, 1.0f));
        GETTER_HAND = ((itemStack, world, livingEntity) -> (livingEntity == null || livingEntity.getMainHand() == AbsoluteHand.b) ? 0.0f : 1.0f);
        GETTER_COOLDOWN = ((itemStack, world, livingEntity) -> (livingEntity instanceof PlayerEntity) ? livingEntity.getItemCooldownManager().getCooldownProgress(itemStack.getItem(), 0.0f) : 0.0f);
        GETTER_CUSTOM_MODEL_DATA = ((itemStack, world, livingEntity) -> itemStack.hasTag() ? ((float)itemStack.getTag().getInt("CustomModelData")) : 0.0f);
        MODIFIER_DAMAGE = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
        MODIFIER_SWING_SPEED = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
        random = new Random();
    }
    
    public static class Settings
    {
        private int fullStackSize;
        private int durability;
        private Item recipeRemainder;
        private ItemGroup itemGroup;
        private Rarity rarity;
        private FoodItemSetting foodSetting;
        
        public Settings() {
            this.fullStackSize = 64;
            this.rarity = Rarity.a;
        }
        
        public Settings food(final FoodItemSetting foodItemSetting) {
            this.foodSetting = foodItemSetting;
            return this;
        }
        
        public Settings stackSize(final int fullStackSize) {
            if (this.durability > 0) {
                throw new RuntimeException("Unable to have damage AND stack.");
            }
            this.fullStackSize = fullStackSize;
            return this;
        }
        
        public Settings durabilityIfNotSet(final int durability) {
            return (this.durability == 0) ? this.durability(durability) : this;
        }
        
        public Settings durability(final int durability) {
            this.durability = durability;
            this.fullStackSize = 1;
            return this;
        }
        
        public Settings recipeRemainder(final Item recipeRemainder) {
            this.recipeRemainder = recipeRemainder;
            return this;
        }
        
        public Settings itemGroup(final ItemGroup itemGroup) {
            this.itemGroup = itemGroup;
            return this;
        }
        
        public Settings rarity(final Rarity rarity) {
            this.rarity = rarity;
            return this;
        }
    }
}
