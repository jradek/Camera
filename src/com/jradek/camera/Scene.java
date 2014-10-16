package com.jradek.camera;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class Scene {
    static final int COORDS_PER_VERTEX = 3;

    private final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 aPosition;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * aPosition;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 uColor;" +
            "void main() {" +
            "  gl_FragColor = uColor;" +
            "}";

    static final float[] colorRed = { 1.0f, 0.0f, 0.0f, 0.0f };
    static final float[] colorGreen = { 0.0f, 1.0f, 0.0f, 0.0f };

    private final FloatBuffer mVertexBuffer;
    private final int mShaderProgram;
    private final int mMVPMatrixHandle;
    private final int mPositionHandle;
    private final int mColorHandle;

    public Scene() {
        {   // shape
            float[] triangle1 = {
                    -0.5f, +0.5f, +0.0f,
                    +0.5f, -0.5f, +0.0f,
                    +0.5f, +0.5f, +0.0f
            };

            float[] triangle2 = {
                    -0.4f, +0.5f, -0.5f,
                    +0.6f, -0.5f, -0.5f,
                    +0.6f, +0.5f, -0.5f
            };

            mVertexBuffer = ByteBuffer.allocateDirect((triangle1.length + triangle2.length) * Util.BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mVertexBuffer.put(triangle1);
            mVertexBuffer.put(triangle2);
        }

        {   // shader program
            int vertexShader = Util.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            int fragmentShader = Util.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

            mShaderProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mShaderProgram, vertexShader);
            GLES20.glAttachShader(mShaderProgram, fragmentShader);
            GLES20.glLinkProgram(mShaderProgram);

            // setup handles
            mMVPMatrixHandle = GLES20.glGetUniformLocation(mShaderProgram, "uMVPMatrix");
            mColorHandle = GLES20.glGetUniformLocation(mShaderProgram, "uColor");
            mPositionHandle = GLES20.glGetAttribLocation(mShaderProgram, "aPosition");
        }
    }

    public void draw(float[] mvpMatrix) {
        GLES20.glUseProgram(mShaderProgram);

        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0,
                mVertexBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glUniform4fv(mColorHandle, 1, colorRed, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        GLES20.glUniform4fv(mColorHandle, 1, colorGreen, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 3, 3);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
