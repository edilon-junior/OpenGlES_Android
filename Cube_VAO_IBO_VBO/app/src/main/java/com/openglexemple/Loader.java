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

    public MeshVBO loadMesh(String modelName) {

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

        float[] positions = new float[faceArray.size() * 3];
        float[] textures = new float[faceArray.size() * 2];
        float[] normals = new float[faceArray.size() * 3];
        float[] vertices = new float[positions.length + textures.length + normals.length];
        int[] indices = new int[faceArray.size()];

        processIndices(positions, textures, normals, vertices, indices,
                positionArray, textureArray, normalArray, faceArray);

        /* Just to test
        System.out.println("positions length: " + positions.length);
        Log.wtf(TAG, "textures length: "+textures.length);
        System.out.println("indices length: "+indices.length);
        System.out.println("positions: "+ Arrays.toString(positions));
        Log.wtf(TAG,"texture: "+ Arrays.toString(textures));
        System.out.println("normals: "+ Arrays.toString(normals));
        Log.wtf(TAG,"vertices: "+Arrays.toString(vertices));
        System.out.println("indices"+ Arrays.toString(indices));
        */
        
        MeshVBO mesh = new MeshVBO(positions, textures, normals, vertices, indices);

        if(materialName.isEmpty()){
            mesh.setMaterial(new Material());
        }else{
            mesh.setMaterial(loadMaterial(materialName));
        }

        return mesh;
    }

    private void processIndices(float[] positions,
                                float[] textures,
                                float[] normals,
                                float[] vertices,
                                int[] indices,
                                List<Float[]> positionArray,
                                List<Float[]> textureArray,
                                List<Float[]> normalArray,
                                List<String> faceArray) {
        for (int i = 0; i < faceArray.size(); i++) {
            int stride = 3;
            String triplet = faceArray.get(i);
            String[] triplet_split = triplet.split("/");

            int v_index = Integer.parseInt(triplet_split[0]) - 1;
            indices[i] = v_index + 1;

            Float[] position = positionArray.get(v_index);
            positions[i * 3 + 0] = position[0];
            positions[i * 3 + 1] = position[1];
            positions[i * 3 + 2] = position[2];

            if(triplet_split.length > 1) {
                int t_index = Integer.parseInt(triplet_split[1]) - 1;
                Float[] texture = textureArray.get(t_index);
                textures[i * 2 + 0] = texture[0];
                textures[i * 2 + 1] = texture[1];

                stride += 2;
            }

            if(triplet_split.length > 2) {
                int n_index = Integer.parseInt(triplet_split[2]) - 1;
                Float[] normal = normalArray.get(n_index);
                normals[i * 3 + 0] = normal[0];
                normals[i * 3 + 1] = normal[1];
                normals[i * 3 + 2] = normal[2];

                stride += 3;
            }

            vertices[i * stride + 0] = position[0];
            vertices[i * stride + 1] = position[1];
            vertices[i * stride + 2] = position[2];

            if(triplet_split.length > 1) {
                vertices[i * stride + 3] = textures[i * 2 + 0];
                vertices[i * stride + 4] = textures[i * 2 + 1];
            }
            if(triplet_split.length > 2) {
                vertices[i * stride + 5] = normals[i * 3 + 0];
                vertices[i * stride + 6] = normals[i * 3 + 1];
                vertices[i * stride + 7] = normals[i * 3 + 2];
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
        
        final Bitmap bitmap = loadAsset(textureName, context);

        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GL_TEXTURE_2D, textureHandle[0]);

        // Set filtering
        GLES20.glTexParameteri(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        bitmap.recycle();

        return textureHandle[0];
    }

    public static Bitmap loadAsset(String textureName, Context context) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

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
        flip.postScale(1f, -1f);

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
}
