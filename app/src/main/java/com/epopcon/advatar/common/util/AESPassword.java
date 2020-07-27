package com.epopcon.advatar.common.util;

public class AESPassword {

    private static final byte[] AESPASSWORD = {
            (byte) 0x45, (byte) 0x50, (byte) 0x4F, (byte) 0x50,
            (byte) 0x43, (byte) 0x4F, (byte) 0x4E, (byte) 0x21, (byte) 0x21
    };


    public static String getPassword()
    {
        return new String(AESPASSWORD);
    }
}
