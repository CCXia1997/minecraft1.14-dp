package net.minecraft.item;

public class BookItem extends Item
{
    public BookItem(final Settings settings) {
        super(settings);
    }
    
    @Override
    public boolean isTool(final ItemStack stack) {
        return stack.getAmount() == 1;
    }
    
    @Override
    public int getEnchantability() {
        return 1;
    }
}
