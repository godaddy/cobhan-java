package com.godaddy.cobhan;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.sun.jna.Platform;

public class Cobhan 
{
    static final int header_size = 64 / 8;
    static final int sizeof_int32 = 32 / 8;
    static final int minimum_cbuffer_size = 1024;

    public static byte[] AllocateCBuffer(int length) {
        if(length < minimum_cbuffer_size) {
            length = minimum_cbuffer_size;
        }

        ByteBuffer cbuffer = ByteBuffer.allocate(header_size + length);
        cbuffer.order(ByteOrder.LITTLE_ENDIAN);
        cbuffer.putInt(length);
        cbuffer.putInt(0);
        return cbuffer.array();
    }

    public static byte[] BufferToCBuffer(byte[] data) {
        return ByteBuffer.allocate(header_size + data.length)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(data.length)
            .putInt(0)
            .put(data, 0, data.length)
            .array();
    }

    public static byte[] CBufferToBuffer(byte[] buffer) throws IOException {
        ByteBuffer cbuffer = ByteBuffer.wrap(buffer);
        cbuffer.order(ByteOrder.LITTLE_ENDIAN);
        int length = cbuffer.getInt();        
        cbuffer.getInt(); //Skip reserved
        if(length < 0) {
            return TempToBuffer(cbuffer, length);
        }
        byte[] data = new byte[length];
        cbuffer.get(data, 0, length);
        return data;
    }

    public static byte[] TempToBuffer(ByteBuffer cbuffer, int length) throws IOException {
        length = 0 - length;
        byte[] data = new byte[length];
        cbuffer.get(data, 0, length);
        String tempFileName = new String(data, StandardCharsets.UTF_8);
        Path tempFile = Paths.get(tempFileName);
        data = Files.readAllBytes(tempFile);
        Files.delete(tempFile);
        return data;
    }

    public static String CBufferToString(byte[] buffer) throws IOException {
        byte[] bytes = CBufferToBuffer(buffer);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] StringToCBuffer(String data) {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        return BufferToCBuffer(bytes);
    }
    public static String GetLibraryFileName(String libraryName) {
        if(!Platform.is64Bit()) {
            throw new UnsupportedOperationException("Non-64bit platform unsupported");
        }
        String libraryFileName;
        switch(Platform.getOSType()) {
            case Platform.LINUX:
                if(Platform.isIntel()) {
                    libraryFileName = libraryName + "-x64.so";
                } else if(Platform.isARM()) {
                    libraryFileName = libraryName + "-arm64.so";
                }  else {
                    throw new UnsupportedOperationException("Unknown CPU architecture");
                }
                break;
            case Platform.WINDOWS:
                if(Platform.isIntel()) {
                    libraryFileName = libraryName + "-x64.dll";
                } else if(Platform.isARM()) {
                    libraryFileName = libraryName + "-arm64.dll";
                } else {
                    throw new UnsupportedOperationException("Unknown CPU architecture");
                }
                break;
            case Platform.MAC:
                if(Platform.isIntel()) {
                    libraryFileName = libraryName + "-x64.dylib";
                } else if(Platform.isARM()) {
                    libraryFileName = libraryName + "-arm64.dylib";
                } else {
                    throw new UnsupportedOperationException("Unknown CPU architecture");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unsupported platform");
        }
        return libraryFileName;
    }    
}
