package no.jstien.jrk.live.stream

import no.jstien.jrk.S3FileReference
import no.jstien.jrk.S3FileRepository

class StreamFileRepository(private val s3FileRepository: S3FileRepository) {
    private val references = ArrayList<S3FileReference>()

    fun downloadFile(s3Ref: S3FileReference): String {
        return s3FileRepository.downloadFile(s3Ref.key, "live")
    }

    fun popRandom(): S3FileReference {
        refreshEpisodesIfEmpty()
        val index = (0 until references.size).random()
        val ref = references[index]
        references.removeAt(index)
        return ref
    }

    private fun refreshEpisodesIfEmpty() {
        if (references.isEmpty()) {
            references.clear()
            references.addAll(s3FileRepository.getAllReferences())
        }
    }
}