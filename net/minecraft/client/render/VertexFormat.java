package net.minecraft.client.render;

import org.apache.logging.log4j.LogManager;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VertexFormat
{
    private static final Logger LOGGER;
    private final List<VertexFormatElement> elements;
    private final List<Integer> offsets;
    private int size;
    private int offsetColor;
    private final List<Integer> offsetsUv;
    private int offsetNormal;
    
    public VertexFormat(final VertexFormat vertexFormat) {
        this();
        for (int integer2 = 0; integer2 < vertexFormat.getElementCount(); ++integer2) {
            this.add(vertexFormat.getElement(integer2));
        }
        this.size = vertexFormat.getVertexSize();
    }
    
    public VertexFormat() {
        this.elements = Lists.newArrayList();
        this.offsets = Lists.newArrayList();
        this.offsetColor = -1;
        this.offsetsUv = Lists.newArrayList();
        this.offsetNormal = -1;
    }
    
    public void clear() {
        this.elements.clear();
        this.offsets.clear();
        this.offsetColor = -1;
        this.offsetsUv.clear();
        this.offsetNormal = -1;
        this.size = 0;
    }
    
    public VertexFormat add(final VertexFormatElement vertexFormatElement) {
        if (vertexFormatElement.isPosition() && this.hasPositionElement()) {
            VertexFormat.LOGGER.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
            return this;
        }
        this.elements.add(vertexFormatElement);
        this.offsets.add(this.size);
        switch (vertexFormatElement.getType()) {
            case NORMAL: {
                this.offsetNormal = this.size;
                break;
            }
            case COLOR: {
                this.offsetColor = this.size;
                break;
            }
            case UV: {
                this.offsetsUv.add(vertexFormatElement.getIndex(), this.size);
                break;
            }
        }
        this.size += vertexFormatElement.getSize();
        return this;
    }
    
    public boolean hasNormalElement() {
        return this.offsetNormal >= 0;
    }
    
    public int getNormalOffset() {
        return this.offsetNormal;
    }
    
    public boolean hasColorElement() {
        return this.offsetColor >= 0;
    }
    
    public int getColorOffset() {
        return this.offsetColor;
    }
    
    public boolean hasUvElement(final int integer) {
        return this.offsetsUv.size() - 1 >= integer;
    }
    
    public int getUvOffset(final int integer) {
        return this.offsetsUv.get(integer);
    }
    
    @Override
    public String toString() {
        String string1 = "format: " + this.elements.size() + " elements: ";
        for (int integer2 = 0; integer2 < this.elements.size(); ++integer2) {
            string1 += this.elements.get(integer2).toString();
            if (integer2 != this.elements.size() - 1) {
                string1 += " ";
            }
        }
        return string1;
    }
    
    private boolean hasPositionElement() {
        for (int integer1 = 0, integer2 = this.elements.size(); integer1 < integer2; ++integer1) {
            final VertexFormatElement vertexFormatElement3 = this.elements.get(integer1);
            if (vertexFormatElement3.isPosition()) {
                return true;
            }
        }
        return false;
    }
    
    public int getVertexSizeInteger() {
        return this.getVertexSize() / 4;
    }
    
    public int getVertexSize() {
        return this.size;
    }
    
    public List<VertexFormatElement> getElements() {
        return this.elements;
    }
    
    public int getElementCount() {
        return this.elements.size();
    }
    
    public VertexFormatElement getElement(final int index) {
        return this.elements.get(index);
    }
    
    public int getElementOffset(final int element) {
        return this.offsets.get(element);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final VertexFormat vertexFormat2 = (VertexFormat)o;
        return this.size == vertexFormat2.size && this.elements.equals(vertexFormat2.elements) && this.offsets.equals(vertexFormat2.offsets);
    }
    
    @Override
    public int hashCode() {
        int integer1 = this.elements.hashCode();
        integer1 = 31 * integer1 + this.offsets.hashCode();
        integer1 = 31 * integer1 + this.size;
        return integer1;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
