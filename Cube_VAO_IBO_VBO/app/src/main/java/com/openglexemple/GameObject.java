package com.example.openglexemple;

import android.opengl.Matrix;

public class GameObject {
    
    private MeshVBO mesh;
    private final float[]  modelMatrix = new float[16];
    private final Vector3f position = new Vector3f();
    private final Vector3f rotation_axis = new Vector3f(1,0,0);
    private float    rotation_angle = 0.0f;
    private final Vector3f translational_velocity = new Vector3f();
    private final Vector3f angular_velocity = new Vector3f();

    public GameObject(){
        Matrix.setIdentityM(modelMatrix, 0);
    }

    public GameObject(String modelName, Loader loader, ShaderProgram shaderProgram){
        mesh = loader.loadMesh(modelName);
        mesh.setupMesh(shaderProgram);
    }

    public void setPosition(float x, float y, float z){
        this.position.set(x,y,z);
    }
    public float[] getPosition(){
        return this.position.toFloat();
    }
    public void setAngularVelocity(float x, float y, float z){
        this.angular_velocity.set(x,y,z);
    }
    public void setRotationAxis(float x, float y, float z){
        this.rotation_axis.set(x,y,z);
    }
    public MeshVBO getMesh(){
        return this.mesh;
    }
    public float[] getModelMatrix(){
        return this.modelMatrix;
    }

    public void setRotation(float angleInDegrees, float x, float y, float z){
       this.rotation_angle = angleInDegrees;
       this.rotation_axis.set(x,y,z);
    }
    public void translate(float x, float y, float z){
        this.position.keepAdd(x,y,z);
    }

    public void update(){
        Matrix.translateM(modelMatrix, 0,position.x,position.y,position.z);
        Matrix.rotateM(modelMatrix, 0, rotation_angle, rotation_axis.x, rotation_axis.y, rotation_axis.z);
    }

    public void cleanUp(){
        Matrix.setIdentityM(modelMatrix, 0);
    }
}
