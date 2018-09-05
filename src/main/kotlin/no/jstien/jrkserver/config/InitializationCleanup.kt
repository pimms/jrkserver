package no.jstien.jrkserver.config

import no.jstien.jrkserver.util.FileUtil
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import javax.annotation.PostConstruct


@Component
private class InitializationCleanup @Autowired constructor(private val fileUtil: FileUtil) {
    @PostConstruct
    fun deleteStorageDirectory() {
        val dir = fileUtil.rootTempDirectory
        LOG.info("Deleting temp-root directory $dir")
        fileUtil.recursiveDelete(File(dir))
        File(dir).mkdirs()
    }

    companion object {
        private val LOG = LogManager.getLogger()
    }
}