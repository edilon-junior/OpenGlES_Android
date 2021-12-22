package com.example.openglexemple;

import android.opengl.GLES30;

public class Material {
    float[] ambient;
    float[] diffuse;
    float[] specular;
    float[] transparency;
    float[] shininess;
    float optical_density;
    float dissolve;
    float illumination;
    int textureId;
    int indexCount = 0;
    String materialName = "";

    public Material(float[] ambient,
                    float[] diffuse,
                    float[] specular,
                    float shininess,
                    float transparency,
                    int texture){
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = new float[]{shininess};
        this.transparency = new float[]{transparency};
        this.textureId = texture;
    }
    public Material(float[] ambient,
                    float[] diffuse,
                    float[] specular,
                    float shininess,
                    float transparency){
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = new float[]{shininess};
        this.transparency = new float[]{transparency};
        this.textureId = 0;
    }
    public Material() {
        this.ambient = new float[]{ 211,211,211};
        this.diffuse = new float[]{169,169,169};
        this.specular = new float[]{255,255,255};
        this.shininess = new float[]{0.1f};
        this.transparency = new float[]{0};
        this.textureId = 0;
    }

    public void setupTexture(){
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, getTexture());
    }

    public void setMaterialName(String materialName){
        this.materialName = materialName;
    }
    public String getMaterialName(){
        return materialName;
    }
    public int getTexture(){
        return this.textureId;
    }
    public void setTexture(int texture){
        this.textureId = texture;
    }
    public void setTransparency(float transparency){
        this.transparency[0] = transparency;
    }
    public float[] getAmbient(){
        return this.ambient;
    }
    public float[] getDiffuse(){
        return this.diffuse;
    }
    public float[] getSpecular(){
        return this.specular;
    }
    public float[] getShininess(){return this.shininess;}
    public float[] getTransparency(){return this.transparency;}
    public void setIndexCount(int indexCount){
        this.indexCount = indexCount;
    }
    public int getIndexCount(){
        return indexCount;
    }
}
