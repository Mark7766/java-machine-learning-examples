package com.sandy.ml.energy.predict.repository;

import com.sandy.ml.energy.predict.model.DeepArModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeepArModelRepository extends JpaRepository<DeepArModel, Long> {
    DeepArModel findByEnabled(boolean enabled);
}

