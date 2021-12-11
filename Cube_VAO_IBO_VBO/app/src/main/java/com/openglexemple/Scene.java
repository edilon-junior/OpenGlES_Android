package com.example.openglexemple;

import static com.example.openglexemple.Colors.GREEN;
import static com.example.openglexemple.Colors.RED;
import static com.example.openglexemple.Colors.WHITE;
import static com.example.openglexemple.Constants.POINT_ATTRIBUTES;
import static com.example.openglexemple.Constants.POINT_FLOAT_UNIFORMS;
import static com.example.openglexemple.Constants.SCENE_ATTRIBUTES;
import static com.example.openglexemple.Constants.SCENE_FLOAT_UNIFORMS;
import static com.example.openglexemple.Constants.SCENE_INT_UNIFORMS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    private final String TAG = "SCENE";

    private final Map<Integer, ShaderProgram> shaderProgramList = new HashMap<>();
    private final List<GameObject> solidModelList = new ArrayList<>();
    private final List<GameObject> pointList = new ArrayList<>();

    public Light getLight() {
        return light;
    }

    public void setLight(Light light) {
        this.light = light;
    }

    private Light light;
    private ShaderProgram sceneProgram;
    private ShaderProgram pointProgram;

    /**
     *
     * @param loader
     * @param transformation
     */
    public Scene(Loader loader, Transformation transformation){
        sceneProgram = new ShaderProgram("per_pixel_vertex_shader.vert", "per_pixel_fragment_shader.frag", SCENE_ATTRIBUTES, loader);
        sceneProgram.setAttributeHandles(SCENE_ATTRIBUTES);
        sceneProgram.setFloatUniformHandles(SCENE_FLOAT_UNIFORMS);
        sceneProgram.setIntUniformHandles(SCENE_INT_UNIFORMS);

        //pointProgram = new ShaderProgram("point_vertex_shader.vert", "point_fragment_shader.frag", POINT_ATTRIBUTES, loader);
        //pointProgram.setAttributeHandles(POINT_ATTRIBUTES);
        //pointProgram.setFloatUniformHandles(POINT_FLOAT_UNIFORMS);

        //index is the program id
        if(sceneProgram.getProgramHandle() > -1){
            shaderProgramList.put(sceneProgram.getProgramHandle(),sceneProgram);
        }
        //if(pointProgram.getProgramHandle() > -1){
           // shaderProgramList.put(pointProgram.getProgramHandle(),pointProgram);
        //}

        light = new Light(transformation);
    }

    public void createGameObjects(Loader loader){
        GameObject cube = new SolidModel("cube_dice", loader, sceneProgram, 0);
        //cube.setAngularVelocity(360/10);
        //cube.setRotationAxis(0,1,0);
        cube.translate(-3,6,-10);
        cube.setRotation(0,0,1,0);
        addGameObject(cube);

        GameObject cube2 = new SolidModel("cube_dice", loader, sceneProgram, 0);
        //cube2.setAngularVelocity(360/10);
        //cube2.setRotationAxis(1,0,0);
        cube2.translate(-3, 0, -10);
        cube2.setRotation(90,0,1,0);
        addGameObject(cube2);

        GameObject cube3 = new SolidModel("cube_dice", loader, sceneProgram, 0);
        //cube2.setAngularVelocity(360/10);
        //cube2.setRotationAxis(1,0,0);
        cube3.translate(-3, -6, -10);
        cube3.setRotation(180,0,1,0);
        addGameObject(cube3);

        GameObject cube4 = new SolidModel("cube_dice", loader, sceneProgram, 0);
        //cube2.setAngularVelocity(360/10);
        //cube2.setRotationAxis(1,0,0);
        cube4.translate(3, 6, -10);
        cube4.setRotation(270,0,1,0);
        addGameObject(cube4);

        GameObject cube5 = new SolidModel("cube_dice", loader, sceneProgram, 0);
        //cube2.setAngularVelocity(360/10);
        //cube2.setRotationAxis(1,0,0);
        cube5.translate(3, 0, -10);
        cube5.setRotation(90,1,0,0);
        addGameObject(cube5);

        GameObject cube6 = new SolidModel("cube_dice", loader, sceneProgram, 0);
        //cube2.setAngularVelocity(360/10);
        //cube2.setRotationAxis(1,0,0);
        cube6.translate(3, -6, -10);
        cube6.setRotation(270,1,0,0);
        addGameObject(cube6);

        GameObject point1 = new Point(new Vector3f(2,5,-6), WHITE, 10.0f, pointProgram);
        GameObject point2 = new Point(new Vector3f(0,0,-6), RED, 10.0f, pointProgram);

        GameObject polygon = new Polygon(new Vector3f(0,0,-6), 16, GREEN, 1,0, pointProgram);
        polygon.setPointSize(10);

        pointList.add(point1);
        pointList.add(point2);
        pointList.add(polygon);

    }

    public void addGameObject(GameObject gameObject){
        solidModelList.add(gameObject);
    }

    public void addGameObject(GameObject gameObject, int index){
        solidModelList.add(index, gameObject);
    }

    public List<GameObject> getGameObjectList(){
        return this.solidModelList;
    }

    public List<GameObject> getPointList(){
        return this.pointList;
    }

    public ShaderProgram getShaderProgram(int shaderProgramId){
        return shaderProgramList.get(shaderProgramId);
    }

    public void setPointProgram(ShaderProgram pointProgram){
        this.pointProgram = pointProgram;
    }
    public ShaderProgram getPointProgram(){
        return this.pointProgram;
    }
}
