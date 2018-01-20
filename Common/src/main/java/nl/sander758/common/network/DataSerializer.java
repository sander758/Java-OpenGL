package nl.sander758.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class DataSerializer {
    private DataOutput output;
    private ByteArrayOutputStream byteArrayOutput;

    public DataSerializer() {
        this.byteArrayOutput = new ByteArrayOutputStream();
        this.output = new DataOutputStream(byteArrayOutput);
    }

    public void writeBoolean(boolean value) {
        try {
            output.writeBoolean(value);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void writeInt(int value) {
        try {
            output.writeInt(value);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void writeFloat(float value) {
        try {
            output.writeFloat(value);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void writeUnsignedByte(int value) {
        try {
            output.writeByte(value);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void writeByteArray(byte[] bytes) {
        if (bytes == null) {
            writeInt(0);
            return;
        }

        writeInt(bytes.length);
        try {
            System.out.println(Arrays.toString(bytes));
            output.write(bytes);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void writeString(String string) {
        if (string == null) {
            writeInt(0);
            return;
        }
        try {
            writeByteArray(string.getBytes("UTF-8"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    public byte[] toByteArray() {
        return byteArrayOutput.toByteArray();
    }
}
