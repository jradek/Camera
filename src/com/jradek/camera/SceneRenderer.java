package com.jradek.camera;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;

public class SceneRenderer implements Renderer {
    private Scene mScene;
    private CheckerBoard mCheckerBoard;

    private final float[] mMVPMatrix = new float[16];
    private final FreeCamera mFreeCamera;

    public SceneRenderer(Context conext) {
        mFreeCamera = new FreeCamera(new vec3(0, 0, 5), new vec3(0, 0, -1), new  vec3(0, 1, 0));
    }

    @Override
    public void onDrawFrame(GL10 arg0) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        mCheckerBoard.draw(mMVPMatrix);
        mScene.draw(mMVPMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        mFreeCamera.setupProjection(75.0f, width / (float) height);
        Matrix.multiplyMM(mMVPMatrix, 0, mFreeCamera.getProjectionMatrix(), 0,
                mFreeCamera.getViewMatrix(), 0);
    }

    @Override
    public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        mScene = new Scene();
        mCheckerBoard = new CheckerBoard(10, 1.0f);
    }

    public void moveCamera(float walk, float strafe, float lift) {
        mFreeCamera.walk(walk);
        mFreeCamera.strafe(strafe);
        mFreeCamera.lift(lift);
        mFreeCamera.update();

        Matrix.multiplyMM(mMVPMatrix, 0, mFreeCamera.getProjectionMatrix(), 0,
                mFreeCamera.getViewMatrix(), 0);
    }

    public void rotateCamera(float rollAngleDegree, float pitchAngleDegree, float yawAngleDegree) {
        mFreeCamera.rotate(rollAngleDegree, pitchAngleDegree, yawAngleDegree);
        mFreeCamera.update();
        Matrix.multiplyMM(mMVPMatrix, 0, mFreeCamera.getProjectionMatrix(), 0,
                mFreeCamera.getViewMatrix(), 0);
    }

    public void resetCamera() {
        mFreeCamera.reset(new vec3(0, 0, 5), new vec3(0, 0, -1), new  vec3(0, 1, 0));
        mFreeCamera.update();

        Matrix.multiplyMM(mMVPMatrix, 0, mFreeCamera.getProjectionMatrix(), 0,
                mFreeCamera.getViewMatrix(), 0);
    }
}
