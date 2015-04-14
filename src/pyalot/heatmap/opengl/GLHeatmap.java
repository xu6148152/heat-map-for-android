package pyalot.heatmap.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

public class GLHeatmap {

	/** Width of the viewport */
	private Integer width;
	/** Height of the viewport */
	private Integer height;
	/** Array which stores the generated buffer object names */
	private int[] quad;
	/** A Heights */
	private Heights heights;
	/** A Shader */
	private Shader shader;
	
	/**
	 * Create a GLHeatmap object.
	 * 
	 * @param width Width of the viewport
	 * @param height Height of the viewport
	 * @param intensityToAlpha Not in use
	 * @param gradientTexture Not in use
	 * @param alphaRange Not in use
	 * @throws Exception
	 */
	public GLHeatmap(final int width, final int height, Boolean intensityToAlpha, Object gradientTexture, float[] alphaRange) throws Exception {

		this.width = width;
		this.height = height;
		
		// ...
		GLES20.glEnableVertexAttribArray(Main.BIND_ZERO);
		//MyGLRenderer.checkGlError("glEnableVertexAttribArray");
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
		//MyGLRenderer.checkGlError("glBlendFunc");
// ...
		String getColorFun  =
			"vec3 getColor(float intensity){						\n" +
			"    vec3 blue   = vec3(0.0, 0.0, 1.0);					\n" +
			"    vec3 cyan   = vec3(0.0, 1.0, 1.0);					\n" +
			"    vec3 green  = vec3(0.0, 1.0, 0.0);					\n" +
			"    vec3 yellow = vec3(1.0, 1.0, 0.0);					\n" +
			"    vec3 red    = vec3(1.0, 0.0, 0.0);					\n" +
			"    \n" +
			"    vec3 color = (\n" +
			"        fade(-0.25, 0.25, intensity)*blue +			\n" +
			"        fade(  0.0,  0.5, intensity)*cyan +			\n" +
			"        fade( 0.25, 0.75, intensity)*green +			\n" +
			"        fade(  0.5,  1.0, intensity)*yellow +			\n" +
			"        smoothstep(0.75, 1.0, intensity)*red			\n" +
			"    );													\n" +
			"    return color;										\n" +
			"}";
// ...
		String output = 
			"vec4 alphaFun(vec3 color, float intensity){			\n" +
			"    float alpha = smoothstep(0.0, 1.0, intensity);		\n" +
			"    return vec4(color*alpha, alpha);					\n" +
			"}";
// ...
		this.shader = new Shader(Main.vertexShaderBlit, Main.fragmentShaderBlit + 
//			"float linstep(float low, float high, float value){		\n" +
//			"    return clamp((value-low)/(high-low), 0.0, 1.0);	\n" +
//			"}\n" +
//			"\n" +
			"float fade(float low, float high, float value){		\n" +
			"    float mid   = (low+high)*0.5;						\n" +
			"    float range = (high-low)*0.5;						\n" +
			"    float x = 1.0 - clamp(abs(mid-value)/range, 0.0, 1.0);\n" +
			"    return smoothstep(0.0, 1.0, x);					\n" +
			"}\n" +
			"\n" + getColorFun + "\n" + output + "\n" +
			"\n" +
			"void main(){\n" +
			"    float intensity = smoothstep(0.0, 1.0, texture2D(source, texcoord).r);\n" +
			"    vec3 color = getColor(intensity);\n" +
			"    gl_FragColor = alphaFun(color, intensity);\n" +
			"}");
// ...
		GLES20.glViewport(0, 0, this.width, this.height);
		this.quad = new int[Main.NUM_BUFFER];
		GLES20.glGenBuffers(Main.NUM_BUFFER, this.quad, Main.BUFFER_OFFSET);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.quad[Main.BUFFER_OFFSET]);
		float[] tmp = new float[]{-1, -1, 0, 1, 1, -1, 0, 1, -1, 1, 0, 1, -1, 1, 0, 1, 1, -1, 0, 1, 1, 1, 0, 1};
		FloatBuffer quad_l = ByteBuffer.allocateDirect(tmp.length * Main.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		quad_l.put(tmp, 0, tmp.length);
		quad_l.position(0);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, quad_l.capacity() * Main.BYTES_PER_FLOAT, quad_l, GLES20.GL_STATIC_DRAW);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, Main.BUFFER_NULL);
		this.heights = new Heights(this, this.width, this.height);
	}
	
	/**
	 * Set a new size.
	 * 
	 * @param width Width of the viewport.
	 * @param height Height of the viewport.
	 */
	public void adjustSize(int width, int height) {
		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			GLES20.glViewport(0, 0, this.width, this.height);
			this.heights.resize(this.width, this.height);
		}
	}
	
//	private String toFixed(double f, int newScale) {
//		BigDecimal numberBigDecimal = new BigDecimal(f);
//		numberBigDecimal = numberBigDecimal .setScale(newScale, BigDecimal.ROUND_HALF_UP);
//		return numberBigDecimal.toString();
//	}
	
	/**
	 * Display the heatmap.
	 */
	public void display() {
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.quad[Main.BUFFER_OFFSET]);
		GLES20.glVertexAttribPointer(Main.BIND_ZERO, Main.POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, 0, Main.BUFFER_OFFSET);
		this.heights.nodeFront.bind(Main.BIND_ZERO);
//		if (this.gradientTexture != null) {
//			try {
//				this.gradientTexture.bind(1);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		this.shader.use()._int(Main.VARIABLE_UNIFORM_SOURCE, Main.BIND_ZERO);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, Main.NUM_INDICES_RENDER);
	}
	
	/**
	 * Update the {@link Heights} object by call {@link Heights#update()}.
	 */
	public void update() {
		this.heights.update();
	}
	
	/**
	 * Clear the {@link Heights} by call {@link Heights#clear()}.
	 */
	public void clear() {
		this.heights.clear();
	}
	
//	public void clamp(Integer min, Integer max) {
//		if (min == null) {
//			min = 0;
//		}
//		if (max == null) {
//			max = 1;
//		}
//		this.heights.clamp(min, max);
//	}
//	
//	public void multiplay(Float value) {
//		if (value == null) {
//			value = 0.95f;
//		}
//		this.heights.multiply(value);
//	}
//	
//	public void blur() {
//		this.heights.blur();
//	}

	/* (non-Javadoc)
	 * 
	 * @see Heights#addPoint(float, float, float, float);
	 */
	public void addPoint(float x, float y, float size, float intensity) {
		this.heights.addPoint(x, y, size, intensity);
	}
	
//	public void addPoints(MyItem[] items) {
//		MyItem item;
//		//_results = [];
//		for (int i = 0; i < items.length; i++) {
//			item = items[i];
//			this.addPoint(item.x, item.y, item.size, item.intensity);
//		}
//	}
	
}
