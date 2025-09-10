package com.sandy.ml.energy.predict;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ModelMetadata {
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String name;
private String path;
private Float rmse;
private Float crps;
private LocalDateTime trainedAt;
}