package com.sandy.ml.time.trend;

import ai.djl.ModelException;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.timeseries.Forecast;
import ai.djl.timeseries.TimeSeriesData;
import ai.djl.timeseries.translator.DeepARTranslator;
import ai.djl.translate.Translator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;
@Configuration
public class DjlConfig {

    @Bean
    public ZooModel<TimeSeriesData, Forecast> deepARModel() throws IOException, ModelException {
        Map<String, ?> arguments = Map.of(
                "context_length", 24,
                "prediction_length", 12,
                "freq", "M"
        );
        Translator<TimeSeriesData, Forecast> translator = DeepARTranslator.builder(arguments).build();

        Criteria<TimeSeriesData, Forecast> criteria= Criteria.builder()
                .setTypes(TimeSeriesData.class, Forecast.class)
                .optModelUrls("https://resources.djl.ai/test-models/pytorch/timeseries/deepar_airpassenger.zip")
                .optTranslator(translator)
                .optEngine("PyTorch")
                .optOption("device", "cpu")
                .build();
        return ModelZoo.loadModel(criteria);
    }
}