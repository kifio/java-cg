import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Common {

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
