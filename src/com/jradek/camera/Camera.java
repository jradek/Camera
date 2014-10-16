package com.jradek.camera;

import android.opengl.Matrix;

/**
 * This class represents a camera for 3D views. Cameras are defined with
 * a position and three mutually-orthogonal axes, namely look (points in the
 * direction faced by the camera), right (points to the right of the camera)
 * and up (points to the top of the camera).
 */
public abstract class Camera {
    protected final float[] mViewMatrix = new float[16];
    protected final float[] mProjectionMatrix = new float[16];

    protected vec3 mLook;
    protected vec3 mUp;
    protected vec3 mRight;
    protected vec3 mPosition;

    protected float mFovyDegree = 45f;
    protected float mAspectRatio = 4f / 3f;

    private final float[] mHelp2Vectors = new float[8];

    /**
     * Changes the view to be that of the camera.
     */
    public abstract void update();

    /**
     * Initialize projection
     *
     * @param fovyDegree field of view in y-Axis [degree]
     * @param aspectRatio aspect ratio (width / height)
     */
    public void setupProjection(float fovyDegree, float aspectRatio) {
        Matrix.perspectiveM(mProjectionMatrix, 0, fovyDegree, aspectRatio, 0.1f, 100f);

        mAspectRatio = aspectRatio;
        mFovyDegree = fovyDegree;
    }

    public float getFovyDegree() {
        return mFovyDegree;
    }

    public float getAspectRatio() {
        return mAspectRatio;
    }

    public float[] getViewMatrix() {
        return mViewMatrix;
    }

    public float[] getProjectionMatrix() {
        return mProjectionMatrix;
    }

    protected void rotateVector(vec3 srcDest, float[] matrix) {
        mHelp2Vectors[0 + 0] = (float)srcDest.x;
        mHelp2Vectors[0 + 1] = (float)srcDest.y;
        mHelp2Vectors[0 + 2] = (float)srcDest.z;
        mHelp2Vectors[0 + 3] = 0;

        Matrix.multiplyMV(mHelp2Vectors, 4, matrix, 0, mHelp2Vectors, 0);

        srcDest.x = mHelp2Vectors[4 + 0];
        srcDest.y = mHelp2Vectors[4 + 1];
        srcDest.z = mHelp2Vectors[4 + 2];
    }
}
