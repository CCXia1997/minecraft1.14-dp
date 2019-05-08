package net.minecraft.util;

public interface BooleanBiFunction
{
    public static final BooleanBiFunction FALSE = (boolean1, boolean2) -> false;
    public static final BooleanBiFunction NOT_OR = (boolean1, boolean2) -> !boolean1 && !boolean2;
    public static final BooleanBiFunction ONLY_SECOND = (boolean1, boolean2) -> boolean2 && !boolean1;
    public static final BooleanBiFunction NOT_FIRST = (boolean1, boolean2) -> !boolean1;
    public static final BooleanBiFunction ONLY_FIRST = (boolean1, boolean2) -> boolean1 && !boolean2;
    public static final BooleanBiFunction NOT_SECOND = (boolean1, boolean2) -> !boolean2;
    public static final BooleanBiFunction NOT_SAME = (boolean1, boolean2) -> boolean1 != boolean2;
    public static final BooleanBiFunction NOT_AND = (boolean1, boolean2) -> !boolean1 || !boolean2;
    public static final BooleanBiFunction AND = (boolean1, boolean2) -> boolean1 && boolean2;
    public static final BooleanBiFunction SAME = (boolean1, boolean2) -> boolean1 == boolean2;
    public static final BooleanBiFunction SECOND = (boolean1, boolean2) -> boolean2;
    public static final BooleanBiFunction CAUSES = (boolean1, boolean2) -> !boolean1 || boolean2;
    public static final BooleanBiFunction FIRST = (boolean1, boolean2) -> boolean1;
    public static final BooleanBiFunction CAUSED_BY = (boolean1, boolean2) -> boolean1 || !boolean2;
    public static final BooleanBiFunction OR = (boolean1, boolean2) -> boolean1 || boolean2;
    public static final BooleanBiFunction TRUE = (boolean1, boolean2) -> true;
    
    boolean apply(final boolean arg1, final boolean arg2);
}
