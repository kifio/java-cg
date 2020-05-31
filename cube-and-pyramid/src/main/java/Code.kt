import com.jogamp.common.nio.Buffers
import com.jogamp.opengl.*
import com.jogamp.opengl.GL.GL_NO_ERROR
import com.jogamp.opengl.GL4.*
import com.jogamp.opengl.awt.GLCanvas
import com.jogamp.opengl.glu.GLU
import com.jogamp.opengl.util.FPSAnimator
import graphicslib3D.GLSLUtils
import mathlib3D.Matrix3D
import javax.swing.JFrame


private const val TITLE = "Chapter4 - program1"

fun main(args: Array<String>) {
    Code()
}

class Code : JFrame(TITLE), GLEventListener {

    private var renderingProgram = -1
    private val canvas = GLCanvas()

    private val vao = IntArray(1)
    private val vbo = IntArray(2)

    private var cameraX = 0.0
    private var cameraY = 0.0
    private var cameraZ = 10.0

    private var cubeLocX = 0.0
    private var cubeLocY = -2.0
    private var cubeLocZ = 0.0

    private var pyrLocX = 3.0
    private var pyrLocY = 0.0
    private var pyrLocZ = 1.0

    private val glslUtils = GLSLUtils()
    private lateinit var pMat: Matrix3D

    init {
        setSize(600, 600)
        canvas.addGLEventListener(this)
        add(canvas)
        isVisible = true
        FPSAnimator(canvas, 50).start()
    }

    override fun init(p0: GLAutoDrawable?) {
        val gl = GLContext.getCurrentGL() as GL4
        renderingProgram = createShaderProgram()
        setupVertices()
        // Aspect ratio matches screen window
        val aspect = (canvas.width.toFloat() / canvas.height.toFloat())
        pMat = Common.perspective(60f, aspect, 0.1f, 1000f)
        gl.glGenVertexArrays(vao.size, vao, 0);
        gl.glBindVertexArray(vao[0]);
    }

    // Black screen on os x
    override fun display(p0: GLAutoDrawable?) {
        val gl = GLContext.getCurrentGL() as GL4
        gl.glClear(GL.GL_DEPTH_BUFFER_BIT)
        gl.glUseProgram(renderingProgram)

        val bkg = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
        val bkgBuffer = Buffers.newDirectFloatBuffer(bkg)
        gl.glClearBufferfv(GL4.GL_COLOR, 0, bkgBuffer)

        drawCube()
        drawPyramid()
    }

    private fun drawCube() {
        val gl = GLContext.getCurrentGL() as GL4

        // view matrix
        val vMat = Matrix3D()
        vMat.translate(-cameraX, -cameraY, -cameraZ)

        // model matrix
        val mMat = Matrix3D()
        mMat.translate(cubeLocX, cubeLocY, cubeLocZ)

        // view model matrix
        val mvMat = Matrix3D()
        mvMat.concatenate(vMat)
        mvMat.concatenate(mMat)

        val mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix")
        val projLoc = gl.glGetUniformLocation(renderingProgram, "proj_matrix")

        gl.glUniformMatrix4fv(projLoc, 1, false,
                pMat.values.map { it.toFloat() }.toFloatArray(),
                0)

        gl.glUniformMatrix4fv(mvLoc, 1, false,
                mvMat.values.map { it.toFloat() }.toFloatArray(),
                0)

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0])
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0)
        gl.glEnableVertexAttribArray(0)

        gl.glEnable(GL.GL_DEPTH_TEST)
        gl.glDepthFunc(GL.GL_LEQUAL)
        gl.glDrawArrays(GL_TRIANGLES, 0, 36)
    }

    private fun drawPyramid() {
        val gl = GLContext.getCurrentGL() as GL4

        // view matrix
        val vMat = Matrix3D()
        vMat.translate(-cameraX, -cameraY, -cameraZ)

        // model matrix
        val mMat = Matrix3D()
        mMat.translate(pyrLocX, pyrLocY, pyrLocZ)

        // view model matrix
        val mvMat = Matrix3D()
        mvMat.concatenate(vMat)
        mvMat.concatenate(mMat)

        val mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix")
        val projLoc = gl.glGetUniformLocation(renderingProgram, "proj_matrix")

        gl.glUniformMatrix4fv(projLoc, 1, false,
                pMat.values.map { it.toFloat() }.toFloatArray(),
                0)

        gl.glUniformMatrix4fv(mvLoc, 1, false,
                mvMat.values.map { it.toFloat() }.toFloatArray(),
                0)

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[1])
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0)
        gl.glEnableVertexAttribArray(0)

        gl.glEnable(GL.GL_DEPTH_TEST)
        gl.glDepthFunc(GL.GL_LEQUAL)
        gl.glDrawArrays(GL_TRIANGLES, 0, 18)
    }

    override fun reshape(p0: GLAutoDrawable?, p1: Int, p2: Int, p3: Int, p4: Int) {
        println("Gl reshape")
    }

    override fun dispose(p0: GLAutoDrawable?) {
        println("Gl dispose")
    }

    private fun setupVertices() {
        val gl = GLContext.getCurrentGL() as GL4

        // 36 vertices of the 12 triangles making up a 2 x 2 x 2 cube centered at the origin
        val cubePositions = floatArrayOf(
            -1.0f, 1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f,-1.0f, -1.0f,

            1.0f, 1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f, 1.0f,

            1.0f, 1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, 1.0f, -1.0f,

            1.0f, -1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, -1.0f,-1.0f, 1.0f,

            -1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f, -1.0f,

            -1.0f, 1.0f, 1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f, 1.0f,
            -1.0f, -1.0f, 1.0f, 1.0f,

            -1.0f, -1.0f, 1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f, -1.0f,
            -1.0f, 1.0f,-1.0f, -1.0f,

            -1.0f, -1.0f, -1.0f, -1.0f,
            -1.0f, 1.0f, -1.0f, 1.0f,
            -1.0f, 1.0f, 1.0f, -1.0f,

            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f,1.0f, -1.0f, 1.0f,
            1.0f, -1.0f, 1.0f, -1.0f
        )

        val pyramidPositions = floatArrayOf(
                -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
                1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
                1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 0.0f, 1.0f, 0.0f,
                -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f
        )

        gl.glGenVertexArrays(vao.size, vao, 0)
        gl.glBindVertexArray(vao[0])
        gl.glGenBuffers(vbo.size, vbo, 0)

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0])
        val cubeBuf = Buffers.newDirectFloatBuffer(cubePositions)
        gl.glBufferData(GL.GL_ARRAY_BUFFER, cubeBuf.limit() * 4L, cubeBuf, GL.GL_STATIC_DRAW)

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[1])
        val pyramidBuf = Buffers.newDirectFloatBuffer(pyramidPositions)
        gl.glBufferData(GL.GL_ARRAY_BUFFER, pyramidBuf.limit() * 4L, pyramidBuf, GL.GL_STATIC_DRAW)
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