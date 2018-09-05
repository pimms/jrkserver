package no.jstien.jrkserver.util

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

internal class ProcessExecutor {
    var exitCode = Int.MIN_VALUE
        private set
    var stdout = ArrayList<String>()
        private set
    var stderr = ArrayList<String>()
        private set

    /**
     * @command must be a single string containing both the command (optionally the path to it) and
     * all the arguments. E.g., "ls -lh".
     */
    fun execute(command: String) {
        exitCode = Int.MIN_VALUE
        stdout.clear()
        stderr.clear()

        try {
            val p = Runtime.getRuntime().exec(command)

            val tOut = Thread { readStdout(p) }
            val tErr = Thread { readStderr(p) }
            tOut.start()
            tErr.start()

            exitCode = p.waitFor()

            tOut.join()
            tErr.join()
        } catch (t: Throwable) {
            throw RuntimeException("Command '$command' failed", t)
        }
    }

    private fun readStdout(proc: Process) {
        readStream(proc.inputStream, stdout)
    }

    private fun readStderr(proc: Process) {
        readStream(proc.errorStream, stderr)
    }

    private fun readStream(stream: InputStream, dest: ArrayList<String>) {
        val bufferedReader = BufferedReader(InputStreamReader(stream))
        var line: String? = bufferedReader.readLine()
        while (line != null) {
            dest.add(line)
            line = bufferedReader.readLine()
        }
        bufferedReader.close()
        stream.close()
    }

}
