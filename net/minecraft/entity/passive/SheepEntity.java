package net.minecraft.entity.passive;

import java.util.stream.Collectors;
import java.util.Arrays;
import net.minecraft.util.SystemUtil;
import com.google.common.collect.Maps;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.block.Blocks;
import java.util.EnumMap;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityPose;
import net.minecraft.container.ContainerType;
import net.minecraft.container.Container;
import java.util.function.Predicate;
import net.minecraft.item.DyeItem;
import java.util.function.Function;
import net.minecraft.recipe.crafting.CraftingRecipe;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.recipe.RecipeType;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.SpawnType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.IWorld;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.entity.ItemEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.loot.LootTables;
import net.minecraft.util.Identifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.recipe.Ingredient;
import net.minecraft.item.Items;
import net.minecraft.entity.ai.goal.AnimalMateGoal;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import net.minecraft.entity.EntityType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.item.ItemProvider;
import net.minecraft.util.DyeColor;
import java.util.Map;
import net.minecraft.entity.data.TrackedData;

public class SheepEntity extends AnimalEntity
{
    private static final TrackedData<Byte> COLOR;
    private static final Map<DyeColor, ItemProvider> DROPS;
    private static final Map<DyeColor, float[]> COLORS;
    private int bD;
    private EatGrassGoal eatGrassGoal;
    
    private static float[] getDyedColor(final DyeColor color) {
        if (color == DyeColor.a) {
            return new float[] { 0.9019608f, 0.9019608f, 0.9019608f };
        }
        final float[] arr2 = color.getColorComponents();
        final float float3 = 0.75f;
        return new float[] { arr2[0] * 0.75f, arr2[1] * 0.75f, arr2[2] * 0.75f };
    }
    
    @Environment(EnvType.CLIENT)
    public static float[] getRgbColor(final DyeColor dyeColor) {
        return SheepEntity.COLORS.get(dyeColor);
    }
    
    public SheepEntity(final EntityType<? extends SheepEntity> type, final World world) {
        super(type, world);
    }
    
