package com.example.openglexemple;

import static com.example.openglexemple.Constants.POINT_FLOAT_UNIFORMS;

import android.opengl.GLES30;
import android.opengl.Matrix;

public class Point extends GameObject{
    private float[] modelPosition =  new float[] {0.0f, 0.0f, 0.0f, 1.0f};

    public Point(Vector3f position, float[] color, float size, ShaderProgram shaderProgram){
        super();
        setPosition(position);
        setColor(color);
        setPointSize(size);
        Mesh pointMesh = new Mesh(new float[]{0,0,0}, null, null, null, null, null);
        pointMesh.setupNonInterleavedMesh(shaderProgram);
        setMesh(pointMesh);
    }

    public void updateModelMatrix(){
        if(updateModelMatrix){
            float[] tempMatrix = new float[16];
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0,getPosition().x,getPosition().y,getPosition().z);
            setModelMatrix(tempMatrix);
            updateModelMatrix = false;
        }
    }

    @Override
    public void update(float time){
        updateModelMatrix();
    }

    @Override
    public void render(ShaderProgram shaderProgram, Transformation transformation){
        float[] mMVPMatrix = transformation.getMVPMatrix(getModelMatrix());
        shaderProgram.passFloatUniforms(POINT_FLOAT_UNIFORMS, mMVPMatrix, getColor(), getPointSize());
        getMesh().render(GLES30.GL_POINTS);
    }

}






