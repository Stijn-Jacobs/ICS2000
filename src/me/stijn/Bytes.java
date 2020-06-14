package me.stijn;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Bytes
{
    public static final String MAC_ADDRESS_REGEX = "^([0-9A-Fa-f]{2}[:-]?){5}([0-9A-Fa-f]{2})$";
    public static final int MAX_VALUE_U_INT_16;
    public static final long MAX_VALUE_U_INT_32;
    public static final int MAX_VALUE_U_INT_8 = 255;
    protected static final char[] hexArray;

    static {
        MAX_VALUE_U_INT_32 = (long)Math.pow(2.0, 32.0) - 1L;
        MAX_VALUE_U_INT_16 = (int)(Math.pow(2.0, 16.0) - 1.0);
        hexArray = "0123456789ABCDEF".toCharArray();
    }

    public static String bytesToHexString(final byte[] array) {
        final char[] array2 = new char[array.length * 2];
        for (int i = 0; i < array.length; ++i) {
            final int n = array[i] & 0xFF;
            final int n2 = i * 2;
            final char[] hexArray = Bytes.hexArray;
            array2[n2] = hexArray[n >>> 4];
            array2[n2 + 1] = hexArray[n & 0xF];
        }
        return new String(array2);
    }

    public static String bytesToMacAddress(final byte[] array) {
        if (array.length == 6) {
            final Character[] array2 = new Character[array.length * 2];
            for (int i = 0; i < array.length; ++i) {
                final int n = array[i] & 0xFF;
                final int n2 = i * 2;
                array2[n2] = Bytes.hexArray[n >>> 4];
                array2[n2 + 1] = Bytes.hexArray[n & 0xF];
            }
            return String.format("%C%C:%C%C:%C%C:%C%C:%C%C:%C%C", (Object[])array2);
        }
        throw new IllegalArgumentException("A mac address must have 6 bytes");
    }

    public static byte[] concatenate(final byte[]... array) {
        final int length = array.length;
        int i = 0;
        int n = 0;
        while (i < length) {
            n += array[i].length;
            ++i;
        }
        final byte[] array2 = new byte[n];
        int j = 0;
        int n2 = 0;
        while (j < array.length) {
            System.arraycopy(array[j], 0, array2, n2, array[j].length);
            n2 += array[j].length;
            ++j;
        }
        return array2;
    }

    public static long getUnsignedInt(final int n) {
        return n & 0xFFFFFFFFL;
    }

    public static byte[] hexStringToByteArray(final String s) {
        final int length = s.length();
        final byte[] array = new byte[length / 2];
        int n = 0;
        while (true) {
            final int n2 = n + 1;
            if (n2 >= length) {
                break;
            }
            array[n / 2] = (byte)((Character.digit(s.charAt(n), 16) << 4) + Character.digit(s.charAt(n2), 16));
            n += 2;
        }
        return array;
    }

    public static void insertBytes(final byte[] array, int n, final byte[] array2) {
        for (int i = 0; i < array2.length; ++i, ++n) {
            array[n] = array2[i];
        }
    }

    public static void insertUnsignedInt16(final byte[] array, final int n, final int n2) {
        array[n] = toUint8(n2 & 0xFF);
        array[n + 1] = toUint8(n2 >> 8 & 0xFF);
    }

    public static void insertUnsignedInt32(final byte[] array, int n, final long n2) {
        final int n3 = n + 1;
        array[n] = toUint8(n2 & 0xFFL);
        n = n3 + 1;
        array[n3] = toUint8(n2 >> 8 & 0xFFL);
        array[n] = toUint8(n2 >> 16 & 0xFFL);
        array[n + 1] = toUint8(n2 >> 24 & 0xFFL);
    }

    public static byte[] macAddressToBytes(final String s) {
        if (s.matches("^([0-9A-Fa-f]{2}[:-]?){5}([0-9A-Fa-f]{2})$")) {
            final byte[] array = new byte[6];
            final String[] split = s.split("[:-]");
            for (int i = 0; i < split.length; ++i) {
                array[i] = (byte)(Integer.parseInt(split[i], 16) & 0xFF);
            }
            return array;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid mac address: ");
        sb.append(s);
        throw new IllegalArgumentException(sb.toString());
    }

    public static int toBitMask(final Set<Integer> set) {
        final Iterator<Integer> iterator = set.iterator();
        int n = 0;
        while (iterator.hasNext()) {
            final Integer n2 = iterator.next();
            if (n2 < 0 && n2 >= 32) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Index out of bounds: ");
                sb.append(n2);
                sb.append(" does not fit an int");
            }
            else {
                n |= (int)Math.pow(2.0, n2);
            }
        }
        return n;
    }

    public static BitSet toBitSet(final short n) {
        final BitSet set = new BitSet(16);
        for (int i = 0; i < 16; ++i) {
            boolean b = true;
            if ((n >> i & 0x1) != 0x1) {
                b = false;
            }
            set.set(i, b);
        }
        return set;
    }

    public static int toInt(final byte b) {
        return b & 0xFF;
    }

    public static int toInt(final byte b, final byte b2) {
        return ((b & 0xFF) | (b2 & 0xFF) << 8) & 0xFFFF;
    }

    public static int toInt(final byte b, final byte b2, final byte b3, final byte b4) {
        return ((b & 0xFF) | ((b2 & 0xFF) << 8 | ((b3 & 0xFF) << 16 | (b4 & 0xFF) << 24))) & 0xFFFF;
    }

    public static short toInt2(final byte b, final byte b2) {
        return toInt2(b, b2, ByteOrder.LITTLE_ENDIAN);
    }

    public static short toInt2(final byte b, final byte b2, final ByteOrder byteOrder) {
        final ByteBuffer put = ByteBuffer.allocate(2).order(byteOrder).put(b).put(b2);
        put.rewind();
        return put.getShort();
    }

    public static Set<Integer> toIntegerSet(final int n, final int n2) {
        final HashSet<Integer> set = new HashSet<Integer>();
        for (int i = 0; i < n2; ++i) {
            if ((n >> i & 0x1) == 0x1) {
                set.add(i);
            }
        }
        return set;
    }

    public static long toLong(final byte b, final byte b2, final byte b3, final byte b4) {
        return ((b & 0xFF) | ((b2 & 0xFF) << 8 | ((b3 & 0xFF) << 16 | (b4 & 0xFF) << 24))) & 0xFFFFFFFFL;
    }

    public static int toUint16(final int n) {
        if (n >= 0 && n <= Bytes.MAX_VALUE_U_INT_16) {
            return ((n & 0xFF) | (n << 8 & 0xFF)) & 0xFFFF;
        }
        throw new IllegalArgumentException("Invalid u_int8. Please enter a value between 0 and 255");
    }

    public static byte toUint8(final int n) {
        if (n >= 0 && n <= 255) {
            return (byte)(n & 0xFF);
        }
        throw new IllegalArgumentException("Invalid u_int8. Please enter a value between 0 and 255");
    }

    public static byte toUint8(final long n) {
        if (n >= 0L && n <= Bytes.MAX_VALUE_U_INT_32) {
            return (byte)(n & 0xFFL);
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid u_int32. Please enter a value between 0 and ");
        sb.append(Bytes.MAX_VALUE_U_INT_32);
        throw new IllegalArgumentException(sb.toString());
    }
}