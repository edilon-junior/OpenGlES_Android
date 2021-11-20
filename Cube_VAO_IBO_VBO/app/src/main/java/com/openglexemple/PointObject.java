package com.example.openglexemple;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class PointObject {

    private final int BYTES_PER_FLOAT = 4;

    private final float[] modelMatrix = new float[16];
    private final float[] modelPosition =  new float[] {0.0f, 0.0f, 0.0f, 1.0f};
    private final float[] color;
    private float[] size;
    private float initial_translate_time = 0;
    private float current_translate_time = 0;
    private Vector3f initial_position = new Vector3f();
    private Vector3f position;
    private Vector3f translate_velocity = new Vector3f();

    Mesh mesh;

    public PointObject(float[] position, float[] color, float[] size, ShaderProgram pointProgram){
        Matrix.setIdentityM(modelMatrix, 0);
        this.position = new Vector3f(position);
        this.color = color;
        this.size = size;
        this.initial_position.set(position);
        mesh = new Mesh(position, null, null, null, null, null);
        mesh.setupNonInterleavedMesh(pointProgram);
    }

    public Mesh getMesh(){
        return this.mesh;
    }

    public float[] getModelMatrix(){
        return modelMatrix;
    }

    public float[] getModelPosition(){
        return modelPosition;
    }

    public void setPosition(float x, float y, float z){
        this.position.set(x,y,z);
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public float[] getColor(){
        return this.color;
    }

    public float[] getSize(){
        return this.size;
    }

    public void translate(float x, float y, float z){
        this.position.keepAdd(x,y,z);
    }
    public void translate(Vector3f displacement){
        this.position.keepAdd(displacement);
    }

    public void update(float time){
        updateTranslate(time);
        Matrix.translateM(modelMatrix, 0,position.x,position.y,position.z);
    }

    public void updateTranslate(float time){
        Vector3f displacement = translate_velocity.times(time);
        translate(displacement);
    }

    public void cleanUp(){
        Matrix.setIdentityM(modelMatrix, 0);
    }

}
