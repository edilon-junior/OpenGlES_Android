package com.example.openglexemple;

import static com.example.openglexemple.MyRenderer.ATTRIBUTE_COLOR;
import static com.example.openglexemple.MyRenderer.ATTRIBUTE_NORMAL;
import static com.example.openglexemple.MyRenderer.ATTRIBUTE_POSITION;
import static com.example.openglexemple.MyRenderer.ATTRIBUTE_TEXTURE_COORDINATE;
import static com.example.openglexemple.MyRenderer.COLOR_SIZE;
import static com.example.openglexemple.MyRenderer.NORMAL_SIZE;
import static com.example.openglexemple.MyRenderer.POSITION_SIZE;
import static com.example.openglexemple.MyRenderer.TEXTURE_COORDINATE_SIZE;
import static com.example.openglexemple.MyRenderer.UNIFORM_AMBIENT;
import static com.example.openglexemple.MyRenderer.UNIFORM_COLOR;
import static com.example.openglexemple.MyRenderer.UNIFORM_DIFFUSE;
import static com.example.openglexemple.MyRenderer.UNIFORM_LIGHT_COLOR;
import static com.example.openglexemple.MyRenderer.UNIFORM_LIGHT_POS;
import static com.example.openglexemple.MyRenderer.UNIFORM_MVP_MATRIX;
import static com.example.openglexemple.MyRenderer.UNIFORM_MV_MATRIX;
import static com.example.openglexemple.MyRenderer.UNIFORM_POINT_SIZE;
import static com.example.openglexemple.MyRenderer.UNIFORM_SHININESS;
import static com.example.openglexemple.MyRenderer.UNIFORM_SPECULAR;
import static com.example.openglexemple.MyRenderer.UNIFORM_TEXTURE;
import static com.example.openglexemple.MyRenderer.UNIFORM_VIEW_POS;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private final String TAG = "SCENE";

    private final List<GameObject> gameObjectList = new ArrayList<GameObject>();
    private final List<PointObject> pointObjectList = new ArrayList<>();

    public Scene(Loader loader, ShaderProgram sceneProgram){

        GameObject cube = new GameObject("grassblock", loader, sceneProgram, 0);
        //cube.setAngularVelocity(360/10);
        //cube.setRotationAxis(0,1,0);
        cube.setRotation(0,0,1,0);
        cube.translate(-3,0,-10);
        addGameObject(cube);

        GameObject cube2 = new GameObject("grassblock", loader, sceneProgram, 0);
        //cube2.setAngularVelocity(360/10);
        //cube2.setRotationAxis(1,0,0);
        cube2.setRotation(90,0,1,0);
        cube2.translate(-3, 6, -10);
        addGameObject(cube2);

        GameObject cube3 = new GameObject("grassblock", loader, sceneProgram, 0);
        //cube2.setAngularVelocity(360/10);
        //cube2.setRotationAxis(1,0,0);
        cube3.setRotation(180,0,1,0);
        cube3.translate(-3, -6, -10);
        addGameObject(cube3);

        GameObject cube4 = new GameObject("grassblock", loader, sceneProgram, 0);
        //cube2.setAngularVelocity(360/10);
        //cube2.setRotationAxis(1,0,0);
        cube4.setRotation(270,0,1,0);
        cube4.translate(3, 6, -10);
        addGameObject(cube4);

        GameObject cube5 = new GameObject("grassblock", loader, sceneProgram, 0);
        //cube2.setAngularVelocity(360/10);
        //cube2.setRotationAxis(1,0,0);
        cube5.setRotation(90,1,0,0);
        cube5.translate(3, 0, -10);
        addGameObject(cube5);

        GameObject cube6 = new GameObject("grassblock", loader, sceneProgram, 0);
        //cube2.setAngularVelocity(360/10);
        //cube2.setRotationAxis(1,0,0);
        cube6.setRotation(180,1,0,0);
        cube6.translate(3, -6, -10);
        addGameObject(cube6);

        PointObject point1 = new PointObject(new Vector3f(2,5,-7), new float[]{1f, 1f, 1f, 1.0f}, new float[]{10.0f});
        PointObject point2 = new PointObject(new Vector3f(0,0,-2), new float[]{1f, 0, 0, 1.0f}, new float[]{10.0f});
        pointObjectList.add(point1);
        pointObjectList.add(point2);

        Log.wtf(TAG, "point list size: "+pointObjectList.size());
    }

    public void addGameObject(GameObject gameObject){
        gameObjectList.add(gameObject);
    }

    public void addGameObject(GameObject gameObject, int index){
        gameObjectList.add(index, gameObject);
    }

    public List<GameObject> getGameObjectList(){
        return this.gameObjectList;
    }

    public List<PointObject> getPointObjectList(){
        return this.pointObjectList;
    }

}
