import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

public class Flattener {
    private final String outDir;
    private int tick;

    public Flattener (String src) throws Exception {
        Path srcPath = Paths.get(src);
        outDir = srcPath.getParent().toString() + File.separator + "flatted";

        System.out.println("Src: "+src);
        System.out.println("Dst: "+outDir);
        
        delDir(outDir);
        if (!new File(outDir).mkdirs())
            throw new Exception ("mkdir");

        Files.find(Paths.get(src),
                        Integer.MAX_VALUE,
                        (filePath, fileAttr) -> fileAttr.isRegularFile())
                .forEach(this::copyFile);
    }

    public void copyFile (Path srcPath) {
        String srcFileName = srcPath.getFileName().toString();
        String dstPath = outDir + File.separator + srcFileName;
        int cnt = 1;
        for(;;) {
            try {
                Files.copy(srcPath, Paths.get(dstPath), StandardCopyOption.COPY_ATTRIBUTES);
                System.out.print('.');
                if (++tick == 100) {
                    System.out.println();
                    tick = 0;
                }
                break;
            } catch (Exception e) {
                //System.out.println(e);
                //System.exit(-1);
                //System.out.println("cpy fail: " + copy);
                dstPath = outDir + File.separator + cnt + "-" + srcFileName;
                cnt++;
            }
        }
    }

    public void delDir(String path) throws Exception {
        Path pathToBeDeleted = Paths.get(path);

        System.out.println("Deleting: "+path);
        Files.walk(pathToBeDeleted)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);

        if (Files.exists(pathToBeDeleted))
            throw new Exception("Failed to del Dir");
        System.out.println("Done!");
    }

    /**
     * Start
     * @param args ignored
     * @throws Exception to see what's went wrong
     */
    public static void main(String[] args) throws Exception {
        new Flattener("C:\\Users\\Administrator\\Desktop\\apktool\\abird");
    }
}
