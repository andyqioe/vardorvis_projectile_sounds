package com.vardorvis_projectile_sounds;

import javax.sound.sampled.AudioFormat;

public class Sound
{
    private byte[] bytes;
    private AudioFormat format;
    private int numBytes;

    Sound(byte[] bytes, AudioFormat format, int numBytes)
    {
        this.bytes = bytes;
        this.format = format;
        this.numBytes = numBytes;
    }

    public byte[] getBytes()
    {
        return bytes;
    }

    public AudioFormat getFormat()
    {
        return format;
    }

    public int getNumBytes()
    {
        return numBytes;
    }
}