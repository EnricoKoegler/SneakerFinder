package com.example.sneakerfinder.helper;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.example.sneakerfinder.data.ClassificationResult;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.InterpreterApi;
import org.tensorflow.lite.InterpreterFactory;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the classification of shoe images using a TensorFlow Lite model.
 * The labels for the classes are given in a separate .csv file.
 */
public class SneakersClassifier {
    private static final String MODEL_FILENAME = "model.tflite";
    private static final String CLASS_LABELS = "class_labels.csv";
    private static final int NUM_CLASSES = 127;
    private static SneakersClassifier sneakersClassifier;

    private final ImageProcessor imageProcessor;
    private final InterpreterApi tflite;
    private final List<String> associatedAxisLabels;

    public static SneakersClassifier getInstance(Application application) throws IOException {
        if (sneakersClassifier == null) sneakersClassifier = new SneakersClassifier(application);
        return sneakersClassifier;
    }

    private SneakersClassifier(Context context) throws IOException {
        // Preprocess and normalize image
        imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .add(new NormalizeOp(0.f, 255.f))
                        .build();

        // Initialise the model
        MappedByteBuffer tfliteModel = FileUtil.loadMappedFile(context, MODEL_FILENAME);
        tflite = new InterpreterFactory().create(
                tfliteModel, new InterpreterApi.Options()
        );

        // Read label csv file
        associatedAxisLabels = FileUtil.loadLabels(context, CLASS_LABELS);
    }

    public List<ClassificationResult> classify(Bitmap bitmap) {
        // Create a TensorImage object. This creates the tensor of the corresponding
        // tensor type (uint8 in this case) that the TensorFlow Lite interpreter needs.
        TensorImage tensorImage = new TensorImage(DataType.UINT8);

        // Load image
        tensorImage.load(bitmap);

        // Preprocess image
        tensorImage = imageProcessor.process(tensorImage);

        TensorBuffer probabilityBuffer =
                TensorBuffer.createFixedSize(new int[]{1, NUM_CLASSES}, DataType.FLOAT32);

        // Classify the image
        tflite.run(tensorImage.getBuffer(), probabilityBuffer.getBuffer());

        // Map of labels and their corresponding probability
        TensorLabel labels = new TensorLabel(associatedAxisLabels, probabilityBuffer);

        // Extract list of classification results
        List<ClassificationResult> classificationResults = new ArrayList<>();
        for (Category c: labels.getCategoryList()) {
            classificationResults.add(
                    new ClassificationResult(c.getIndex(), c.getLabel(), c.getScore())
            );
        }

        return classificationResults;
    }
}
