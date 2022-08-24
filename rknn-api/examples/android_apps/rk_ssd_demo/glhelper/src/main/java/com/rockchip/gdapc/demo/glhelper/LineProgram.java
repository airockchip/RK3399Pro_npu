package com.rockchip.gdapc.demo.glhelper;

import android.content.Context;
import android.graphics.RectF;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINE_LOOP;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glVertexAttribPointer;

/**
 * Created by Randall on 2018/5/15
 */

/**
 * 检测框
 */
public class LineProgram extends ShaderProgram {

    static final float[][] color = {
            {1, 0, 0, 1},   // red
            {0, 1, 0, 1},   // green
            {0, 0, 1, 1},   // blue
            {0.54f, 0.17f, 0.88f, 1},   // Blueviolet
            {0.65f, 0.16f, 0.16f, 1},   // Brown
    };

    private int mTotalColorCnt = color.length;

    // Attribute locations
    private final int mPositionLocation;
    private final int mColor;

    public LineProgram(Context context) {
        super(context, R.raw.line_vertex, R.raw.line_fragment);

        mPositionLocation = glGetAttribLocation(mProgram, "a_Position");
        mColor = glGetUniformLocation(mProgram,"_color");

    }

//    public void draw(ArrayList<SSDDetection.Recognition> recognitions) {
//
//        int totalCnt = recognitions.size();
//
//
//        if( totalCnt <= 0) {
//            return;
//        }
//
//        useProgram();
//
//        glEnableVertexAttribArray(mPositionLocation);
//
//        glLineWidth(10);
//
//        for (int i=0; i<totalCnt; ++i) {
//            SSDDetection.Recognition rego = recognitions.get(i);
//
//            int classId = Integer.parseInt(rego.getId());
//
////            Log.d("xxxx", rego.toString());
//            //person   1
//            //bicycle  2
//            //car      3
//            //motorcycle    4
//            //bus   6
//            //train 7
//            //truck 8
//            //traffic light  10
////            if ((classId == 1)
////                    || (classId == 2)
////                    || (classId == 3)
////                    || (classId == 4)
////                    || (classId == 6)
////                    || (classId == 7)
////                    || (classId == 8)
////                    || (classId == 10)) {
//
//
//            int id =  Integer.parseInt(rego.getId()) % mTotalColorCnt;
//
//            RectF detection = rego.getLocation();
//
//            float xmin = detection.left * 2.0f - 1.0f;
//            float ymin = (1.0f - detection.top) * 2.0f - 1.0f;
//            float xmax = detection.right * 2.0f - 1.0f;
//            float ymax = (1.0f - detection.bottom) * 2.0f - 1.0f;
//
//            float[] pos = {xmin, ymin, xmax, ymin, xmax, ymax, xmin, ymax};
//
//            FloatBuffer buffer = GlUtil.createFloatBuffer(pos);
//
//            glUniform4f(mColor, color[id][0], color[id][1], color[id][2], color[id][3]);
//            glVertexAttribPointer(mPositionLocation, 2, GL_FLOAT, false, 0, buffer);
//            glDrawArrays(GL_LINE_LOOP, 0, 4);
////            }
//
//        }
//
//
//        glDisableVertexAttribArray(mPositionLocation);
//    }
}
