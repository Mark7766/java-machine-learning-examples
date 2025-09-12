package com.sandy.ml.energy.predict.service;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.timeseries.Forecast;
import ai.djl.timeseries.TimeSeriesData;
import ai.djl.timeseries.translator.DeepARTranslator;
import ai.djl.training.util.ProgressBar;
import com.sandy.ml.energy.predict.model.DeepArConfig;
import com.sandy.ml.energy.predict.repository.DeepArConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DeepArConfigService {
    @Autowired
    private DeepArConfigRepository repository;

    public List<DeepArConfig> findAll() {
        return repository.findAll();
    }

    public Optional<DeepArConfig> findById(Long id) {
        return repository.findById(id);
    }

    public DeepArConfig save(DeepArConfig config) {
        return repository.save(config);
    }

    public DeepArConfig update(Long id, DeepArConfig config) {
        config.setId(id);
        return repository.save(config);
    }

    public ZooModel<TimeSeriesData, Forecast> createDeepARModel(DeepArConfig config) throws ModelNotFoundException, MalformedModelException, IOException {
            Map<String, Object> arguments = new ConcurrentHashMap<>();
            arguments.put("prediction_length", config.getPredictionLength());
            arguments.put("freq", config.getFreq());
            arguments.put("context_length", config.getContextLength());
            arguments.put("num_layers", config.getNumLayers());
            arguments.put("dropout_rate", config.getDropoutRate());
            arguments.put("learning_rate", config.getLearningRate());
            arguments.put("epochs", config.getEpochs());
            arguments.put("num_samples", config.getNumSamples());
            arguments.put("use_dynamic_feat", true);
            arguments.put("use_static_cat", true);

            DeepARTranslator translator = DeepARTranslator.builder(arguments).build();
            Criteria<TimeSeriesData, Forecast> criteria = Criteria.builder()
                    .setTypes(TimeSeriesData.class, Forecast.class)
                    .optTranslator(translator)
                    .optProgress(new ProgressBar())
                    .build();
            return criteria.loadModel();
    }
}

