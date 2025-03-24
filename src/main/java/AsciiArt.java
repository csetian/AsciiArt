import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AsciiArt {

    private static int WIDTH ;  // Width of the grid (number of columns)
    private static int HEIGHT; // Height of the grid (number of rows)
    private static String FILE_PATH;
    private static String DRAW_CHAR;

    //Default values could be moved to a config file rather than being hardcoded.
    private static final int DEFAULT_WIDTH = 200;  // Width of the grid (number of columns)
    private static final int DEFAULT_HEIGHT = 400; // Height of the grid (number of rows)
    private static final String DEFAULT_FILE_PATH = "ukpostcodes.csv.zip";
    private static final String DEFAULT_DRAW_CHAR = "*";

    public static void main(String[] args) {
        //Parse arguments
        parseArgs(args);

        // read in csv
        List<List<String>> records;
        try {
            records =  getFile(FILE_PATH);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        if (records == null || records.isEmpty()) {
            System.out.println("No records found in file. Exiting");
            return;
        }

        List<UkPostcode> postcodes = new ArrayList<>();
        double minLongitude = Double.MAX_VALUE;
        double maxLongitude = Double.MIN_VALUE;
        double minLatitude = Double.MAX_VALUE;
        double maxLatitude = Double.MIN_VALUE;
        // create objects and find min/max longitude and latitude
        for(List<String> record : records) {
            UkPostcode p = new UkPostcode(record.get(0), record.get(1), record.get(2), record.get(3));
            postcodes.add(p);
            minLongitude = Math.min(minLongitude, p.getLongitude());
            maxLongitude = Math.max(maxLongitude, p.getLongitude());
            minLatitude = Math.min(minLatitude, p.getLatitude());
            maxLatitude = Math.max(maxLatitude, p.getLatitude());
        }

        draw(postcodes, minLongitude, minLatitude, maxLongitude, maxLatitude);
    }

    private static void draw(List<UkPostcode> postcodes, double minLongitude, double minLatitude, double maxLongitude,
                             double maxLatitude) {
        // Initialize the canvas
        BufferedImage bufferedImage = new BufferedImage(
                WIDTH, HEIGHT,
                BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        for(UkPostcode postcode : postcodes) {
            // Normalize longitude and latitude to fit the grid
            Double longitude = postcode.getLongitude();
            Double latitude = postcode.getLatitude();
            int x = (int) ((longitude - minLongitude) / (maxLongitude - minLongitude) * (WIDTH - 1));
            int y = HEIGHT - 1 - (int) ((latitude - minLatitude) / (maxLatitude - minLatitude) * (HEIGHT - 1));
            // Mark the grid with a symbol (e.g., '*')
            graphics2D.drawString(DRAW_CHAR, x, y);
        }

        for (int y = 0; y < HEIGHT; y++) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int x = 0; x < WIDTH; x++) {
                stringBuilder.append(bufferedImage.getRGB(x, y) == -16777216 ? " " : DRAW_CHAR);
            }

            if (stringBuilder.toString().trim().isEmpty()) {
                continue;
            }

            System.out.println(stringBuilder);
        }
    }

    public static void parseArgs(String[] args) {
        if(args == null || args.length == 0) {
            System.out.println("No arguments detected. Running with defaults");
            HEIGHT = DEFAULT_HEIGHT;
            WIDTH = DEFAULT_WIDTH;
            FILE_PATH = DEFAULT_FILE_PATH;
            return;
        }
        FILE_PATH = args[0].trim();
        WIDTH = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_WIDTH;
        HEIGHT = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_HEIGHT;
        DRAW_CHAR = args.length > 3 ? args[3].trim() : DEFAULT_DRAW_CHAR;
        if (WIDTH <=0 || HEIGHT <=0) {
            System.out.println("Error: Width and/or Height cannot be <= 0");
            throw new IllegalArgumentException("Error: Width and/or Height cannot be <= 0");
        }
    }

    // this assumes all files come in the same format of
    // id, postcode, longitude, latitude
    public static List<List<String>> getFile(String filePath) throws IOException {
        List<List<String>> records;
        if (filePath.endsWith(".zip")) {
            try (ZipFile zipFile = new ZipFile(filePath)) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    // Check if entry is a directory
                    if (!entry.isDirectory()) {
                        try (InputStream inputStream = zipFile.getInputStream(entry)) {
                            // Read and process the entry contents using the inputStream
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            records = reader.lines()
                                    .skip(1)
                                    .map(line -> Arrays.asList(line.split(",")))
                                    .collect(Collectors.toList());
                            return records;
                        }
                    }
                }
            }
        }
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
            records = reader.lines()
                    .skip(1)
                    .map(line -> Arrays.asList(line.split(",")))
                    .collect(Collectors.toList());
            return records;
        }

    }


}
