package com.mulyac.mulyac_android_client.utils;

import java.nio.ByteBuffer;

public class SerialBuffer {
    private ByteBuffer readBuffer;
    private byte[] readBuffer_compatible;

    public SerialBuffer(boolean paramBoolean)
    {
        if (paramBoolean)
        {
            this.readBuffer = ByteBuffer.allocate(16384);
            return;
        }
        this.readBuffer_compatible = new byte[16384];
    }

    public byte[] getBufferCompatible()
    {
        return this.readBuffer_compatible;
    }




}
