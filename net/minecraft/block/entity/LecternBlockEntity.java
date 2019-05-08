package net.minecraft.block.entity;

import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.container.LecternContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.TextComponent;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.StringTextComponent;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.item.WrittenBookItem;
import javax.annotation.Nullable;
import net.minecraft.block.LecternBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.inventory.Inventory;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.util.Clearable;

public class LecternBlockEntity extends BlockEntity implements Clearable, NameableContainerProvider
{
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;
    private ItemStack book;
    private int currentPage;
    private int pageCount;
    
    public LecternBlockEntity() {
        super(BlockEntityType.LECTERN);
        this.inventory = new Inventory() {
            @Override
            public int getInvSize() {
                return 1;
            }
            
            @Override
            public boolean isInvEmpty() {
                return LecternBlockEntity.this.book.isEmpty();
            }
            
            @Override
            public ItemStack getInvStack(final int slot) {
                return (slot == 0) ? LecternBlockEntity.this.book : ItemStack.EMPTY;
            }
            
            @Override
            public ItemStack takeInvStack(final int slot, final int integer2) {
                if (slot == 0) {
                    final ItemStack itemStack3 = LecternBlockEntity.this.book.split(integer2);
                    if (LecternBlockEntity.this.book.isEmpty()) {
                        LecternBlockEntity.this.onBookRemoved();
                    }
                    return itemStack3;
                }
                return ItemStack.EMPTY;
            }
            
            @Override
            public ItemStack removeInvStack(final int slot) {
                if (slot == 0) {
                    final ItemStack itemStack2 = LecternBlockEntity.this.book;
                    LecternBlockEntity.this.book = ItemStack.EMPTY;
                    LecternBlockEntity.this.onBookRemoved();
                    return itemStack2;
                }
                return ItemStack.EMPTY;
            }
            
            @Override
            public void setInvStack(final int slot, final ItemStack itemStack) {
            }
            
            @Override
            public int getInvMaxStackAmount() {
                return 1;
            }
            
            @Override
            public void markDirty() {
                LecternBlockEntity.this.markDirty();
            }
            
            @Override
            public boolean canPlayerUseInv(final PlayerEntity playerEntity) {
                return LecternBlockEntity.this.world.getBlockEntity(LecternBlockEntity.this.pos) == LecternBlockEntity.this && playerEntity.squaredDistanceTo(LecternBlockEntity.this.pos.getX() + 0.5, LecternBlockEntity.this.pos.getY() + 0.5, LecternBlockEntity.this.pos.getZ() + 0.5) <= 64.0 && LecternBlockEntity.this.hasBook();
            }
            
            @Override
            public boolean isValidInvStack(final int slot, final ItemStack itemStack) {
                return false;
            }
            
            @Override
            public void clear() {
            }
        };
        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(final int key) {
                return (key == 0) ? LecternBlockEntity.this.currentPage : 0;
            }
            
            @Override
            public void set(final int key, final int value) {
                if (key == 0) {
                    LecternBlockEntity.this.setCurrentPage(value);
                }
            }
            
            @Override
            public int size() {
                return 1;
            }
        };
        this.book = ItemStack.EMPTY;
    }
    
    public ItemStack getBook() {
        return this.book;
    }
    
    public boolean hasBook() {
        final Item item1 = this.book.getItem();
        return item1 == Items.nD || item1 == Items.nE;
    }
    
    public void setBook(final ItemStack book) {
        this.setBook(book, null);
    }
    
    private void onBookRemoved() {
        this.currentPage = 0;
        this.pageCount = 0;
        LecternBlock.setHasBook(this.getWorld(), this.getPos(), this.getCachedState(), false);
    }
    
    public void setBook(final ItemStack book, @Nullable final PlayerEntity player) {
        this.book = this.resolveBook(book, player);
        this.currentPage = 0;
        this.pageCount = WrittenBookItem.getPageCount(this.book);
        this.markDirty();
    }
    
    private void setCurrentPage(final int currentPage) {
        final int integer2 = MathHelper.clamp(currentPage, 0, this.pageCount - 1);
        if (integer2 != this.currentPage) {
            this.currentPage = integer2;
            this.markDirty();
            LecternBlock.setPowered(this.getWorld(), this.getPos(), this.getCachedState());
        }
    }
    
    public int getCurrentPage() {
        return this.currentPage;
    }
    
    public int getComparatorOutput() {
        final float float1 = (this.pageCount > 1) ? (this.getCurrentPage() / (this.pageCount - 1.0f)) : 1.0f;
        return MathHelper.floor(float1 * 14.0f) + (this.hasBook() ? 1 : 0);
    }
    
    private ItemStack resolveBook(final ItemStack book, @Nullable final PlayerEntity player) {
        if (this.world instanceof ServerWorld && book.getItem() == Items.nE) {
            WrittenBookItem.resolve(book, this.getCommandSource(player), player);
        }
        return book;
    }
    
    private ServerCommandSource getCommandSource(@Nullable final PlayerEntity player) {
        String string2;
        TextComponent textComponent3;
        if (player == null) {
            string2 = "Lectern";
            textComponent3 = new StringTextComponent("Lectern");
        }
        else {
            string2 = player.getName().getString();
            textComponent3 = player.getDisplayName();
        }
        final Vec3d vec3d4 = new Vec3d(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);
        return new ServerCommandSource(CommandOutput.DUMMY, vec3d4, Vec2f.ZERO, (ServerWorld)this.world, 2, string2, textComponent3, this.world.getServer(), player);
    }
    
    @Override
    public void fromTag(final CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        if (compoundTag.containsKey("Book", 10)) {
            this.book = this.resolveBook(ItemStack.fromTag(compoundTag.getCompound("Book")), null);
        }
        else {
            this.book = ItemStack.EMPTY;
        }
        this.pageCount = WrittenBookItem.getPageCount(this.book);
        this.currentPage = MathHelper.clamp(compoundTag.getInt("Page"), 0, this.pageCount - 1);
    }
    
    @Override
    public CompoundTag toTag(final CompoundTag compoundTag) {
        super.toTag(compoundTag);
        if (!this.getBook().isEmpty()) {
            compoundTag.put("Book", this.getBook().toTag(new CompoundTag()));
            compoundTag.putInt("Page", this.currentPage);
        }
        return compoundTag;
    }
    
    @Override
    public void clear() {
        this.setBook(ItemStack.EMPTY);
    }
    
    @Override
    public Container createMenu(final int syncId, final PlayerInventory playerInventory, final PlayerEntity playerEntity) {
        return new LecternContainer(syncId, this.inventory, this.propertyDelegate);
    }
    
    @Override
    public TextComponent getDisplayName() {
        return new TranslatableTextComponent("container.lectern", new Object[0]);
    }
}
