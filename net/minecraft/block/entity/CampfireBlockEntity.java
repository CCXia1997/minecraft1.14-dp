package net.minecraft.block.entity;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.CompoundTag;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.particle.ParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ItemScatterer;
import net.minecraft.recipe.cooking.CampfireCookingRecipe;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.state.property.Property;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.Clearable;

public class CampfireBlockEntity extends BlockEntity implements Clearable, Tickable
{
    private final DefaultedList<ItemStack> itemsBeingCooked;
    private final int[] cookingTimes;
    private final int[] cookingTotalTimes;
    
    public CampfireBlockEntity() {
        super(BlockEntityType.CAMPFIRE);
        this.itemsBeingCooked = DefaultedList.<ItemStack>create(4, ItemStack.EMPTY);
        this.cookingTimes = new int[4];
        this.cookingTotalTimes = new int[4];
    }
    
    @Override
    public void tick() {
        final BlockState blockState1 = this.hasWorld() ? this.getCachedState() : null;
        if (blockState1 == null || blockState1.getBlock() != Blocks.lV) {
            return;
        }
        final boolean boolean2 = blockState1.<Boolean>get((Property<Boolean>)CampfireBlock.LIT);
        final boolean boolean3 = this.world.isClient;
        if (boolean3) {
            if (boolean2) {
                this.spawnSmokeParticles();
            }
            return;
        }
        if (boolean2) {
            this.updateItemsBeingCooked();
        }
        else {
            for (int integer4 = 0; integer4 < this.itemsBeingCooked.size(); ++integer4) {
                if (this.cookingTimes[integer4] > 0) {
                    this.cookingTimes[integer4] = MathHelper.clamp(this.cookingTimes[integer4] - 2, 0, this.cookingTotalTimes[integer4]);
                }
            }
        }
    }
    
    private void updateItemsBeingCooked() {
        for (int integer1 = 0; integer1 < this.itemsBeingCooked.size(); ++integer1) {
            final ItemStack itemStack2 = this.itemsBeingCooked.get(integer1);
            if (!itemStack2.isEmpty()) {
                final int[] cookingTimes = this.cookingTimes;
                final int n = integer1;
                ++cookingTimes[n];
                if (this.cookingTimes[integer1] >= this.cookingTotalTimes[integer1]) {
                    final Inventory inventory3 = new BasicInventory(new ItemStack[] { itemStack2 });
                    final ItemStack itemStack3 = this.world.getRecipeManager().<Inventory, CampfireCookingRecipe>getFirstMatch(RecipeType.CAMPFIRE_COOKING, inventory3, this.world).<ItemStack>map(campfireCookingRecipe -> campfireCookingRecipe.craft(inventory3)).orElse(itemStack2);
                    final BlockPos blockPos5 = this.getPos();
                    ItemScatterer.spawn(this.world, blockPos5.getX(), blockPos5.getY(), blockPos5.getZ(), itemStack3);
                    this.itemsBeingCooked.set(integer1, ItemStack.EMPTY);
                    this.updateListeners();
                }
            }
        }
    }
    
    private void spawnSmokeParticles() {
        final World world1 = this.getWorld();
        if (world1 == null) {
            return;
        }
        final BlockPos blockPos2 = this.getPos();
        final Random random3 = world1.random;
        if (random3.nextFloat() < 0.11f) {
            for (int integer4 = 0; integer4 < random3.nextInt(2) + 2; ++integer4) {
                CampfireBlock.spawnSmokeParticle(world1, blockPos2, this.getCachedState().<Boolean>get((Property<Boolean>)CampfireBlock.SIGNAL_FIRE), false);
            }
        }
        int integer4 = this.getCachedState().<Direction>get((Property<Direction>)CampfireBlock.FACING).getHorizontal();
        for (int integer5 = 0; integer5 < this.itemsBeingCooked.size(); ++integer5) {
            if (!this.itemsBeingCooked.get(integer5).isEmpty() && random3.nextFloat() < 0.2f) {
                final Direction direction6 = Direction.fromHorizontal(Math.floorMod(integer5 + integer4, 4));
                final float float7 = 0.3125f;
                final double double8 = blockPos2.getX() + 0.5 - direction6.getOffsetX() * 0.3125f + direction6.rotateYClockwise().getOffsetX() * 0.3125f;
                final double double9 = blockPos2.getY() + 0.5;
                final double double10 = blockPos2.getZ() + 0.5 - direction6.getOffsetZ() * 0.3125f + direction6.rotateYClockwise().getOffsetZ() * 0.3125f;
                for (int integer6 = 0; integer6 < 4; ++integer6) {
                    world1.addParticle(ParticleTypes.Q, double8, double9, double10, 0.0, 5.0E-4, 0.0);
                }
            }
        }
    }
    
    public DefaultedList<ItemStack> getItemsBeingCooked() {
        return this.itemsBeingCooked;
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.itemsBeingCooked.clear();
        Inventories.fromTag(compoundTag, this.itemsBeingCooked);
        if (compoundTag.containsKey("CookingTimes", 11)) {
            final int[] arr2 = compoundTag.getIntArray("CookingTimes");
            System.arraycopy(arr2, 0, this.cookingTimes, 0, Math.min(this.cookingTotalTimes.length, arr2.length));
        }
        if (compoundTag.containsKey("CookingTotalTimes", 11)) {
            final int[] arr2 = compoundTag.getIntArray("CookingTotalTimes");
            System.arraycopy(arr2, 0, this.cookingTotalTimes, 0, Math.min(this.cookingTotalTimes.length, arr2.length));
        }
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        this.saveInitialChunkData(compoundTag);
        compoundTag.putIntArray("CookingTimes", this.cookingTimes);
        compoundTag.putIntArray("CookingTotalTimes", this.cookingTotalTimes);
        return compoundTag;
    }
    
    private CompoundTag saveInitialChunkData(final CompoundTag tag) {
        super.toTag(tag);
        Inventories.toTag(tag, this.itemsBeingCooked, true);
        return tag;
    }
    
    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 13, this.toInitialChunkDataTag());
    }
    
    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.saveInitialChunkData(new CompoundTag());
    }
    
    public Optional<CampfireCookingRecipe> getRecipeFor(final ItemStack item) {
        if (this.itemsBeingCooked.stream().noneMatch(ItemStack::isEmpty)) {
            return Optional.<CampfireCookingRecipe>empty();
        }
        return this.world.getRecipeManager().<BasicInventory, CampfireCookingRecipe>getFirstMatch(RecipeType.CAMPFIRE_COOKING, new BasicInventory(new ItemStack[] { item }), this.world);
    }
    
    public boolean addItem(final ItemStack item, final int integer) {
        for (int integer2 = 0; integer2 < this.itemsBeingCooked.size(); ++integer2) {
            final ItemStack itemStack4 = this.itemsBeingCooked.get(integer2);
            if (itemStack4.isEmpty()) {
                this.cookingTotalTimes[integer2] = integer;
                this.cookingTimes[integer2] = 0;
                this.itemsBeingCooked.set(integer2, item.split(1));
                this.updateListeners();
                return true;
            }
        }
        return false;
    }
    
    private void updateListeners() {
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }
    
    @Override
    public void clear() {
        this.itemsBeingCooked.clear();
    }
    
    public void spawnItemsBeingCooked() {
        if (!this.getWorld().isClient) {
            ItemScatterer.spawn(this.getWorld(), this.getPos(), this.getItemsBeingCooked());
        }
        this.updateListeners();
    }
}
