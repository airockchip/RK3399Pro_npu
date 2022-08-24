package com.rockchip.gpadc.ssddemo;

import android.content.res.AssetManager;
import android.graphics.RectF;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import static com.rockchip.gpadc.ssddemo.CameraSurfaceRender.TAG;
import static java.lang.Math.max;
import static java.lang.Math.min;

import com.rockchip.gpadc.ssddemo.InferenceResult.Recognition;

/**
 * Created by randall on 18-4-28.
 */

public class PostProcess {
    private float mMinScore = 0.5f;        // SSD 阈值
    private float mNmsThreshold = 0.45f;   // NMS阈值
    ArrayList<Recognition> recognitions = new ArrayList<Recognition>();

    // Only return this many results.
    public static final int INPUT_SIZE = 300;
    public static final int INPUT_CHANNEL = 3;
    public static final int NUM_RESULTS = 1917;
    public static final int NUM_CLASSES = 91;    //输出分类

    private static final float Y_SCALE = 10.0f;
    private static final float X_SCALE = 10.0f;
    private static final float H_SCALE = 5.0f;
    private static final float W_SCALE = 5.0f;

    private final float[][] boxPriors = new float[4][NUM_RESULTS];

    // Pre-allocated buffers.
    private Vector<String> labels = new Vector<String>();

    public PostProcess() {

    }

    public void init(AssetManager assetManager) throws IOException {
        loadCoderOptions(assetManager, "file:///android_asset/box_priors.txt", boxPriors);
        loadLabelName(assetManager, "file:///android_asset/coco_labels_list.txt", labels);
        recognitions.clear();
    }

    public  ArrayList<Recognition> postProcess(InferenceResult.OutputBuffer outputs) {

        recognitions.clear();

        if ((outputs == null) || (outputs.mLocations == null) || (outputs.mClasses == null)) {
            return recognitions;
        }

        float[] outputLocations = outputs.mLocations;
        float[] outputClasses = outputs.mClasses;
        int[] [] output = new int[2][NUM_RESULTS];
        int validCount = 0;

        decodeCenterSizeBoxes(outputLocations);

        // Scale them back to the input size.
        for (int i = 0; i < NUM_RESULTS; ++i) {
            float topClassScore = -1000f;
            int topClassScoreIndex = -1;

            // Skip the first catch-all class.
            for (int j = 1; j < NUM_CLASSES; ++j) {
                float score = expit(outputClasses[i*NUM_CLASSES +j]);

                if (score > topClassScore) {
                    topClassScoreIndex = j;
                    topClassScore = score;
                }
            }

            if (topClassScore >= mMinScore) {

                output[0][validCount] = i;
                output[1][validCount] = topClassScoreIndex;

                ++validCount;

            }
        }

        if(validCount > 100) {
            Log.d(TAG, "something may wrong! validCount=" + validCount);
            return recognitions;
        }

        //NMS
        for (int i=0; i<validCount; ++i) {

            if (output[0][i] == -1) {
                continue;
            }

            int n = output[0][i];

            for (int j=i+1; j<validCount; ++j) {
                int m = output[0][j];

                if (m == -1) {
                    continue;
                }

                float xmin0 = outputLocations[n*4 + 1];
                float ymin0 = outputLocations[n*4 + 0];
                float xmax0 = outputLocations[n*4 + 3];
                float ymax0 = outputLocations[n*4 + 2];

                float xmin1 = outputLocations[m*4 + 1];
                float ymin1 = outputLocations[m*4 + 0];
                float xmax1 = outputLocations[m*4 + 3];
                float ymax1 = outputLocations[m*4 + 2];

                float iou = CalculateOverlap(xmin0, ymin0, xmax0, ymax0, xmin1, ymin1, xmax1, ymax1);

                if (iou >= mNmsThreshold) {
                    output[0][j] = -1;
                }
            }
        }

        for (int i=0; i<validCount; ++i) {

            if (output[0][i] == -1) {
                continue;
            }

            int n = output[0][i];
            int topClassScoreIndex = output[1][i];

            RectF detection =
                    new RectF(
                            outputLocations[n*4 + 1],
                            outputLocations[n*4 + 0],
                            outputLocations[n*4 + 3],
                            outputLocations[n*4 + 2]);

            Recognition recog = new Recognition(
                    topClassScoreIndex,
                    labels.get(topClassScoreIndex),
                    expit(outputClasses[n*NUM_CLASSES + topClassScoreIndex]),
                    detection);

            recognitions.add(recog);
        }

        return recognitions;
    }

