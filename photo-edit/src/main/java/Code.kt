import com.jogamp.common.nio.Buffers
import com.jogamp.opengl.*
import com.jogamp.opengl.GL.GL_NO_ERROR
import com.jogamp.opengl.GL4.*
import com.jogamp.opengl.awt.GLCanvas
import com.jogamp.opengl.glu.GLU
import com.jogamp.opengl.util.FPSAnimator
import com.jogamp.opengl.util.texture.Texture
import com.jogamp.opengl.util.texture.TextureIO
import graphicslib3D.GLSLUtils
import mathlib3D.Matrix3D
import java.io.File
import java.lang.RuntimeException
import javax.swing.JFrame


private const val TITLE = "Grayscale"

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
    private var cameraZ = 3.0

    private var locX = 0.0
    private var locY = 0.0
    private var locZ = 0.0

    private val glslUtils = GLSLUtils()
    private lateinit var pMat: Matrix3D
    private var texture: Int = 0

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
        val photo = loadTexture("IMG_3620.JPG")
        texture = photo.textureObject
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

        // view matrix
        val vMat = Matrix3D()
        vMat.translate(-cameraX, -cameraY, -cameraZ)

        // model matrix
        val mMat = Matrix3D()
        mMat.translate(locX, locY, locZ)

        // view model matrix
        val mvMat = Matrix3D()
        mvMat.concatenate(vMat)
        mvMat.concatenate(mMat)

        val mvLoc = gl.glGetUniformLocation(renderingProgram, "mv_matrix")
        val projLoc = gl.glGetUniformLocation(renderingProgram, "proj_matrix")
        val timeloc = gl.glGetUniformLocation(renderingProgram, "time")

        gl.glUniformMatrix4fv(projLoc, 1, false,
                pMat.values.map { it.toFloat() }.toFloatArray(),
                0)

        gl.glUniformMatrix4fv(mvLoc, 1, false,
                mvMat.values.map { it.toFloat() }.toFloatArray(),
                0)

        gl.glProgramUniform1f(renderingProgram, timeloc, System.currentTimeMillis().toFloat())

        // Activate buffer with vertices
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0])
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 0, 0)
        gl.glEnableVertexAttribArray(0)

        // Activate buffer with texture coordinates
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[1])
        gl.glVertexAttribPointer(1, 2, GL.GL_FLOAT, false, 0, 0)
        gl.glEnableVertexAttribArray(1)

        gl.glActiveTexture(GL.GL_TEXTURE0)
        gl.glBindTexture(GL_TEXTURE_2D, texture)

        gl.glEnable(GL.GL_DEPTH_TEST)
        gl.glDepthFunc(GL.GL_LEQUAL)
        gl.glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
    }

    override fun reshape(p0: GLAutoDrawable?, p1: Int, p2: Int, p3: Int, p4: Int) {
        println("Gl reshape")
    }

    override fun dispose(p0: GLAutoDrawable?) {
        println("Gl dispose")
    }

    private fun setupVertices() {
        val gl = GLContext.getCurrentGL() as GL4

        val verticesPositions = floatArrayOf(
            -1f,  1f, 1f,
            -1f, -1f, 1f,
            1f,  1f, 1f,
            1f, -1f, 1f
        )

        val texturePositions = floatArrayOf(
                0f,  1f,
                0f, 0f,
                1f,  1f,
                1f, 0f
        )

        gl.glGenVertexArrays(vao.size, vao, 0)
        gl.glBindVertexArray(vao[0])
        gl.glGenBuffers(vbo.size, vbo, 0)

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[0])
        val verticesBuf = Buffers.newDirectFloatBuffer(verticesPositions)
        gl.glBufferData(GL.GL_ARRAY_BUFFER, verticesBuf.limit() * 4L, verticesBuf, GL.GL_STATIC_DRAW)

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo[1])
        val textureBuf = Buffers.newDirectFloatBuffer(texturePositions)
        gl.glBufferData(GL.GL_ARRAY_BUFFER, textureBuf.limit() * 4L, textureBuf, GL.GL_STATIC_DRAW)

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

    private fun loadTexture(path: String): Texture {
        try {
            return TextureIO.newTexture(File(path), false)
        } catch (e: java.io.IOException) {
            e.printStackTrace()
            throw RuntimeException("Texture $path not found")
        }
    }
}