package com.example.openglexemple;

import static com.example.openglexemple.Constants.SCENE_FLOAT_UNIFORMS;
import static com.example.openglexemple.Constants.SCENE_INT_UNIFORMS;

import android.opengl.GLES30;
import android.opengl.Matrix;

public class SolidModel extends GameObject {
    /**
     * @param modelName
     * @param loader
     * @param shaderProgram
     * @param type          define setup of array buffers:
     *                      0 to VAO, IBO and Interleaved vertex array
     *                      1 to VAO, IBO and no Interleaved vertex array
     *                      2 to no use of VAO, VBO and IBO
     */
    public SolidModel(String modelName, Loader loader, ShaderProgram shaderProgram, int type) {
        super(); //call default constructor
        setShaderProgramId(shaderProgram.getProgramHandle());
        Mesh solidMesh = loader.loadMeshIBO(modelName);
        if (type == 0) {
            solidMesh.setupMesh(shaderProgram);
        } else if (type == 1) {
            solidMesh.setupNonInterleavedMesh(shaderProgram);
        } else if (type == 2) {
            solidMesh.createBuffers();
        }
        setMesh(solidMesh);
    }

    private void updateModelMatrix() {
        if (isUpdateModelMatrix()) {
            float[] tempMatrix = new float[16];
            Matrix.setIdentityM(tempMatrix, 0);
            Matrix.translateM(tempMatrix, 0, getPosition().x, getPosition().y, getPosition().z);
            Matrix.rotateM(tempMatrix, 0, getAngularDisplacement(),
                    getRotationAxis().x, getRotationAxis().y, getRotationAxis().z);
            Matrix.scaleM(tempMatrix, 0, getScale().x, getScale().y, getScale().z);
            setModelMatrix(tempMatrix);
            setUpdateModelMatrix(false);
        }
    }

    private void updateRotation(float time) {
        float delta_t = time - getInitialRotTime();
        float angularDisplacement = getAngularVelocity() * (delta_t);
        //when complete one cycle
        if (delta_t > 360 * (1 / getAngularVelocity())) {
            setInitialRotTime(time);
        }
        if (angularDisplacement != 0) {
            rotate(angularDisplacement, getRotationAxis().x, getRotationAxis().y, getRotationAxis().z);
        }
        setAngularDisplacement(angularDisplacement);
    }

    @Override
    public void update(float time) {
        updateModelMatrix();
        //translate first!
        updateRotation(time);
    }

    @Override
    public void render(Scene scene, Transformation transformation){
        float[] mMVPMatrix = transformation.getMVPMatrix(this.getModelMatrix());

        float[] ambient = this.getMesh().getMaterial().ambient;
        float[] diffuse = this.getMesh().getMaterial().diffuse;
        float[] specular = this.getMesh().getMaterial().specular;
        float[] shininess = this.getMesh().getMaterial().shininess;

        Light light = scene.getLight();
        ShaderProgram shaderProgram = scene.getShaderProgram(getShaderProgramId());

        shaderProgram.passIntUniforms(SCENE_INT_UNIFORMS, new int[]{0});
        shaderProgram.passFloatUniforms(SCENE_FLOAT_UNIFORMS, light.getLightPosInEyeSpace(), light.getLightColor(), new float[]{0,0,0},
                ambient, diffuse, specular, shininess,
                transformation.getViewMatrix(), mMVPMatrix);
        getMesh().render(GLES30.GL_TRIANGLES);
    }

    @Override
    public void cleanUp(){

    }
}





