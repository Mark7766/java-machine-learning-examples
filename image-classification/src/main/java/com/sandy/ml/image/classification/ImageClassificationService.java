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
import ai.djl.ndarray.NDList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class ImageClassificationService {
    private static final Logger logger = LoggerFactory.getLogger(ImageClassificationService.class);
    private static final String MODEL_PATH = "model";
    private static final String MODEL_NAME = "cats-vs-dogs.pt";

    public Classifications classifyImage(MultipartFile file) throws Exception {
        // 定义模型结构
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
            File modelDir = new File(MODEL_PATH);
            if (!modelDir.exists()) {
                throw new IOException("Model directory does not exist: " + modelDir.getAbsolutePath());
            }
            model.load(modelDir.toPath(), MODEL_NAME);

            // 定义图像预处理管道
            Pipeline pipeline = new Pipeline()
                    .add(new Resize(64, 64))
                    .add(new ToTensor());

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

            // 处理上传的图片
            try (Predictor<Image, Classifications> predictor = model.newPredictor(translator)) {
                // 将 MultipartFile 转换为临时文件
                Path tempFile = Files.createTempFile("uploaded-image", ".jpg");
                file.transferTo(tempFile.toFile());
                Image img = ImageFactory.getInstance().fromFile(tempFile);
                Classifications result = predictor.predict(img);
                logger.info("Prediction result: {}", result);
                // 删除临时文件
                Files.deleteIfExists(tempFile);
                return result;
            }
        }
    }
}
