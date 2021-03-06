package net.minecraft.util;

public class TypedActionResult<T>
{
    private final ActionResult result;
    private final T value;
    
    public TypedActionResult(final ActionResult actionResult, final T object) {
        this.result = actionResult;
        this.value = object;
    }
    
    public ActionResult getResult() {
        return this.result;
    }
    
    public T getValue() {
        return this.value;
    }
}
