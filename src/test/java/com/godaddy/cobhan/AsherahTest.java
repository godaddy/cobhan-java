package com.godaddy.cobhan;

import static org.junit.Assert.assertEquals;

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
        " \"Verbose\":true } ";

        Asherah.SetupJson(configJson);
        String input = "testString";
        byte[] encrypted = Asherah.EncryptString("partitionId", "testString");
        String output = Asherah.DecryptString("partitionId", encrypted);
        Asherah.Shutdown();
        assertEquals(input, output);
    }
}
