package com.godaddy.cobhan;

import static org.junit.Assert.assertEquals;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class AsherahTest {
        /**
     * @throws Exception
     */
    @Test
    public void asherahRoundTrip() throws Exception {
        String configJson = "{ " +
        "\"ServiceName\":\"service1\"," +
        "\"ProductID\":\"product1\", " +
        " \"Metastore\":\"memory\", " +
        " \"KMS\":\"static\", " +
        " \"EnableSessionCaching\":true, " +
        " \"Verbose\":false } ";

        Asherah.SetupJson(configJson);
        String input = "testString";
        final int iterations = 500000;
        long s1 = System.nanoTime();        
        for(int i = 0; i < iterations; i++) {            
            ByteBuffer encrypted = Asherah.EncryptString("partitionId", input);
            String output = Asherah.DecryptString("partitionId", encrypted);
            assertEquals(input, output);
        }
        long s2 = System.nanoTime();
        long milliseconds = TimeUnit.MILLISECONDS.convert(s2 - s1, TimeUnit.NANOSECONDS);
        System.out.printf("Result: %f milliseconds per round trip", (double)milliseconds / (double)iterations);
        
        Asherah.Shutdown();
    }
}
