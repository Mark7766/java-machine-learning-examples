package com.sandy.ml.energy.predict.controller;

import com.sandy.ml.energy.predict.model.DeepArConfig;
import com.sandy.ml.energy.predict.model.DeepArData;
import com.sandy.ml.energy.predict.model.DeepArModel;
import com.sandy.ml.energy.predict.service.DeepArConfigService;
import com.sandy.ml.energy.predict.service.DeepArDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/data")
public class DeepArDataController {
    @Autowired
    private DeepArDataService dataService;
    @Autowired
    private DeepArConfigService configService;

    @PostMapping("/predict")
    public String predict(@ModelAttribute DeepArData param, Model model) {
        DeepArConfig config = configService.findAll().isEmpty() ? new DeepArConfig() : configService.findAll().get(0);
        Object forecast = dataService.predict(param, config);
        model.addAttribute("forecast", forecast);
        return "energy_predict";
    }

    @GetMapping("/models")
    @ResponseBody
    public List<DeepArModel> getModelList() {
        return dataService.getModelList();
    }

    @PostMapping("/model/enable")
    @ResponseBody
    public String enableModel(@RequestParam("modelId") String modelId) {
        dataService.enableModel(modelId);
        return "模型已启用";
    }

    @PostMapping("/model/disable")
    @ResponseBody
    public String disableModel(@RequestParam("modelId") String modelId) {
        dataService.disableModel(modelId);
        return "模型已禁用";
    }

    // 合并上传和训练接口
    @PostMapping("/uploadAndTrain")
    @ResponseBody
    public String uploadAndTrain(@RequestParam("file") MultipartFile file) {
        DeepArConfig config = configService.findAll().isEmpty() ? new DeepArConfig() : configService.findAll().get(0);
        String sessionId = UUID.randomUUID().toString();
        // 异步执行训练（如需更优体验可用线程池，这里简化为新线程）
        new Thread(() -> dataService.uploadAndTrain(file, config, sessionId)).start();
        return sessionId;
    }

    // 训练进度查询接口
    @GetMapping("/trainProgress")
    @ResponseBody
    public String getTrainProgress(@RequestParam("sessionId") String sessionId) {
        return dataService.getTrainProgress(sessionId);
    }
}
