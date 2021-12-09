package com.example.openglexemple;

import static com.example.openglexemple.Constants.POINT_FLOAT_UNIFORMS;

import android.opengl.GLES30;
import android.opengl.Matrix;

public class Point {

    private float[] modelPosition =  new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    private float[] color;
    private float[] size = new float[1];
    private float[] modelMatrix = new float[16];
    private Vector3f position = new Vector3f();
    private int shaderProgramId = -1;
    private Mesh mesh;
    private boolean updateModelMatrix = false;

    public Point(Vector3f position, float[] color, float size, ShaderProgram shaderProgram){
        setShaderProgramId(shaderProgram.getProgramHandle());
        setPosition(position);
        setColor(color);
        setSize(size);
        mesh = new Mesh(new float[]{0,0,0}, null, null, null, null, null);
        mesh.setupNonInterleavedMesh(shaderProgram);
    }

    private void setPosition(Vector3f position) {
        this.position.set(position);
        updateModelMatrix = true;
    }

    private void setShaderProgramId(int programHandle) {
        this.shaderProgramId = programHandle;
    }

    public void setColor(float[] color){
        this.color = color;
    }

    public float[] getColor(){
        return this.color;
    }

    public void setSize(float size){
        this.size[0] = size;
    }

    public void translate(float x, float y, float z){
        this.position.keepAdd(x,y,z);
        Matrix.translateM(this.modelMatrix, 0,this.position.x,this.position.y,this.position.z);
    }

    public float[] getSize(){
        return this.size;
    }

    private float[] getModelMatrix() {
    return this.modelMatrix;
}

    public void updateModelMatrix(){
        if(updateModelMatrix == true){
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0,position.x,position.y,position.z);
            updateModelMatrix = false;
        }
    }

    public void update(float time){
        updateModelMatrix();
    }

    public void render(ShaderProgram shaderProgram, Transformation transformation){
        float[] mMVPMatrix = transformation.getMVPMatrix(this.modelMatrix);
        shaderProgram.passFloatUniforms(POINT_FLOAT_UNIFORMS, mMVPMatrix, this.color, this.size);
        this.mesh.render(GLES30.GL_POINTS);
    }

    public void cleanUp(){
        System.out.println("point model matrix");
        Utils.printMatrix(modelMatrix);
        //Matrix.setIdentityM(modelMatrix, 0);
    }

}
