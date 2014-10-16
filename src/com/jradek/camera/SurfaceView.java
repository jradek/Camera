package com.jradek.camera;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.View;


public class SurfaceView extends GLSurfaceView {
    private final SceneRenderer mRenderer;

    public SurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // extra
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new SceneRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void onClick(View v, Util.Modus mode) {
        int id = v.getId();

        if (id == R.id.button_reset) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mRenderer.resetCamera();
                }
            });

            requestRender();
        }

        switch (mode) {
            case Movement:
                handleMovement(id);
                break;
            case Rotation:
                handleRotation(id);
                break;
        }
    }

    private void handleMovement(int buttonId) {
        final float factor = 0.5f;

        float walk = 0;
        float strafe = 0;
        float lift = 0;

        // Button layout
        // | 0 | 1 | 2 | MODE |
        // | 3 | 4 | 5 | RESET |

        switch (buttonId) {
            case R.id.button_0:
                lift = -factor;
                break;
            case R.id.button_2:
                lift = factor;
                break;
            case R.id.button_1:
                walk = factor;
                break;
            case R.id.button_4:
                walk = -factor;
                break;
            case R.id.button_3:
                strafe = -factor;
                break;
            case R.id.button_5:
                strafe = factor;
                break;
        };

        final float argLift = lift;
        final float argWalk = walk;
        final float argStrafe = strafe;

        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.moveCamera(argWalk, argStrafe, argLift);
            }
        });

        requestRender();
    }

    private void handleRotation(int buttonId) {
        final float factor = 5f;

        float rollDegree = 0.0f;
        float pitchDegree = 0.0f;
        float yawDegree = 0.0f;
        switch (buttonId) {
            case R.id.button_0:
                rollDegree = factor;
                break;
            case R.id.button_3:
                rollDegree = -factor;
                break;
            case R.id.button_1:
                pitchDegree = factor;
                break;
            case R.id.button_4:
                pitchDegree = -factor;
                break;
            case R.id.button_2:
                yawDegree = -factor;
                break;
            case R.id.button_5:
                yawDegree = factor;
                break;
        }

        final float argRoll = rollDegree;
        final float argPitch = pitchDegree;
        final float argYaw = yawDegree;

        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.rotateCamera(argRoll, argPitch, argYaw);
            }
        });

        requestRender();
    }
}