    @Override
    protected void initGoals() {
        this.eatGrassGoal = new EatGrassGoal(this);
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.25));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0));
        this.goalSelector.add(3, new TemptGoal(this, 1.1, Ingredient.ofItems(Items.jP), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.1));
        this.goalSelector.add(5, this.eatGrassGoal);
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }
    
    @Override
    protected void mobTick() {
        this.bD = this.eatGrassGoal.getTimer();
        super.mobTick();
    }
    
    @Override
    public void updateState() {
        if (this.world.isClient) {
            this.bD = Math.max(0, this.bD - 1);
        }
        super.updateState();
    }
    
    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(8.0);
        this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513);
    }
    
    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.<Byte>startTracking(SheepEntity.COLOR, (Byte)0);
    }
    
    public Identifier getLootTableId() {
        if (this.isSheared()) {
            return this.getType().getLootTableId();
        }
        switch (this.getColor()) {
            default: {
                return LootTables.ENTITY_SHEEP_WHITE;
            }
            case b: {
                return LootTables.ENTITY_SHEEP_ORANGE;
            }
            case c: {
                return LootTables.ENTITY_SHEEP_MAGENTA;
            }
            case d: {
                return LootTables.ENTITY_SHEEP_LIGHT_BLUE;
            }
            case e: {
                return LootTables.ENTITY_SHEEP_YELLOW;
            }
            case f: {
                return LootTables.ENTITY_SHEEP_LIME;
            }
            case g: {
                return LootTables.ENTITY_SHEEP_PINK;
            }
            case h: {
                return LootTables.ENTITY_SHEEP_GRAY;
            }
            case i: {
                return LootTables.ENTITY_SHEEP_LIGHT_GRAY;
            }
            case j: {
                return LootTables.ENTITY_SHEEP_CYAN;
            }
            case k: {
                return LootTables.ENTITY_SHEEP_PURPLE;
            }
            case l: {
                return LootTables.ENTITY_SHEEP_BLUE;
            }
            case m: {
                return LootTables.ENTITY_SHEEP_BROWN;
            }
            case n: {
                return LootTables.ENTITY_SHEEP_GREEN;
            }
            case o: {
                return LootTables.ENTITY_SHEEP_RED;
            }
            case BLACK: {
                return LootTables.ENTITY_SHEEP_BLACK;
            }
        }
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public void handleStatus(final byte status) {
        if (status == 10) {
            this.bD = 40;
        }
        else {
            super.handleStatus(status);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public float v(final float float1) {
        if (this.bD <= 0) {
            return 0.0f;
        }
        if (this.bD >= 4 && this.bD <= 36) {
            return 1.0f;
        }
        if (this.bD < 4) {
            return (this.bD - float1) / 4.0f;
        }
        return -(this.bD - 40 - float1) / 4.0f;
    }
    
    @Environment(EnvType.CLIENT)
    public float w(final float float1) {
        if (this.bD > 4 && this.bD <= 36) {
            final float float2 = (this.bD - 4 - float1) / 32.0f;
            return 0.62831855f + 0.21991149f * MathHelper.sin(float2 * 28.7f);
        }
        if (this.bD > 0) {
            return 0.62831855f;
        }
        return this.pitch * 0.017453292f;
    }
    
    @Override
    public boolean interactMob(final PlayerEntity player, final Hand hand) {
        final ItemStack itemStack3 = player.getStackInHand(hand);
        if (itemStack3.getItem() == Items.lW && !this.isSheared() && !this.isChild()) {
            this.dropItems();
            if (!this.world.isClient) {
                itemStack3.<PlayerEntity>applyDamage(1, player, playerEntity -> playerEntity.sendToolBreakStatus(hand));
            }
        }
        return super.interactMob(player, hand);
    }
    
    public void dropItems() {
        if (!this.world.isClient) {
            this.setSheared(true);
            for (int integer1 = 1 + this.random.nextInt(3), integer2 = 0; integer2 < integer1; ++integer2) {
                final ItemEntity itemEntity3 = this.dropItem(SheepEntity.DROPS.get(this.getColor()), 1);
                if (itemEntity3 != null) {
                    itemEntity3.setVelocity(itemEntity3.getVelocity().add((this.random.nextFloat() - this.random.nextFloat()) * 0.1f, this.random.nextFloat() * 0.05f, (this.random.nextFloat() - this.random.nextFloat()) * 0.1f));
                }
            }
        }
        this.playSound(SoundEvents.jS, 1.0f, 1.0f);
    }
    
    @Override
    public void writeCustomDataToTag(final CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putBoolean("Sheared", this.isSheared());
        tag.putByte("Color", (byte)this.getColor().getId());
    }
    
    @Override
    public void readCustomDataFromTag(final CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setSheared(tag.getBoolean("Sheared"));
        this.setColor(DyeColor.byId(tag.getByte("Color")));
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.jP;
    }
    
    @Override
    protected SoundEvent getHurtSound(final DamageSource source) {
        return SoundEvents.jR;
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.jQ;
    }
    
    @Override
    protected void playStepSound(final BlockPos pos, final BlockState state) {
        this.playSound(SoundEvents.jT, 0.15f, 1.0f);
    }
    
    public DyeColor getColor() {
        return DyeColor.byId(this.dataTracker.<Byte>get(SheepEntity.COLOR) & 0xF);
    }
    
    public void setColor(final DyeColor dyeColor) {
        final byte byte2 = this.dataTracker.<Byte>get(SheepEntity.COLOR);
        this.dataTracker.<Byte>set(SheepEntity.COLOR, (byte)((byte2 & 0xF0) | (dyeColor.getId() & 0xF)));
    }
    
    public boolean isSheared() {
        return (this.dataTracker.<Byte>get(SheepEntity.COLOR) & 0x10) != 0x0;
    }
    
    public void setSheared(final boolean boolean1) {
        final byte byte2 = this.dataTracker.<Byte>get(SheepEntity.COLOR);
        if (boolean1) {
            this.dataTracker.<Byte>set(SheepEntity.COLOR, (byte)(byte2 | 0x10));
        }
        else {
            this.dataTracker.<Byte>set(SheepEntity.COLOR, (byte)(byte2 & 0xFFFFFFEF));
        }
    }
    
    public static DyeColor generateDefaultColor(final Random random) {
        final int integer2 = random.nextInt(100);
        if (integer2 < 5) {
            return DyeColor.BLACK;
        }
        if (integer2 < 10) {
            return DyeColor.h;
        }
        if (integer2 < 15) {
            return DyeColor.i;
        }
        if (integer2 < 18) {
            return DyeColor.m;
        }
        if (random.nextInt(500) == 0) {
            return DyeColor.g;
        }
        return DyeColor.a;
    }
    
    @Override
    public SheepEntity createChild(final PassiveEntity mate) {
        final SheepEntity sheepEntity2 = (SheepEntity)mate;
        final SheepEntity sheepEntity3 = EntityType.SHEEP.create(this.world);
        sheepEntity3.setColor(this.getChildColor(this, sheepEntity2));
        return sheepEntity3;
    }
    
    @Override
    public void onEatingGrass() {
        this.setSheared(false);
        if (this.isChild()) {
            this.growUp(60);
        }
    }
    
    @Nullable
    @Override
    public EntityData initialize(final IWorld iWorld, final LocalDifficulty localDifficulty, final SpawnType difficulty, @Nullable EntityData entityData, @Nullable final CompoundTag compoundTag) {
        entityData = super.initialize(iWorld, localDifficulty, difficulty, entityData, compoundTag);
        this.setColor(generateDefaultColor(iWorld.getRandom()));
        return entityData;
    }
    
    private DyeColor getChildColor(final AnimalEntity animalEntity1, final AnimalEntity animalEntity2) {
        final DyeColor dyeColor3 = ((SheepEntity)animalEntity1).getColor();
        final DyeColor dyeColor4 = ((SheepEntity)animalEntity2).getColor();
        final CraftingInventory craftingInventory5 = a(dyeColor3, dyeColor4);
        final Object o;
        final Object o2;
        return this.world.getRecipeManager().<CraftingInventory, CraftingRecipe>getFirstMatch(RecipeType.CRAFTING, craftingInventory5, this.world).map(craftingRecipe -> craftingRecipe.craft(craftingInventory5)).map(ItemStack::getItem).filter(DyeItem.class::isInstance).map(DyeItem.class::cast).<DyeColor>map(DyeItem::getColor).orElseGet(() -> this.world.random.nextBoolean() ? o : o2);
    }
    
    private static CraftingInventory a(final DyeColor dyeColor1, final DyeColor dyeColor2) {
        final CraftingInventory craftingInventory3 = new CraftingInventory(new Container(null, -1) {
            @Override
            public boolean canUse(final PlayerEntity player) {
                return false;
            }
        }, 2, 1);
        craftingInventory3.setInvStack(0, new ItemStack(DyeItem.fromColor(dyeColor1)));
        craftingInventory3.setInvStack(1, new ItemStack(DyeItem.fromColor(dyeColor2)));
        return craftingInventory3;
    }
    
    @Override
    protected float getActiveEyeHeight(final EntityPose entityPose, final EntitySize entitySize) {
        return 0.95f * entitySize.height;
    }
    
    static {
        COLOR = DataTracker.<Byte>registerData(SheepEntity.class, TrackedDataHandlerRegistry.BYTE);
        DROPS = SystemUtil.<Map<DyeColor, ItemProvider>>consume(Maps.newEnumMap(DyeColor.class), enumMap -> {
            enumMap.put(DyeColor.a, Blocks.aX);
            enumMap.put(DyeColor.b, Blocks.aY);
            enumMap.put(DyeColor.c, Blocks.aZ);
            enumMap.put(DyeColor.d, Blocks.ba);
            enumMap.put(DyeColor.e, Blocks.bb);
            enumMap.put(DyeColor.f, Blocks.bc);
            enumMap.put(DyeColor.g, Blocks.bd);
            enumMap.put(DyeColor.h, Blocks.be);
            enumMap.put(DyeColor.i, Blocks.bf);
            enumMap.put(DyeColor.j, Blocks.bg);
            enumMap.put(DyeColor.k, Blocks.bh);
            enumMap.put(DyeColor.l, Blocks.bi);
            enumMap.put(DyeColor.m, Blocks.bj);
            enumMap.put(DyeColor.n, Blocks.bk);
            enumMap.put(DyeColor.o, Blocks.bl);
            enumMap.put(DyeColor.BLACK, Blocks.bm);
            return;
        });
        COLORS = Maps.<Enum, Object>newEnumMap(Arrays.<DyeColor>stream(DyeColor.values()).collect(Collectors.toMap(dyeColor -> dyeColor, SheepEntity::getDyedColor)));
    }
}
