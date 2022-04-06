package com.godaddy.cobhan;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.jna.Native;

public class NativeAsherah {
    public static native int SetupJson(ByteBuffer configJson);
    public static native int EstimateBuffer(int dataLen, int partitionLen);
    public static native int EncryptToJson(ByteBuffer partitionId, ByteBuffer data, ByteBuffer json);
    public static native int DecryptFromJson(ByteBuffer partitionId, ByteBuffer json, ByteBuffer data); 
    public static native void Shutdown();

    static {
        String libraryPath = System.getProperty("asherah_path");
        if(libraryPath == null) {
            libraryPath = System.getenv("ASHERAH_PATH");
            if(libraryPath == null) {
                libraryPath = "./";
            }
        }
        String libraryFileName = Cobhan.GetLibraryFileName("libasherah");
        Path path = Paths.get(libraryPath, libraryFileName).toAbsolutePath();
        Native.register(path.toString());
    }
}