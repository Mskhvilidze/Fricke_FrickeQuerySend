package fricke.util;

import java.io.File;
import java.io.IOException;

public class FilePath {

    public static void ifExistOverwrite(File file) {
        if (file.exists()) {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeAllFile() {
        File file = new File("files/");
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length > 0) {
                for (File toDeleteFile : files){
                    toDeleteFile.delete();
                }
            }
        }
    }
}
