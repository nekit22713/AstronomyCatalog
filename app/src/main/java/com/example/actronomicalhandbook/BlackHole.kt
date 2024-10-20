import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import com.example.actronomicalhandbook.ShaderCompiler
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class BlackHole(context: Context, private val textureResId: Int) {
    private var vertexBuffer: FloatBuffer
    private var textureBuffer: FloatBuffer
    private var textureId: Int

    private val vertices = floatArrayOf(
        -1f,  1f, 0f,  // Верхний левый
        -1f, -1f, 0f,  // Нижний левый
        1f, -1f, 0f,  // Нижний правый
        1f,  1f, 0f   // Верхний правый
    )

    private val textureCoords = floatArrayOf(
        0f, 0f,
        0f, 1f,
        1f, 1f,
        1f, 0f
    )

    private var shaderProgram: ShaderCompiler

    init {
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertices)
                position(0)
            }
        }

        textureBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(textureCoords)
                position(0)
            }
        }

        val vertexShaderCode = """
            uniform mat4 u_MVPMatrix;
            attribute vec4 a_Position;
            attribute vec2 a_TexCoord;
            
            varying vec2 v_TexCoord;
            
            void main() {
                v_TexCoord = a_TexCoord;
            
                gl_Position = u_MVPMatrix * a_Position;
            }
        """.trimIndent()

        val fragmentShaderCode = """
            precision mediump float;
           
            uniform sampler2D u_Texture;
            varying vec2 v_TexCoord;
            
            void main() {
                vec4 textureColor = texture2D(u_Texture, v_TexCoord);
            
                if (textureColor.a < 0.1) {
                    discard;
                }
                gl_FragColor = textureColor;
            }
        """.trimIndent()

        shaderProgram = ShaderCompiler(vertexShaderCode, fragmentShaderCode)
        textureId = loadTexture(context, textureResId)
    }

    private fun loadTexture(context: Context, resId: Int): Int {
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val options = BitmapFactory.Options()
            options.inScaled = false
            val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            bitmap.recycle()
        }

        return textureHandle[0]
    }

    fun draw(mvpMatrix: FloatArray, tr: Float) {
        val modelMatrix = FloatArray(16)
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, tr, -tr+1f, 10f)

        val finalMatrix = FloatArray(16)
        Matrix.multiplyMM(finalMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
        shaderProgram.use()

        val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_Position")
        val texCoordHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_TexCoord")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_MVPMatrix")

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, finalMatrix, 0)

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }
}

