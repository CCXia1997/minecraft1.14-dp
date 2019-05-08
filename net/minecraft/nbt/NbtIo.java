package net.minecraft.nbt;

import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.io.File;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;

public class NbtIo
{
    public static CompoundTag readCompressed(final InputStream stream) throws IOException {
        try (final DataInputStream dataInputStream2 = new DataInputStream(new BufferedInputStream(new GZIPInputStream(stream)))) {
            return read(dataInputStream2, PositionTracker.DEFAULT);
        }
    }
    
    public static void writeCompressed(final CompoundTag tag, final OutputStream stream) throws IOException {
        try (final DataOutputStream dataOutputStream3 = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(stream)))) {
            write(tag, dataOutputStream3);
        }
    }
    
    @Environment(EnvType.CLIENT)
    public static void safeWrite(final CompoundTag tag, final File file) throws IOException {
        final File file2 = new File(file.getAbsolutePath() + "_tmp");
        if (file2.exists()) {
            file2.delete();
        }
        write(tag, file2);
        if (file.exists()) {
            file.delete();
        }
        if (file.exists()) {
            throw new IOException("Failed to delete " + file);
        }
        file2.renameTo(file);
    }
    
    @Environment(EnvType.CLIENT)
    public static void write(final CompoundTag tag, final File file) throws IOException {
        final DataOutputStream dataOutputStream3 = new DataOutputStream(new FileOutputStream(file));
        try {
            write(tag, dataOutputStream3);
        }
        finally {
            dataOutputStream3.close();
        }
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static CompoundTag read(final File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        final DataInputStream dataInputStream2 = new DataInputStream(new FileInputStream(file));
        try {
            return read(dataInputStream2, PositionTracker.DEFAULT);
        }
        finally {
            dataInputStream2.close();
        }
    }
    
    public static CompoundTag read(final DataInputStream stream) throws IOException {
        return read(stream, PositionTracker.DEFAULT);
    }
    
    public static CompoundTag read(final DataInput input, final PositionTracker trakcer) throws IOException {
        final Tag tag3 = read(input, 0, trakcer);
        if (tag3 instanceof CompoundTag) {
            return (CompoundTag)tag3;
        }
        throw new IOException("Root tag must be a named compound tag");
    }
    
    public static void write(final CompoundTag tag, final DataOutput output) throws IOException {
        write((Tag)tag, output);
    }
    
    private static void write(final Tag tag, final DataOutput output) throws IOException {
        output.writeByte(tag.getType());
        if (tag.getType() == 0) {
            return;
        }
        output.writeUTF("");
        tag.write(output);
    }
    
    private static Tag read(final DataInput input, final int depth, final PositionTracker tracker) throws IOException {
        final byte byte4 = input.readByte();
        if (byte4 == 0) {
            return new EndTag();
        }
        input.readUTF();
        final Tag tag5 = Tag.createTag(byte4);
        try {
            tag5.read(input, depth, tracker);
        }
        catch (IOException iOException6) {
            final CrashReport crashReport7 = CrashReport.create(iOException6, "Loading NBT data");
            final CrashReportSection crashReportSection8 = crashReport7.addElement("NBT Tag");
            crashReportSection8.add("Tag type", byte4);
            throw new CrashException(crashReport7);
        }
        return tag5;
    }
}
