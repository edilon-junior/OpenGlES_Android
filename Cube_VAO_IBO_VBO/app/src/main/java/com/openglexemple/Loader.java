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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Loader {

    private final Context context;

    public Loader(Context context) {
        this.context = context;
    }

    private static final String TAG = "LOADER";

    public Mesh loadMeshIBO(String modelName) {

        List<Mesh> meshes = new ArrayList<>();

        List<Float[]> positionArray = new ArrayList<>();
        List<Float[]> textureArray = new ArrayList<>();
        List<Float[]> normalArray = new ArrayList<>();
        List<Integer[]> faceArray = new ArrayList<>();
        String materialName = null;
        String meshName = "";
        String materialLib = null;
        int faceCounter = 0;
        ArrayList<Integer> indexCounter = new ArrayList<>();

        AssetManager assetManager = context.getAssets();

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(assetManager.open(
                            "models/"+modelName+"/"+ modelName+".obj"), StandardCharsets.UTF_8));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                String[] ls = line.split("\\s+");
                switch (ls[0]) {
                    case "mtllib":
                        materialLib = ls[1];
                        break;
                    case "v":
                        positionArray.add(new Float[]{
                                Float.parseFloat(ls[1]),
                                Float.parseFloat(ls[2]),
                                Float.parseFloat(ls[3])});
                        break;
                    case "vt":
                        textureArray.add(new Float[]{
                                Float.parseFloat(ls[1]),
                                Float.parseFloat(ls[2])});
                        break;
                    case "vn":
                        normalArray.add(new Float[]{
                                Float.parseFloat(ls[1]),
                                Float.parseFloat(ls[2]),
                                Float.parseFloat(ls[3])});
                        break;
                    case "usemtl":
                        materialName = ls[1];
                        break;
                    case "f":
                        String[] f1 = ls[1].split("/");
                        faceArray.add(new Integer[]{
                                Utils.parseInt(f1[0]),
                                Utils.parseInt(f1[1]),
                                Utils.parseInt(f1[2])});
                        String[] f2 = ls[2].split("/");
                        faceArray.add(new Integer[]{
                                Utils.parseInt(f2[0]),
                                Utils.parseInt(f2[1]),
                                Utils.parseInt(f2[2])});
                        String[] f3 = ls[3].split("/");
                        faceArray.add(new Integer[]{
                                Utils.parseInt(f3[0]),
                                Utils.parseInt(f3[1]),
                                Utils.parseInt(f3[2])});
                        faceCounter++;
                        break;
                    case "o":
                        faceCounter = 0;
                        meshName = ls[1];
                        break;
                    case "endmesh":
                        // 3 faces by line
                        indexCounter.add(faceCounter * 3);
                        break;
                }
            }

        } catch (IOException e) {
            Log.d(TAG, "error to read file " + modelName);
            e.printStackTrace();
        }
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //add the last mesh
        Log.wtf(TAG, "create mesh : "+ meshName);
        System.out.println("faces counter:");
        for(int fc: indexCounter){
            System.out.println(faceCounter );
        }
        System.out.println("indices.size: "+faceArray.size());
        Mesh mesh = createMesh(modelName, positionArray, textureArray, normalArray, faceArray, materialName);
        mesh.setMeshName(meshName);

        Material[] materials = loadMaterialLib(modelName, materialLib);
        for(int i=0; i<materials.length;i++){
            materials[i].setIndexCount(indexCounter.get(i));
        }
        //add material to meshes
        mesh.setMaterials(materials);

        return mesh;
    }

    private Mesh createMesh(String modelName,
                            List<Float[]> positionArray,
                            List<Float[]> textureArray,
                            List<Float[]> normalArray,
                            List<Integer[]> faceArray,
                            String materialName){

        if(positionArray.size() == 0){
            return null;
        }

        float[] positions = Utils.floatListToArray(positionArray);
        float[] textures  = new float[positionArray.size() * 2];
        float[] normals   = new float[positionArray.size() * 3];
        float[] vertices  = new float[positions.length + textures.length + normals.length];
        int[]   indices   = new int[faceArray.size()];

        int vertexStride = 3;// position x+y+z

        for (int i = 0; i < faceArray.size(); i++) {
            int stride = 3;

            int v_index = faceArray.get(i)[0] - 1;
            indices[i] = v_index;

            if(faceArray.get(i)[1] > 0) {
                int t_index = faceArray.get(i)[1] - 1;
                Float[] texture = textureArray.get(t_index);

                textures[v_index * 2]     = texture[0];
                textures[v_index * 2 + 1] = texture[1];

                stride += 2;
            }

            if(faceArray.get(i)[2] > 0) {
                int n_index = faceArray.get(i)[2] - 1;
                Float[] normal = normalArray.get(n_index);
                normals[v_index * 3]     = normal[0];
                normals[v_index * 3 + 1] = normal[1];
                normals[v_index * 3 + 2] = normal[2];

                stride += 3;
            }

            vertices[v_index * stride]     = positions[v_index * 3];
            vertices[v_index * stride + 1] = positions[v_index * 3 + 1];
            vertices[v_index * stride + 2] = positions[v_index * 3 + 2];

            if(faceArray.get(i)[1] > 0
            ) {
                vertices[v_index * stride + 3] = textures[v_index * 2];
                vertices[v_index * stride + 4] = textures[v_index * 2 + 1];
                vertexStride = 5; // position + texture
            }
            if(faceArray.get(i)[2] > 0) {
                vertices[v_index * stride + 5] = normals[v_index * 3];
                vertices[v_index * stride + 6] = normals[v_index * 3 + 1];
                vertices[v_index * stride + 7] = normals[v_index * 3 + 2];
                vertexStride = 8; //position + texture + normal
            }
        }

        Mesh mesh = new Mesh(positions, textures, normals, null, vertices, indices);
        mesh.setVertexStrider(vertexStride);
        mesh.setModelName(modelName);

        return mesh;
    }

    public Material[] loadMaterialLib(String modelName, String materialLib) {

        Material[] materials = null;
        int materialIndex = 0;

        if (materialLib == null) {
            materials = new Material[]{new Material()};
            return materials;
        }

        String materialName = null;
        float[] ambient = new float[3];
        float[] diffuse = new float[3];
        float[] specular = new float[3];
        float transparency = 0;
        float shininess = 0;
        String textureName;

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(
                            "models/"+modelName+"/" + materialLib), StandardCharsets.UTF_8));

        } catch (IOException e) {
            e.printStackTrace();
        }

        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                String[] ls = line.split("\\s+");
                switch (ls[0]) {
                    case "#":
                        if(ls[1].equals("Material")){
                            materials = new Material[Integer.parseInt(ls[3])];
                        }
                        break;
                    case "newmtl":
                        materialName = ls[1];
                        break;
                    case "Ka":
                        ambient[0] = Float.parseFloat(ls[1]);
                        ambient[1] = Float.parseFloat(ls[2]);
                        ambient[2] = Float.parseFloat(ls[3]);
                        break;
                    case "Kd":
                        diffuse[0] = Float.parseFloat(ls[1]);
                        diffuse[1] = Float.parseFloat(ls[2]);
                        diffuse[2] = Float.parseFloat(ls[3]);
                        break;
                    case "Ks":
                        specular[0] = Float.parseFloat(ls[1]);
                        specular[1] = Float.parseFloat(ls[2]);
                        specular[2] = Float.parseFloat(ls[3]);
                        break;
                    case "Tr":
                        transparency = Float.parseFloat(ls[1]);
                        break;
                    case "Ns":
                        shininess = Float.parseFloat(ls[1]);
                        break;
                    case "map_Kd":
                        textureName = ls[1];
                        int texture = loadTexture(modelName, textureName);
                        Material material = new Material(ambient, diffuse, specular, shininess, transparency, texture);
                        material.setMaterialName(materialName);
                        materials[materialIndex] = material;
                        materialIndex++;
                        break;
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

        if (materials == null) {
            materials = new Material[]{new Material()};
        }

        return materials;
    }

    public int loadTexture(final String modelName, final String textureName)
    {
        if(textureName.isEmpty() || (textureName == null)){
            return 0;
        }

        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error generating texture name.");
        }

        final Bitmap bitmap = loadAssetBitmap("models/"+ modelName+"/"+textureName);

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

    public Bitmap loadAssetBitmap(String path) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        AssetManager assetManager =  context.getAssets();
        InputStream inputStream = null;

        try {
            inputStream = assetManager.open(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(inputStream == null){
            System.out.println("error to load bitmap from "+ path);
        }

        Bitmap bitmap_asset = BitmapFactory.decodeStream(inputStream, null, options);

        //flip image on y axis
        Matrix flip = new Matrix();
        flip.postScale(1f, -1f);

        return Bitmap.createBitmap(bitmap_asset, 0, 0, bitmap_asset.getWidth(), bitmap_asset.getHeight(), flip, true);
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

