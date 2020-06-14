package me.stijn;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Cryptographer {

    public static String decryptToString(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3) {
        String str = "";
        paramArrayOfByte1 = decrypt(paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3);
        if (paramArrayOfByte1 == null) {
            paramArrayOfByte1 = null;
        } else {
            str = new String(paramArrayOfByte1, StandardCharsets.UTF_8);
        }
        return str;
    }

    public static byte[] decrypt(byte[] paramArrayOfByte1, byte[] aes, byte[] paramArrayOfByte3) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(aes, "AES");
        try
        {
            final Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
            instance.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(paramArrayOfByte3));
            return instance.doFinal(paramArrayOfByte1);
        }
        catch (IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] encrypt(byte[] doFinal, final byte[] aes, byte[] array2) {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(aes, "AES");
        try {
            final Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
            instance.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(array2));
            return instance.doFinal(doFinal);
        } catch (InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException |
                BadPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }
}
