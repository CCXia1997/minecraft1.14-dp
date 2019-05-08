package net.minecraft.realms;

import net.minecraft.client.render.VertexFormatElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsVertexFormatElement
{
    private final VertexFormatElement v;
    
    public RealmsVertexFormatElement(final VertexFormatElement vertexFormatElement) {
        this.v = vertexFormatElement;
    }
    
    public VertexFormatElement getVertexFormatElement() {
        return this.v;
    }
    
    public boolean isPosition() {
        return this.v.isPosition();
    }
    
    public int getIndex() {
        return this.v.getIndex();
    }
    
    public int getByteSize() {
        return this.v.getSize();
    }
    
    public int getCount() {
        return this.v.getCount();
    }
    
    @Override
    public int hashCode() {
        return this.v.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.v.equals(o);
    }
    
    @Override
    public String toString() {
        return this.v.toString();
    }
}
