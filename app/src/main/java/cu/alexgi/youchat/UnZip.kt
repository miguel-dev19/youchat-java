package cu.alexgi.youchat

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

@Throws(IOException::class)
fun unzip(zipFile: File?, targetDirectory: File?) {
    val zis = ZipInputStream(BufferedInputStream(FileInputStream(zipFile!!)))
    try {
        var ze: ZipEntry? = null
        var count: Int
        val buffer = ByteArray(16 * 1024)
        while (zis.nextEntry?.also { ze = it } != null) {
            val file = File(targetDirectory, ze!!.name)
            val dir = if (ze!!.isDirectory) file else file.parentFile
            if (!dir.isDirectory && !dir.mkdirs()) {
                throw FileNotFoundException("Failed to ensure directory: " + dir.absolutePath)
            }
            if (ze!!.isDirectory) continue
            val fout = FileOutputStream(file)
            try {
                while (zis.read(buffer).also { count = it } != -1) {
                    fout.write(buffer, 0, count)
                }
            } finally {
                fout.close()
            }
        }
    } finally {
        zis.close()
    }
}