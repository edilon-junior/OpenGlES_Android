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
    int[] textureId;
    int indexCount = 0;
    int[] sample2Did = new int[1];
    String materialName = "";

    public Material(float[] ambient,
                    float[] diffuse,
                    float[] specular,
                    float shininess,
                    float transparency,
                    int[] texture){
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
        this.textureId = new int[1];
    }
    public Material() {
        this.ambient = new float[]{ 211,211,211};
        this.diffuse = new float[]{169,169,169};
        this.specular = new float[]{255,255,255};
        this.shininess = new float[]{0.1f};
        this.transparency = new float[]{0};
        this.textureId =  new int[1];
    }
    // id varies from 0 to 31
    public void setupTexture(int id){
        GLES30.glActiveTexture(33984 + id);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, getTexture()[0]);
    }

    public void setMaterialName(String materialName){
        this.materialName = materialName;
    }
    public String getMaterialName(){
        return materialName;
    }
    public int[] getTexture(){
        return this.textureId;
    }
    public void setTexture(int[] texture){
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
    public int[] getSample2Did() {
        return sample2Did;
    }

    public void setSample2Did(int[] sample2Did) {
        this.sample2Did = sample2Did;
    }
}
