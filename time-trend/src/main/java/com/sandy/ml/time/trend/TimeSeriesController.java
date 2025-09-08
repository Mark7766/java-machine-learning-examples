package com.sandy.ml.time.trend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class TimeSeriesController {

    private final TimeSeriesService timeSeriesService;

    public TimeSeriesController(TimeSeriesService timeSeriesService) {
        this.timeSeriesService = timeSeriesService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("dataModel", new TimeSeriesDataModel());
        return "index";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        try {
            TimeSeriesDataModel dataModel = timeSeriesService.processCsvFile(file);
            model.addAttribute("dataModel", dataModel);
        } catch (IOException e) {
            model.addAttribute("error", "Failed to process file: " + e.getMessage());
        }
        return "index";
    }
}
