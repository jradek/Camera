package com.jradek.camera;

import android.opengl.GLES20;

public class Util {
    public enum Modus {
        Movement,
        Rotation
    };

    static final int BYTES_PER_FLOAT = 4;

    private Util() {
    }

    static int loadShader(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
}
