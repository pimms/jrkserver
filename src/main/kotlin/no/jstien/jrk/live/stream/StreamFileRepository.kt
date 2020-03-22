package no.jstien.jrk.live.stream

import no.jstien.jrk.S3FileRepository

class StreamFileRepository(private val s3FileRepository: S3FileRepository) {
    private val s3Keys = ArrayList<String>()

    fun downloadFile(s3Key: String): String {
        return s3FileRepository.downloadFile(s3Key)
    }

    fun popRandomS3Key(): String {
        refreshEpisodesIfEmpty()
        val index = (0 until s3Keys.size).random()
        val key = s3Keys[index]
        s3Keys.removeAt(index)
        return key
    }

    private fun refreshEpisodesIfEmpty() {
        if (s3Keys.isEmpty()) {
            s3Keys.clear()
            s3Keys.addAll(s3FileRepository.getAllFileNames())
        }
    }
}