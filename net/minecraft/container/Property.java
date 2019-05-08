package net.minecraft.container;

public abstract class Property
{
    private int oldValue;
    
    public static Property create(final PropertyDelegate propertyDelegate, final int key) {
        return new Property() {
            @Override
            public int get() {
                return propertyDelegate.get(key);
            }
            
            @Override
            public void set(final int value) {
                propertyDelegate.set(key, value);
            }
        };
    }
    
    public static Property create(final int[] arr, final int key) {
        return new Property() {
            @Override
            public int get() {
                return arr[key];
            }
            
            @Override
            public void set(final int value) {
                arr[key] = value;
            }
        };
    }
    
    public static Property create() {
        return new Property() {
            private int value;
            
            @Override
            public int get() {
                return this.value;
            }
            
            @Override
            public void set(final int value) {
                this.value = value;
            }
        };
    }
    
    public abstract int get();
    
    public abstract void set(final int arg1);
    
    public boolean detectChanges() {
        final int integer1 = this.get();
        final boolean boolean2 = integer1 != this.oldValue;
        this.oldValue = integer1;
        return boolean2;
    }
}
