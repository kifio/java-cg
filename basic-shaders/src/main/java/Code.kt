import com.jogamp.opengl.*
import com.jogamp.opengl.GL.GL_POINTS
import com.jogamp.opengl.GL4.GL_FRAGMENT_SHADER
import com.jogamp.opengl.GL4.GL_VERTEX_SHADER
import com.jogamp.opengl.awt.GLCanvas
import javax.swing.JFrame

private const val TITLE = "Chapter2 - program3"

fun main(args: Array<String>) {
    Code()
}

// Workaround for black screen on OS X
private fun getCanvas(): GLCanvas {
    val glp = GLProfile.getMaxProgrammableCore(true)
    return GLCanvas(GLCapabilities(glp))
}

class Code : JFrame(TITLE), GLEventListener {

    private var renderingProgram = -1
    private val vao = intArrayOf(1)

    private val canvas = getCanvas()

    init {
        setSize(400, 400)
        setLocation(200, 200)
        canvas.addGLEventListener(this)
        add(canvas)
        isVisible = true
    }

    override fun reshape(p0: GLAutoDrawable?, p1: Int, p2: Int, p3: Int, p4: Int) {
        println("Gl reshape")
    }

    // Black screen on os x
    override fun display(p0: GLAutoDrawable?) {
        println("Gl display")
        val gl = GLContext.getCurrentGL() as GL4
        gl.glUseProgram(renderingProgram);
	    gl.glPointSize(30f);
        Common.clearBuffer(gl)
        gl.glDrawArrays(GL_POINTS, 0, 1);
    }

    override fun init(p0: GLAutoDrawable?) {
        println("Gl init")
        val gl = GLContext.getCurrentGL() as GL4
        renderingProgram = createShaderProgram()
        gl.glGenVertexArrays(vao.size, vao, 0);
        gl.glBindVertexArray(vao[0]);
    }

    override fun dispose(p0: GLAutoDrawable?) {
        println("Gl dispose")
    }

    private fun createShaderProgram(): Int {
        val gl = GLContext.getCurrentGL() as GL4

        val vertexShaderSource = Common.readShaderSource("vert.shader")
        val fragmentShaderSource = Common.readShaderSource("frag.shader")

        val vertexShader = gl.glCreateShader(GL_VERTEX_SHADER)
        gl.glShaderSource(vertexShader, vertexShaderSource.size, vertexShaderSource, null, 0)    // count - lines of code
        gl.glCompileShader(vertexShader)

        val fragmentShader = gl.glCreateShader(GL_FRAGMENT_SHADER)
        gl.glShaderSource(fragmentShader, fragmentShaderSource.size, fragmentShaderSource, null, 0)    // count - lines of code
        gl.glCompileShader(fragmentShader)

        val program = gl.glCreateProgram()
        gl.glAttachShader(program, vertexShader)
        gl.glAttachShader(program, fragmentShader)
        gl.glLinkProgram(program)

        gl.glDeleteShader(vertexShader)
        gl.glDeleteShader(fragmentShader)

        return program
    }
}
