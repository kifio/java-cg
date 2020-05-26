import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static com.jogamp.opengl.GL2ES3.GL_COLOR;

public class Common {

    private static final float[] BUFFER_ARRAY = {
            0.0f, 0.0f, 0.0f, 1.0f
    };

    public static void clearBuffer(GL4 gl) {
        gl.glClearBufferfv(
                GL_COLOR, 0,
                Buffers.newDirectFloatBuffer(BUFFER_ARRAY)
        );
    }

    public static String[] readShaderSource(String filename) {
        Scanner scanner;
        ArrayList<String> lines = new ArrayList<>();
        try {
            scanner = new Scanner(new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        while (scanner.hasNext()) {
            lines.add(scanner.nextLine() + "\n");
        }

        String[] result = new String[lines.size()];

        return lines.toArray(result);
    }
}
