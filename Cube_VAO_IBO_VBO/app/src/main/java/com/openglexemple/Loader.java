package com.example.openglexemple;

import static android.opengl.GLES20.GL_TEXTURE_2D;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Loader {

    private final Context context;

    public Loader(Context context) {
        this.context = context;
    }

    private static final String TAG = "LOADER";

    public MeshVBO loadMeshIBO(String modelName) {

        List<Float[]> positionArray = new ArrayList<>();
        List<Float[]> textureArray = new ArrayList<>();
        List<Float[]> normalArray = new ArrayList<>();
        List<String> faceArray = new ArrayList<>();
        String materialName = "";

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("models/" + modelName), StandardCharsets.UTF_8));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] ls = line.split("\\s+");
                if (ls[0].equals("mtllib")) {
                    materialName = ls[1];
                }
                if (ls[0].equals("v")) {
                    positionArray.add(new Float[]{
                            Float.parseFloat(ls[1]),
                            Float.parseFloat(ls[2]),
                            Float.parseFloat(ls[3])});
                } else if (ls[0].equals("vt")) {
                    textureArray.add(new Float[]{
                            Float.parseFloat(ls[1]),
                            Float.parseFloat(ls[2])});
                } else if (ls[0].equals("vn")) {
                    normalArray.add(new Float[]{
                            Float.parseFloat(ls[1]),
                            Float.parseFloat(ls[2]),
                            Float.parseFloat(ls[3])});
                } else if (ls[0].equals("f")) {
                    faceArray.add(ls[1]);
                    faceArray.add(ls[2]);
                    faceArray.add(ls[3]);
                }
            }
        } catch (IOException e) {
            Log.d(TAG, "error to read file " + modelName);
            e.printStackTrace();
        }
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        float[] positions = Utils.floatListToArray(positionArray);
        float[] textures = new float[positionArray.size() * 2];
        float[] normals = new float[positionArray.size() * 3];
        float[] vertices = new float[positions.length + textures.length + normals.length];
        int[] indices = new int[faceArray.size()];


        processIndices(positions, textures, normals, vertices, indices,
                textureArray, normalArray, faceArray);

        System.out.println("positions length: " + positions.length);
        Log.wtf(TAG, "textures length: "+textures.length);
        System.out.println("normal length: "+ normals.length);
        System.out.println("vertices length: "+ vertices.length);
        Log.wtf(TAG,"indices length: "+indices.length);
        System.out.println("positions: "+ Arrays.toString(positions));
        Log.wtf(TAG,"texture: "+ Arrays.toString(textures));
        System.out.println("normals: "+ Arrays.toString(normals));
        Log.wtf(TAG,"vertices: "+Arrays.toString(vertices));
        System.out.println("indices"+ Arrays.toString(indices));

        MeshVBO mesh = new MeshVBO(positions, textures, normals, null, vertices, indices);

        if(materialName.isEmpty()){
            mesh.setMaterial(new Material());
        }else{
            mesh.setMaterial(loadMaterial(materialName));
        }

        return mesh;
    }

    private void processIndices(
                                float[] pos,
                                float[] textures,
                                float[] normals,
                                float[] vertices,
                                int[] indices,
                                List<Float[]> textureArray,
                                List<Float[]> normalArray,
                                List<String> faceArray) {


        for (int i = 0; i < faceArray.size(); i++) {
            int stride = 3;
            String triplet = faceArray.get(i);
            String[] triplet_split = triplet.split("/");

            int v_index = Integer.parseInt(triplet_split[0]) - 1;
            indices[i] = v_index;

            if(triplet_split.length > 1) {
                int t_index = Integer.parseInt(triplet_split[1]) - 1;
                Float[] texture = textureArray.get(t_index);
                textures[v_index * 2 + 0] = texture[0];
                textures[v_index * 2 + 1] = texture[1];

                stride += 2;
            }

            if(triplet_split.length > 2) {
                int n_index = Integer.parseInt(triplet_split[2]) - 1;
                Float[] normal = normalArray.get(n_index);
                normals[v_index * 3 + 0] = normal[0];
                normals[v_index * 3 + 1] = normal[1];
                normals[v_index * 3 + 2] = normal[2];

                stride += 3;
            }

            vertices[v_index * stride + 0] = pos[v_index * 3 + 0];
            vertices[v_index * stride + 1] = pos[v_index * 3 + 1];
            vertices[v_index * stride + 2] = pos[v_index * 3 + 2];

            if(triplet_split.length > 1) {
                vertices[v_index * stride + 3] = textures[v_index * 2 + 0];
                vertices[v_index * stride + 4] = textures[v_index * 2 + 1];
            }
            if(triplet_split.length > 2) {
                vertices[v_index * stride + 5] = normals[v_index * 3 + 0];
                vertices[v_index * stride + 6] = normals[v_index * 3 + 1];
                vertices[v_index * stride + 7] = normals[v_index * 3 + 2];
            }
        }
    }

    public Material loadMaterial(String materialPath) {

        if (materialPath.isEmpty()) {
            return new Material();
        }

        float[] ambient = new float[3];
        float[] diffuse = new float[3];
        float[] specular = new float[3];
        float transparency = 0;
        float shininess = 0;
        String textureName = null;

        //String extension = materialPath.substring(materialPath.length() - 3);

        Log.i(TAG, "material name: " + materialPath);

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("models/" + materialPath), StandardCharsets.UTF_8));

        } catch (IOException e) {
            e.printStackTrace();
        }
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                String[] ls = line.split("\\s+");
                if (ls[0].equals("Ka")) {
                    ambient[0] = Float.parseFloat(ls[1]);
                    ambient[1] = Float.parseFloat(ls[2]);
                    ambient[2] = Float.parseFloat(ls[3]);
                } else if (ls[0].equals("Kd")) {
                    diffuse[0] = Float.parseFloat(ls[1]);
                    diffuse[1] = Float.parseFloat(ls[2]);
                    diffuse[2] = Float.parseFloat(ls[3]);
                } else if (ls[0].equals("Ks")) {
                    specular[0] = Float.parseFloat(ls[1]);
                    specular[1] = Float.parseFloat(ls[2]);
                    specular[2] = Float.parseFloat(ls[3]);
                } else if (ls[0].equals("Tr")) {
                    transparency = Float.parseFloat(ls[1]);
                } else if (ls[0].equals("Ns")) {
                    shininess = Float.parseFloat(ls[1]);
                } else if (ls[0].equals("map_Kd")) {
                    textureName = ls[1];
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int texture = loadTexture(context,textureName);
        return new Material(ambient, diffuse, specular, shininess, transparency, texture);
    }

    public Bitmap loadBitmap(String textureName) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        //final int textureId = context.getResources().getIdentifier(textureName, "drawable",
        //     context.getPackageName());

        //Log.i(TAG, "texture name: " + textureId);

        //final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), textureId, options);

        AssetManager assetManager =  context.getAssets();
        InputStream inputStream = null;

        try {
            inputStream = assetManager.open("textures/" + textureName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(inputStream == null){
            Log.wtf(TAG, "error to load bitmap from "+textureName);
        }

        Bitmap bitmap_asset = BitmapFactory.decodeStream(inputStream, null, options);

        return bitmap_asset;
    }

    public static int loadTexture(final Context context, final String textureName)
    {
        if(textureName.isEmpty()){
            return 0;
        }

        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error generating texture name.");
        }
/*
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;	// No pre-scaling

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
*/
        final Bitmap bitmap = loadAsset(textureName, context);

        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GL_TEXTURE_2D, textureHandle[0]);

        // Set filtering
        GLES20.glTexParameteri(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        //glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bitmap.getWidth(), bitmap.getHeight(), 0, GL_RGBA,GL_UNSIGNED_BYTE,bitmap.getB);
        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle();

        return textureHandle[0];
    }

    public static Bitmap loadAsset(String textureName, Context context) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        //final int textureId = context.getResources().getIdentifier(textureName, "drawable",
        //     context.getPackageName());

        //Log.i(TAG, "texture name: " + textureId);

        //final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), textureId, options);

        AssetManager assetManager =  context.getAssets();
        InputStream inputStream = null;

        try {
            inputStream = assetManager.open("textures/" + textureName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(inputStream == null){
            System.out.println("error to load bitmap from "+textureName);
        }

        Bitmap bitmap_asset = BitmapFactory.decodeStream(inputStream, null, options);

        //flip image on y axis
        Matrix flip = new Matrix();
        flip.postScale(1f, 1f);

        return Bitmap.createBitmap(bitmap_asset, 0, 0, bitmap_asset.getWidth(), bitmap_asset.getHeight(), flip, true);
    }

    public ShaderProgram loadShaderProgram(String vertexName, String fragmentName, String[] attributes){
        String vertexString = shaderToString(vertexName);
        String fragmentString = shaderToString(fragmentName);
        ShaderProgram shaderProgram = new ShaderProgram(vertexString, fragmentString, attributes);
        return shaderProgram;
    }

    public String shaderToString(String shaderName) {

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("shaders/" + shaderName), StandardCharsets.UTF_8));

        } catch (IOException e) {
            Log.wtf(TAG, "can not load shader "+shaderName);
            e.printStackTrace();
        }
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        //bufferedReader.toString();

        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (Exception e) {

        }
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringBuilder.toString();
    }

    /*
    public Mesh loadPLYFile(String modelName) {
        BufferedReader bufferedReader = null;
        List<Vector3f> vertexArray = new ArrayList<Vector3f>();
        List<Vector3f> normalArray = new ArrayList<Vector3f>();
        List<Vector2f> textureArray = new ArrayList<Vector2f>();
        List<float[]> colorArray  = new ArrayList<float[]>();
        float[] vertices = null;
        float[] textures = null;
        float[] normals = null;
        float[] colors = null;
        int[] faces = null;

        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("models/" + modelName), StandardCharsets.UTF_8));

            String line;

            int vertices_size = 0;
            int faces_size = 0;
            int vertex_counter = 0;
            int face_counter = 0;
            boolean val = false;
            while ((line = bufferedReader.readLine()) != null) {
                String[] ls = line.split("\\s+");
                if(!ls[0].equals("end_header") && val==false){
                    if(ls[1].equals("vertex")){
                        vertices_size = Integer.parseInt(ls[2]);
                    }
                    if(ls[1].equals("face")){
                        faces_size = Integer.parseInt(ls[2]);
                        vertices = new float[faces_size * 3 * 3];
                        normals = new float[faces_size * 3 * 3];
                        textures = new float[faces_size * 3 * 2];
                        colors = new float[faces_size * 3 * 4];
                        faces = new int[faces_size * 3];
                    }
                    continue;
                }
                if(ls[0].equals("end_header") && val==false){
                    val = true;
                    continue;
                }
                if(vertex_counter < vertices_size) {
                    Vector3f vertex = new Vector3f(
                            Float.parseFloat(ls[0]),
                            Float.parseFloat(ls[1]),
                            Float.parseFloat(ls[2]));
                    Vector3f normal = new Vector3f(
                            Float.parseFloat(ls[3]),
                            Float.parseFloat(ls[4]),
                            Float.parseFloat(ls[5]));
                    Vector2f texture = new Vector2f(
                            Float.parseFloat(ls[6]),
                            Float.parseFloat(ls[7]));
                    float[] color = new float[]{
                            Float.parseFloat(ls[8]),
                            Float.parseFloat(ls[9]),
                            Float.parseFloat(ls[10]),
                            Float.parseFloat(ls[11])};
                    vertexArray.add(vertex);
                    normalArray.add(normal);
                    textureArray.add(texture);
                    colorArray.add(color);
                }
                // now begins reading the faces
                int v1 = Integer.parseInt(ls[1]);
                int v2 = Integer.parseInt(ls[2]);
                int v3 = Integer.parseInt(ls[3]);

                faces[face_counter] = v1;
                faces[face_counter + 1] = v2;
                faces[face_counter + 2] = v3;

                vertex_counter++;
                face_counter++;
            }
        } catch (IOException e) {
            Log.d(TAG, "error to read file " + modelName);
            e.printStackTrace();
        }
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0;i < faces.length;i++){
            vertices[i * 3 + 0] = vertexArray.get(i).x;
            vertices[i * 3 + 1] = vertexArray.get(i).y;
            vertices[i * 3 + 2] = vertexArray.get(i).z;

            normals[i * 3 + 0] = normalArray.get(i).x;
            normals[i * 3 + 1] = normalArray.get(i).y;
            normals[i * 3 + 2] = normalArray.get(i).z;

            textures[i * 2 + 0] = textureArray.get(i).x;
            textures[i * 2 + 1] = textureArray.get(i).y;

            colors[i * 4 + 0] = colorArray.get(i)[0];
            colors[i * 4 + 1] = colorArray.get(i)[1];
            colors[i * 4 + 2] = colorArray.get(i)[2];
            colors[i * 4 + 3] = colorArray.get(i)[3];
        }

        Mesh mesh = new Mesh(vertices, normals, textures, colors);
        return mesh;
    }
    */
    public List<String> parseOBJtoStringList(String modelName) throws Exception {
        List<String> list = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("models/" + modelName), StandardCharsets.UTF_8));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}

