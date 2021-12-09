package com.example.openglexemple;

import static com.example.openglexemple.Constants.POINT_FLOAT_UNIFORMS;
import static com.example.openglexemple.Constants.POSITION_SIZE;

import android.opengl.GLES30;
import android.opengl.Matrix;

public class Polygon {

    private float side_length = 0;
    private float radius = 0;
    private float[] color;
    private float[] size = new float[1];
    private float[] modelMatrix = new float[16];
    private int shaderProgramId = -1;
    private Vector3f position = new Vector3f();
    private Vector3f scale = new Vector3f(1,1,1);
    private Mesh mesh;
    private boolean updateModelMatrix = false;

    /**
 * @param sides: define the number of sides
 * @param size: size of point in OpenGL shader program
 * @param color: color of lines
 * @param side_length: define length of side. If this param is grater than zero, then radius shall be zero.
 * @param radius: The distance from the center of a regular polygon to any vertex .
 * @param shaderProgram: shader program used by this object
 **/

    public Polygon(int sides, float size, float[] color, float side_length, float radius, ShaderProgram shaderProgram){

        if(side_length > 0 ){
            this.side_length = side_length;
            this.radius = calcRadius(sides, side_length);
        }else{
            this.side_length = calcSideLength(sides, radius);
            this.radius = radius;
        }

        setShaderProgramId(shaderProgram.getProgramHandle());
        setPosition(new Vector3f(0,0,-6));
        setColor(color);
        setSize(size);
        setScale(new Vector3f(radius, radius, radius));

        float[] positions = createVertices(sides);
        mesh = new Mesh(positions, null, null, null, null, null);
        mesh.setupNonInterleavedMesh(shaderProgram);
    }

    private void setScale(Vector3f scale) {
        this.scale.set(scale);
    }

    private void setPosition(Vector3f position) {
        this.position.set(position);
        updateModelMatrix = true;
    }

    private void setShaderProgramId(int programHandle) {
        this.shaderProgramId = programHandle;
    }

    private float calcRadius(int sides, float side_length){
        return (float) (side_length/(2*Math.sin(Math.PI / sides)));
    }

    private float calcSideLength(int sides, float radius){
        return (float) (radius*(2*Math.sin(Math.PI / sides)));
    }

    private float calcScale(int sides, float side_length){
        return (float) ((1/radius)* side_length/(2*Math.sin(Math.PI / sides)));
    }

    private float[] createVertices(int sides){
        double angle = 2*Math.PI / sides;

        float[] vertices = new float[sides * POSITION_SIZE];
        //length of radius is 1, because this is the standard model size in opengl
        for(int i=0; i < sides; i++){
            vertices[i*POSITION_SIZE] = (float) (1 * Math.cos((i+1) * angle));
            vertices[i*POSITION_SIZE + 1] = (float) (1 * Math.sin((i+1) * angle));
            vertices[i*POSITION_SIZE + 2] = 0;
        }
        System.out.println("polygon vertices:");
        Utils.printFloatArray(vertices);
        return vertices;
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

    public float[] getSize(){
        return this.size;
    }

    private int getShaderProgramId() {
        return this.shaderProgramId;
    }

    private float[] getModelMatrix() {
        return this.modelMatrix;
    }

    public void translate(float x, float y, float z){
        this.position.keepAdd(x,y,z);
        Matrix.translateM(this.modelMatrix, 0,this.position.x,this.position.y,this.position.z);
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
        this.mesh.render(GLES30.GL_LINE_LOOP);
    }

    public void cleanUp(){
        //Matrix.setIdentityM(modelMatrix, 0);
        System.out.println("polygon model matrix");
        Utils.printMatrix(modelMatrix);
    }
}
