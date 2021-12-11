package com.example.openglexemple;

import static com.example.openglexemple.Constants.SCENE_FLOAT_UNIFORMS;
import static com.example.openglexemple.Constants.SCENE_INT_UNIFORMS;

import android.opengl.GLES30;
import android.opengl.Matrix;

public class SolidModel extends GameObject{
    private boolean updateModelMatrix;

    /**
     *
     * @param modelName
     * @param loader
     * @param shaderProgram
     * @param type define setup of array buffers:
     *             0 to VAO, IBO and Interleaved vertex array
     *             1 to VAO, IBO and no Interleaved vertex array
     *             2 to no use of VAO, VBO and IBO
     */public SolidModel(String modelName, Loader loader, ShaderProgram shaderProgram, int type){
         super(); //call default constructor
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

     public void update(float time){
         updateModelMatrix();
         //translate first!
         updateRotation(time);
     }

     public void updateModelMatrix(){
         if(updateModelMatrix == true){
             Matrix.setIdentityM(modelMatrix, 0);
             Matrix.scaleM(modelMatrix, 0, getScale().x, getScale().y, getScale().z);
             Matrix.rotateM(modelMatrix, 0, getAngularDisplacement(),
                     getRotationAxis().x,getRotationAxis().y, getRotationAxis().z);
             Matrix.translateM(modelMatrix, 0, getPosition().x, getPosition().y, getPosition().z);
             updateModelMatrix = false;
         }
     }

        public void updateRotation(float time){
            float delta_t = time - getInitialRotTime();
            float angularDisplacement = getAngularVelocity() * (delta_t);
            //when complete one cycle
            if(delta_t > 360 * (1/getAngularVelocity()) ){
                setInitialRotTime(time);
            }
            if(angularDisplacement != 0){
                rotate(angularDisplacement, getRotationAxis().x, getRotationAxis().y, getRotationAxis().z);
            }
            setAngularDisplacement(angularDisplacement);
        }

        public void render(Scene scene, ShaderProgram shaderProgram, Transformation transformation){
            float[] mMVPMatrix = transformation.getMVPMatrix(this.getModelMatrix());

            float[] ambient = this.getMesh().getMaterial().ambient;
            float[] diffuse = this.getMesh().getMaterial().diffuse;
            float[] specular = this.getMesh().getMaterial().specular;
            float[] shininess = this.getMesh().getMaterial().shininess;

            Light light = scene.getLight();

            shaderProgram.passIntUniforms(SCENE_INT_UNIFORMS, new int[]{0});
            shaderProgram.passFloatUniforms(SCENE_FLOAT_UNIFORMS, light.getLightPosInEyeSpace(), light.getLightColor(), new float[]{0,0,0},
                    ambient, diffuse, specular, shininess,
                    transformation.getViewMatrix(), mMVPMatrix);
            this.mesh.render(GLES30.GL_TRIANGLES);
        }

        public void render(ShaderProgram shaderProgram, Transformation transformation){
            System.out.println("for now do nothing");
        }

        public void cleanUp(){

        }
}


