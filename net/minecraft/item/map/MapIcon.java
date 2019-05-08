package net.minecraft.item.map;

import net.minecraft.util.math.MathHelper;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import javax.annotation.Nullable;
import net.minecraft.text.TextComponent;

public class MapIcon
{
    private final Type type;
    private byte x;
    private byte z;
    private byte angle;
    private final TextComponent text;
    
    public MapIcon(final Type type, final byte x, final byte z, final byte rotation, @Nullable final TextComponent text) {
        this.type = type;
        this.x = x;
        this.z = z;
        this.angle = rotation;
        this.text = text;
    }
    
    @Environment(EnvType.CLIENT)
    public byte getTypeId() {
        return this.type.getId();
    }
    
    public Type getType() {
        return this.type;
    }
    
    public byte getX() {
        return this.x;
    }
    
    public byte getZ() {
        return this.z;
    }
    
    public byte getAngle() {
        return this.angle;
    }
    
    @Environment(EnvType.CLIENT)
    public boolean renderIfNotHeld() {
        return this.type.renderIfNotHeld();
    }
    
    @Nullable
    public TextComponent getText() {
        return this.text;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MapIcon)) {
            return false;
        }
        final MapIcon mapIcon2 = (MapIcon)o;
        return this.type == mapIcon2.type && this.angle == mapIcon2.angle && this.x == mapIcon2.x && this.z == mapIcon2.z && Objects.equals(this.text, mapIcon2.text);
    }
    
    @Override
    public int hashCode() {
        int integer1 = this.type.getId();
        integer1 = 31 * integer1 + this.x;
        integer1 = 31 * integer1 + this.z;
        integer1 = 31 * integer1 + this.angle;
        integer1 = 31 * integer1 + Objects.hashCode(this.text);
        return integer1;
    }
    
    public enum Type
    {
        a(false), 
        b(true), 
        c(false), 
        d(false), 
        e(true), 
        f(true), 
        g(false), 
        h(false), 
        i(true, 5393476), 
        j(true, 3830373), 
        k(true), 
        l(true), 
        m(true), 
        n(true), 
        o(true), 
        p(true), 
        q(true), 
        r(true), 
        s(true), 
        t(true), 
        u(true), 
        v(true), 
        w(true), 
        x(true), 
        y(true), 
        z(true), 
        A(true);
        
        private final byte id;
        private final boolean renderNotHeld;
        private final int tintColor;
        
        private Type(final boolean renderNotHeld) {
            this(renderNotHeld, -1);
        }
        
        private Type(final boolean renderNotHeld, final int tintColor) {
            this.id = (byte)this.ordinal();
            this.renderNotHeld = renderNotHeld;
            this.tintColor = tintColor;
        }
        
        public byte getId() {
            return this.id;
        }
        
        @Environment(EnvType.CLIENT)
        public boolean renderIfNotHeld() {
            return this.renderNotHeld;
        }
        
        public boolean hasTintColor() {
            return this.tintColor >= 0;
        }
        
        public int getTintColor() {
            return this.tintColor;
        }
        
        public static Type byId(final byte id) {
            return values()[MathHelper.clamp(id, 0, values().length - 1)];
        }
    }
}
