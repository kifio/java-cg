import com.jogamp.opengl.*
import com.jogamp.opengl.GL.GL_NO_ERROR
import com.jogamp.opengl.GL.GL_POINTS
import com.jogamp.opengl.GL2ES2.*
import com.jogamp.opengl.GL4.GL_FRAGMENT_SHADER
import com.jogamp.opengl.GL4.GL_VERTEX_SHADER
import com.jogamp.opengl.awt.GLCanvas
import com.jogamp.opengl.glu.GLU
import com.jogamp.opengl.util.FPSAnimator
import com.sun.org.apache.xpath.internal.operations.Bool
import javax.swing.JFrame


private const val TITLE = "Chapter2 - program6"

fun main(args: Array<String>) {
    Code()
}

// Workaround for black screen on OS X
private fun getCanvas(): GLCanvas {
    return GLCanvas()
}

class Code : JFrame(TITLE), GLEventListener {

    private var renderingProgram = -1
    private val vao = intArrayOf(1)

    private val canvas = getCanvas()

    private var x = 1.0F
    private var y = 1.0F
    private var z = 1.0F
    private var rad = 0.0F
    private var inc = 0.01F

    init {
        setSize(400, 400)
        setLocation(200, 200)
        canvas.addGLEventListener(this)
        add(canvas)
        isVisible = true
        FPSAnimator(canvas, 50).start()
    }

    override fun reshape(p0: GLAutoDrawable?, p1: Int, p2: Int, p3: Int, p4: Int) {
        println("Gl reshape")
    }

    // Black screen on os x
    override fun display(p0: GLAutoDrawable?) {
        val gl = GLContext.getCurrentGL() as GL4
        gl.glUseProgram(renderingProgram);
        Common.clearBuffer(gl)
        gl.glDrawArrays(GL_TRIANGLES, 0, 3)

        x += inc
        y += inc
        z += inc
//        rad += inc

        if (x > 2F || x < 0.5F) {
            inc = -inc
        }

        gl.glProgramUniform1f(renderingProgram,
                gl.glGetUniformLocation(renderingProgram, "x"),
                x)

        gl.glProgramUniform1f(renderingProgram,
                gl.glGetUniformLocation(renderingProgram, "y"),
                y)

        gl.glProgramUniform1f(renderingProgram,
                gl.glGetUniformLocation(renderingProgram, "z"),
                z)

//        gl.glProgramUniform1f(renderingProgram,
//                gl.glGetUniformLocation(renderingProgram, "rad"),
//                rad)
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

        val vertexCompiled = IntArray(1)
        val fragmentCompiled = IntArray(1)
        val linked = IntArray(1)

        val vertexShader = gl.glCreateShader(GL_VERTEX_SHADER)
        gl.glShaderSource(vertexShader, vertexShaderSource.size, vertexShaderSource, null, 0)    // count - lines of code
        gl.glCompileShader(vertexShader)

        checkOpenGlError()
        gl.glGetShaderiv(vertexShader, GL_COMPILE_STATUS, vertexCompiled, 0)
        if (vertexCompiled[0] == 1) {
            println("Vertex compilation success")
        } else {
            println("Vertex compilation failed")
            printShaderLog(vertexShader)
        }

        val fragmentShader = gl.glCreateShader(GL_FRAGMENT_SHADER)
        gl.glShaderSource(fragmentShader, fragmentShaderSource.size, fragmentShaderSource, null, 0)    // count - lines of code
        gl.glCompileShader(fragmentShader)

        checkOpenGlError()
        gl.glGetShaderiv(fragmentShader, GL_COMPILE_STATUS, fragmentCompiled, 0)
        if (fragmentCompiled[0] == 1) {
            println("Fragment compilation success")
        } else {
            println("Fragment compilation failed")
            printShaderLog(fragmentShader)
        }

        val program = gl.glCreateProgram()
        gl.glAttachShader(program, vertexShader)
        gl.glAttachShader(program, fragmentShader)
        gl.glLinkProgram(program)

        checkOpenGlError()
        gl.glGetProgramiv(program, GL_LINK_STATUS, linked, 0)
        if (linked[0] == 1) {
            println("Linking success")
        } else {
            println("Linking failed")
            printProgramLog(program)
        }

        gl.glDeleteShader(vertexShader)
        gl.glDeleteShader(fragmentShader)

        return program
    }

    private fun printShaderLog(shader: Int) {
        val gl = GLContext.getCurrentGL() as GL4
        val len = IntArray(1)
        val chWrittn = IntArray(1)
        var log: ByteArray? = null

        gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, len, 0)
        if (len[0] > 0) {
            log = ByteArray(len[0])
            gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0)
            println("Shader info log: ")
            for (i in log) {
                print(i.toChar())
            }
        }
    }

    private fun printProgramLog(prog: Int) {
        val gl = GLContext.getCurrentGL() as GL4
        var len = IntArray(1)
        var chWrittn = IntArray(1)
        var log: ByteArray? = null

        gl.glGetProgramiv(prog, GL_INFO_LOG_LENGTH, len, 0)
        if (len[0] > 0) {
            log = ByteArray(len[0])
            gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0)
            println("Program info log: ")
            for (i in log) {
                print(i.toChar())
            }
        }
    }

    private fun checkOpenGlError(): Boolean {
        val gl = GLContext.getCurrentGL() as GL4
        var foundError = false
        val glu = GLU()
        var glErr = gl.glGetError()
        while (glErr != GL_NO_ERROR) {
            println("glError: ${glu.gluErrorString(glErr)}")
            foundError = true
            glErr = gl.glGetError()
        }
        return foundError
    }
}