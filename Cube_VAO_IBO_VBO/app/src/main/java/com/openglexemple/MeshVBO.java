package com.example.openglexemple;

import static android.opengl.GLES30.GL_TRIANGLES;
import static android.opengl.GLES30.GL_TRIANGLE_STRIP;
import static android.opengl.GLES30.GL_UNSIGNED_INT;
import static android.opengl.GLES30.glGenBuffers;
import static android.opengl.GLES30.glDrawElements;
import static android.opengl.GLES30.GL_ARRAY_BUFFER;
import static android.opengl.GLES30.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES30.GL_STATIC_DRAW;
import static android.opengl.GLES30.glBindBuffer;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glBufferData;
import static android.opengl.GLES30.glGenVertexArrays;
import static com.example.openglexemple.MyRenderer.ATTRIBUTE_NORMAL;
import static com.example.openglexemple.MyRenderer.ATTRIBUTE_POSITION;
import static com.example.openglexemple.MyRenderer.ATTRIBUTE_TEXTURE_COORDINATE;
import static com.example.openglexemple.MyRenderer.BYTES_PER_FLOAT;
import static com.example.openglexemple.MyRenderer.BYTES_PER_INT;
import static com.example.openglexemple.MyRenderer.NORMAL_SIZE;
import static com.example.openglexemple.MyRenderer.POSITION_SIZE;
import static com.example.openglexemple.MyRenderer.STRIDE_T;
import static com.example.openglexemple.MyRenderer.TEXTURE_COORDINATE_SIZE;

