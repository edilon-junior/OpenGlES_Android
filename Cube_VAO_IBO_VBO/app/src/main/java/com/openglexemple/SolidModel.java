package com.example.openglexemple;

import static com.example.openglexemple.Constants.SCENE_FLOAT_UNIFORMS;
import static com.example.openglexemple.Constants.SCENE_INT_UNIFORMS;

import android.opengl.GLES30;
import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.List;

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
        List<Mesh> solidMeshes = new ArrayList<>();
        solidMeshes.add(solidMesh);
        for(Mesh mesh: solidMeshes) {
            if (type == 0) {
                mesh.setupMesh(shaderProgram);
            } else if (type == 1) {
                mesh.setupNonInterleavedMesh(shaderProgram);
            } else if (type == 2) {
                mesh.createBuffers();
            }
        }
        setMesh(solidMeshes);
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
           setRotation(angularDisplacement, getRotationAxis().x, getRotationAxis().y, getRotationAxis().z);
        }
    }

    @Override
    public void update(float time) {
        updateModelMatrix();
        //translate first!
        updateRotation(time);
    }

    @Override
    public void render(Scene scene, Transformation transformation){
        for(Mesh mesh: this.getMesh()) {
            if(mesh == null) return; //do nothing

            int fromIndex = 0;
            int mIndex = 0;
            for(Material material: mesh.getMaterials()) {

                float[] mMVPMatrix = transformation.getMVPMatrix(this.getModelMatrix());

                float[] ambient = material.ambient;
                float[] diffuse = material.diffuse;
                float[] specular = material.specular;
                float[] shininess = material.shininess;

                int toIndex = material.getIndexCount();;

                Light light = scene.getLight();
                ShaderProgram shaderProgram = scene.getShaderProgram(getShaderProgramId());

                material.setupTexture(mIndex);
                shaderProgram.passIntUniforms(SCENE_INT_UNIFORMS, material.getSample2Did());
                shaderProgram.passFloatUniforms(SCENE_FLOAT_UNIFORMS, light.getLightPosInEyeSpace(), light.getLightColor(), new float[]{0, 0, 0},
                        ambient, diffuse, specular, shininess,
                        transformation.getViewMatrix(), mMVPMatrix);
                //arithmetic progression: a_n = a_0 + n*r; a_0 = 0
                int toIndexInBytes = toIndex * mesh.getVertexStrider() * Constants.BYTES_PER_FLOAT;
                mesh.render(GLES30.GL_TRIANGLES, fromIndex, toIndexInBytes);

                fromIndex = toIndex;

                mIndex++;
            }
        }
    }

    @Override
    public void cleanUp(){
    }
}


