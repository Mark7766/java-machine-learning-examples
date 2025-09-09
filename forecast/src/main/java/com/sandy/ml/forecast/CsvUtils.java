package com.sandy.ml.forecast;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
public class CsvUtils {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CsvUtils.class);
    public static void parseCsv(MultipartFile file, List<LocalDateTime> timestamps, List<Float> values)
            throws IOException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            reader.readNext(); // Skip header
            while ((line = reader.readNext()) != null) {
                if (line.length >= 2) {
                    log.info("Parsing line: {}", (Object) line);
                    timestamps.add(LocalDateTime.parse(line[0])); // Expecting ISO format, e.g., 2023-01-01T00:00:00
                    values.add(Float.parseFloat(line[1]));
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("Invalid CSV format", e);
        }
    }
}