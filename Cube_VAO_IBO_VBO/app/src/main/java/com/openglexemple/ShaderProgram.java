package com.example.openglexemple;

import android.opengl.GLES20;
import android.opengl.GLES32;
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

    public ShaderProgram(final String vertexString, final String fragmentString, final String[] attributes){
        intUniformHandles = new HashMap<>();
        floatUniformHandles = new HashMap<>();
        attributeHandles = new HashMap<>();
        programHandle = createShaderProgram(vertexString, fragmentString, attributes);
    }
    /**
     * Helper function to compile a shader.
     *
     * @return An OpenGL handle to the shader.
     */
    public int createShaderProgram(final String vertexString, final String fragmentString, final String[] attributes)
    {
        final int vertexShaderHandle   = compileShader(GLES20.GL_VERTEX_SHADER, vertexString);
        final int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentString);
        int programHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                attributes);
        return programHandle;
    }

    private static int  compileShader(final int shaderType, final String shaderSource)
    {
        int shaderHandle = GLES20.glCreateShader(shaderType);

        if (shaderHandle != 0)
        {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, shaderSource);

            // Compile the shader.
            GLES20.glCompileShader(shaderHandle);

            // Get the compilation status.
            int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0)
            {
                Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
                GLES20.glDeleteShader(shaderHandle);
                shaderHandle = 0;
            }
        }

        if (shaderHandle == 0)
        {
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
    private static int createAndLinkProgram(int vertexShaderHandle, int fragmentShaderHandle, String[] attributes)
    {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0)
        {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            if (attributes != null)
            {
                for (int i = 0; i < attributes.length; i++)
                {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0)
            {
                Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
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
        GLES20.glUseProgram(programHandle);
    }

    public void passAttributesBuffers(String[] attributes, int[] sizes, FloatBuffer[] attribBuffers) {
        for(int i=0; i<attributes.length; i++) {
            String attributeName = attributes[i];
            int handle = attributeHandles.get(attributeName);
            if(attribBuffers[i]==null){
                //Log.i(TAG, attributeName+" has null data");
            }else{
                GLES20.glVertexAttribPointer(handle, sizes[i], GLES20.GL_FLOAT, false,
                        0, attribBuffers[i]);
                GLES20.glEnableVertexAttribArray(handle);
            }
        }
    }

    public void enableVertexAttribArray(String attributeName, int size, int stride, int pointer){
        int handle = attributeHandles.get(attributeName);

        GLES32.glEnableVertexAttribArray(handle);

        GLES32.glVertexAttribPointer(handle, size, GLES20.GL_FLOAT, false,
                stride, pointer);

    }

    public void setIntUniformHandles(final String[] intUniforms) {
        for (String uniformName : intUniforms) {
            int handle = GLES20.glGetUniformLocation(programHandle, uniformName);
            intUniformHandles.put(uniformName, handle);
        }
    }
    public void setFloatUniformHandles(final String[] floatUniforms) {
        for (String uniformName : floatUniforms) {
            int handle = GLES20.glGetUniformLocation(programHandle, uniformName);
            floatUniformHandles.put(uniformName, handle);
        }
    }

    public void setAttributeHandles(final String[] attributes){
        for(String attribName : attributes){
            int handle =GLES20.glGetAttribLocation(programHandle, attribName);
            attributeHandles.put(attribName,handle);
        }
    }

    // flatUniforms and datas must be in same order
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
            GLES20.glUniform1i(handle, data[0]);
        }
    }

    public void passFloatUniform(int handle, final float[] data){
        if(data.length == 1){
            GLES20.glUniform1f(handle, data[0]);
        }
        if(data.length == 2){
            GLES20.glUniform2f(handle, data[0], data[1]);
        }
        if(data.length == 3){
            GLES20.glUniform3f(handle, data[0], data[1], data[2]);
        }
        if(data.length == 4){
            GLES20.glUniform4f(handle, data[0], data[1], data[2], data[3]);
        }
        if(data.length == 16){
            GLES20.glUniformMatrix4fv(handle, 1, false, data, 0);
        }
    }

    public void passUniformMatrix(int handle , final float[] data){
        GLES20.glUniformMatrix4fv(handle, 1, false, data, 0);
    }
    public void passUniformFloat3(int handle, final float[] data){
        GLES20.glUniform3f(handle, data[0], data[1], data[2]);
    }
    public void passUniformFloat2(int handle, final float[] data){
        GLES20.glUniform2f(handle, data[0], data[1]);
    }
    public void passUniformFloat1(int handle, final float[] data){
        GLES20.glUniform1f(handle, data[0]);
    }

    public void disableAttributes(){
        for (int handle : attributeHandles.values()) {
            GLES20.glDisableVertexAttribArray(handle);
        }

    }

}
