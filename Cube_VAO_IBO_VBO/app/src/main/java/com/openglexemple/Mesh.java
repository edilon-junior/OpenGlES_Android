package com.example.openglexemple;

import static android.opengl.GLES30.GL_LINE_LOOP;
import static android.opengl.GLES30.GL_POINTS;
import static android.opengl.GLES30.GL_UNSIGNED_SHORT;
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
import static com.example.openglexemple.Constants.ATTRIBUTE_NORMAL;
import static com.example.openglexemple.Constants.ATTRIBUTE_POSITION;
import static com.example.openglexemple.Constants.ATTRIBUTE_TEXTURE_COORDINATE;
import static com.example.openglexemple.Constants.BYTES_PER_FLOAT;
import static com.example.openglexemple.Constants.BYTES_PER_SHORT;
import static com.example.openglexemple.Constants.NORMAL_SIZE;
import static com.example.openglexemple.Constants.POSITION_SIZE;
import static com.example.openglexemple.Constants.STRIDE_T;
import static com.example.openglexemple.Constants.TEXTURE_COORDINATE_SIZE;

import android.opengl.GLES30;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Mesh {

    private static final String TAG = "MESH_VBO";

    private int vertexStrider = 0;
    private int vboLength = 0;
    private int iboLength = 0;
    final int[] vao = new int[1];
    private int[] vbo;
    private final int[] ibo = new int[1];
    protected final float[] vertices;
    private float[] positions;
    private float[] textures;
    private float[] normals;
    private final float[] colors;
    private final short[] indices;
    private FloatBuffer vertexBuffer;
    private FloatBuffer positionBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer textureBuffer;
    private ShortBuffer indexBuffer;
    private String modelName;
    private String meshName;
    private String materialName;
    Material[] materials;

    public Mesh(float[] positions, float[] textures, float[] normals, float[] colors, float[] vertices, short[] indices) {
        this.positions = positions;
        this.textures = textures;
        this.normals = normals;
        this.colors = colors;
        this.vertices = vertices;
        this.indices = indices;
        this.materialName = null;
    }

    public Material[] getMaterials(){
        return this.materials;
    }

    public void setMaterials(Material[] material){
        this.materials = material;
    }

    public void setupMesh(ShaderProgram shaderProgram){
        createBuffers();
        vbo = new int[vboLength];
        createVBO(1);
        createIBO(iboLength);
        setupBufferData();
        createVAO();
        bindVAO();
        setupAttributes(shaderProgram);
        //unbindVBO(1);
        unbindVAO();
    }

    public void setupNonInterleavedMesh(ShaderProgram shaderProgram){
        createNonInterleavedBuffers();
        vbo = new int[vboLength];
        createVBO(vboLength);
        createIBO(iboLength);
        setNoInterleavedBufferData();
        createVAO();
        bindVAO();
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
            vboLength++;
        }

        //for PLY file format
        if (colors != null) {
            colorBuffer = ByteBuffer.allocateDirect(colors.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            colorBuffer.put(colors).position(0);
            vboLength++;
        }

        if (normals != null) {
            normalBuffer = ByteBuffer.allocateDirect(normals.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            normalBuffer.put(normals).position(0);
            vboLength++;
        }

        if (textures != null) {
            textureBuffer = ByteBuffer.allocateDirect(textures.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            textureBuffer.put(textures).position(0);
            vboLength++;
        }

        if(indices != null){
            indexBuffer = ByteBuffer.allocateDirect(indices.length * BYTES_PER_SHORT)
                    .order(ByteOrder.nativeOrder()).asShortBuffer();
            indexBuffer.put(indices).position(0);
            iboLength++;
        }
    }

    public void createBuffers() {
        if(vertices != null){
            vertexBuffer = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            vertexBuffer.put(vertices).position(0);
            vboLength++;
        }

        if(indices != null){
            indexBuffer = ByteBuffer.allocateDirect(indices.length * BYTES_PER_SHORT)
                    .order(ByteOrder.nativeOrder()).asShortBuffer();
            indexBuffer.put(indices).position(0);
            iboLength++;
        }
    }

    public void createVAO() {
        glGenVertexArrays(1, vao, 0);
    }

    public void createVBO(int n) {
        glGenBuffers(n, vbo, 0);
    }

    public void createIBO(int n){
        if(n == 0){
            return;
        }
        glGenBuffers(n, ibo, 0);
    }

    public void setupBufferData(){
        if (vbo[0] > 0 && ibo[0] > 0) {
            glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer.capacity() * BYTES_PER_FLOAT,
                    vertexBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * BYTES_PER_SHORT,
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
        if (vboLength > 0 ) {
            //position vbo
            glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
            glBufferData(GL_ARRAY_BUFFER, positionBuffer.capacity() * BYTES_PER_FLOAT,
                    positionBuffer, GL_STATIC_DRAW);
        }

        if (vboLength > 1 ) {
            //texture cordinate vbo
            glBindBuffer(GL_ARRAY_BUFFER,vbo[1]);
            glBufferData(GL_ARRAY_BUFFER, textureBuffer.capacity() * BYTES_PER_FLOAT,
                    textureBuffer, GL_STATIC_DRAW);
        }

        if (vboLength > 2 ) {
            //normal vbo
            glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
            glBufferData(GL_ARRAY_BUFFER, normalBuffer.capacity() * BYTES_PER_FLOAT,
                    normalBuffer, GL_STATIC_DRAW);
        }

        if(ibo[0] > 0){
            //ibo
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * BYTES_PER_SHORT,
                    indexBuffer, GL_STATIC_DRAW);
        }
    }
    public void setupNoInterleavedAttributes(ShaderProgram shaderProgram){
        if(vboLength > 0) {
            glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
            shaderProgram.enableVertexAttribArray(ATTRIBUTE_POSITION, POSITION_SIZE, 0, 0);
        }
        if(vboLength > 1) {
            glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
            shaderProgram.enableVertexAttribArray(ATTRIBUTE_TEXTURE_COORDINATE, TEXTURE_COORDINATE_SIZE, 0, 0);
        }
        if(vboLength > 2) {
            glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
            shaderProgram.enableVertexAttribArray(ATTRIBUTE_NORMAL, NORMAL_SIZE, 0, 0);
        }
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

    public void render(int type, int fromIndex, int toIndex){
        // Bind the VAO
        bindVAO();

        // Draw
        switch (type){
            case GL_TRIANGLES:
                glDrawElements(GL_TRIANGLES, toIndex, GL_UNSIGNED_SHORT, fromIndex);
                break;
            case GL_POINTS:
                drawPoint();
                break;
            case GL_LINE_LOOP:
                GLES30.glDrawArrays(GL_LINE_LOOP, 0, positions.length / POSITION_SIZE);
                break;
        }

        unbindVAO();
    }

    public void drawPoint(){
        GLES30.glDrawArrays(GL_POINTS, 0, 1);
    }

    public void draw(){
        GLES30.glDrawArrays(GL_TRIANGLES, 0, positions.length / POSITION_SIZE);
    }

    public int getVertexStrider() {
        return vertexStrider;
    }

    public void setVertexStrider(int vertexStrider) {
        this.vertexStrider = vertexStrider;
    }
    public void setPositions(float[] positions){
        this.positions = positions;
    }
    public void setTextures(float[] textures){
        this.textures = textures;
    }
    public void setNormals(float[] normals){
        this.normals = normals;
    }
    public float[] getVertices(){
        return vertices;
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

    public void setModelName(String modelName){
        this.modelName = modelName;
    }

    public String getModelName(){
        return modelName;
    }
    public String getMeshName() {
        return meshName;
    }

    public void setMeshName(String meshName) {
        this.meshName = meshName;
    }
    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
    public String getMaterialName(){
        return materialName;
    }
}


