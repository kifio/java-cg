import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.math.Matrix4;
import mathlib3D.Matrix3D;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
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

    public static Matrix3D perspective(float fovy, float aspect, float n, float f) {
        float q = 1.0f / (float) Math.tan(Math.toRadians(0.5f * fovy));
        float A = q / aspect;
        float B = (n + f) / (n - f);
        float C = (2.0f * n * f) / (n - f);
        Matrix3D matrix3D = new Matrix3D();

        matrix3D.setElementAt(0, 0, A);
        matrix3D.setElementAt(1, 1, q);
        matrix3D.setElementAt(2, 2, B);

        matrix3D.setElementAt(3, 2, -1f);
        matrix3D.setElementAt(2, 3, C);
        matrix3D.setElementAt(3, 3, 0f);

        return matrix3D;
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
