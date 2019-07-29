package ru.vonabe.audiostreaming

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.widget.Toast

class CustomMicrophoneRead(private val context: Context) {

    private var record: AudioRecord? = null

    init {
        recordAudioCreate()
    }

    fun recordAudioCreate() {
        if (record != null) return

        val buffersize = AudioRecord.getMinBufferSize(
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        record = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100, AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT, buffersize
        )
        record?.startRecording()

        Toast.makeText(context, "buffer-size: " + buffersize + " --- audio session " + record?.audioSessionId, Toast.LENGTH_SHORT).show()
        println(" *** audio session " + record?.audioSessionId)
    }

    fun read(): ByteArray {
        var buffer = ByteArray(2048)
        val read = record?.read(buffer, 0, buffer.size)
        return buffer
    }


}