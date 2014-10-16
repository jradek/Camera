package com.jradek.camera;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class CheckerBoard {
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

    static final float[] colorGray = { 0.7f, 0.7f, 0.7f, 0.0f };
    static final float[] colorRed = { 1.0f, 0.0f, 0.0f, 0.0f };
    static final float[] colorGreen = { 0.0f, 1.0f, 0.0f, 0.0f };
    static final float[] colorBlue = { 0.0f, 0.0f, 1.0f, 0.0f };

    private final int mNumTilesPerSide;
    private final float mTileLenght;
    private final int mNumVertices;

    private final FloatBuffer mBoardVertexBuffer;
    private final FloatBuffer mAxisVertexBuffer;

    private final int mShaderProgram;
    private final int mMVPMatrixHandle;
    private final int mPositionHandle;
    private final int mColorHandle;

    public CheckerBoard(int numTilesPerSide, float tileLength) {
        if (numTilesPerSide < 2) {
            numTilesPerSide = 10;
        }

        if ((numTilesPerSide % 2) != 0) {
            numTilesPerSide += 1;
        }

        if (tileLength < 0.0f) {
            tileLength = 1.0f;
        }

        mNumTilesPerSide = numTilesPerSide;
        mTileLenght = tileLength;

        { // board
            final int numLines = (mNumTilesPerSide + 1) * 2;
            mNumVertices = numLines * 2;

            float[] vertexData = new float[mNumVertices * COORDS_PER_VERTEX];
            int offset = 0;

            // lines parallel to x-axis
            final float maxCoord = (mNumTilesPerSide / 2) * mTileLenght;
            final float minCoord = -maxCoord;

            for (int i = 0; i < (numTilesPerSide + 1); ++i) {
                // parallel x-axis (left to right)
                vertexData[offset++] = minCoord;
                vertexData[offset++] = 0.0f;
                vertexData[offset++] = minCoord + (i * mTileLenght);

                vertexData[offset++] = maxCoord;
                vertexData[offset++] = 0.0f;
                vertexData[offset++] = minCoord + (i * mTileLenght);

                // parallel z-axis (back to front)
                vertexData[offset++] = minCoord + (i * mTileLenght);
                vertexData[offset++] = 0.0f;
                vertexData[offset++] = minCoord;

                vertexData[offset++] = minCoord + (i * mTileLenght);
                vertexData[offset++] = 0.0f;
                vertexData[offset++] = maxCoord;
            }

            mBoardVertexBuffer = ByteBuffer.allocateDirect(vertexData.length * Util.BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mBoardVertexBuffer.put(vertexData);
        }

        {   // axes
            float[] vertexData = {
                    // x-axis
                    +0.0f, +0.0f, +0.0f,
                    +1.0f, +0.0f, +0.0f,
                    // y-axis
                    +0.0f, +0.0f, +0.0f,
                    +0.0f, +1.0f, +0.0f,
                    // z-axis
                    +0.0f, +0.0f, +0.0f,
                    +0.0f, +0.0f, +1.0f
            };

            mAxisVertexBuffer = ByteBuffer.allocateDirect(vertexData.length * Util.BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mAxisVertexBuffer.put(vertexData);
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

        // board
        mBoardVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0,
                mBoardVertexBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        GLES20.glUniform4fv(mColorHandle, 1, colorGray, 0);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, mNumVertices);

        GLES20.glDisableVertexAttribArray(mPositionHandle);

        // axis
        mAxisVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0,
                mAxisVertexBuffer);

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        // x
        GLES20.glUniform4fv(mColorHandle, 1, colorRed, 0);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);

        // y
        GLES20.glUniform4fv(mColorHandle, 1, colorGreen, 0);
        GLES20.glDrawArrays(GLES20.GL_LINES, 2, 2);

        // z
        GLES20.glUniform4fv(mColorHandle, 1, colorBlue, 0);
        GLES20.glDrawArrays(GLES20.GL_LINES, 4, 2);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
