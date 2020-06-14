package me.stijn;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

public class Message
{
    public static final int DATA_SIZE = 1024;
    public static final int FRAME_NUMBER_BROADCAST = 254;
    public static final int FRAME_NUMBER_DEFAULT = 1;
    public static final int FRAME_NUMBER_REPLY = 2;
    public static final int HEADER_SIZE = 43;
    public static final int IV_SIZE = 16;
    public static final int MAX_DATA_SIZE = 8192;
    public static final int MAX_SEGMENTS = 8;
    public static final int MAX_SEGMENT_SIZE = 1067;
    private static Random random;
    protected byte[] data;
    protected byte[] header;

    static {
        Message.random = new Random();
    }

    public Message() {
        this(new byte[43]);
    }

    public Message(final byte[] array) {
        this.header = Arrays.copyOf(array, 43);
        final int dataLength = this.getDataLength();
        if (dataLength > 0) {
            this.data = Arrays.copyOfRange(array, 43, dataLength + 43);
        }
    }

    public Message(final byte[] header, final byte[] array) {
        this.header = header;
        this.setData(array, false);
    }

    private static long generateMagicNumber() {
        final double nextDouble = Message.random.nextDouble();
        final double n = Bytes.MAX_VALUE_U_INT_32 - 1L;
        Double.isNaN(n);
        return (long)(nextDouble * n + 1.0);
    }

    public void append(final Message message) {
        this.setData(Bytes.concatenate(new byte[][] { this.data, message.getData(false) }), false);
    }

    public long getCloudID() {
        final byte[] header = this.header;
        return Bytes.toLong(header[37], header[38], header[39], header[40]);
    }

    public byte[] getData() {
        return this.getData(true);
    }

    public byte[] getData(final boolean b) {
        if (b) {
            final byte[] aesKey = Bytes.hexStringToByteArray(Main.AES_KEY);
            final byte[] copyOfRange = Arrays.copyOfRange(this.data, 0, 16);
            final byte[] data = this.data;
            return Cryptographer.decrypt(Arrays.copyOfRange(data, 16, data.length), aesKey, copyOfRange);
        }
        final byte[] data2 = this.data;
        if (data2 != null) {
            final byte[] array = data2;
            if (data2.length != 0) {
                return array;
            }
        }
        return new byte[0];
    }

    public String getDataAsJson() {
        return new String(this.getData(true), StandardCharsets.UTF_8);
    }

    public int getDataLength() {
        final byte[] header = this.header;
        return Bytes.toInt(header[41], header[42]);
    }

    public long getEntityId() {
        final byte[] header = this.header;
        return Bytes.toLong(header[29], header[30], header[31], header[32]);
    }

    public int getFrameNumber() {
        return Bytes.toInt(this.header[0]);
    }

    public byte[] getHeader() {
        return this.header;
    }

    public String getMacAddress() {
        return Bytes.bytesToMacAddress(this.getMacAddressBytes());
    }

    public byte[] getMacAddressBytes() {
        return Arrays.copyOfRange(this.header, 3, 9);
    }

    public long getMagicNumber() {
        final byte[] header = this.header;
        return Bytes.toLong(header[9], header[10], header[11], header[12]);
    }

    public Message getSegment(int length) {
        final int n = length * 1024;
        final int n2 = n + 1024;
        final byte[] data = this.data;
        length = n2;
        if (n2 > data.length) {
            length = data.length;
        }
        final Message message = new Message(this.header, Arrays.copyOfRange(data, n, length));
        message.setMagicNumber();
        return message;
    }

    public int getSegmentCount() {
        final byte[] data = this.data;
        int n;
        if (data == null) {
            n = 1;
        }
        else {
            n = (int)Math.ceil(data.length / 1024.0f);
        }
        return n;
    }

    public int getSegmentNumber() {
        return Bytes.toInt(this.header[1]);
    }

    public int getTemporaryId() {
        final byte[] header = this.header;
        return Bytes.toInt(header[33], header[34]);
    }

