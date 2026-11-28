package com.induztek.protocolos.data.repository

import com.induztek.protocolos.data.remote.ImageUploadResponse
import com.induztek.protocolos.data.remote.MediaApi
import com.induztek.protocolos.data.remote.toUserMessage
import com.induztek.protocolos.domain.errores.AppException
import com.induztek.protocolos.domain.errores.CodigoError
import com.induztek.protocolos.testutil.assertCodigo
import com.induztek.protocolos.testutil.assertHttp
import com.induztek.protocolos.testutil.httpError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.File
import java.io.RandomAccessFile
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import retrofit2.Response

/**
 * Subida de imágenes al backend (MediaRepository).
 *
 * Criterios de aceptación:
 * - CA-MED-01: una URL remota o una ruta vacía no se sube de nuevo.
 * - CA-MED-02: solo se suben archivos del almacenamiento privado de la app [MED-001].
 * - CA-MED-03: una imagen de más de 10 MB se rechaza antes de subirla [MED-002].
 * - CA-MED-04: una imagen válida se sube, se devuelve su URL y se borra la copia local.
 * - CA-MED-05: los errores del backend al subir se muestran con su código [HTTP-415].
 */
class MediaRepositoryTest {
    @get:Rule
    val carpetas = TemporaryFolder()

    private val api = mockk<MediaApi>()

    private fun repositorio(appDir: File) = MediaRepository(api, appDir)

    private fun codigoDe(bloque: suspend () -> Unit): CodigoError? = try {
        kotlinx.coroutines.runBlocking { bloque() }
        null
    } catch (e: AppException) {
        e.error
    }

    // CA-MED-01: una URL ya subida o una ruta vacía se devuelven tal cual, sin llamar al backend.
    @Test
    fun urlRemotaNoSeSube() = runTest {
        val repo = repositorio(carpetas.newFolder("app"))
        assertEquals("https://cdn/x.jpg", repo.uploadIfLocal("https://cdn/x.jpg"))
        assertNull(repo.uploadIfLocal(null))
        coVerify(exactly = 0) { api.subirImagen(any()) }
    }

    // CA-MED-02 [MED-001]: un archivo fuera de filesDir no se sube.
    @Test
    fun archivoFueraDelAlmacenamientoPrivado() {
        val repo = repositorio(carpetas.newFolder("app"))
        val externo = carpetas.newFile("externo.jpg")
        assertEquals(CodigoError.MED_001, codigoDe { repo.uploadIfLocal(externo.absolutePath) })
    }

    // CA-MED-03 [MED-002]: más de 10 MB se rechaza localmente.
    @Test
    fun imagenDemasiadoGrande() {
        val appDir = carpetas.newFolder("app")
        val grande = File(appDir, "grande.jpg")
        RandomAccessFile(grande, "rw").use { it.setLength(10L * 1024 * 1024 + 1) }
        val repo = repositorio(appDir)
        assertEquals(CodigoError.MED_002, codigoDe { repo.uploadIfLocal(grande.absolutePath) })
        assertCodigo(CodigoError.MED_002, AppException(CodigoError.MED_002).toUserMessage())
    }

    // CA-MED-04: una imagen válida se sube, devuelve su URL y se borra la copia local.
    @Test
    fun imagenValidaSeSubeYSeBorra() = runTest {
        val appDir = carpetas.newFolder("app")
        val foto = File(appDir, "foto.jpg").apply { writeBytes(byteArrayOf(1, 2, 3)) }
        coEvery { api.subirImagen(any()) } returns Response.success(ImageUploadResponse("foto.jpg", "https://api/upload/foto.jpg"))
        assertEquals("https://api/upload/foto.jpg", repositorio(appDir).uploadIfLocal(foto.absolutePath))
        assertFalse(foto.exists())
    }

    // CA-MED-05 [HTTP-415]: el backend rechaza el formato del archivo.
    @Test
    fun formatoRechazadoPorElBackend() {
        val appDir = carpetas.newFolder("app")
        val foto = File(appDir, "foto.gif").apply { writeBytes(byteArrayOf(1)) }
        coEvery { api.subirImagen(any()) } throws httpError(415, "UNSUPPORTED_MEDIA_TYPE", "Only JPEG or PNG")
        val mensaje = try {
            kotlinx.coroutines.runBlocking { repositorio(appDir).uploadIfLocal(foto.absolutePath) }
            null
        } catch (e: Exception) {
            e.toUserMessage()
        }
        assertHttp(415, mensaje)
    }
}
