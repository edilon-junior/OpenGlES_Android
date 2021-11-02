package com.example.openglexemple;

import static android.opengl.GLES32.GL_ARRAY_BUFFER;
import static android.opengl.GLES32.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES32.GL_STATIC_DRAW;
import static android.opengl.GLES32.glBindBuffer;
import static android.opengl.GLES32.glBindVertexArray;
import static android.opengl.GLES32.glBufferData;
import static com.example.openglexemple.MyRenderer.ATTRIBUTE_NORMAL;
import static com.example.openglexemple.MyRenderer.ATTRIBUTE_POSITION;
import static com.example.openglexemple.MyRenderer.ATTRIBUTE_TEXTURE_COORDINATE;
import static com.example.openglexemple.MyRenderer.BYTES_PER_FLOAT;
import static com.example.openglexemple.MyRenderer.BYTES_PER_INT;
import static com.example.openglexemple.MyRenderer.NORMAL_SIZE;
import static com.example.openglexemple.MyRenderer.POSITION_SIZE;
import static com.example.openglexemple.MyRenderer.STRIDE_T;
import static com.example.openglexemple.MyRenderer.TEXTURE_COORDINATE_SIZE;

import android.opengl.GLES32;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MeshVBO {

    private static final String TAG = "MESH_VBO";
    final int[] vao = new int[1];
    final int[] vbo = new int[1];
    final int[] ibo = new int[1];
    private float[] vertices;
    private final float[] positions;
    private final float[] textures;
    private final float[] normals;
    private float[] colors;
    private final int[] indices;
    private FloatBuffer vertexBuffer;
    private FloatBuffer positionBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer textureBuffer;
    private IntBuffer indexBuffer;
    Material material;

    public MeshVBO(float[] positions, float[] textures, float[] normals, float[] vertices,  int[] indices) {
        this.positions = positions;
        this.textures = textures;
        this.normals = normals;
        this.colors = new float[0];
        this.vertices = vertices;
        this.indices = indices;
    }

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
        initializeBuffers();
        createVBO();
        createIBO();
        bindBufferDataVBO();
        bindBufferDataIBO();
        createVAO();
        bind();
        setAttributes(shaderProgram);
        unbind();
    }

    public void initializeBuffers() {
        vertexBuffer = ByteBuffer.allocateDirect(vertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertices).position(0);

        indexBuffer = ByteBuffer.allocateDirect(indices.length * BYTES_PER_INT)
               .order(ByteOrder.nativeOrder()).asIntBuffer();
        indexBuffer.put(indices).position(0);
    }

    public void createVBO() {
        GLES32.glGenBuffers(1, vbo, 0);
    }

    public void createIBO(){
        GLES32.glGenBuffers(1, ibo, 0);
    }

    public void bindBufferDataVBO(){
        if (vbo[0] > 0 && ibo[0] > 0) {
            glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
            glBufferData(GL_ARRAY_BUFFER, vertexBuffer.capacity() * BYTES_PER_FLOAT,
                    vertexBuffer, GL_STATIC_DRAW);
        } else {
            Log.e(TAG, "VBO is not generated");
        }
    }

    public void bindBufferDataIBO(){
        if (ibo[0] > 0){
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.capacity() * BYTES_PER_INT,
                    indexBuffer, GL_STATIC_DRAW);
        } else {
            Log.e(TAG, "IBO is not generated");
        }
    }

    public void createVAO() {
        GLES32.glGenVertexArrays(1, vao, 0);
    }

    public void bind(){
        glBindVertexArray(vao[0]);
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
    }

    public void setAttributes(ShaderProgram shaderProgram){
        glBindBuffer(GL_ARRAY_BUFFER,vbo[0]);
        shaderProgram.enableVertexAttribArray(ATTRIBUTE_POSITION, POSITION_SIZE, STRIDE_T, 0);
        shaderProgram.enableVertexAttribArray(ATTRIBUTE_NORMAL, NORMAL_SIZE, STRIDE_T,POSITION_SIZE*BYTES_PER_FLOAT );
        shaderProgram.enableVertexAttribArray(ATTRIBUTE_TEXTURE_COORDINATE, TEXTURE_COORDINATE_SIZE, STRIDE_T, (POSITION_SIZE+NORMAL_SIZE)*BYTES_PER_FLOAT);
    }

    public void unbind(){
        glBindVertexArray ( 0 );
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void render(){
        // Bind the VAO
        glBindVertexArray ( vao[0]);

        // Draw
        GLES32.glDrawElements(GLES32.GL_TRIANGLE_STRIP, indices.length, GLES32.GL_UNSIGNED_INT, 0);

        glBindVertexArray ( 0 );
    }
}
