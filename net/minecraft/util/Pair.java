package net.minecraft.util;

public class Pair<A, B>
{
    private A a;
    private B b;
    
    public Pair(final A left, final B right) {
        this.a = left;
        this.b = right;
    }
    
    public A getLeft() {
        return this.a;
    }
    
    public B getRight() {
        return this.b;
    }
}
