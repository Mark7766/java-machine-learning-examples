package com.sandy.ml.energy.predict;

import lombok.Data;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Data
@Entity
public class ParameterConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int predictionLength;
    private String freq;
    private int contextLength;
    private int numLayers;
    private int numCells;
    private float dropoutRate;
    private float learningRate;
    private int epochs;
    private int numSamples;
    private String likelihood;
    private int[] cardinality;
    private int dynamicDim;
}
