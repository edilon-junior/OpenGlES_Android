package com.example.openglexemple;

import android.opengl.Matrix;

public class GameObject {

    private MeshVBO mesh;
    private final float[]  modelMatrix = new float[16];
    private final Vector3f position = new Vector3f();
    private final Vector3f rotation_axis = new Vector3f(1,0,0);
    private final Vector3f translational_velocity = new Vector3f();
    private float angular_displacement = 0.0f;
    private float angular_velocity = 0;
    private float initial_rot_time = 0;
    private float current_rot_time = 0;

    public GameObject(){
        Matrix.setIdentityM(modelMatrix, 0);
    }

    /**
     *
     * @param modelName
     * @param loader
     * @param shaderProgram
     * @param type define setup of array buffers:
     *             0 to VAO, IBO and Interleaved vertex array
     *             1 to VAO, IBO and no Interleaved vertex array
     *             2 to no use of VAO, VBO and IBO
     */
    public GameObject(String modelName, Loader loader, ShaderProgram shaderProgram, int type){
        mesh = loader.loadMeshIBO(modelName);
        if(type == 0){
            mesh.setupMesh(shaderProgram);
        }else if(type == 1){
            mesh.setupNonInterleavedMesh(shaderProgram);
        }else if(type == 2){
            mesh.createBuffers();
        }
    }

    public void setPosition(float x, float y, float z){
        this.position.set(x,y,z);
    }
    public float[] getPosition(){
        return this.position.toFloat();
    }
    public void setAngularVelocity(float angular_velocity){
        this.angular_velocity = angular_velocity;
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

    public void setAngularDisplacement(float angleInDegrees){
        this.angular_displacement = angleInDegrees;
    }

    public void translate(float x, float y, float z){
        this.position.keepAdd(x,y,z);
    }

    public void update(float time){
        updateRotation(time);
        Matrix.translateM(modelMatrix, 0,position.x,position.y,position.z);
        Matrix.rotateM(modelMatrix, 0, angular_displacement, rotation_axis.x, rotation_axis.y, rotation_axis.z);
    }

    public void updateRotation(float time){
        float delta_t = time - initial_rot_time;
        angular_displacement = angular_velocity * (delta_t);
        //when complete one cycle
        if(delta_t > 360 * (1/angular_velocity) ){
            initial_rot_time = time;
        }
    }

    public void cleanUp(){
        Matrix.setIdentityM(modelMatrix, 0);
    }
}
