package net.minecraft.server.rcon;

import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;

public class DataStreamHelper
{
    private final ByteArrayOutputStream byteArrayOutputStream;
    private final DataOutputStream dataOutputStream;
    
    public DataStreamHelper(final int integer) {
        this.byteArrayOutputStream = new ByteArrayOutputStream(integer);
        this.dataOutputStream = new DataOutputStream(this.byteArrayOutputStream);
    }
    
    public void write(final byte[] arr) throws IOException {
        this.dataOutputStream.write(arr, 0, arr.length);
    }
    
    public void writeBytes(final String string) throws IOException {
        this.dataOutputStream.writeBytes(string);
        this.dataOutputStream.write(0);
    }
    
    public void write(final int integer) throws IOException {
        this.dataOutputStream.write(integer);
    }
    
    public void writeShort(final short short1) throws IOException {
        this.dataOutputStream.writeShort(Short.reverseBytes(short1));
    }
    
    public byte[] bytes() {
        return this.byteArrayOutputStream.toByteArray();
    }
    
    public void reset() {
        this.byteArrayOutputStream.reset();
    }
}
