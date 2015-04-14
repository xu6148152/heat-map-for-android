package pyalot.heatmap.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.example.heatmap.MyGLRenderer;

import android.opengl.GLES20;

public class Heights {
	
	/** Width of the viewport */
	private int width;
	/** Height of the viewport */
	private int height;
	private Shader shader;
//	private Shader clampShader;
//	private Shader multiplyShader;
//	private Shader blurShader;
	/** A Node */
	private Node nodeBack;
	/** A node */
	Node nodeFront;
	/** Array which stores the generated buffer object names */
	private int[] vertexBuffer;
//	private int maxPointCount;
	private FloatBuffer vertexBufferData;
	/** Stores the current index position in the {@code vertexBufferData} */
	private int bufferIndex;
	/** Number of points added to the  {@code vertexBufferData} */
	private int pointCount;
	@SuppressWarnings("unused")
	private static final String LOG = "Heights";
	
	/**
	 * Create a Height object.
	 * 
	 * @param heatmap A heatmap
	 * @param width Width of the viewport
	 * @param height Height of the viewport
	 */
	public Heights(GLHeatmap heatmap, final int width, final int height) {
		this.width = width;
		this.height = height;
		this.shader = new Shader(
			"attribute vec4 position, intensity;		\n" +
			"varying vec2 off, dim;						\n" +
			"varying float vIntensity;					\n" +
			"uniform vec2 viewport;						\n" +
			"											\n" +
			"void main(){								\n" +
			"    dim = abs(position.zw);				\n" +
			"    off = position.zw;						\n" +
			"    vec2 pos = position.xy + position.zw;	\n" +
			"    vIntensity = intensity.x;				\n" +
			"    gl_Position = vec4((pos/viewport)*2.0-1.0, 0.0, 1.0);\n" +
			"}",
			"#ifdef GL_FRAGMENT_PRECISION_HIGH			\n" +
			"    precision highp int;					\n" +
			"    precision highp float;					\n" +
			"#else										\n" +
			"    precision mediump int;					\n" +
			"    precision mediump float;				\n" +
			"#endif										\n" +
			"varying vec2 off, dim;						\n" +
			"varying float vIntensity;					\n" +
			"void main(){								\n" +
			"    float falloff = (1.0 - smoothstep(0.0, 1.0, length(off/dim)));\n" +
			"    float intensity = falloff*vIntensity;	\n" +
			"    gl_FragColor = vec4(intensity);		\n" +
			"}");
//		this.clampShader = new Shader(Main.vertexShaderBlit, Main.fragmentShaderBlit + 
//				"uniform float low, high;					\n" +
//				"void main(){								\n" +
//				"    gl_FragColor = vec4(clamp(texture2D(source, texcoord).rgb, low, high), 1.0);\n" +
//				"}");
//		this.multiplyShader = new Shader(Main.vertexShaderBlit, Main.fragmentShaderBlit + 
//				"uniform float value;						\n" +
//				"void main(){								\n" +
//				"    gl_FragColor = vec4(texture2D(source, texcoord).rgb*value, 1.0);\n" +
//				"}");
//		this.blurShader = new Shader(Main.vertexShaderBlit, Main.fragmentShaderBlit + 
//				"uniform vec2 viewport;						\n" +
//				"void main(){								\n" +
//				"    vec4 result = vec4(0.0);				\n" +
//				"    for(int x=-1; x<=1; x++){				\n" +
//				"        for(int y=-1; y<=1; y++){			\n" +
//				"            vec2 off = vec2(x,y)/viewport;\n" +
//				"            //float factor = 1.0 - smoothstep(0.0, 1.5, length(off));\n" +
//				"            float factor = 1.0;			\n" +
//				"            result += vec4(texture2D(source, texcoord+off).rgb*factor, factor);\n" +
//				"        }									\n" +
//				"    }										\n" +
//				"    gl_FragColor = vec4(result.rgb/result.w, 1.0);\n" +
//				"}");
		this.nodeBack = new Node(this.width, this.height);
		this.nodeFront = new Node(this.width, this.height);
		this.vertexBuffer = new int[Main.NUM_BUFFER];
		GLES20.glGenBuffers(Main.NUM_BUFFER, this.vertexBuffer, Main.BUFFER_OFFSET);
		this.vertexBufferData = ByteBuffer.allocateDirect(Main.VERTEX_SIZE * Main.NUM_INDICES_RENDER * Main.BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		this.bufferIndex = 0;
		this.pointCount = 0;
	}

	/**
	 * Set new size.
	 *  
	 * @param width Width of the viewport
	 * @param height Height of the viewport
	 */
	void resize(final int width, final int height) {
		this.width = width;
		this.height = height;
		this.nodeBack.resize(this.width, this.height);
		this.nodeFront.resize(this.width, this.height);
	};
	
	/**
	 * Update the heatmap, i.e. draw all buffered points from the {code vertexBufferData}.
	 */
	public void update() {
		if (this.pointCount > 0) {
			GLES20.glEnable(GLES20.GL_BLEND);
			this.nodeFront.use();
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.vertexBuffer[0]);
			//MyGLRenderer.checkGlError("glBindBuffer");
			this.vertexBufferData.position(0);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, this.vertexBufferData.capacity() * Main.BYTES_PER_FLOAT, this.vertexBufferData, GLES20.GL_STREAM_DRAW);
			MyGLRenderer.checkGlError("glBufferData");			
//			int positionLoc = this.shader.attribLocation(Main.VARIABLE_ATTRIBUTE_POSITION);
//			int intensityLoc = this.shader.attribLocation(Main.VARIABLE_ATTRIBUTE_INTENSITY);
			//Log.i(LOG, positionLoc + "_" + intensityLoc);
			GLES20.glEnableVertexAttribArray(1);
			//MyGLRenderer.checkGlError("glEnableVertexAttribArray");	
			GLES20.glVertexAttribPointer(0, Main.POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, Main.STRIDE_BYTES, 0 * Main.POSITION_DATA_SIZE);
			//MyGLRenderer.checkGlError("glVertexAttribPointer");	
			GLES20.glVertexAttribPointer(1, Main.POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, Main.STRIDE_BYTES, Main.BYTES_PER_FLOAT * Main.POSITION_DATA_SIZE);
			//MyGLRenderer.checkGlError("glVertexAttribPointer");	
			this.shader.use().vec2(Main.VARIABLE_UNIFORM_VIEWPORT, this.width, this.height);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, this.pointCount * Main.NUM_INDICES_RENDER);
			//MyGLRenderer.checkGlError("glDrawArrays");	
			GLES20.glDisableVertexAttribArray(1);
			//MyGLRenderer.checkGlError("glDisableVertexAttribArray");	
			this.pointCount = 0;
			this.bufferIndex = 0;
			this.nodeFront.end();
			GLES20.glDisable(GLES20.GL_BLEND);
		}
	}

	/**
	 * Clear color and buffer.
	 */
	public void clear() {
		this.nodeFront.use();
		GLES20.glClearColor(0, 0, 0, 1);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		this.nodeFront.end();
	}

