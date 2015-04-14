package pyalot.heatmap.opengl;

import android.opengl.GLES20;

public class Framebuffer {

	/** Array which stores the generated framebuffer object names */
	private int[] buffer;
	
	/**
	 * Create a Framebuffer object. The constructor generates a framebuffer.
	 */
	public Framebuffer() {
		this.buffer = new int[Main.NUM_BUFFER];
		GLES20.glGenFramebuffers(Main.NUM_BUFFER, this.buffer, Main.BUFFER_OFFSET);
	}

	/**
	 * Delete the framebuffer.
	 */
	void destroy() {
		GLES20.glDeleteFramebuffers(Main.NUM_BUFFER, this.buffer, Main.BUFFER_OFFSET);
	}
	
	/**
	 * Bind the framebuffer.
	 * 
	 * @return This
	 */
	Framebuffer bind() {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, this.buffer[Main.BUFFER_OFFSET]);
		//MyGLRenderer.checkGlError("glBindFramebuffer");
		return this;
	}

	/**
	 * Unbind the framebuffer by bind buffer zero.
	 * 
	 * @return This
	 */
	Framebuffer unbind() {
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, Main.BUFFER_NULL);
		return this;
	}	

	/**
	 * Check the status of the framebuffer. If an error occurred, an exception is thrown.
	 * 
	 * @return This
	 * @throws RuntimeException
	 */
	Framebuffer check() throws RuntimeException {
		final int result = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		// TODO: Check maybe GLES20.GL_FRAMEBUFFER_COMPLETE
		switch (result) {
		case GLES20.GL_FRAMEBUFFER_UNSUPPORTED:
			throw new RuntimeException("Framebuffer is unsupported");
			// break;
		case GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
			throw new RuntimeException("Framebuffer incomplete attachment");
			// break;
		case GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
			throw new RuntimeException("Framebuffer incomplete dimensions");
			// break;
		case GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
			throw new RuntimeException("Framebuffer incomplete missing attachment");
		}
		return this;
	}

	/**
	 * Attach the given texture object as a buffer to the currently bound
	 * framebuffer object and checks errors afterwards by calling
	 * {@link #check()}.
	 * 
	 * @param texture A texture
	 * @return This
	 */
	Framebuffer color(Texture texture) {
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, texture.target, texture.handle[Main.BUFFER_OFFSET], Main.LEVEL);
		//MyGLRenderer.checkGlError("glFramebufferTexture2D");
		try {
			this.check();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return this;
	}

//	void depth(MyBuffer buffer) {
//		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, buffer.id);
//		try {
//			this.check();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
}
