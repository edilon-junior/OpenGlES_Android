package com.example.openglexemple;

import android.opengl.Matrix;

public class Transformation {

    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] viewProjectionMatrix = new float[16];

    public Transformation(){

    }

    public void createViewMatrix(){
        // Position the eye in front of the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = -0.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    public void createPerspectiveMatrix(int width, int height){
        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);
    }

    public float[] convertIntoEyeSpace(float[] vector, float[] modelMatrix){
        float[] tempMatrix = new float[16];
        float[] result = new float[4];

        Matrix.multiplyMV(tempMatrix, 0, modelMatrix, 0, vector, 0);
        Matrix.multiplyMV(result, 0, viewMatrix, 0, tempMatrix, 0);

        return result;
    }

    public void createViewProjectionMatrix(){
        Matrix.multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    public float[] getMVPMatrix(float[] modelMatrix){
        float[] mMVPMatrix = new float[16];

        Matrix.multiplyMM(mMVPMatrix, 0, viewProjectionMatrix, 0,modelMatrix, 0);

        return mMVPMatrix;
    }


    public float[] getModelViewProjectionMatrix(float[] modelMatrix){
        float[] mMVMatrix = new float[16];
        float[] mMVPMatrix = new float[16];

        Matrix.multiplyMM(mMVMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, mMVMatrix, 0);

        return mMVPMatrix;
    }

    public float[] getViewMatrix(){
        return this.viewMatrix;
    }

    public float[] getProjectionMatrix(){
        return this.projectionMatrix;
    }

}
