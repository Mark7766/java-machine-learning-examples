package com.sandy.ml.image.classification;
import ai.djl.Model;
import ai.djl.basicdataset.cv.classification.ImageFolder;
import ai.djl.metric.Metrics;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.convolutional.Conv2d;
import ai.djl.nn.core.Linear;
import ai.djl.nn.pooling.Pool;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.EasyTrain;
import ai.djl.training.Trainer;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.training.optimizer.Adam;
import ai.djl.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

public class TrainCatsVsDogsBak {
    private static final Logger logger = LoggerFactory.getLogger(TrainCatsVsDogsBak.class);

    public static void main(String[] args) throws Exception {
        // 启用 MPS
        System.setProperty("PYTORCH_MPS", "true");
        System.setProperty("PYTORCH_MPS_FALLBACK", "0");
        logger.info("MPS Environment Variable: {}", System.getenv("PYTORCH_MPS"));
        logger.info("MPS Fallback Property: {}", System.getProperty("PYTORCH_MPS_FALLBACK"));

        // 定义模型
        SequentialBlock block = new SequentialBlock()
                .add(Conv2d.builder().setKernelShape(new Shape(3, 3)).setFilters(16).build())
                .add(Pool.maxPool2dBlock(new Shape(2, 2), new Shape(2, 2)))
                .add(Blocks.batchFlattenBlock())
                .add(Linear.builder().setUnits(128).build())
                .add(Linear.builder().setUnits(2).build());

        // 配置数据集
        ImageFolder dataset = ImageFolder.builder()
                .setRepositoryPath(new File("cats_vs_dogs/processed").toPath())
                .setSampling(32, true)
                .build();
        dataset.prepare();
        logger.info("数据集准备完成，包含 {} 张图像", dataset.size());

        // 配置训练
        try (Model model = Model.newInstance("CatsVsDogs")) {
            model.setBlock(block);
            DefaultTrainingConfig config = new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
                    .addEvaluator(new Accuracy())
                    .optOptimizer(Adam.builder().build())
                    .optDevices(new Device[]{Device.cpu()})// 显式使用 MPS
                    .addTrainingListeners(TrainingListener.Defaults.logging());
            try (Trainer trainer = model.newTrainer(config)) {
                trainer.setMetrics(new Metrics());
                trainer.initialize(new Shape(1, 3, 64, 64));
                logger.info("训练设备：{}", trainer.getDevices()[0]);
                EasyTrain.fit(trainer, 5, dataset, null);
                model.save(new File("model").toPath(), "cats-vs-dogs");
                logger.info("模型保存成功");
            }
        }
    }
}
