package com.example.openglexemple;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
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
    public static final String UNIFORM_TEXTURE = "u_Texture";
    public static final String UNIFORM_COLOR = "u_Color";
    public static final String UNIFORM_POINT_SIZE = "u_PointSize";

    public static final int POSITION_SIZE = 3;
    public static final int NORMAL_SIZE = 3;
    public static final int TEXTURE_COORDINATE_SIZE = 2;
    public static final int COLOR_SIZE = 4;
    public static final int BYTES_PER_FLOAT = 4;
    public static final int BYTES_PER_INT = 4;
    public static final int STRIDE_T = (POSITION_SIZE + NORMAL_SIZE + TEXTURE_COORDINATE_SIZE) * BYTES_PER_FLOAT;
    public static final int STRIDE_C = (POSITION_SIZE + NORMAL_SIZE + COLOR_SIZE) * BYTES_PER_FLOAT;


    private final String[] sceneFloatUniforms = new String[]{
            UNIFORM_LIGHT_POS, UNIFORM_LIGHT_COLOR, UNIFORM_VIEW_POS,
            UNIFORM_AMBIENT, UNIFORM_DIFFUSE, UNIFORM_SPECULAR, UNIFORM_SHININESS,
            UNIFORM_MV_MATRIX, UNIFORM_MVP_MATRIX
    };

    public final String[] sceneIntUniforms = new String[]{
            UNIFORM_TEXTURE
    };

    public final String[] sceneAttributes = new String[]{
            ATTRIBUTE_POSITION, ATTRIBUTE_NORMAL, ATTRIBUTE_TEXTURE_COORDINATE, ATTRIBUTE_COLOR
    };

    public int[] sceneAttributesSizes = new int[]{
            POSITION_SIZE, NORMAL_SIZE, TEXTURE_COORDINATE_SIZE, COLOR_SIZE
    };

    private final String[] pointAttributes = new String[] {ATTRIBUTE_POSITION};
    private final String[] pointFloatUniforms = new String[]{UNIFORM_MVP_MATRIX, UNIFORM_COLOR, UNIFORM_POINT_SIZE};


    private final float[] lightColor = new float[]{1, 1, 1};

    private float[] mLightPosInEyeSpace = new float[4];


    ShaderProgram sceneProgram;
    ShaderProgram pointProgram;
    private final Transformation transformation;
    private final Loader loader;
    private Scene scene;

    float initial_time = 0;
    boolean start = false;

    public MyRenderer(final Context activityContext)
    {
        mActivityContext = activityContext;
        loader = new Loader(mActivityContext);
        transformation = new Transformation();
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        sceneProgram = new ShaderProgram("per_pixel_vertex_shader.vert", "per_pixel_fragment_shader.frag", sceneAttributes, loader);
        pointProgram = new ShaderProgram("point_vertex_shader.vert", "point_fragment_shader.frag", pointAttributes, loader);

        setupSceneProgram();
        setupPointProgram()
        
        scene = new Scene(loader, sceneProgram);
        // Set the background clear color.
        GLES30.glClearColor(0.5f, 0.5f, 0.5f, 0.0f);

        // Use culling to remove back faces.
        GLES30.glEnable(GLES30.GL_CULL_FACE);
        //GLES20.glCullFace(GLES20.GL_FRONT);
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
       
        pointProgram.useProgram();
        for(PointObject point : scene.getPointObjectList()){
            point.update(time);
            drawLight(point);

            mLightPosInEyeSpace = transformation.convertIntoEyeSpace(point.getModelPosition(), point.getModelMatrix());
            point.cleanUp();
        }

        sceneProgram.useProgram();
        for(GameObject gameObject: scene.getGameObjectList()){
            gameObject.getMesh().setupTexture();

            gameObject.update(time);
            drawVAOCube(gameObject);
            gameObject.cleanUp();
        }

        deltaX = 0.0f;
        deltaY = 0.0f;
    }

    private void drawVAOCube(GameObject cube)
    {
        float[] mMVPMatrix = transformation.getMVPMatrix(cube.getModelMatrix());

        float[] ambient = cube.getMesh().getMaterial().ambient;
        float[] diffuse = cube.getMesh().getMaterial().diffuse;
        float[] specular = cube.getMesh().getMaterial().specular;
        float[] shininess = cube.getMesh().getMaterial().shininess;

        sceneProgram.passIntUniforms(sceneIntUniforms, new int[]{0});
        sceneProgram.passFloatUniforms(sceneFloatUniforms, mLightPosInEyeSpace, lightColor, new float[]{0,0,0},
                ambient, diffuse, specular, shininess,
                transformation.getViewMatrix(), mMVPMatrix);

        cube.getMesh().render(GLES30.GL_TRIANGLES);
    }

    private void drawPoint(PointObject pointObject)
    {

        float[] mMVPMatrix = transformation.getMVPMatrix(pointObject.getModelMatrix());

        pointProgram.passFloatUniforms(pointFloatUniforms, mMVPMatrix, pointObject.getColor(), pointObject.getSize());

        pointObject.getMesh().render(GLES30.GL_POINTS);
    }

    public void setupSceneProgram(){
        sceneProgram.setAttributeHandles(sceneAttributes);
        sceneProgram.setFloatUniformHandles(sceneFloatUniforms);
        sceneProgram.setIntUniformHandles(sceneIntUniforms);
    }

    public void setupPointProgram(){
        pointProgram.setAttributeHandles(pointAttributes);
        pointProgram.setFloatUniformHandles(pointFloatUniforms);
    }
}
