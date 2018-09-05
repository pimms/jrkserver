package no.jstien.jrkserver.util

import java.io.File

class FileUtil(val rootTempDirectory: String) {
    fun recursiveDelete(file: File) {
        verifyLackOfMonkeyBusiness(file)

        if (file.isDirectory) {
            file.listFiles().forEach { f ->
                recursiveDelete(f)
            }
        }

        file.delete()
    }

    private fun verifyLackOfMonkeyBusiness(file: File) {
        val absPath = file.absolutePath

        // Well this should never happen...? :)
        if (absPath.contains(".."))
            throw RuntimeException("PATH TRAVERSAL")

        if (!absPath.startsWith(rootTempDirectory))
            throw RuntimeException("Only able to delete from '${rootTempDirectory}', $absPath seems fishy")
    }
}