import android.graphics.Shader;
import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MeshVBO {

    private static final String TAG = "MESH_VBO";
    final int[] vao = new int[1];
    private final int[] vbo = new int[1];
    private final int[] vboIds = new int[3];
    private final int[] ibo = new int[1];
    private final float[] vertices;
    private final float[] positions;
    private final float[] textures;
    private final float[] normals;
    private final float[] colors;
    private final int[] indices;
    private FloatBuffer vertexBuffer;
    private FloatBuffer positionBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer textureBuffer;
    private IntBuffer indexBuffer;
    Material material;

    public MeshVBO(float[] positions, float[] textures, float[] normals, float[] colors, float[] vertices, int[] indices) {
        this.positions = positions;
        this.textures = textures;
        this.normals = normals;
        this.colors = colors;
        this.vertices = vertices;
        this.indices = indices;
    }

    public Material getMaterial(){
        return this.material;
    }

    public void setMaterial(Material material){
        this.material = material;
    }

    public void setupMesh(ShaderProgram shaderProgram){
        createBuffers();
        createVBO(1);
        createIBO();
        setupBufferData();
        createVAO();
        bindVAO();
        setupAttributes(shaderProgram);
        //unbindVBO(1);
        unbindVAO();
    }

    public void setupNonInterleavedMesh(ShaderProgram shaderProgram){
        createNonInterleavedBuffers();
        createVBO(3);
        createIBO();
        createVAO();
        bindVAO();
        setNoInterleavedBufferData();
        setupNoInterleavedAttributes(shaderProgram);
        //unbindVBO(3);
        unbindVAO();
    }

    public void createNonInterleavedBuffers(){
        // Initialize the buffers.
        if (positions != null) {
            positionBuffer = ByteBuffer.allocateDirect(positions.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            positionBuffer.put(positions).position(0);
        }

        //for PLY file format
        if (colors != null) {
            colorBuffer = ByteBuffer.allocateDirect(colors.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            colorBuffer.put(colors).position(0);
        }

        if (normals != null) {
            normalBuffer = ByteBuffer.allocateDirect(normals.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            normalBuffer.put(normals).position(0);
        }

        if (textures != null) {
            textureBuffer = ByteBuffer.allocateDirect(textures.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            textureBuffer.put(textures).position(0);
        }

        if(indices != null){
            indexBuffer = ByteBuffer.allocateDirect(indices.length * BYTES_PER_INT)
                    .order(ByteOrder.nativeOrder()).asIntBuffer();
            indexBuffer.put(indices).position(0);
        }
    }

    public void createBuffers() {
        if(vertices != null){
            vertexBuffer = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            vertexBuffer.put(vertices).position(0);
        }

        if(indices != null){
            indexBuffer = ByteBuffer.allocateDirect(indices.length * BYTES_PER_INT)
                    .order(ByteOrder.nativeOrder()).asIntBuffer();
            indexBuffer.put(indices).position(0);
        }
    }

    public void createVAO() {
        glGenVertexArrays(1, vao, 0);
    }

    public void createVBO(int i) {
        if(i == 1){
            glGenBuffers(1, vbo, 0);
        }else{
            glGenBuffers(i, vboIds, 0);
        }
    }

    public void createIBO(){
        glGenBuffers(1, ibo, 0);
    }

    public void setupBufferData(){
        if (vbo[0] > 0 && ibo[0] > 0) {
            glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer.capacity() * BYTES_PER_FLOAT,
                    vertexBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * BYTES_PER_INT,
                    indexBuffer, GL_STATIC_DRAW);
        } else {
            Log.e(TAG, "VBO and IBO are not generated");
        }
    }

    public void setupAttributes(ShaderProgram shaderProgram){
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        shaderProgram.enableVertexAttribArray(ATTRIBUTE_POSITION, POSITION_SIZE, STRIDE_T, 0);
        shaderProgram.enableVertexAttribArray(ATTRIBUTE_TEXTURE_COORDINATE, TEXTURE_COORDINATE_SIZE, STRIDE_T, POSITION_SIZE *BYTES_PER_FLOAT);
        shaderProgram.enableVertexAttribArray(ATTRIBUTE_NORMAL, NORMAL_SIZE, STRIDE_T,(POSITION_SIZE + TEXTURE_COORDINATE_SIZE)*BYTES_PER_FLOAT );
    }

    public void setNoInterleavedBufferData(){
        if (vbo[0] > 0 ) {
            //position vbo
            glBindBuffer(GL_ARRAY_BUFFER, vboIds[0]);
            glBufferData(GL_ARRAY_BUFFER, positionBuffer.capacity() * BYTES_PER_FLOAT,
                    positionBuffer, GL_STATIC_DRAW);
        }else {
            Log.e(TAG, "VBO "+vboIds[0]+" is not generated");
        }

        if (vbo[1] > 0 ) {
            //texture cordinate vbo
            glBindBuffer(GL_ARRAY_BUFFER,vboIds[1]);
            glBufferData(GL_ARRAY_BUFFER, textureBuffer.capacity() * BYTES_PER_FLOAT,
                    textureBuffer, GL_STATIC_DRAW);
        }else {
            Log.e(TAG, "VBO "+vboIds[0]+" is not generated");
        }

        if (vbo[2] > 0 ) {
            //normal vbo
            glBindBuffer(GL_ARRAY_BUFFER, vboIds[2]);
            glBufferData(GL_ARRAY_BUFFER, normalBuffer.capacity() * BYTES_PER_FLOAT,
                    normalBuffer, GL_STATIC_DRAW);
        }else {
            Log.e(TAG, "VBO "+vboIds[0]+" is not generated");
        }

        if(ibo[0] > 0){
            //ibo
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * BYTES_PER_INT,
                    indexBuffer, GL_STATIC_DRAW);
        }else{
            Log.e(TAG, "IBO is not generated");
        }

    }
    public void setupNoInterleavedAttributes(ShaderProgram shaderProgram){
        glBindBuffer(GL_ARRAY_BUFFER, vboIds[0]);
        shaderProgram.enableVertexAttribArray(ATTRIBUTE_POSITION, POSITION_SIZE, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vboIds[1]);
        shaderProgram.enableVertexAttribArray(ATTRIBUTE_TEXTURE_COORDINATE, TEXTURE_COORDINATE_SIZE, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vboIds[2]);
        shaderProgram.enableVertexAttribArray(ATTRIBUTE_NORMAL, NORMAL_SIZE, 0, 0);
    }
    public void bindVAO(){
        glBindVertexArray(vao[0]);
    }

    public void unbindVBO(int i){
        glBindBuffer(GL_ARRAY_BUFFER, i);
    }

    public void unbindVAO(){
        glBindVertexArray ( 0 );
    }

    public void render(){
        // Bind the VAO
        bindVAO();
        //GLES30.glEnableVertexAttribArray(0);

        // Draw
        glDrawElements(GL_TRIANGLE_STRIP, indices.length, GL_UNSIGNED_INT, 0);

        unbindVAO();
    }

    public void draw(){
        GLES30.glDrawArrays(GL_TRIANGLES, 0, positions.length / POSITION_SIZE);
    }

    public void setupTexture(){
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, this.getMaterial().getTexture());
    }

    public FloatBuffer getBufferPositions() {
        return this.positionBuffer;
    }

    public FloatBuffer getBufferNormals() {
        return this.normalBuffer;
    }

    public FloatBuffer getBufferTextureCoordinates() {
        return this.textureBuffer;
    }

    public FloatBuffer getBufferColors() {
        return this.colorBuffer;
    }
}
