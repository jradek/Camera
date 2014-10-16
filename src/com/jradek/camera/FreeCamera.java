package com.jradek.camera;

import android.opengl.Matrix;

/**
 * Special camera which can be move around freely.
 */
public class FreeCamera extends Camera {
    private vec3 mTranslation;

    private final float[] mHelpMatrix = new float[16];

    private float mRollAngleDegree = 0.0f;
    private float mPitchAngleDegree = 0.0f;
    private float mYawAngleDegree = 0.0f;

    /**
     * Constructs a new camera.
     * @param position The position of the camera
     * @param look The direction the camera is looking
     * @param up The "up" direction for the camera
     */
    public FreeCamera(final vec3 position, final vec3 look, final vec3 up) {
        reset(position, look, up);
    }

    public void reset(final vec3 position, final vec3 look, final vec3 up) {
        mTranslation = new vec3(0, 0, 0);

        mPosition = position.clone();
        mLook = look.clone().normalize();
        mUp = up.clone().normalize();

        mRight = mLook.cross(mUp).normalize();

        mRollAngleDegree = 0.0f;
        mPitchAngleDegree = 0.0f;
        mYawAngleDegree = 0.0f;

        update();
    }

    /**
     * Moves the camera by the specified displacement in the look direction.
     * @param delta    The displacement by which to move
     */
    public void walk(float delta) {
        mTranslation = mTranslation.addScaled(mLook, delta);
    }

    /**
     * Moves the camera by the specified displacement in the right direction.
     * @param delta    The displacement by which to move
     */
    public void strafe(float delta) {
        mTranslation = mTranslation.addScaled(mRight, delta);
    }

    /**
     * Moves the camera by the specified displacement in the up direction.
     * @param delta    The displacement by which to move
     */
    public void lift(float delta) {
        mTranslation = mTranslation.addScaled(mUp, delta);
    }

    /**
     * Rotate the camera
     *
     * @param deltaRollDegree The displacement around the "look" direction
     * @param deltaPitchDegree The displacement around the "right" direction
     * @param deltaYawDegree The displacement around the "up" direction
     */
    public void rotate(float deltaRollDegree, float deltaPitchDegree, float deltaYawDegree) {
        mRollAngleDegree += deltaRollDegree;
        mPitchAngleDegree += deltaPitchDegree;
        mYawAngleDegree += deltaYawDegree;
    }

    @SuppressWarnings("unused")
    private void setPosition(vec3 position) {
        mPosition = position.clone();
    }

    @Override
    public void update() {
        Matrix.setIdentityM(mHelpMatrix, 0);

        mPosition.add(mTranslation);
        mTranslation.x = 0;
        mTranslation.y = 0;
        mTranslation.z = 0;

        // TODO: find a way to combine this rotations into one

        // apply roll
        Matrix.setRotateM(mHelpMatrix, 0, mRollAngleDegree, (float)mLook.x, (float)mLook.y, (float)mLook.z);
        rotateVector(mUp, mHelpMatrix);
        mRight = mLook.cross(mUp).normalize();

        // apply pitch
        Matrix.setRotateM(mHelpMatrix, 0, mPitchAngleDegree, (float)mRight.x, (float)mRight.y, (float)mRight.z);
        rotateVector(mLook, mHelpMatrix);
        rotateVector(mUp, mHelpMatrix);

        // apply yaw
        Matrix.setRotateM(mHelpMatrix, 0, mYawAngleDegree, (float)mUp.x, (float)mUp.y, (float)mUp.z);
        rotateVector(mLook, mHelpMatrix);
        mRight = mLook.cross(mUp).normalize();

        mRollAngleDegree = 0;
        mPitchAngleDegree = 0;
        mYawAngleDegree = 0;

        Matrix.setLookAtM(mViewMatrix, 0,
                (float)mPosition.x, (float)mPosition.y, (float)mPosition.z,
                (float)(mPosition.x + mLook.x), (float)(mPosition.y + mLook.y), (float)(mPosition.z + mLook.z),
                (float)mUp.x, (float)mUp.y, (float)mUp.z);
    }
}
