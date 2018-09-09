package no.jstien.jrkserver.config

import no.jstien.jrkserver.util.ROOT_TEMP_DIRECTORY
import no.jstien.jrkserver.util.recursiveDelete
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Component
import java.io.File
import javax.annotation.PostConstruct

@Component
internal class InitializationCleanup {
    @PostConstruct
    fun deleteStorageDirectory() {
        LOG.info("Deleting temp-root directory $ROOT_TEMP_DIRECTORY")
        recursiveDelete(File(ROOT_TEMP_DIRECTORY))
        File(ROOT_TEMP_DIRECTORY).mkdirs()
    }

    companion object {
        private val LOG = LogManager.getLogger()
    }
}