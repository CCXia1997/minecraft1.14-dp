package net.minecraft.server.rcon;

import java.nio.charset.StandardCharsets;

public class BufferHelper
{
    public static final char[] HEX_CHARS_LOOKUP;
    
    public static String getString(final byte[] buf, final int integer2, final int integer3) {
        int integer4;
        int integer5;
        for (integer4 = integer3 - 1, integer5 = ((integer2 > integer4) ? integer4 : integer2); 0 != buf[integer5] && integer5 < integer4; ++integer5) {}
        return new String(buf, integer2, integer5 - integer2, StandardCharsets.UTF_8);
    }
    
    public static int getIntLE(final byte[] buf, final int start) {
        return getIntLE(buf, start, buf.length);
    }
    
    public static int getIntLE(final byte[] buf, final int start, final int limit) {
        if (0 > limit - start - 4) {
            return 0;
        }
        return buf[start + 3] << 24 | (buf[start + 2] & 0xFF) << 16 | (buf[start + 1] & 0xFF) << 8 | (buf[start] & 0xFF);
    }
    
    public static int getIntBE(final byte[] buf, final int start, final int limit) {
        if (0 > limit - start - 4) {
            return 0;
        }
        return buf[start] << 24 | (buf[start + 1] & 0xFF) << 16 | (buf[start + 2] & 0xFF) << 8 | (buf[start + 3] & 0xFF);
    }
    
    public static String toHex(final byte b) {
        return "" + BufferHelper.HEX_CHARS_LOOKUP[(b & 0xF0) >>> 4] + BufferHelper.HEX_CHARS_LOOKUP[b & 0xF];
    }
    
    static {
        HEX_CHARS_LOOKUP = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
