package com.example.openglexemple;

import static com.example.openglexemple.Constants.SCENE_FLOAT_UNIFORMS;
import static com.example.openglexemple.Constants.SCENE_INT_UNIFORMS;

import android.opengl.GLES30;
import android.opengl.Matrix;

public class GameObject {

    private boolean updateModelMatrix;
    private int meshIndex;
    private int shaderProgramId;
    private float angularDisplacement;
    private float angularVelocity;
    private float initialRotTime;
    private final float currentRotTime;
    protected final float[]  modelMatrix;
    private final Vector3f scale;
    private final Vector3f initialPosition;
    private final Vector3f position;
    private final Vector3f rotationAxis;
    private final Vector3f translationalVelocity;
    protected Mesh mesh;

    public GameObject(){
        updateModelMatrix = false;
        meshIndex = -1;
        shaderProgramId = -1;
        initialPosition = new Vector3f();
        position = new Vector3f();
        rotationAxis = new Vector3f(1,0,0);
        translationalVelocity = new Vector3f();
        angularDisplacement = 0.0f;
        scale= new Vector3f(1.0f, 1.0f, 1.0f);
        modelMatrix = new float[16];
        angularVelocity = 0;
        initialRotTime = 0;
        currentRotTime = 0;
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
        this(); //call default constructor
        setShaderProgramId(shaderProgram.getProgramHandle());
        mesh = loader.loadMeshIBO(modelName);
        if(type == 0){
            mesh.setupMesh(shaderProgram);
        }else if(type == 1){
            mesh.setupNonInterleavedMesh(shaderProgram);
        }else if(type == 2){
            mesh.createBuffers();
        }
    }
    public void setShaderProgramId(int shaderProgramId){
        this.shaderProgramId = shaderProgramId;
    }
    public int getShaderProgramId(){
        return shaderProgramId;
    }
    public void setMesh(Mesh mesh){
        this.mesh = mesh;
    }
    public Mesh getMesh(){
        return this.mesh;
    }
    public void setMeshIndex(int index){
        this.meshIndex = index;
    }
    public int getMeshIndex(){
        return this.meshIndex;
    }

    public void setAngularVelocity(float angular_velocity){
        this.angularVelocity = angular_velocity;
    }
    public void setRotationAxis(float x, float y, float z){
        this.rotationAxis.set(x,y,z);
    }

    public float[] getModelMatrix(){
        return this.modelMatrix;
    }

    public void setAngularDisplacement(float angleInDegrees){
        this.angularDisplacement = angleInDegrees;
    }

    public void setScale(Vector3f scale){
        this.scale.set(scale);
        updateModelMatrix = true;
    }

    public void setPosition(Vector3f position){
        this.position.set(position);
        updateModelMatrix = true;
    }

    public Vector3f getPosition(){
        return this.position.clone();
    }

    public void setRotation(float angle, float x, float y, float z){
        setRotationAxis(x,y,z);
        setAngularDisplacement(angle);
        updateModelMatrix = true;
    }
    public void translate(float x, float y, float z){
        this.position.keepAdd(x,y,z);
        Matrix.translateM(this.modelMatrix, 0,this.position.x,this.position.y,this.position.z);
    }

    public void rotate(float increment_angle, float x, float y, float z){
        setRotationAxis(x,y,z);
        setAngularDisplacement(increment_angle);
        Matrix.rotateM(modelMatrix, 0, angularDisplacement, rotationAxis.x, rotationAxis.y, rotationAxis.z);
    }

    public void update(float time){
        updateModelMatrix();
        //translate first!
        updateRotation(time);
    }

    public void updateModelMatrix(){
        if(updateModelMatrix == true){
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.scaleM(modelMatrix, 0, scale.x, scale.y, scale.z);
            Matrix.rotateM(modelMatrix, 0, angularDisplacement, rotationAxis.x, rotationAxis.y, rotationAxis.z);
            Matrix.translateM(modelMatrix, 0,position.x,position.y,position.z);
            updateModelMatrix = false;
        }
    }

    public void updateRotation(float time){
        float delta_t = time - initialRotTime;
        angularDisplacement = angularVelocity * (delta_t);
        //when complete one cycle
        if(delta_t > 360 * (1/angularVelocity) ){
            initialRotTime = time;
        }
        if(angularDisplacement != 0){
            rotate(angularDisplacement, rotationAxis.x, rotationAxis.y, rotationAxis.z);
        }
    }

    public void render(Scene scene, Transformation transformation){
        float[] mMVPMatrix = transformation.getMVPMatrix(this.getModelMatrix());

        float[] ambient = this.getMesh().getMaterial().ambient;
        float[] diffuse = this.getMesh().getMaterial().diffuse;
        float[] specular = this.getMesh().getMaterial().specular;
        float[] shininess = this.getMesh().getMaterial().shininess;

        Light light = scene.getLight();

        scene.getShaderProgram(this.shaderProgramId).passIntUniforms(SCENE_INT_UNIFORMS, new int[]{0});
        scene.getShaderProgram(this.shaderProgramId).passFloatUniforms(SCENE_FLOAT_UNIFORMS, light.getLightPosInEyeSpace(), light.getLightColor(), new float[]{0,0,0},
                ambient, diffuse, specular, shininess,
                transformation.getViewMatrix(), mMVPMatrix);
        this.mesh.render(GLES30.GL_TRIANGLES);
    }

    public void render(ShaderProgram shaderProgram, Transformation transformation){
       
    }

    public void cleanUp(){

    }
}

