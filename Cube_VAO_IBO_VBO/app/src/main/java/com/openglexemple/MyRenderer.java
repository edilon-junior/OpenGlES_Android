package com.example.openglexemple;

import static com.example.openglexemple.Constants.POINT_ATTRIBUTES;
import static com.example.openglexemple.Constants.POINT_FLOAT_UNIFORMS;
import static com.example.openglexemple.Constants.SCENE_ATTRIBUTES;
import static com.example.openglexemple.Constants.SCENE_FLOAT_UNIFORMS;
import static com.example.openglexemple.Constants.SCENE_INT_UNIFORMS;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;

public class MyRenderer implements GLSurfaceView.Renderer
{
    public volatile float deltaX;
    public volatile float deltaY;

    private static final String TAG = "RENDERER";

    private final Context mActivityContext;
    private final Transformation transformation;
    private final Loader loader;
    private Scene scene;
    float initial_time = 0;
    boolean start = false;
    private ShaderProgram pointProgram;
    //private ShaderProgram sceneProgram;

    public MyRenderer(final Context activityContext)
    {
        mActivityContext = activityContext;
        loader = new Loader(mActivityContext);
        transformation = new Transformation();
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        //sceneProgram = new ShaderProgram("per_pixel_vertex_shader.vert", "per_pixel_fragment_shader.frag", SCENE_ATTRIBUTES, loader);
       // sceneProgram.setAttributeHandles(SCENE_ATTRIBUTES);
        //sceneProgram.setFloatUniformHandles(SCENE_FLOAT_UNIFORMS);
        //sceneProgram.setIntUniformHandles(SCENE_INT_UNIFORMS);

        pointProgram = new ShaderProgram("point_vertex_shader.vert", "point_fragment_shader.frag", POINT_ATTRIBUTES, loader);
        pointProgram.setAttributeHandles(POINT_ATTRIBUTES);
        pointProgram.setFloatUniformHandles(POINT_FLOAT_UNIFORMS);

        scene = new Scene(loader, transformation);
        scene.setPointProgram(pointProgram);
        scene.createGameObjects(loader);
        // Set the background clear color.
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.0f);

        GLES30.glLineWidth(4);

        // Use culling to remove back faces.
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        GLES30.glCullFace(GLES30.GL_BACK);
        // Enable depth testing
        GLES30.glEnable(GLES30.GL_DEPTH_TEST);

        //GLES30.glFrontFace(GLES30.GL_CCW);

        transformation.createViewMatrix();
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        deltaX = 0.0f;
        deltaY = 0.0f;

        // Set the OpenGL viewport to the same size as the surface.
        GLES30.glViewport(0, 0, width, height);

        transformation.createPerspectiveMatrix(width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        transformation.createViewProjectionMatrix();

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);

        if(start == false){
            initial_time = SystemClock.uptimeMillis();
            start = true;
        }
        float time = (SystemClock.uptimeMillis() - initial_time) / 1000; //time in seconds

        //Draw a point to indicate the light.

        //ShaderProgram pointProgram = scene.getPointProgram();
        pointProgram.useProgram();
        pointProgram.setAttributeHandles(POINT_ATTRIBUTES);
        pointProgram.setFloatUniformHandles(POINT_FLOAT_UNIFORMS);
        for(Point point : scene.getPointList()){
            point.update(time);
            point.render(pointProgram, transformation);
            point.cleanUp();
        }
        for(Polygon polygon : scene.getPolygonList()){
            polygon.update(time);
            polygon.render(pointProgram, transformation);
            polygon.cleanUp();
        }
       /*
        sceneProgram.useProgram();
        for(GameObject gameObject: scene.getGameObjectList()){
            gameObject.getMesh().setupTexture();
            gameObject.update(time);
            gameObject.render(scene, transformation);
            gameObject.cleanUp();
        }
*/
        deltaX = 0.0f;
        deltaY = 0.0f;
    }
/*
    private void drawCube(GameObject cube)
    {
        float[] mMVPMatrix = transformation.getMVPMatrix(cube.getModelMatrix());

        FloatBuffer bPosition = cube.getMesh().getBufferPositions();
        FloatBuffer bNormal = cube.getMesh().getBufferNormals();
        FloatBuffer bTexture = cube.getMesh().getBufferTextureCoordinates();
        FloatBuffer bColor = cube.getMesh().getBufferColors();
        FloatBuffer[] attribBuffers = new FloatBuffer[]{bPosition, bNormal, bTexture, bColor};

        float[] ambient = cube.getMesh().getMaterial().ambient;
        float[] diffuse = cube.getMesh().getMaterial().diffuse;
        float[] specular = cube.getMesh().getMaterial().specular;
        float[] shininess = cube.getMesh().getMaterial().shininess;

        sceneProgram.enableVertexAttribArray(sceneAttributes,
                sceneAttributesSizes, attribBuffers);
        sceneProgram.passIntUniforms(sceneIntUniforms, new int[]{0});
        sceneProgram.passFloatUniforms(sceneFloatUniforms, mLightPosInEyeSpace, lightColor, new float[]{0,0,0},
                ambient, diffuse, specular, shininess,
                transformation.getViewMatrix(), mMVPMatrix);

        cube.getMesh().draw();

        sceneProgram.disableAttributes();
    }
*/

}
