package com.godaddy.cobhan;

import java.nio.ByteBuffer;

public class Asherah {
    public static void SetupJson(String configJson) throws Exception {
        ByteBuffer buffer = Cobhan.StringToCBuffer(configJson);
        int result = NativeAsherah.SetupJson(buffer);
        if(result != 0) {
            throw new Exception("Failed to configure with SetupJson(" + result + ")");
        }
    }

    public static ByteBuffer EncryptString(String partitionId, String input) throws Exception {
        ByteBuffer partitionBuf = Cobhan.StringToCBuffer(partitionId);
        ByteBuffer inputBuf = Cobhan.StringToCBuffer(input);
        ByteBuffer outputBuf = Cobhan.AllocateCBuffer(EstimateBuffer(inputBuf.limit(), partitionBuf.limit()));
        int result = NativeAsherah.EncryptToJson(partitionBuf, inputBuf, outputBuf);
        if(result != 0) {
            throw new Exception("Failed to EncryptToJson");
        }
        return Cobhan.CBufferToBuffer(outputBuf);
    }

    public static String DecryptString(String partitionId, ByteBuffer input) throws Exception {
        ByteBuffer partitionBuf = Cobhan.StringToCBuffer(partitionId);
        ByteBuffer inputBuf = Cobhan.BufferToCBuffer(input);
        ByteBuffer outputBuf = Cobhan.AllocateCBuffer(inputBuf.limit());
        int result = NativeAsherah.DecryptFromJson(partitionBuf, inputBuf, outputBuf);
        if(result != 0) {
            throw new Exception("Failed to DecryptFromJson");
        }
        return Cobhan.CBufferToString(outputBuf);
    }

    public static void Shutdown() {
        NativeAsherah.Shutdown();
    }

    private static final int EstimatedEncryptionOverhead = 48;
    private static final int EstimatedEnvelopeOverhead = 185;
    private static final double Base64Overhead = 1.34;
    private static int EstimatedIntermediateKeyOverhead = 1024; //TODO: Base this on the service and product name
    
    private static int EstimateBuffer(int dataLen, int partitionLen) {
      double estimatedDataLen = (dataLen + EstimatedEncryptionOverhead) * Base64Overhead;
      return (int)(EstimatedEnvelopeOverhead + EstimatedIntermediateKeyOverhead + partitionLen + estimatedDataLen);
    }
}
