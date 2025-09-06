package com.sandy.ml.image.classification;

import ai.djl.Device;
import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.types.Shape;
import ai.djl.nn.Blocks;
import ai.djl.nn.SequentialBlock;
import ai.djl.nn.convolutional.Conv2d;
import ai.djl.nn.core.Linear;
import ai.djl.nn.pooling.Pool;
import ai.djl.training.DefaultTrainingConfig;
import ai.djl.training.evaluator.Accuracy;
import ai.djl.training.listener.TrainingListener;
import ai.djl.training.loss.Loss;
import ai.djl.training.optimizer.Adam;
import ai.djl.translate.Pipeline;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import ai.djl.ndarray.NDList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Arrays;

public class Inference {
    private static final Logger logger = LoggerFactory.getLogger(Inference.class);

    public static void main(String[] args) throws Exception {
        // 定义模型
        SequentialBlock block = new SequentialBlock()
                .add(Conv2d.builder().setKernelShape(new Shape(3, 3)).setFilters(16).build())
                .add(Pool.maxPool2dBlock(new Shape(2, 2), new Shape(2, 2)))
                .add(Blocks.batchFlattenBlock())
                .add(Linear.builder().setUnits(128).build())
                .add(Linear.builder().setUnits(2).build());
        DefaultTrainingConfig config = new DefaultTrainingConfig(Loss.softmaxCrossEntropyLoss())
                .addEvaluator(new Accuracy())
                .optOptimizer(Adam.builder().build())
                .optDevices(new Device[]{Device.cpu()}) // 显式使用 CPU
                .addTrainingListeners(TrainingListener.Defaults.logging());
        // 加载模型
        try (Model model = Model.newInstance("CatsVsDogs", "PyTorch")) {
            model.setBlock(block);
            model.newTrainer(config);
            model.load(new File("model").toPath(), "cats-vs-dogs.pt");

            // 定义图像预处理管道
            Pipeline pipeline = new Pipeline()
                    .add(new Resize(64, 64)) // 调整大小为 64x64
                    .add(new ToTensor());    // 转换为张量

            Translator<Image, Classifications> translator = new Translator<>() {
                @Override
                public Classifications processOutput(TranslatorContext ctx, NDList list) {
                    return new Classifications(Arrays.asList("cat", "dog"), list.get(0).softmax(0));
                }

                @Override
                public NDList processInput(TranslatorContext ctx, Image input) {
                    NDArray array = input.toNDArray(ctx.getNDManager(), Image.Flag.COLOR);
                    return pipeline.transform(new NDList(array));
                }
            };

            try (Predictor<Image, Classifications> predictor = model.newPredictor(translator)) {
                Image img = ImageFactory.getInstance().fromFile(new File("/Users/mark/work/gitspace/opensource/java-machine-learning-examples/image-classification/src/main/resources/kagglecatsanddogs_5340/PetImages/Cat/50.jpg").toPath());
                Classifications result = predictor.predict(img);
                logger.info("Prediction: {}", result);
            }
        }
    }
}