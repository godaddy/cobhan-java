package com.godaddy.cobhan;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

public class CobhanTest 
{
    /**
     * @throws IOException
     */
    @Test
    public void stringRoundTrip() throws IOException
    {
        String input = "testString";
        ByteBuffer bytes = Cobhan.StringToCBuffer("testString");
        String output = Cobhan.CBufferToString(bytes);
        assertEquals(input, output);
    }
}
