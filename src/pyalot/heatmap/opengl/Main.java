package pyalot.heatmap.opengl;

public class Main {
	
	/** Number of bytes per float */
	public static final int BYTES_PER_FLOAT = 4;
	/** Size of the vertex */
	public static final int VERTEX_SIZE = 8;
	/** Number of elements per vertex */
	public static final int STRIDE_BYTES = VERTEX_SIZE * BYTES_PER_FLOAT;
	/** Size of the position data in elements */
	public static final int POSITION_DATA_SIZE = 4;
	/** Value for a null buffer */
	public static final int BUFFER_NULL = 0;
	/** Value for the level zero */
	public static final int LEVEL = 0;
	/** Value for one buffer */
	public static final int NUM_BUFFER = 1;
	/** Value for the buffer offset zero */
	public static final int BUFFER_OFFSET = 0;	
	/** Number of indices to be rendered **/ 
	public static final int NUM_INDICES_RENDER = 6;
	
	public static final int VERTEX_ATTRIB_ARRAY_POSTION_1 = 1;
	public static final int BIND_ZERO = 0;
	
	/* ---------------------- OpenGL variables ---------------------- */
	public static final String VARIABLE_ATTRIBUTE_POSITION = "position";
	public static final String VARIABLE_ATTRIBUTE_INTENSITY = "intensity";
	
	public static final String VARIABLE_UNIFORM_VIEWPORT = "viewport";
	public static final String VARIABLE_UNIFORM_SOURCE = "source";
	public static final String VARIABLE_UNIFORM_GRADIENTTEXTURE = "gradientTexture";	
	
	/* ---------------------- Shader ---------------------- */
	
	public static final String vertexShaderBlit =
		"attribute vec4 position;				\n" +
		"varying vec2 texcoord;					\n" +
		"void main(){							\n" +
		"    texcoord = position.xy*0.5+0.5;	\n" +
		"    gl_Position = position;			\n" +
		"}";
	public static final String fragmentShaderBlit =
		"#ifdef GL_FRAGMENT_PRECISION_HIGH		\n" +
		"    precision highp int;				\n" +
		"    precision highp float;				\n" +
		"#else									\n" +
		"    precision mediump int;				\n" +
		"    precision mediump float;			\n" +
		"#endif									\n" +
		"uniform sampler2D source;				\n" +
		"varying vec2 texcoord;";
}
