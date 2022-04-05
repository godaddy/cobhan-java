package com.godaddy.cobhan;

public class Asherah {
    public static void SetupJson(String configJson) throws Exception {
        byte[] buffer = Cobhan.StringToCBuffer(configJson);
        int result = NativeAsherah.SetupJson(buffer);
        if(result != 0) {
            throw new Exception("Failed to configure with SetupJson(" + result + ")");
        }
    }

    public static byte[] EncryptString(String partitionId, String input) throws Exception {
        byte[] partitionBuf = Cobhan.StringToCBuffer(partitionId);
        byte[] inputBuf = Cobhan.StringToCBuffer(input);
        byte[] outputBuf = Cobhan.AllocateCBuffer(NativeAsherah.EstimateBuffer(inputBuf.length, partitionBuf.length));
        int result = NativeAsherah.EncryptToJson(partitionBuf, inputBuf, outputBuf);
        if(result != 0) {
            throw new Exception("Failed to EncryptToJson");
        }
        return Cobhan.CBufferToBuffer(outputBuf);
    }

    public static String DecryptString(String partitionId, byte[] input) throws Exception {
        byte[] partitionBuf = Cobhan.StringToCBuffer(partitionId);
        byte[] inputBuf = Cobhan.BufferToCBuffer(input);
        byte[] outputBuf = Cobhan.AllocateCBuffer(inputBuf.length);
        int result = NativeAsherah.DecryptFromJson(partitionBuf, inputBuf, outputBuf);
        if(result != 0) {
            throw new Exception("Failed to DecryptFromJson");
        }
        return Cobhan.CBufferToString(outputBuf);
    }

    public static void Shutdown() {
        NativeAsherah.Shutdown();
    }
}