//	public void clamp(int min, int max) {
//		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.heatmap.quad.get(0));
//		GLES20.glVertexAttribPointer(0, Main.POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);
//		this.nodeFront.bind(0);
//		this.nodeBack.use();
//		this.clampShader.use()._int("source", 0)._float("low", min)._float("high", max);
//		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, Main.UNKOWN_SIX);
//		this.nodeBack.end();
//		this.swap();
//	}
//
//	public void multiply(float value) {
//		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.heatmap.quad.get(0));
//		GLES20.glVertexAttribPointer(0, Main.POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);
//		this.nodeFront.bind(0);
//		this.nodeBack.use();
//		this.multiplyShader.use()._int("source", 0)._float("value", value);
//		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, Main.UNKOWN_SIX);
//		this.nodeBack.end();
//		this.swap();
//	}
//
//	public void blur() {
//		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, this.heatmap.quad.get(0));
//		GLES20.glVertexAttribPointer(0, Main.POSITION_DATA_SIZE, GLES20.GL_FLOAT, false, 0, 0);
//		this.nodeFront.bind(0);
//		this.nodeBack.use();
//		this.blurShader.use()._int("source", 0).vec2("viewport", this.width, this.height);
//		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, Main.UNKOWN_SIX);
//		this.nodeBack.end();
//		this.swap();
//	}
	
//	private void swap() {
//		Node tmp = this.nodeFront;
//		this.nodeFront = this.nodeBack;
//		this.nodeBack = tmp;
//	}
	
	/**
	 * Add a point to the vertex buffer.
	 * 
	 * @param x x-coordinate of the point
	 * @param y y-coordinate of the point
	 * @param xs
	 * @param ys
	 * @param intensity intensity (>= 0 and <=1) of the point
	 */
	private void addVertex(float x, float y, float xs, float ys, float intensity) {
		//Log.i("addVertex", bufferIndex+ "_" + x + "_" + y + "_" + xs + "_" + ys + "_" + intensity);
		this.vertexBufferData.put(this.bufferIndex++, x);
		this.vertexBufferData.put(this.bufferIndex++, y);
		this.vertexBufferData.put(this.bufferIndex++, xs);
		this.vertexBufferData.put(this.bufferIndex++, ys);
		this.vertexBufferData.put(this.bufferIndex++, intensity);
		this.vertexBufferData.put(this.bufferIndex++, intensity);
		this.vertexBufferData.put(this.bufferIndex++, intensity);
		this.vertexBufferData.put(this.bufferIndex++, intensity);
	}

	/**
	 * Add a point to the heatmap.
	 * 
	 * @param x x-coordinate
	 * @param y y-coordinate
	 * @param size Size (diameter)
	 * @param intensity Intensity (>= 0 and <= 1)
	 */
	public void addPoint(float x, float y, float size, float intensity) {
		if (this.pointCount >= 1) {
			this.update();
		}
		y = this.height - y;
		float s = size / 2;
		this.addVertex(x, y, -s, -s, intensity);
		this.addVertex(x, y, +s, -s, intensity);
		this.addVertex(x, y, -s, +s, intensity);
		this.addVertex(x, y, -s, +s, intensity);
		this.addVertex(x, y, +s, -s, intensity);
		this.addVertex(x, y, +s, +s, intensity);
		this.pointCount += 1;
	}

}
