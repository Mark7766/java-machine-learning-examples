package com.sandy.ml.time.trend;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;

public class CsvUtils {

    public static void parseCsv(MultipartFile file, List<LocalDateTime> timestamps, List<Float> values)
            throws IOException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            reader.readNext(); // Skip header
            while ((line = reader.readNext()) != null) {
                if (line.length >= 2) {
                    timestamps.add(LocalDateTime.parse(line[0])); // Expecting ISO format, e.g., 2023-01-01T00:00:00
                    values.add(Float.parseFloat(line[1]));
                }
            }
        } catch (CsvValidationException e) {
            throw new IOException("Invalid CSV format", e);
        }
    }
}