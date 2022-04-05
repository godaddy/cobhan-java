package com.godaddy.cobhan;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.jna.Native;

public class NativeAsherah {
    public static native int SetupJson(byte[] configJson);
    public static native int EstimateBuffer(int dataLen, int partitionLen);
    public static native int EncryptToJson(byte[] partitionId, byte[] data, byte[] json);
    public static native int DecryptFromJson(byte[] partitionId, byte[] json, byte[] data); 
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