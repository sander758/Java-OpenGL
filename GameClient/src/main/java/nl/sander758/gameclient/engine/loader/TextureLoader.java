package nl.sander758.gameclient.engine.loader;

import nl.sander758.gameclient.engine.guiSystem.texts.TextFactory;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.Math.*;
import static org.lwjgl.BufferUtils.createByteBuffer;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageResize.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class TextureLoader {

    private static TextureLoader textureLoader = new TextureLoader();

    public static TextureLoader getTextureLoader() {
        return textureLoader;
    }

    public Texture loadTexture(String fileName) {
        ByteBuffer imageBuffer;
        try {
            imageBuffer = ioResourceToByteBuffer(fileName, 8 * 1024);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MemoryStack stack = stackPush();
        IntBuffer wBuffer = stack.mallocInt(1);
        IntBuffer hBuffer = stack.mallocInt(1);
        IntBuffer compBuffer = stack.mallocInt(1);

        // Use info to read image metadata without decoding the entire image.
        // We don't need this for this demo, just testing the API.
        if (!stbi_info_from_memory(imageBuffer, wBuffer, hBuffer, compBuffer)) {
            throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
        } else {
            System.out.println("OK with reason: " + stbi_failure_reason());
        }

        System.out.println("Image width: " + wBuffer.get(0));
        System.out.println("Image height: " + hBuffer.get(0));
        System.out.println("Image components: " + compBuffer.get(0));
        System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));

        ByteBuffer image = stbi_load_from_memory(imageBuffer, wBuffer, hBuffer, compBuffer, 0);
        if (image == null) {
            throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
        }

        int textureID = GL11.glGenTextures();
        Texture texture = new Texture(textureID);

        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        int w = wBuffer.get(0);
        int h = hBuffer.get(0);
        int comp = compBuffer.get(0);

        int format;
        if (comp == 3) {
            if ((w & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (w & 1));
            }
            format = GL_RGB;
        } else {
            int stride = w * 4;
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    int i = y * stride + x * 4;

                    float alpha = (image.get(i + 3) & 0xFF) / 255.0f;
                    image.put(i + 0, (byte)round(((image.get(i + 0) & 0xFF) * alpha)));
                    image.put(i + 1, (byte)round(((image.get(i + 1) & 0xFF) * alpha)));
                    image.put(i + 2, (byte)round(((image.get(i + 2) & 0xFF) * alpha)));
                }
            }

            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

            format = GL_RGBA;
        }

        glTexImage2D(GL_TEXTURE_2D, 0, format, w, h, 0, format, GL_UNSIGNED_BYTE, image);

        ByteBuffer input_pixels = image;
        int input_w = w;
        int input_h = h;
        int mipmapLevel  = 0;
        while (1 < input_w || 1 < input_h) {
            int output_w = Math.max(1, input_w >> 1);
            int output_h = Math.max(1, input_h >> 1);

            ByteBuffer output_pixels = memAlloc(output_w * output_h * comp);
            stbir_resize_uint8_generic(
                    input_pixels, input_w, input_h, input_w * comp,
                    output_pixels, output_w, output_h, output_w * comp,
                    comp, comp == 4 ? 3 : STBIR_ALPHA_CHANNEL_NONE, STBIR_FLAG_ALPHA_PREMULTIPLIED,
                    STBIR_EDGE_CLAMP,
                    STBIR_FILTER_MITCHELL,
                    STBIR_COLORSPACE_SRGB
            );

            if (mipmapLevel == 0) {
                stbi_image_free(image);
            } else {
                memFree(input_pixels);
            }

            glTexImage2D(GL_TEXTURE_2D, ++mipmapLevel, format, output_w, output_h, 0, format, GL_UNSIGNED_BYTE, output_pixels);

            input_pixels = output_pixels;
            input_w = output_w;
            input_h = output_h;
        }
        if (mipmapLevel == 0) {
            stbi_image_free(image);
        } else {
            memFree(input_pixels);
        }

        return texture;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        ClassLoader loader = TextureLoader.class.getClassLoader();

        InputStream source = loader.getResourceAsStream("fonts/" + resource + ".png");
        ReadableByteChannel rbc = Channels.newChannel(source);
        buffer = createByteBuffer(bufferSize);

        while (true) {
            int bytes = rbc.read(buffer);
            if (bytes == -1) {
                break;
            }
            if (buffer.remaining() == 0) {
                buffer = resizeBuffer(buffer, buffer.capacity() * 3 / 2); // 50%
            }
        }


        buffer.flip();
        return buffer.slice();
    }
}
