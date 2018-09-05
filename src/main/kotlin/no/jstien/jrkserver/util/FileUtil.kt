package no.jstien.jrkserver.util

import java.io.File

const val ROOT_TEMP_DIRECTORY = "/tmp/jrk_srv"

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

    if (!absPath.startsWith(ROOT_TEMP_DIRECTORY))
        throw RuntimeException("Only able to delete from '$ROOT_TEMP_DIRECTORY', $absPath seems fishy")
}
