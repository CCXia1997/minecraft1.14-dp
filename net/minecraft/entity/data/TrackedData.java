package net.minecraft.entity.data;

public class TrackedData<T>
{
    private final int id;
    private final TrackedDataHandler<T> dataType;
    
    public TrackedData(final int id, final TrackedDataHandler<T> trackedDataHandler) {
        this.id = id;
        this.dataType = trackedDataHandler;
    }
    
    public int getId() {
        return this.id;
    }
    
    public TrackedDataHandler<T> getType() {
        return this.dataType;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final TrackedData<?> trackedData2 = o;
        return this.id == trackedData2.id;
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
}