//import android.content.Context
//import android.graphics.BitmapFactory
//import android.opengl.GLES20
//import android.opengl.GLUtils
//import android.opengl.Matrix
//import com.example.actronomicalhandbook.ShaderCompiler
//import java.nio.ByteBuffer
//import java.nio.ByteOrder
//import java.nio.FloatBuffer
//
//class BlackHole(context: Context, private val textureResId: Int) {
//    private var vertexBuffer: FloatBuffer
//    private var textureBuffer: FloatBuffer
//    private var textureId: Int
//
//    private val vertices = floatArrayOf(
//        -1f,  1f, 0f,  // Верхний левый
//        -1f, -1f, 0f,  // Нижний левый
//        1f, -1f, 0f,  // Нижний правый
//        1f,  1f, 0f   // Верхний правый
//    )
//
//    private val textureCoords = floatArrayOf(
//        0f, 0f,
//        0f, 1f,
//        1f, 1f,
//        1f, 0f
//    )
//
//    private var shaderProgram: ShaderCompiler
//
//    init {
//        // Буфер вершин
//        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).run {
//            order(ByteOrder.nativeOrder())
//            asFloatBuffer().apply {
//                put(vertices)
//                position(0)
//            }
//        }
//
//        // Буфер текстурных координат
//        textureBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4).run {
//            order(ByteOrder.nativeOrder())
//            asFloatBuffer().apply {
//                put(textureCoords)
//                position(0)
//            }
//        }
//
//        // Компиляция шейдеров
//        val vertexShaderCode = """
//           uniform mat4 u_MVPMatrix;
//attribute vec4 a_Position;
//attribute vec2 a_TexCoord;
//
//varying vec2 v_TexCoord;
//
//void main() {
//    // Передаем текстурные координаты
//    v_TexCoord = a_TexCoord;
//
//    // Вычисляем итоговую позицию
//    gl_Position = u_MVPMatrix * a_Position;
//}
//        """.trimIndent()
//
//        val fragmentShaderCode = """
//precision mediump float;
//
//uniform vec2 u_BlackHoleCenter;         // Центр черной дыры
//uniform float u_BlackHoleRadius;        // Радиус черной дыры
//uniform vec2 u_ScreenResolution;        // Разрешение экрана
//uniform sampler2D u_Texture;            // Текстура сцены (например, фона или космоса)
//
//varying vec2 v_TexCoord;
//
//void main() {
//    // Преобразуем координаты фрагмента в экранные координаты
//    vec2 screenPos = gl_FragCoord.xy / u_ScreenResolution;
//
//    // Вектор от центра черной дыры до текущего фрагмента
//    vec2 offset = screenPos - u_BlackHoleCenter;
//
//    // Дистанция от фрагмента до центра черной дыры
//    float distance = length(offset);
//
//    // Если фрагмент находится внутри радиуса черной дыры, делаем его черным
//    if (distance < u_BlackHoleRadius) {
//        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);  // Черная дыра
//        return;
//    }
//
//    // Эффект гравитационного искажения (линзирования)
//    float distortionFactor = 1.0 / (distance * distance + 0.1);  // Чем ближе, тем сильнее искажение
//
//    // Смещаем текстурные координаты для создания искажения
//    vec2 distortedTexCoord = v_TexCoord + offset * distortionFactor * 0.05;
//
//    // Получаем цвет из текстуры с учетом искажения
//    vec4 sceneColor = texture2D(u_Texture, distortedTexCoord);
//
//    // Отрисовываем искаженный фрагмент
//    gl_FragColor = sceneColor;
//}
//
//        """.trimIndent()
//
//        shaderProgram = ShaderCompiler(vertexShaderCode, fragmentShaderCode)
//
//        // Загрузка текстуры черной дыры
//        textureId = loadTexture(context, textureResId)
//    }
//
//    private fun loadTexture(context: Context, resId: Int): Int {
//        val textureHandle = IntArray(1)
//        GLES20.glGenTextures(1, textureHandle, 0)
//
//        if (textureHandle[0] != 0) {
//            val options = BitmapFactory.Options()
//            options.inScaled = false
//            val bitmap = BitmapFactory.decodeResource(context.resources, resId, options)
//
//            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
//            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
//
//            // Передаем данные с текстурой, включая альфа-канал
//            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
//
//            bitmap.recycle()
//        }
//
//        return textureHandle[0]
//    }
//
//
//    fun draw(mvpMatrix: FloatArray, tr: Float, screenWidth: Float, screenHeight: Float) {
//        // Включение режима прозрачности
//        GLES20.glEnable(GLES20.GL_BLEND)
//        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
//
//        // Матрица модели для черной дыры (изменяем по оси X для движения)
//        val modelMatrix = FloatArray(16)
//        Matrix.setIdentityM(modelMatrix, 0)
//        Matrix.translateM(modelMatrix, 0, tr, -tr + 1f, 9f)  // Черная дыра на заднем плане
//
//        val finalMatrix = FloatArray(16)
//        Matrix.multiplyMM(finalMatrix, 0, mvpMatrix, 0, modelMatrix, 0)
//
//        shaderProgram.use()
//
//        val positionHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_Position")
//        val texCoordHandle = GLES20.glGetAttribLocation(shaderProgram.programId, "a_TexCoord")
//        val mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_MVPMatrix")
//
//        // Устанавливаем параметры черной дыры (центр, радиус)
//        val blackHoleCenterHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_BlackHoleCenter")
//        val blackHoleRadiusHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_BlackHoleRadius")
//        val screenResolutionHandle = GLES20.glGetUniformLocation(shaderProgram.programId, "u_ScreenResolution")
//
//        GLES20.glEnableVertexAttribArray(positionHandle)
//        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
//
//        GLES20.glEnableVertexAttribArray(texCoordHandle)
//        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)
//
//        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, finalMatrix, 0)
//
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
//
//        // Передача позиции черной дыры в шейдер
//        val blackHoleCenter = floatArrayOf(screenWidth / 2f, screenHeight / 2f)
//        GLES20.glUniform2fv(blackHoleCenterHandle, 1, blackHoleCenter, 0)
//
//        // Радиус черной дыры
//        GLES20.glUniform1f(blackHoleRadiusHandle, 0.2f)
//
//        // Разрешение экрана
//        val screenResolution = floatArrayOf(screenWidth, screenHeight)
//        GLES20.glUniform2fv(screenResolutionHandle, 1, screenResolution, 0)
//
//        // Рисуем черную дыру как квадрат с прозрачным фоном
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)
//
//        // Отключаем прозрачность после отрисовки
//        GLES20.glDisableVertexAttribArray(positionHandle)
//        GLES20.glDisableVertexAttribArray(texCoordHandle)
//
//        GLES20.glDisable(GLES20.GL_BLEND)  // Отключение прозрачности после отрисовки
//    }
//
//
//}
