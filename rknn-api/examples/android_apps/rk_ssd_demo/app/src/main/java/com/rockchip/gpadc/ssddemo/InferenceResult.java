package com.rockchip.gpadc.ssddemo;

import android.content.res.AssetManager;
import android.graphics.RectF;
import android.nfc.Tag;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import static java.lang.System.arraycopy;

public class InferenceResult {

    OutputBuffer mOutputBuffer;
    ArrayList<Recognition> recognitions = null;
    private boolean mIsVaild = false;   //是否需要重新计算
    PostProcess mPostProcess = new PostProcess();

    public void init(AssetManager assetManager) throws IOException {
        mOutputBuffer = new OutputBuffer();
        mPostProcess.init(assetManager);
    }

    public void reset() {
        if (recognitions != null) {
            recognitions.clear();
            mIsVaild = true;
        }
    }
    public synchronized void setResult(OutputBuffer outputs) {

        if (mOutputBuffer.mLocations == null) {
            mOutputBuffer.mLocations = outputs.mLocations.clone();
            mOutputBuffer.mClasses = outputs.mClasses.clone();
        } else {
            arraycopy(outputs.mLocations, 0, mOutputBuffer.mLocations, 0, outputs.mLocations.length);
            arraycopy(outputs.mClasses, 0, mOutputBuffer.mClasses, 0, outputs.mClasses.length);
        }
        mIsVaild = false;
    }

    public synchronized ArrayList<Recognition> getResult() {
        if (!mIsVaild) {
            mIsVaild = true;
            recognitions = mPostProcess.postProcess(mOutputBuffer);
        }

        return recognitions;
    }

    public static class OutputBuffer {
        public float[] mLocations;
        public float[] mClasses;
    }

    /**
     * An immutable result returned by a Classifier describing what was recognized.
     */
    public static class Recognition {
        /**
         * A unique identifier for what has been recognized. Specific to the class, not the instance of
         * the object.
         */
        private final int id;

        /**
         * Display name for the recognition.
         */
        private final String title;

        /**
         * A sortable score for how good the recognition is relative to others. Higher should be better.
         */
        private final Float confidence;

        /** Optional location within the source image for the location of the recognized object. */
        private RectF location;

        public Recognition(
                final int id, final String title, final Float confidence, final RectF location) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
            this.location = location;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Float getConfidence() {
            return confidence;
        }

        public RectF getLocation() {
            return new RectF(location);
        }

        public void setLocation(RectF location) {
            this.location = location;
        }

        @Override
        public String toString() {
            String resultString = "";

            resultString += "[" + id + "] ";

            if (title != null) {
                resultString += title + " ";
            }

            if (confidence != null) {
                resultString += String.format("(%.1f%%) ", confidence * 100.0f);
            }

            if (location != null) {
                resultString += location + " ";
            }

            return resultString.trim();
        }
    }
}
