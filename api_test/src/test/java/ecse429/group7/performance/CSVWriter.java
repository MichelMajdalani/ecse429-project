package ecse429.group7.performance;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CSVWriter {
    private static final String CSV_FILE_NAME = "performance_results.csv";
    static {
        addLine(new String[] {
                "endpoint",
                "n",
                "operation",
                "total time (ns)",
                "single operation time (ns)",
                "cpu usage %",
                "memory usage %"
        }, false);
    }



    public static void addLine(String[] line, boolean append) {
        if (line.length == 0) return;
        try (FileWriter f = new FileWriter(CSV_FILE_NAME, append); PrintWriter pw = new PrintWriter(f)) {
            StringBuilder csv = new StringBuilder(line[0]);
            for (int i = 1; i < line.length; i++) {
                csv.append(",").append(line[i]);
            }
            pw.println(csv);
        } catch (IOException ignored) {}
    }

    public static void addLine(String[] line) { addLine(line, true); }
}
