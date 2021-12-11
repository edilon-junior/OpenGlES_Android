package com.example.openglexemple;

import static com.example.openglexemple.Constants.POINT_FLOAT_UNIFORMS;
import static com.example.openglexemple.Constants.POSITION_SIZE;

import android.opengl.GLES30;
import android.opengl.Matrix;

public class Polygon extends GameObject{

    private float sideLength = 0;
    private float radius = 0;

    /**
     * @param position: define initial position
 * @param sides: define the number of sides
 * @param color: color of lines
 * @param side_length: define length of side. If this param is grater than zero, then radius shall be zero.
 * @param radius: The distance from the center of a regular polygon to any vertex .
 * @param shaderProgram: shader program used by this object
 **/

    public Polygon(Vector3f position, int sides, float[] color, float side_length, float radius, ShaderProgram shaderProgram){
        super();

        if(side_length > 0 ){
            this.sideLength = side_length;
            this.radius = calcRadius(sides, side_length);
        }else{
            this.sideLength = calcSideLength(sides, radius);
            this.radius = radius;
        }

        setPosition(position);
        setShaderProgramId(shaderProgram.getProgramHandle());
        setColor(color);
        setScale(new Vector3f(radius, radius, radius));

        float[] positions = createVertices(sides);
        Mesh polygonMesh = new Mesh(positions, null, null, null, null, null);
        polygonMesh.setupNonInterleavedMesh(shaderProgram);
        setMesh(polygonMesh);
    }

    private float calcRadius(int sides, float side_length){
        return (float) (side_length / (2 * Math.sin(Math.PI / sides)));
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

        return vertices;
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
        getMesh().render(GLES30.GL_LINE_LOOP);
    }
}
