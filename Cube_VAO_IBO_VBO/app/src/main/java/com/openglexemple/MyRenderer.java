package com.example.openglexemple;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

public class MyRenderer implements GLSurfaceView.Renderer
{
    public volatile float deltaX;
    public volatile float deltaY;

    private static final String TAG = "RENDERER";

    private final Context mActivityContext;

    public static final String ATTRIBUTE_POSITION = "a_Position";
    public static final String ATTRIBUTE_NORMAL = "a_Normal";
    public static final String ATTRIBUTE_TEXTURE_COORDINATE = "a_TexCoordinate";
    public static final String ATTRIBUTE_COLOR = "a_Color";
    public static final String UNIFORM_LIGHT_POS = "u_LightPos";
    public static final String UNIFORM_LIGHT_COLOR = "u_LightColor";
    public static final String UNIFORM_VIEW_POS = "u_ViewPos";
    public static final String UNIFORM_AMBIENT = "u_Ambient";
    public static final String UNIFORM_DIFFUSE = "u_Diffuse";
    public static final String UNIFORM_SPECULAR = "u_Specular";
    public static final String UNIFORM_SHININESS = "u_Shininess";
    public static final String UNIFORM_MV_MATRIX = "u_MVMatrix";
    public static final String UNIFORM_MVP_MATRIX = "u_MVPMatrix";

    private final String[] sceneFloatUniforms = new String[]{
            "u_LightPos", "u_LightColor", "u_ViewPos",
            "u_Ambient", "u_Diffuse", "u_Specular", "u_Shininess",
            "u_MVMatrix","u_MVPMatrix"
    };
    private final String[] sceneIntUniforms = new String[]{
            "u_Texture"
    };

    private final String[] sceneAttributes = new String[]{
            "a_Position", "a_Normal", "a_TexCoordinate","a_Color"
    };

    private final String[] pointAttributes = new String[] {"a_Position"};
    private final String[] pointFloatUniforms = new String[]{"u_MVPMatrix", "u_Color", "u_PointSize"};

    public static final int POSITION_SIZE = 3;
    public static final int NORMAL_SIZE = 3;
    public static final int TEXTURE_COORDINATE_SIZE = 2;
    public static final int COLOR_SIZE = 4;
    public static final int BYTES_PER_FLOAT = 4;
    public static final int BYTES_PER_INT = 4;
    public static final int STRIDE_T = (POSITION_SIZE + NORMAL_SIZE + TEXTURE_COORDINATE_SIZE) * BYTES_PER_FLOAT;
    public static final int STRIDE_C = (POSITION_SIZE + NORMAL_SIZE + COLOR_SIZE) * BYTES_PER_FLOAT;

    private final float[] lightColor = new float[]{1, 1, 1};

    private float[] mLightPosInEyeSpace = new float[4];

    private ShaderProgram sceneProgram;
    private ShaderProgram pointProgram;
    private Transformation transformation;
    private Loader loader;

    GameObject cube1;
    GameObject cube2;
    PointObject point;
    PointObject point2;

    public MyRenderer(final Context activityContext)
    {
        mActivityContext = activityContext;
        loader = new Loader(activityContext);
        transformation = new Transformation();
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        sceneProgram = loader.loadShaderProgram("per_pixel_vertex_shader.vert", "per_pixel_fragment_shader.frag", sceneAttributes);
        sceneProgram.setAttributeHandles(sceneAttributes);
        sceneProgram.setFloatUniformHandles(sceneFloatUniforms);
        sceneProgram.setIntUniformHandles(sceneIntUniforms);

        pointProgram = loader.loadShaderProgram("point_vertex_shader.vert", "point_fragment_shader.frag", pointAttributes);

        cube1 = new GameObject("cube_dice.obj", loader, sceneProgram);
        cube1.translate(0,0,-7);

        cube2 = new GameObject("cube_dice.obj", loader, sceneProgram);
        cube2.translate(0, 6, -10);

        point = new PointObject(new Vector3f(2,5,-7), new float[]{1f, 1f, 1f, 1.0f}, new float[]{10.0f});
        point2 = new PointObject(new Vector3f(0,0,-2), new float[]{1f, 0, 0, 1.0f}, new float[]{10.0f});
        // Set the background clear color to black.
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.0f);

        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        //GLES20.glCullFace(GLES20.GL_FRONT);
        // Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

       // GLES20.glFrontFace(GLES20.GL_CCW);

        transformation.createViewMatrix();
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        deltaX = 0.0f;
        deltaY = 0.0f;

        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        transformation.createPerspectiveMatrix(width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        transformation.createViewProjectionMatrix();

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Do a complete rotation every 10 seconds.
        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

        //Draw a point to indicate the light.
        pointProgram.useProgram();
        pointProgram.setAttributeHandles(pointAttributes);
        pointProgram.setFloatUniformHandles(pointFloatUniforms);
        point.setPosition(2,5,-7);
        point.setRotation(angleInDegrees,1,0,0);
        point.update();
        //point2.setPosition(2,5,-7);
        //point2.setRotation(angleInDegrees,1,0,0);
        point2.update();
        drawLight(point);
        drawLight(point2);

        mLightPosInEyeSpace = transformation.convertIntoEyeSpace(point.getModelPosition(), point.getModelMatrix());

        sceneProgram.useProgram();
        //sceneProgram.setAttributeHandles(sceneAttributes);
        //sceneProgram.setFloatUniformHandles(sceneFloatUniforms);
        //sceneProgram.setIntUniformHandles(sceneIntUniforms);
        setupTexture(cube1.getMesh().getMaterial().texture);

        cube1.setRotation(angleInDegrees,0,1,0);
        cube1.update();

        drawCube(cube1);

        setupTexture(cube2.getMesh().getMaterial().texture);

        cube2.setRotation(angleInDegrees,1,0,0);
        cube2.update();

        drawCube(cube2);

        cube1.cleanUp();
        cube2.cleanUp();
        point.cleanUp();
        point2.cleanUp();

        deltaX = 0.0f;
        deltaY = 0.0f;
    }

    private void drawCube(GameObject cube)
    {

        float[] mMVPMatrix = transformation.getMVPMatrix(cube.getModelMatrix());

        float[] ambient = cube.getMesh().getMaterial().ambient;
        float[] diffuse = cube.getMesh().getMaterial().diffuse;
        float[] specular = cube.getMesh().getMaterial().specular;
        float[] shininess = cube.getMesh().getMaterial().shininess;

        sceneProgram.passIntUniforms(sceneIntUniforms, new int[]{0});
        // "u_LightPos", "u_LightColor", "u_ViewPos","u_Ambient", "u_Diffuse", "u_Specular", "u_Shininess", "u_MVMatrix","u_MVPMatrix"
        sceneProgram.passFloatUniforms(sceneFloatUniforms, mLightPosInEyeSpace, lightColor, new float[]{0,0,0},
                ambient, diffuse, specular, shininess,
                transformation.getViewMatrix(), mMVPMatrix);

        cube.getMesh().render();
    }

    /**
     * Draws a point representing the position of the light.
     */
    private void drawLight(PointObject pointObject)
    {

        float[] mMVPMatrix = transformation.getMVPMatrix(pointObject.getModelMatrix());

        pointProgram.passAttributesBuffers(pointAttributes, new int[]{4}, new FloatBuffer[]{pointObject.getBufferPosition()});
        pointProgram.passFloatUniforms(pointFloatUniforms, mMVPMatrix, pointObject.getColor(), pointObject.getSize());

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }

    public void setupTexture(final int texture){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
    }
}