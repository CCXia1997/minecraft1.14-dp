package net.minecraft.realms;

import java.util.Iterator;
import net.minecraft.client.render.VertexFormatElement;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.render.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RealmsVertexFormat
{
    private VertexFormat v;
    
    public RealmsVertexFormat(final VertexFormat vertexFormat) {
        this.v = vertexFormat;
    }
    
    public RealmsVertexFormat from(final VertexFormat vertexFormat) {
        this.v = vertexFormat;
        return this;
    }
    
    public VertexFormat getVertexFormat() {
        return this.v;
    }
    
    public void clear() {
        this.v.clear();
    }
    
    public int getUvOffset(final int integer) {
        return this.v.getUvOffset(integer);
    }
    
    public int getElementCount() {
        return this.v.getElementCount();
    }
    
    public boolean hasColor() {
        return this.v.hasColorElement();
    }
    
    public boolean hasUv(final int integer) {
        return this.v.hasUvElement(integer);
    }
    
    public RealmsVertexFormatElement getElement(final int integer) {
        return new RealmsVertexFormatElement(this.v.getElement(integer));
    }
    
    public RealmsVertexFormat addElement(final RealmsVertexFormatElement realmsVertexFormatElement) {
        return this.from(this.v.add(realmsVertexFormatElement.getVertexFormatElement()));
    }
    
    public int getColorOffset() {
        return this.v.getColorOffset();
    }
    
    public List<RealmsVertexFormatElement> getElements() {
        final List<RealmsVertexFormatElement> list1 = Lists.newArrayList();
        for (final VertexFormatElement vertexFormatElement3 : this.v.getElements()) {
            list1.add(new RealmsVertexFormatElement(vertexFormatElement3));
        }
        return list1;
    }
    
    public boolean hasNormal() {
        return this.v.hasNormalElement();
    }
    
    public int getVertexSize() {
        return this.v.getVertexSize();
    }
    
    public int getOffset(final int integer) {
        return this.v.getElementOffset(integer);
    }
    
    public int getNormalOffset() {
        return this.v.getNormalOffset();
    }
    
    public int getIntegerSize() {
        return this.v.getVertexSizeInteger();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.v.equals(o);
    }
    
    @Override
    public int hashCode() {
        return this.v.hashCode();
    }
    
    @Override
    public String toString() {
        return this.v.toString();
    }
}
