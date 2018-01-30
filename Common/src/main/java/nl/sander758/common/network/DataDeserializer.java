package nl.sander758.common.network;

import org.joml.Vector3f;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

public class DataDeserializer {
    private DataInput input;

    public DataDeserializer(byte[] data) {
        this.input = new DataInputStream(new ByteArrayInputStream(data));
    }

    public boolean readBoolean() {
        try {
            return input.readBoolean();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public int readInt() {
        try {
            return input.readInt();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public float readFloat() {
        try {
            return input.readFloat();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public Vector3f readVector3f() {
        try {
            float x = input.readFloat();
            float y = input.readFloat();
            float z = input.readFloat();
            return new Vector3f(x, y, z);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte readUnsignedByte() {
        try {
            return input.readByte();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte[] readByteArray() {
        byte[] bytes = new byte[readInt()];
        try {
            input.readFully(bytes);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return bytes;
    }

    public String readString() {
        try {
            return new String(readByteArray(), "UTF-8");
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
