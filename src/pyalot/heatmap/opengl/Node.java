package pyalot.heatmap.opengl;


public class Node {

	/** Width of the viewport */
	private int width;
	/** Height of the viewport */
	private int height;
	/** A Texture */
	private Texture texture;
	/** A Framebuffer */
	private Framebuffer fbo;
	
	/**
	 * Create Node object. The constructor creates a {@link Texture} and {@link Framebuffer}.
	 * 
	 * @param width Width of the viewport
	 * @param height Height of the viewport
	 */
	public Node(final int width, final int height) {
		this.width = width;
		this.height = height;
		try {
// TODO:
			this.texture = new Texture(null, null).bind(Main.BIND_ZERO).setSize(this.width, this.height).nearest().clampToEdge();
//			String floatExt =  this.gl.getFloatExtension({
//				require: ['renderable']
//			});
//			this.texture = new Texture(floatExt.type).bind(0).setSize(this.width, this.height).nearest().clampToEdge();
			this.fbo = new Framebuffer().bind().color(this.texture).unbind();
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Bind the {@link Framebuffer} object by call {@link Framebuffer#bind()}.
	 */
	public void use() {
		this.fbo.bind();
	}	
	
	/**
	 * Bind the {@link Texture} object by call {@link Texture#bind(int)} .
	 * 
	 * @param unit Texture unit
	 */
	public void bind(int unit) {
		try {
			this.texture.bind(unit);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	

	/**
	 * Unbind the {@link Framebuffer} object by call {@link Framebuffer#unbind()}.
	 */
	public void end() {
		this.fbo.unbind();
	}

	/**
	 * Set a new size.
	 * 
	 * @param width Width of the viewport
	 * @param height Height of the viewport
	 */
	public void resize(final int width, final int height) {
		this.width = width;
		this.height = height;
		try {
			this.texture.bind(Main.BIND_ZERO).setSize(this.width, this.height);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
