package com.sandy.ml.image.classification;

import ai.djl.modality.Classifications;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ImageClassificationController {
    private static final Logger logger = LoggerFactory.getLogger(ImageClassificationController.class);

    @Autowired
    private ImageClassificationService classificationService;

    @GetMapping("/classify")
    public String showUploadPage() {
        return "upload"; // 返回 upload.html
    }

    @PostMapping("/classify")
    public ResponseEntity<Map<String, Object>> classifyImage(@RequestParam("image") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (file.isEmpty()) {
                logger.warn("No image uploaded");
                response.put("error", "请上传一张图片");
                return ResponseEntity.badRequest().body(response);
            }
            if (file.getSize() > 10 * 1024 * 1024) { // 限制文件大小为 10MB
                logger.warn("Image size exceeds limit: {} bytes", file.getSize());
                response.put("error", "图片文件过大，最大支持 10MB");
                return ResponseEntity.badRequest().body(response);
            }
            Classifications result = classificationService.classifyImage(file);
            Classifications.Classification best = result.best();
            String prediction = best.getClassName();
            double probability = best.getProbability() * 100; // 转换为百分比
            response.put("message", "图片分类成功");
            response.put("prediction", prediction);
            response.put("probability", String.format("%.2f%%", probability));
            logger.info("Classification successful: prediction={}, probability={}", prediction, probability);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Classification failed", e);
            response.put("error", "分类失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}