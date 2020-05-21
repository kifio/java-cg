import com.jogamp.common.nio.Buffers
import com.jogamp.opengl.*
import com.jogamp.opengl.GL4.GL_COLOR
import com.jogamp.opengl.awt.GLCanvas
import javax.swing.JFrame


private const val TITLE = "Chapter2 - program1"

fun main(args: Array<String>) {
    Code()
}

// Workaround for black screen on OS X
private fun getCanvas(): GLCanvas {
    val glp = GLProfile.getMaxProgrammableCore(true)
    return GLCanvas(GLCapabilities(glp))
}

class Code : JFrame(TITLE), GLEventListener {

    private val canvas = getCanvas()

    init {
        setSize(600, 400)
        setLocation(200, 200)
        canvas.addGLEventListener(this)
        add(canvas)
        isVisible = true
    }

    override fun reshape(p0: GLAutoDrawable?, p1: Int, p2: Int, p3: Int, p4: Int) {
        println("Gl reshape")
    }

    override fun display(p0: GLAutoDrawable?) {
        println("Gl display")
        val gl = GLContext.getCurrentGL() as GL4

        gl.glClearBufferfv(
            GL_COLOR, 0,
            Buffers.newDirectFloatBuffer(
                floatArrayOf(1.0f, 1.0f, 0.0f, 1.0f)
            )
        )
    }

    override fun init(p0: GLAutoDrawable?) {
        println("Gl init")
    }

    override fun dispose(p0: GLAutoDrawable?) {
        println("Gl dispose")
    }

}