    private float CalculateOverlap(float xmin0, float ymin0, float xmax0, float ymax0, float xmin1, float ymin1, float xmax1, float ymax1) {
        float w = max(0.f, min(xmax0, xmax1) - max(xmin0, xmin1));
        float h = max(0.f, min(ymax0, ymax1) - max(ymin0, ymin1));
        float i = w * h;
        float u = (xmax0 - xmin0) * (ymax0 - ymin0) + (xmax1 - xmin1) * (ymax1 - ymin1) - i;
        return u <= 0.f ? 0.f : (i / u);
    }

    private float expit(final float x) {
        return (float) (1. / (1. + Math.exp(-x)));
    }

    private void loadLabelName(final AssetManager assetManager, final String locationFilename, Vector<String> labels) throws IOException {
        // Try to be intelligent about opening from assets or sdcard depending on prefix.
        final String assetPrefix = "file:///android_asset/";
        InputStream is;
        if (locationFilename.startsWith(assetPrefix)) {
            is = assetManager.open(locationFilename.split(assetPrefix, -1)[1]);
        } else {
            is = new FileInputStream(locationFilename);
        }

        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        String line;
        while ((line = reader.readLine()) != null) {
            labels.add(line);
        }
        reader.close();

        Log.d(TAG, "Loaded label!");
    }

    private void loadCoderOptions(
            final AssetManager assetManager, final String locationFilename, final float[][] boxPriors)
            throws IOException {
        // Try to be intelligent about opening from assets or sdcard depending on prefix.
        final String assetPrefix = "file:///android_asset/";
        InputStream is;
        if (locationFilename.startsWith(assetPrefix)) {
            is = assetManager.open(locationFilename.split(assetPrefix, -1)[1]);
        } else {
            is = new FileInputStream(locationFilename);
        }

        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        for (int lineNum = 0; lineNum < 4; ++lineNum) {
            String line = reader.readLine();
            final StringTokenizer st = new StringTokenizer(line, ", ");
            int priorIndex = 0;
            while (st.hasMoreTokens()) {
                final String token = st.nextToken();
                try {
                    final float number = Float.parseFloat(token);
                    boxPriors[lineNum][priorIndex++] = number;
                } catch (final NumberFormatException e) {
                    // Silently ignore.
                }
            }
            if (priorIndex != NUM_RESULTS) {
                throw new RuntimeException(
                        "BoxPrior length mismatch: " + priorIndex + " vs " + NUM_RESULTS);
            }
        }

        Log.d(TAG, "Loaded box priors!");
    }

    float formatlocation(float n) {
        if (n < 0.0f) {
            return  0.0f;
        }

        if (n > 1.0f) {
            return  1.0f;
        }

        return n;
    }
    void decodeCenterSizeBoxes(float[] predictions) {

        for (int i = 0; i < NUM_RESULTS; ++i) {
            float ycenter = predictions[i*4 + 0] / Y_SCALE * boxPriors[2][i] + boxPriors[0][i];
            float xcenter = predictions[i*4 + 1] / X_SCALE * boxPriors[3][i] + boxPriors[1][i];
            float h = (float) Math.exp(predictions[i*4 + 2] / H_SCALE) * boxPriors[2][i];
            float w = (float) Math.exp(predictions[i*4 + 3] / W_SCALE) * boxPriors[3][i];

            float ymin = ycenter - h / 2.f;
            float xmin = xcenter - w / 2.f;
            float ymax = ycenter + h / 2.f;
            float xmax = xcenter + w / 2.f;

            predictions[i*4 + 0] = formatlocation(ymin);
            predictions[i*4 + 1] = formatlocation(xmin);
            predictions[i*4 + 2] = formatlocation(ymax);
            predictions[i*4 + 3] = formatlocation(xmax);

        }
    }
}
