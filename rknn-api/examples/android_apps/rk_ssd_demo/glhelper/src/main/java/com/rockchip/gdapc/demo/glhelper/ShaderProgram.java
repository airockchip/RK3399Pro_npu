package com.rockchip.gdapc.demo.glhelper;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.opengl.GLES20.glDeleteProgram;
import static android.opengl.GLES20.glUseProgram;

/**
 * Created by Randall on 2018/5/15
 */

public class ShaderProgram {

    protected int mProgram;

    protected final Context mContext;

    protected ShaderProgram(Context context, int vertexId, int fragId) {
        mContext = context;

        mProgram = ShaderHelper.buildProgram(readText(context, vertexId),
                readText(context, fragId));

    }

    public void useProgram() {
        glUseProgram(mProgram);
    }

    public void release() {
        glDeleteProgram(mProgram);
        mProgram = -1;
    }

    public static String readText(Context context,
                                  int resourceId) {
        StringBuilder body = new StringBuilder();

        try {
            InputStream inputStream = context.getResources()
                    .openRawResource(resourceId);
            InputStreamReader inputStreamReader = new InputStreamReader(
                    inputStream);
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);

            String nextLine;

            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine);
                body.append('\n');
            }

            bufferedReader.close();
            bufferedReader = null;
            inputStreamReader.close();
            inputStreamReader = null;
            inputStream.close();
            inputStream = null;
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not open resource: " + resourceId, e);
        } catch (Resources.NotFoundException nfe) {
            throw new RuntimeException("Resource not found: "
                    + resourceId, nfe);
        }

        return body.toString();
    }
}
