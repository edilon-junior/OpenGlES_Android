package com.example.openglexemple;

public class Constants {
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

    public static final String[] SCENE_FLOAT_UNIFORMS = new String[]{
            UNIFORM_LIGHT_POS, UNIFORM_LIGHT_COLOR, UNIFORM_VIEW_POS,
            UNIFORM_AMBIENT, UNIFORM_DIFFUSE, UNIFORM_SPECULAR, UNIFORM_SHININESS,
            UNIFORM_MV_MATRIX, UNIFORM_MVP_MATRIX
    };

    public static final String[] SCENE_INT_UNIFORMS = new String[]{
            UNIFORM_TEXTURE
    };

    public static final String[] SCENE_ATTRIBUTES = new String[]{
            ATTRIBUTE_POSITION, ATTRIBUTE_NORMAL, ATTRIBUTE_TEXTURE_COORDINATE, ATTRIBUTE_COLOR
    };

    public static int[] SCENE_ATTRIBUTES_SIZES = new int[]{
            POSITION_SIZE, NORMAL_SIZE, TEXTURE_COORDINATE_SIZE, COLOR_SIZE
    };

    public static final String[] POINT_ATTRIBUTES = new String[] {ATTRIBUTE_POSITION};

    public static final String[] POINT_FLOAT_UNIFORMS = new String[]{UNIFORM_MVP_MATRIX, UNIFORM_COLOR, UNIFORM_POINT_SIZE};


}
