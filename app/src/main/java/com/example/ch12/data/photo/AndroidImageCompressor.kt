package com.example.ch12.data.photo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import com.example.ch12.util.PROFILE_PHOTO_QUALITY
import com.example.ch12.util.PROFILE_PHOTO_SIZE
import com.example.ch12.util.calculateInSampleSize
import com.example.ch12.util.centerSquare
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

// Foto 4000x3000 -> JPEG persegi 400x400, sekitar 30-60 KB
class AndroidImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context
) : ImageCompressor {

    // Decode dan kompres JPEG adalah kerja CPU + I/O: jangan di main thread
    override suspend fun compress(uri: String): CompressedImage = withContext(Dispatchers.Default) {
        val source = Uri.parse(uri)

        // 1. Baca ukuran asli (bounds) tanpa memuat piksel ke memori
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        open(source).use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw ImageDecodeException("Berkas yang dipilih bukan gambar.")
        }

        // 2. Dekode tahap kedua dengan inSampleSize yang dihitung
        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, PROFILE_PHOTO_SIZE)
        }
        val sampled = open(source).use { stream ->
            BitmapFactory.decodeStream(stream, null, decodeOptions)
                ?: throw ImageDecodeException("Gagal mendecode gambar.")
        }

        // T6.2: Ambil koordinat dan ukuran persegi di tengah menggunakan centerSquare(...)
        val crop = centerSquare(sampled.width, sampled.height)

        // 3. Matriks untuk skala dan rotasi (mengatasi orientasi EXIF kamera)
        val matrix = Matrix().apply {
            val scale = PROFILE_PHOTO_SIZE.toFloat() / crop.size
            postScale(scale, scale)
            postRotate(readRotation(source).toFloat())
        }

        // T6.2: Potong bitmap menggunakan area centerSquare (crop.left, crop.top, crop.size, crop.size)
        val resultBitmap = Bitmap.createBitmap(
            sampled,
            crop.left,
            crop.top,
            crop.size,
            crop.size,
            matrix,
            true
        )
        if (resultBitmap !== sampled) {
            sampled.recycle()
        }

        // 4. Kompres bitmap ke ByteArrayOutputStream dengan format JPEG
        val bytes = ByteArrayOutputStream().use { out ->
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, PROFILE_PHOTO_QUALITY, out)
            out.toByteArray()
        }

        // 5. Bersihkan memori bitmap dan kembalikan objek CompressedImage
        val finalWidth = resultBitmap.width
        val finalHeight = resultBitmap.height
        resultBitmap.recycle()

        CompressedImage(
            bytes = bytes,
            originalWidth = bounds.outWidth,
            originalHeight = bounds.outHeight,
            width = finalWidth,
            height = finalHeight
        )
    }

    // content:// (Photo Picker) maupun file:// (CameraX) dibuka lewat ContentResolver
    @Suppress("unused")
    private fun open(uri: Uri): InputStream =
        context.contentResolver.openInputStream(uri)
            ?: throw ImageDecodeException("Gambar tidak dapat dibuka.")

    // Kamera menyimpan piksel apa adanya + tag orientasi. BitmapFactory
    // mengabaikan tag itu, sehingga tanpa ini foto potret bisa tampil miring.
    @Suppress("unused")
    private fun readRotation(uri: Uri): Int = runCatching {
        open(uri).use { ExifInterface(it).rotationDegrees }
    }.getOrDefault(0)
}