    public Message setAreaDataVersion(final int n) {
        if (n >= 0 && n <= Bytes.MAX_VALUE_U_INT_16) {
            Bytes.insertUnsignedInt16(this.header, 21, n);
            return this;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid area data version ");
        sb.append(n);
        throw new IllegalArgumentException(sb.toString());
    }

    public Message setData(final String s) {
        return this.setData(s, true);
    }

    public Message setData(final String s, final boolean b) {
        return this.setData(s.getBytes(StandardCharsets.UTF_8), b);
    }

    public Message setData(byte[] data, final boolean b) {
        int length = 0;
        if (b) {
            final byte[] aesKey = Bytes.hexStringToByteArray(Main.AES_KEY);
            final byte[] array = new byte[16];
            data = Cryptographer.encrypt(data, aesKey, array);
            if (data.length + 16 >= 8192) {
                throw new NullPointerException();
            }
            this.data = Bytes.concatenate(new byte[][] { array, data });
        }
        else {
            this.data = data;
        }
        final byte[] header = this.header;
        data = this.data;
        if (data != null) {
            length = data.length;
        }
        Bytes.insertUnsignedInt16(header, 41, length);
        return this;
    }

    public Message setDeviceDataVersion(final int n) {
        if (n >= 0 && n <= Bytes.MAX_VALUE_U_INT_16) {
            Bytes.insertUnsignedInt16(this.header, 19, n);
            return this;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid module data version ");
        sb.append(n);
        throw new IllegalArgumentException(sb.toString());
    }

    public Message setDeviceStateVersion(final int n) {
        if (n >= 0 && n <= Bytes.MAX_VALUE_U_INT_16) {
            Bytes.insertUnsignedInt16(this.header, 17, n);
            return this;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid module status version ");
        sb.append(n);
        throw new IllegalArgumentException(sb.toString());
    }

    public Message setEntityId(final long n) {
        if (n >= 0L && n <= Bytes.MAX_VALUE_U_INT_32) {
            Bytes.insertUnsignedInt32(this.header, 29, n);
            return this;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid entity ID ");
        sb.append(n);
        throw new IllegalArgumentException(sb.toString());
    }

    public Message setEntityTrackerID(final long n) {
        if (n >= 0L && n <= Bytes.MAX_VALUE_U_INT_32) {
            Bytes.insertUnsignedInt32(this.header, 37, n);
            return this;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid entity tracker ID ");
        sb.append(n);
        throw new IllegalArgumentException(sb.toString());
    }

    public Message setFrameNumber(final int n) {
        if (n < 0 && n > 255) {
            throw new IllegalArgumentException("Invalid frame number");
        }
        this.header[0] = Bytes.toUint8(n);
        return this;
    }

    public Message setGlobalVersion(final int n) {
        if (n >= 0 && n <= Bytes.MAX_VALUE_U_INT_16) {
            Bytes.insertUnsignedInt16(this.header, 13, n);
            return this;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid global version ");
        sb.append(n);
        throw new IllegalArgumentException(sb.toString());
    }

    public Message setMacAddress(final byte[] array) {
        if (array.length == 6) {
            Bytes.insertBytes(this.header, 3, array);
            return this;
        }
        throw new IllegalArgumentException("Invalid mac address");
    }

    public Message setMagicNumber(final long n) {
        if (n >= 0L && n <= Bytes.MAX_VALUE_U_INT_32) {
            Bytes.insertUnsignedInt32(this.header, 9, n);
            return this;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid magic number ");
        sb.append(n);
        throw new IllegalArgumentException(sb.toString());
    }

    public void setMagicNumber() {
        this.setMagicNumber(generateMagicNumber());
    }

    public Message setMessageType(final int n) {
        this.header[2] = Bytes.toUint8(n);
        return this;
    }

    public Message setRuleDataVersion(final int n) {
        if (n >= 0 && n <= Bytes.MAX_VALUE_U_INT_16) {
            Bytes.insertUnsignedInt16(this.header, 25, n);
            return this;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid scenario data version ");
        sb.append(n);
        throw new IllegalArgumentException(sb.toString());
    }

    public Message setRuleStateVersion(final int n) {
        if (n >= 0 && n <= Bytes.MAX_VALUE_U_INT_16) {
            Bytes.insertUnsignedInt16(this.header, 23, n);
            return this;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid scenario status version ");
        sb.append(n);
        throw new IllegalArgumentException(sb.toString());
    }

    public Message setSceneDataVersion(final int n) {
        if (n >= 0 && n <= Bytes.MAX_VALUE_U_INT_16) {
            Bytes.insertUnsignedInt16(this.header, 27, n);
            return this;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid scene data version ");
        sb.append(n);
        throw new IllegalArgumentException(sb.toString());
    }

    public Message setSegmentNumber(final int n) {
        if (n >= 0 && n <= 255) {
            this.header[1] = Bytes.toUint8(n);
            return this;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid segment number ");
        sb.append(n);
        throw new IllegalArgumentException(sb.toString());
    }

    public Message setSettingsVersion(final int n) {
        if (n >= 0 && n <= Bytes.MAX_VALUE_U_INT_16) {
            Bytes.insertUnsignedInt16(this.header, 15, n);
            return this;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid settings version ");
        sb.append(n);
        throw new IllegalArgumentException(sb.toString());
    }

    public Message setSmartDeviceId(final int n) {
        if (n >= 0 && n <= Bytes.MAX_VALUE_U_INT_16) {
            Bytes.insertUnsignedInt16(this.header, 33, n);
            return this;
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("Invalid smart device ID ");
        sb.append(n);
        throw new IllegalArgumentException(sb.toString());
    }

    public Message setSmartDeviceId(final long n) {
        return this.setSmartDeviceId((int)n);
    }

    public Message setVersion() {
        this.setGlobalVersion(55);
        this.setSettingsVersion(55);
        this.setDeviceStateVersion(55);
        this.setDeviceDataVersion(55);
        this.setAreaDataVersion(55);
        this.setRuleStateVersion(55);
        this.setRuleDataVersion(55);
        this.setSceneDataVersion(55);
        return this;
    }

    public int size() {
        final byte[] header = this.header;
        int length = 0;
        int length2;
        if (header == null) {
            length2 = 0;
        }
        else {
            length2 = header.length;
        }
        final byte[] data = this.data;
        if (data != null) {
            length = data.length;
        }
        return length2 + length;
    }

    public byte[] toBytes() {
        final byte[] data = this.data;
        byte[] array;
        if (data != null && data.length != 0) {
            array = Bytes.concatenate(new byte[][] { this.header, data });
        }
        else {
            array = this.header;
        }
        return array;
    }
}
