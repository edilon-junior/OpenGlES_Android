package com.example.openglexemple;

import android.opengl.Matrix;

public abstract class GameObject {

    private boolean updateModelMatrix;
    private int meshIndex;
    private int shaderProgramId;
    private float angularDisplacement;
    private float angularVelocity;
    private float initialRotTime;
    private final float currentRotTime;
    private final float[]  modelMatrix;
    private float[] color;
    private final float[] pointSize = new float[1];
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
        color = Colors.GRAY;
        scale= new Vector3f(1.0f, 1.0f, 1.0f);
        modelMatrix = new float[16];
        angularVelocity = 0;
        initialRotTime = 0;
        currentRotTime = 0;
        Matrix.setIdentityM(modelMatrix, 0);
    }

    public void setUpdateModelMatrix(boolean updateModelMatrix){
        this.updateModelMatrix = updateModelMatrix;
    }
    public boolean isUpdateModelMatrix(){
        return updateModelMatrix;
    }
    public void setColor(float[] color){
        this.color = color;
    }
    public float[] getColor() {
        return color;
    }
    public void setPointSize(float size) {
        this.pointSize[0] = size;
    }

    public float[] getPointSize(){
        return this.pointSize;
    }
    public void setScale(Vector3f scale){
        this.scale.set(scale);
        setUpdateModelMatrix(true);
    }
    public Vector3f getScale(){
        return this.scale.clone();
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
    public void setInitialRotTime(float initialRotTime){
        this.initialRotTime = initialRotTime;
    }
    public float getInitialRotTime() {
        return initialRotTime;
    }
    public float getAngularDisplacement() {
        return angularDisplacement;
    }
    public float getAngularVelocity(){
        return angularVelocity;
    }
    public Vector3f getRotationAxis(){
        return this.rotationAxis.clone();
    }

    public void setAngularVelocity(float angular_velocity){
        this.angularVelocity = angular_velocity;
    }
    public void setRotationAxis(float x, float y, float z){
        this.rotationAxis.set(x,y,z);
    }

    public void setModelMatrix(float[] modelMatrix){
        for(int i= 0; i<modelMatrix.length; i++){
            this.modelMatrix[i] = modelMatrix[i];
        }
    }

    public float[] getModelMatrix(){
        return this.modelMatrix;
    }

    public void setAngularDisplacement(float angleInDegrees){
        this.angularDisplacement = angleInDegrees;
    }

    public void setPosition(Vector3f position){
        this.position.set(position);
        setUpdateModelMatrix(true);
    }

    public Vector3f getPosition(){
        return this.position.clone();
    }

    public void setRotation(float angle, float x, float y, float z){
        setRotationAxis(x,y,z);
        setAngularDisplacement(angle);
        setUpdateModelMatrix(true);
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

    }

    public void render(Scene scene, Transformation transformation){

    }
    public void render(ShaderProgram shaderProgram, Transformation transformation){

    }
    public void cleanUp(){

    }
}
