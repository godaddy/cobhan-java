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

    public static ByteBuffer AllocateCBuffer(int length) {
        if(length < minimum_cbuffer_size) {
            length = minimum_cbuffer_size;
        }

        ByteBuffer cbuffer = ByteBuffer.allocateDirect(header_size + length);
        cbuffer.order(ByteOrder.LITTLE_ENDIAN);
        cbuffer.putInt(length);
        cbuffer.putInt(0);
        cbuffer.rewind();
        return cbuffer;
    }

    public static ByteBuffer BufferToCBuffer(ByteBuffer data) {
        return ByteBuffer.allocateDirect(header_size + data.limit())
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(data.limit())
            .putInt(0)
            .put(data)
            .rewind();
    }

    public static ByteBuffer CBufferToBuffer(ByteBuffer cbuffer) throws IOException {
        cbuffer.order(ByteOrder.LITTLE_ENDIAN);
        int length = cbuffer.getInt();        
        cbuffer.getInt(); //Skip reserved
        if(length < 0) {
            return TempToBuffer(cbuffer, length);
        }
        byte[] data = new byte[length];
        cbuffer.get(data, 0, length);
        return ByteBuffer.wrap(data);
    }

    public static ByteBuffer TempToBuffer(ByteBuffer cbuffer, int length) throws IOException {
        length = 0 - length;
        byte[] data = new byte[length];
        cbuffer.get(data, 0, length);
        String tempFileName = new String(data, StandardCharsets.UTF_8);
        Path tempFile = Paths.get(tempFileName);
        data = Files.readAllBytes(tempFile);
        Files.delete(tempFile);
        return ByteBuffer.wrap(data);
    }

    public static String CBufferToString(ByteBuffer buffer) throws IOException {
        ByteBuffer bytes = CBufferToBuffer(buffer);
        return new String(bytes.array(), StandardCharsets.UTF_8);
    }

    public static ByteBuffer StringToCBuffer(String data) {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        return BufferToCBuffer(ByteBuffer.wrap(bytes));
    }
    static String GetLibraryFileName(String libraryName) {
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
