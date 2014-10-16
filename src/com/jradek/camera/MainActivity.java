package com.jradek.camera;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jradek.camera.Util.Modus;

public class MainActivity extends Activity implements OnClickListener {
    /**
     * Hold a reference to our GLSurfaceView
     */
    private SurfaceView surfaceView;

    private Util.Modus mMode = Modus.Movement;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        surfaceView = new SurfaceView(this);
        setContentView(surfaceView);

        setupUiControls();
    }

    private void setupUiControls() {
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParamsUpDown =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setGravity(Gravity.BOTTOM | Gravity.LEFT);

        LayoutInflater inflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View linearLayoutView = inflater.inflate(R.layout.ui_controls, layout, false);
        layout.addView(linearLayoutView);

        addContentView(layout, layoutParamsUpDown);

        // install click listeners
        final int[] buttons =
                {R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4,
                        R.id.button_5, R.id.button_mode, R.id.button_reset};

        for (int id : buttons) {
            Button btn = (Button) findViewById(id);
            btn.setOnClickListener(this);
        }

        updateButtonLabels();
    }


    @Override
    protected void onPause() {
        super.onPause();
        surfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceView.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.button_mode) {
            // toggle mode
            switch (mMode) {
                case Movement: mMode = Modus.Rotation; break;
                case Rotation: mMode = Modus.Movement; break;
            }

            updateButtonLabels();
            return;
        }

        surfaceView.onClick(v, mMode);
    }

    private void updateButtonLabels() {
        final int[] buttons = {
                R.id.button_0,
                R.id.button_1,
                R.id.button_2,
                R.id.button_3,
                R.id.button_4,
                R.id.button_5,
                R.id.button_mode
        };

        final int[] movementLabels = {
                R.string.lift_minus,
                R.string.walk_plus,
                R.string.lift_plus,
                R.string.strafe_minus,
                R.string.walk_minus,
                R.string.strafe_plus,
                R.string.mode_roll_pitch_yaw
        };

        final int[] rotationLabels = {
                R.string.roll_plus,
                R.string.pitch_plus,
                R.string.yaw_plus,
                R.string.roll_minus,
                R.string.pitch_minus,
                R.string.yaw_minus,
                R.string.mode_xyz
        };

        for (int i = 0; i < buttons.length; ++i) {
            Button btn = (Button) findViewById(buttons[i]);

            switch (mMode) {
                case Movement:
                    btn.setText(movementLabels[i]);
                    break;
                case Rotation:
                    btn.setText(rotationLabels[i]);
                    break;
            }
        }
    }
}
