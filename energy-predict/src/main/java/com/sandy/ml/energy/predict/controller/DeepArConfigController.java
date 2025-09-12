package com.sandy.ml.energy.predict.controller;

import com.sandy.ml.energy.predict.model.DeepArConfig;
import com.sandy.ml.energy.predict.service.DeepArConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/config")
public class DeepArConfigController {
    @Autowired
    private DeepArConfigService configService;

    @GetMapping("")
    public String getConfig(Model model) {
        List<DeepArConfig> configs = configService.findAll();
        DeepArConfig config = configs.isEmpty() ? new DeepArConfig() : configs.get(0);
        model.addAttribute("config", config);
        return "energy_predict";
    }

    @PostMapping("/save")
    public String saveConfig(@ModelAttribute DeepArConfig config, Model model) {
        configService.save(config);
        model.addAttribute("config", config);
        model.addAttribute("message", "保存成功");
        return "energy_predict";
    }
}

