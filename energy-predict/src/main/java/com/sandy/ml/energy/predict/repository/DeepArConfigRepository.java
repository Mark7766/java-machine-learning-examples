package com.sandy.ml.energy.predict.repository;

import com.sandy.ml.energy.predict.model.DeepArConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeepArConfigRepository extends JpaRepository<DeepArConfig, Long> {
}
