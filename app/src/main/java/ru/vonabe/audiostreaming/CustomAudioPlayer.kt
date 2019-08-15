package ru.vonabe.audiostreaming

import android.app.AlertDialog
import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.AsyncTask
import android.util.Log
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.TimeUnit

class CustomAudioPlayer(val context: Context) : Runnable {

    inner class Task : AsyncTask<Void, Double, Void>() {

        private var percent = 0.0
        override fun onPreExecute() {
            super.onPreExecute()
            alertDialog.show()
        }

        override fun onProgressUpdate(vararg values: Double?) {
            super.onProgressUpdate(*values)
            txt.text = "Buffering ${"%.2f".format(percent)}%"
            if (percent >= 100) {
                alertDialog.hide()
                cancel(false)
            }
            if (percent.isNaN()) {
                cancel(false)
            }
        }

        override fun doInBackground(vararg params: Void?): Void? {
            while (percent < 100) {
                percent = 100 / (delay / timer)
                publishProgress(percent)
                TimeUnit.MILLISECONDS.sleep(100)
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
        }

        override fun onCancelled(result: Void?) {
            super.onCancelled(result)
            alertDialog.hide()
        }

    }

    private val LOG_TAG = "CustomAudioPlayer"
    private var timer = 0.0
    private var delay: Int = 0
    private var buffersStack = ConcurrentLinkedDeque<ByteArray>()
    private var track: AudioTrack? = null

    private var progress: ProgressBar? = null
    private var txt: TextView
    private val alertDialog: AlertDialog

    init {
        this.progress = ProgressBar(context)
        var builder = AlertDialog.Builder(context)
        this.alertDialog = builder.create()

        this.txt = TextView(context)
        this.txt.text = "Buffering"
        this.txt.gravity = Gravity.CENTER
        var core = LinearLayout(context)
        core.gravity = Gravity.CENTER
        core.orientation = LinearLayout.VERTICAL
        core.addView(this.progress)
        core.addView(this.txt)

        this.alertDialog.setView(core)
//        this.alertDialog.show()
    }

    override fun run() {
        while (true) {
            if (timer >= delay) {
                if (!buffersStack.isEmpty()) {
                    val data = buffersStack.pop()
                    data?.let {
                        track?.write(data, 0, data.size)
                        track?.flush()
//                    Log.d(LOG_TAG, "write data ${Arrays.toString(data)}")
                    }
                } else {
//                    Log.d(LOG_TAG, "Buffer Empty")
                }
            } else {
//                Log.d(LOG_TAG, "Don't Buffering Size $timer < $delay")
            }
        }
    }

    fun getDelay():Int{
        return delay
    }

    fun setDelay(delay: Int) {
        this.delay = delay
        this.timer = 0.0
        this.track?.stop()
        this.track?.flush()
        this.track?.play()
        this.buffersStack.clear()
//        this.alertDialog.show()
        Task().execute()
    }

    fun audioPlayerCreate(samplerate: Int, bufferSize: Int, channels: Int) {
        if (track != null) return
        track = AudioTrack(
            AudioManager.STREAM_MUSIC,
            samplerate,
            if (channels == 2) AudioFormat.CHANNEL_OUT_STEREO else AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
            AudioTrack.MODE_STREAM
        )
        track?.play()
        Log.d(LOG_TAG, "createAudioPlayer")

//        InputStreamSamples: Samples 960, numOfSamples 480, bytesPerSample 2, channels 1  samplesPerSec 48000

        Thread(this).start()
    }

    private fun byteSampleToSec(Hz: Int, bytes: Int, bitDepth: Int): Double {
        return (bytes * 8.0) / (Hz * bitDepth)
    }

    private fun byteSampleToSec(Hz: Int, bytes: Int, bitDepth: Int, channels: Int): Double {
        return (bytes * 8.0 / (Hz * channels * bitDepth))
    }

    fun millisecondsToSamples(milliSeconds: Int, sampleRate: Int): Int {
        return milliSeconds * (sampleRate / 1000)
    }

    fun writeData(data: ByteArray, samplerate: Int, bufferSize: Int, channels: Int) {
        var empty = true
        for (datum in data) {
            if (datum != 0x0.toByte()) {
                empty = false
                break
            }
        }
        if (empty) return

        if (track == null || track?.sampleRate != samplerate) {
            audioPlayerCreate(samplerate, bufferSize, channels)
        }

        buffersStack.addLast(data)
        timer += byteSampleToSec(samplerate, data.size, 16, channels)


//        timer += data.size/2 * 48000
//        Log.d(LOG_TAG, "Duration Samples $timer $delay size ${buffersStack.size}")
    }

}