package com.example.openglexemple;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINK_STATUS;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES30.GL_FRAGMENT_SHADER;
import static android.opengl.GLES30.GL_VERTEX_SHADER;
import static android.opengl.GLES30.glEnableVertexAttribArray;
import static android.opengl.GLES30.glGetShaderiv;
import static android.opengl.GLES30.glVertexAttribPointer;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class ShaderProgram {

    private static final String TAG = "SHADER_PROGRAM";

    private final int programHandle;

    private final Map<String, Integer> intUniformHandles;
    private final Map<String, Integer> floatUniformHandles;
    private final Map<String, Integer> attributeHandles;

    private String vertexName;
    private String fragmentName;

    public ShaderProgram(final String vertexName, final String fragmentName, final String[] attributes, Loader loader){
        this.vertexName = vertexName;
        this.fragmentName = fragmentName;

        intUniformHandles = new HashMap<>();
        floatUniformHandles = new HashMap<>();
        attributeHandles = new HashMap<>();

        String vertexString = loader.shaderToString(vertexName);
        String fragmentString = loader.shaderToString(fragmentName);

        programHandle = createShaderProgram(vertexString, fragmentString, attributes);
    }
    /**
     * Helper function to compile a shader.
     *
     * @return An OpenGL handle to the shader.
     */
    public int createShaderProgram(final String vertexString, final String fragmentString, final String[] attributes)
    {
        final int vertexShaderHandle   = compileShader(GL_VERTEX_SHADER, vertexString);
        final int fragmentShaderHandle = compileShader(GL_FRAGMENT_SHADER, fragmentString);
        int programHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                attributes);
        return programHandle;
    }

    private int  compileShader(final int shaderType, final String shaderSource)
    {
        int shaderHandle = GLES30.glCreateShader(shaderType);

        if (shaderHandle != 0)
        {
            // Pass in the shader source.
            GLES30.glShaderSource(shaderHandle, shaderSource);

            // Compile the shader.
            GLES30.glCompileShader(shaderHandle);

            // Get the compilation status.
            int[] compileStatus = new int[1];
            glGetShaderiv(shaderHandle, GLES30.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                if(shaderType == GL_VERTEX_SHADER){
                    Log.d(TAG, "Load "+vertexName+" failed: "+GLES30.glGetShaderInfoLog(shaderHandle));

                }else{
                    Log.d(TAG, "Load "+fragmentName+" failed: "+GLES30.glGetShaderInfoLog(shaderHandle));
                }

                GLES30.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0)
        {
            if(shaderType == GL_VERTEX_SHADER){
                Log.d(TAG, "Load "+vertexName+"  failed. Creation");
            }else{
                Log.d(TAG, "Load "+fragmentName+"  failed. Creation");
            }

            throw new RuntimeException("Error creating shader.");
        }

        return shaderHandle;
    }

    /**
     * Helper function to compile and link a program.
     *
     * @param vertexShaderHandle An OpenGL handle to an already-compiled vertex shader.
     * @param fragmentShaderHandle An OpenGL handle to an already-compiled fragment shader.
     * @param attributes Attributes that need to be bound to the program.
     * @return An OpenGL handle to the program.
     */
    private int createAndLinkProgram(int vertexShaderHandle, int fragmentShaderHandle, String[] attributes)
    {
        int programHandle = GLES30.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES30.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES30.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            if (attributes != null)
            {
                for (int i = 0; i < attributes.length; i++)
                {
                    GLES30.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            // Link the two shaders together into a program.
            GLES30.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES30.glGetProgramiv(programHandle, GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                Log.e(TAG, "Error compiling program: " + GLES30.glGetProgramInfoLog(programHandle));
                GLES30.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0)
        {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;
    }

    public int getProgramHandle(){
        return this.programHandle;
    }

    public void useProgram(){
        GLES30.glUseProgram(programHandle);
    }

    public void enableVertexAttribArray(String[] attributes, int[] sizes, FloatBuffer[] attribBuffers) {
        for(int i=0; i<attributes.length; i++) {
            String attributeName = attributes[i];
            int handle = attributeHandles.get(attributeName);

            if(attribBuffers[i]==null){
                //Log.i(TAG, attributeName+" has null data");
            }else{
                glEnableVertexAttribArray(handle);
                //attribBuffers[i].position(0);
                glVertexAttribPointer(handle, sizes[i], GL_FLOAT, false,
                        0, attribBuffers[i]);

            }
        }
    }

    public void enableVertexAttribArray(String attributeName, int size, int stride, int pointer){
        int handle = attributeHandles.get(attributeName);

        glEnableVertexAttribArray(handle);

        glVertexAttribPointer(handle, size, GL_FLOAT, false,
                stride, pointer);
    }

    public void setIntUniformHandles(final String[] intUniforms) {
        for (String uniformName : intUniforms) {
            int handle = glGetUniformLocation(programHandle, uniformName);
            intUniformHandles.put(uniformName, handle);
        }
    }
    public void setFloatUniformHandles(final String[] floatUniforms) {
        for (String uniformName : floatUniforms) {
            int handle = glGetUniformLocation(programHandle, uniformName);
            floatUniformHandles.put(uniformName, handle);
        }
    }

    public void setAttributeHandles(final String[] attributes){
        for(String attribName : attributes){
            int handle = glGetAttribLocation(programHandle, attribName);
            //System.out.println(attribName +": "+handle);
            attributeHandles.put(attribName,handle);
        }
    }

    public void passAttrib3f(String name, float[] attribute){
        int handle = attributeHandles.get(name);
        GLES30.glVertexAttrib3f(handle, attribute[0], attribute[1], attribute[2]);
    }

    public void passAttrib3f(String name, Vector3f attribute){
        int handle = attributeHandles.get(name);
        GLES30.glVertexAttrib3f(handle, attribute.x, attribute.y, attribute.z);
    }

    // flatUniforms and data must be in same order
    public void passFloatUniforms(final String[] floatUniforms,  final float[]... uniforms){
        for(int i = 0; i<floatUniforms.length;i++){
            int handle = floatUniformHandles.get(floatUniforms[i]);
            passFloatUniform(handle, uniforms[i]);
        }
    }
    // intUniforms and datas must be in same order
    public void passIntUniforms(final String[] intUniforms,  final int[]... uniforms){
        for(int i = 0; i<intUniforms.length;i++){
            int handle = intUniformHandles.get(intUniforms[i]);
            passIntUniform(handle , uniforms[i]);
        }
    }

    public void passIntUniform(final int handle, final int[] data){
        if(data.length == 1){
            GLES30.glUniform1i(handle, data[0]);
        }
    }

    public void passFloatUniform(int handle, final float[] data){
        if(data.length == 1){
            GLES30.glUniform1f(handle, data[0]);
        }
        if(data.length == 2){
            GLES30.glUniform2f(handle, data[0], data[1]);
        }
        if(data.length == 3){
            GLES30.glUniform3f(handle, data[0], data[1], data[2]);
        }
        if(data.length == 4){
            GLES30.glUniform4f(handle, data[0], data[1], data[2], data[3]);
        }
        if(data.length == 16){
            GLES30.glUniformMatrix4fv(handle, 1, false, data, 0);
        }
    }

    public void passUniformMatrix(int handle , final float[] data){
        GLES30.glUniformMatrix4fv(handle, 1, false, data, 0);
    }
    public void passUniformFloat3(int handle, final float[] data){
        GLES30.glUniform3f(handle, data[0], data[1], data[2]);
    }
    public void passUniformFloat2(int handle, final float[] data){
        GLES30.glUniform2f(handle, data[0], data[1]);
    }
    public void passUniformFloat1(int handle, final float[] data){
        GLES30.glUniform1f(handle, data[0]);
    }

    public void disableAttributes(){
        for (int handle : attributeHandles.values()) {
            if(handle != -1){
                GLES30.glDisableVertexAttribArray(handle);
            }
        }
    }
}
