package net.minecraft.client.render;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class VertexFormatElement
{
    private static final Logger LOGGER;
    private final Format format;
    private final Type type;
    private final int index;
    private final int count;
    
    public VertexFormatElement(final int index, final Format format, final Type type, final int count) {
        if (this.isValidType(index, type)) {
            this.type = type;
        }
        else {
            VertexFormatElement.LOGGER.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
            this.type = Type.UV;
        }
        this.format = format;
        this.index = index;
        this.count = count;
    }
    
    private final boolean isValidType(final int index, final Type type) {
        return index == 0 || type == Type.UV;
    }
    
    public final Format getFormat() {
        return this.format;
    }
    
    public final Type getType() {
        return this.type;
    }
    
    public final int getCount() {
        return this.count;
    }
    
    public final int getIndex() {
        return this.index;
    }
    
    @Override
    public String toString() {
        return this.count + "," + this.type.getName() + "," + this.format.getName();
    }
    
    public final int getSize() {
        return this.format.getSize() * this.count;
    }
    
    public final boolean isPosition() {
        return this.type == Type.POSITION;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final VertexFormatElement vertexFormatElement2 = (VertexFormatElement)o;
        return this.count == vertexFormatElement2.count && this.index == vertexFormatElement2.index && this.format == vertexFormatElement2.format && this.type == vertexFormatElement2.type;
    }
    
    @Override
    public int hashCode() {
        int integer1 = this.format.hashCode();
        integer1 = 31 * integer1 + this.type.hashCode();
        integer1 = 31 * integer1 + this.index;
        integer1 = 31 * integer1 + this.count;
        return integer1;
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
    
    @Environment(EnvType.CLIENT)
    public enum Type
    {
        POSITION("Position"), 
        NORMAL("Normal"), 
        COLOR("Vertex Color"), 
        UV("UV"), 
        BONE_MATRIX("Bone Matrix"), 
        BLEND_WEIGHT("Blend Weight"), 
        PADDING("Padding");
        
        private final String name;
        
        private Type(final String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }
    
    @Environment(EnvType.CLIENT)
    public enum Format
    {
        FLOAT(4, "Float", 5126), 
        UNSIGNED_BYTE(1, "Unsigned Byte", 5121), 
        BYTE(1, "Byte", 5120), 
        UNSIGNED_SHORT(2, "Unsigned Short", 5123), 
        SHORT(2, "Short", 5122), 
        UNSIGNED_INT(4, "Unsigned Int", 5125), 
        INT(4, "Int", 5124);
        
        private final int size;
        private final String name;
        private final int glId;
        
        private Format(final int size, final String name, final int glId) {
            this.size = size;
            this.name = name;
            this.glId = glId;
        }
        
        public int getSize() {
            return this.size;
        }
        
        public String getName() {
            return this.name;
        }
        
        public int getGlId() {
            return this.glId;
        }
    }
}
