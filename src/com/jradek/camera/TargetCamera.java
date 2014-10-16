package com.jradek.camera;

import android.opengl.Matrix;

public class TargetCamera extends Camera {
    private vec3 mTarget;

    /**
     * distance from the target
     */
    private double mDistance = 0.0;

    /**
     * inclination between y-Axis and x-z-plane. 0 degree is on y-Axis
     */
    private float mInclinationDegree = 0.0f;

    /**
     * azimuth in x-z-plane. 0 degree is on x-Axis
     */
    private float mAzimuthDegree = 0.0f;

    private final float[] mHelpMatrix = new float[16];
    private final float[] mHelpVectors = new float[16];

    public TargetCamera(final vec3 position, final vec3 target) {
        mLook = new vec3(0,0,-1);
        mUp = new vec3(0,1,0);
        mRight = mLook.cross(mUp).normalize();
        reset(position, target);
    }

    public void reset(final vec3 position, final vec3 target) {
        mPosition = position.clone();
        setTarget(target);

        update();
    }

    public void setTarget(final vec3 target) {
        mTarget = target.clone();

        // calculate inclination and azimuth using mTarget
        // as origin of reference coordinate system
        vec3 tmpPos = mPosition.clone().substract(mTarget);
        mDistance = tmpPos.length();
        mInclinationDegree = (float)(Math.acos(tmpPos.y / mDistance) * 180.0 / Math.PI);
        mAzimuthDegree = (float)(Math.atan2(tmpPos.x, tmpPos.z) * 180.0 / Math.PI);
        mAzimuthDegree += 270;
    }

    public void pan(float deltaRight, float deltaUp) {
        final vec3 x = mRight.clone().scale(deltaRight);
        final vec3 y = mUp.clone().scale(deltaUp);

        mPosition.add(x).add(y);
        mTarget.add(x).add(y);
    }

    public void move(float deltaRight, float deltaLook) {
        final vec3 x = mRight.clone().scale(deltaRight);
        final vec3 y = mLook.clone().scale(deltaLook);

        mPosition.add(x).add(y);
        mTarget.add(x).add(y);
    }

    public void zoom(float amount) {
        mPosition = mPosition.addScaled(mLook, amount);
        mDistance = vec3.distance(mPosition, mTarget);

        // limit
        final double minDistance = 0.1;
        final double maxDistance = 100;
        mDistance = Math.max(minDistance, Math.min(mDistance, maxDistance));
    }

    public void rotate(float deltaAzimuthDegree, float deltaInclinationDegree) {
        mAzimuthDegree += deltaAzimuthDegree;
        mInclinationDegree += deltaInclinationDegree;
    }

    @Override
    public void update() {
        Matrix.setIdentityM(mHelpMatrix, 0);

        // rotate position, up-vector according to azimuth and inclination

        // position helper
        mHelpVectors[0 + 0] = (float)mDistance;
        mHelpVectors[0 + 1] = 0;
        mHelpVectors[0 + 2] = 0;
        mHelpVectors[0 + 3] = 0;

        // rotation axis helper, used later as reference for inclination
        mHelpVectors[4 + 0] = 0;
        mHelpVectors[4 + 1] = 0;
        mHelpVectors[4 + 2] = -1.0f;
        mHelpVectors[4 + 3] = 0;

        // step 1: azimuth rotation around y-Axis (DOES NOT effect up!)
        Matrix.setRotateM(mHelpMatrix, 0, mAzimuthDegree, 0, 1, 0);

        // rotate position
        Matrix.multiplyMV(mHelpVectors, 8, mHelpMatrix, 0, mHelpVectors, 0);
        // rotate axis
        Matrix.multiplyMV(mHelpVectors, 12, mHelpMatrix, 0, mHelpVectors, 4);

        // step 2: inclination rotation around help axis
        mUp.x = 0;
        mUp.y = 1;
        mUp.z = 0;

        // ATTENTION: inclination is measured against y-axis, i.e. 0 degree is on y-Axis
        Matrix.setRotateM(mHelpMatrix, 0, 90f - mInclinationDegree,
                mHelpVectors[12 + 0], mHelpVectors[12 + 1], mHelpVectors[12 + 2]);

        // rotate position
        Matrix.multiplyMV(mHelpVectors, 0, mHelpMatrix, 0, mHelpVectors, 8);
        // rotate up
        rotateVector(mUp, mHelpMatrix);

        mPosition.x = mHelpVectors[0 + 0];
        mPosition.y = mHelpVectors[0 + 1];
        mPosition.z = mHelpVectors[0 + 2];

        // move to target
        mPosition.add(mTarget);

        // determine mLook
        mLook = mTarget.clone().substract(mPosition).normalize();

        mRight = mLook.cross(mUp).normalize();

        Matrix.setLookAtM(mViewMatrix, 0,
                (float)mPosition.x, (float)mPosition.y, (float)mPosition.z,
                (float)(mPosition.x + mLook.x), (float)(mPosition.y + mLook.y), (float)(mPosition.z + mLook.z),
                (float)mUp.x, (float)mUp.y, (float)mUp.z);
    }
}
