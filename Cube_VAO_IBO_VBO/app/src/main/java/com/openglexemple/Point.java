package com.example.openglexemple;

import static com.example.openglexemple.Constants.POINT_FLOAT_UNIFORMS;

import android.opengl.GLES30;
import android.opengl.Matrix;

public class Point extends GameObject{
    private float[] modelPosition =  new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    private float[] color;
    private final float[] size = new float[1];

    public Point(Vector3f position, float[] color, float size, ShaderProgram shaderProgram){
        super();
        setPosition(position);
        setColor(color);
        setSize(size);
        Mesh pointMesh = new Mesh(new float[]{0,0,0}, null, null, null, null, null);
        pointMesh.setupNonInterleavedMesh(shaderProgram);
        setMesh(pointMesh);
    }

    public void setColor(float[] color) {
        this.color = color;
    }

    public void setSize(float size) {
        this.size[0] = size;
    }

    public float[] getSize(){
        return this.size;
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
        System.out.println("render from pointObject class");
        float[] mMVPMatrix = transformation.getMVPMatrix(getModelMatrix());
        Utils.printMatrix(getModelMatrix());
        shaderProgram.passFloatUniforms(POINT_FLOAT_UNIFORMS, mMVPMatrix, this.color, this.size);
        getMesh().render(GLES30.GL_POINTS);
    }

}




