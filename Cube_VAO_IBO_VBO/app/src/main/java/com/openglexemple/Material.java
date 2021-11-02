package com.example.openglexemple;

public class Material {
    float[] ambient;
    float[] diffuse;
    float[] specular;
    float[] transparency;
    float[] shininess;
    float optical_density;
    float dissolve;
    float illumination;
    int texture;

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
        this.texture = texture;
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
        this.texture = 0;
    }
    public Material() {
        this.ambient = new float[]{ 211,211,211};
        this.diffuse = new float[]{169,169,169};
        this.specular = new float[]{255,255,255};
        this.shininess = new float[]{0.1f};
        this.transparency = new float[]{0};
        this.texture = 0;
    }
    public int getTexture(){
        return this.texture;
    }
    public void setTexture(int texture){
        this.texture = texture;
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
}
