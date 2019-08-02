package ru.vonabe.audiostreaming

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import io.agora.rtc.Constants
import io.agora.rtc.Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
import io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER
import io.agora.rtc.IAudioFrameObserver
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import kotlinx.android.synthetic.main.activity_main.*
import ru.vonabe.audiostreaming.only.EngineConfig
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private val REQUEST_PERMISSIONS_RECORD = 1000
    }

    private val LOG_TAG: String = "MainActivity"

    private var output: TextView? = null
    private var scrollPanel: ScrollView? = null

    private var mRtcEngine: RtcEngine? = null

    private var mEngineConfig: EngineConfig? = null

    private var customMic: CustomMicrophoneRead? = null
    private var customPlayer: CustomAudioPlayer? = null

    private var microphoneIsRead = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ResourcesCompat.getColor(resources, R.color.agora_blue, theme)

//        StrictMode.setThreadPolicy(
//            StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads()
//                .detectDiskWrites()
//                .detectNetwork()
//                .penaltyLog()
//                .build()
//        )

        setContentView(R.layout.activity_main)

        this.mEngineConfig = EngineConfig()

        output = findViewById(R.id.outputTextView)
        scrollPanel = findViewById(R.id.scrollPanel)

//        recordAudio.setOnCheckedChangeListener { buttonView, isChecked ->
//            if (isChecked) {
//                val muteLocalAudioStream = mRtcEngine?.muteAllRemoteAudioStreams(true)
//                log(LOG_TAG, "Mute true $muteLocalAudioStream")
////                var cache = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "delay")
////                if (!cache.exists()) {
////                    cache.mkdirs()
////                    cache.createNewFile()
////                }
////                val startAudioRecording= mRtcEngine?.startAudioRecording(cache.absolutePath, Constants.AUDIO_RECORDING_QUALITY_HIGH)
////                log(LOG_TAG, "StartRecording: $startAudioRecording, cache: ${cache.absolutePath}")
//            } else {
//                val muteLocalAudioStream = mRtcEngine?.muteAllRemoteAudioStreams(false)
//                log(LOG_TAG, "Mute false $muteLocalAudioStream")
////                val stopAudioRecording = mRtcEngine?.stopAudioRecording()
////                log(LOG_TAG, "Stop Recording $stopAudioRecording")
//            }
//        }
//        APP certificate: f39d70c70d6845108bc97becd0788884

        val bufferingTitle = findViewById<TextView>(R.id.titleSeek)

        seekTimeout.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                bufferingTitle.text = getString(R.string.buffering_title).plus(" ${seekBar?.progress}")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let { customPlayer?.setDelay(seekBar.progress) }
            }
        })

        btnStartStream.setOnClickListener {
            //Set the user role as the host.
//            CLIENT_ROLE_AUDIENCE, CLIENT_ROLE_BROADCASTER

            if (!checkPermission()) {
                requestPermissions()
                return@setOnClickListener
            }

            microphoneIsRead = true

            mRtcEngine?.leaveChannel()

            mEngineConfig?.mClientRole = CLIENT_ROLE_BROADCASTER

            mRtcEngine?.setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING)
            mRtcEngine?.enableAudioVolumeIndication(1000, 3) // 200 ms
            mRtcEngine?.setClientRole(mEngineConfig!!.mClientRole)
//            mRtcEngine?.setLocalVoiceChanger(Constants.VOICE_CHANGER_OLDMAN)

            volumeControlStream = AudioManager.STREAM_VOICE_CALL

//            mRtcEngine?.setEnableSpeakerphone(true)
//            mRtcEngine?.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY_STEREO, Constants.AUDIO_SCENARIO_SHOWROOM)
//            mRtcEngine?.enableVideo()
//            mRtcEngine?.enableAudio()

            //Create and join a channel.
            val joinChannel = mRtcEngine?.joinChannel(
//                getString(R.string.token_channel),
                null,
                getString(R.string.channel_name),
                "Test Broadcast Channel Vonabe",
                mEngineConfig!!.mUid
            )
            log(LOG_TAG, "joinChannel - $joinChannel")

            customPlayer = if (customPlayer == null) CustomAudioPlayer(this) else customPlayer
//            customPlayer?.audioPlayerCreate()


//            customMic = if(customMic == null) CustomMicrophoneRead(this) else customMic
//            customMic?.recordAudioCreate()
//            mRtcEngine?.setExternalAudioSource(
//                true,      // Enable the external audio source.
//                44100,     // Sampling rate. Set it as 8k, 16k, 32k, 44.1k, or 48kHz.
//                1          // The number of external audio channels. The maximum value is 2.
//            )
//            Thread{
//                while(microphoneIsRead) {
//                    val data = customMic?.read()
//                    mRtcEngine?.pushExternalAudioFrame(data, System.currentTimeMillis())
////                    println("Write Data: ${data?.size}")
//                }
//            }.start()

        }

        btnJoinStream.setOnClickListener {

            if (!checkPermission()) {
                requestPermissions()
                return@setOnClickListener
            }

            microphoneIsRead = false

            mRtcEngine?.leaveChannel()

            mRtcEngine?.setChannelProfile(CHANNEL_PROFILE_LIVE_BROADCASTING)
//            mRtcEngine?.enableAudioVolumeIndication(200, 3) // 200 ms
            mRtcEngine?.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)

//            mRtcEngine?.setExternalAudioSource(
//                true,      // Enable the external audio source.
//                44100,     // Sampling rate. Set it as 8k, 16k, 32k, 44.1k, or 48kHz.
//                1          // The number of external audio channels. The maximum value is 2.
//            )

//            mRtcEngine?.enableLocalAudio(true)
//            mRtcEngine?.pushExternalAudioFrame(
//                data,             // The audio data in the format of byte[].
//                timestamp         // The timestamp of the audio frame.
//            )
//            mRtcEngine?.setRecordingAudioFrameParameters(44100, 1, ) // Устанавливает формат записи звука для обратного вызова onRecordFrame .

//            mRtcEngine?.setPlaybackAudioFrameParameters(44100, 1, Constants.RAW_AUDIO_FRAME_OP_MODE_WRITE_ONLY, 960) // Устанавливает формат воспроизведения аудио для обратного вызова onPlaybackFrame .

//            Sets the sample points (samples) returned in the onPlaybackFrame callback. samplesPerCall is usually set as 1024 for stream pushing. samplesPerCall = (int)(samplesPerSec × sampleInterval × numChannels), where sampleInterval ≥ 0.01 in seconds.

//            mRtcEngine?.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_HIGH_QUALITY_STEREO, Constants.AUDIO_SCENARIO_SHOWROOM)
//            mRtcEngine?.enableVideo()
//            mRtcEngine?.enableAudio()

            //Create and join a channel.
            val joinChannel = mRtcEngine?.joinChannel(
//                getString(R.string.token_channel),
                null,
                getString(R.string.channel_name),
                "Test Broadcast Channel Vonabe",
                mEngineConfig!!.mUid
            )
            log(LOG_TAG, "joinChannel - $joinChannel")

            volumeControlStream = AudioManager.STREAM_VOICE_CALL

            customPlayer = if (customPlayer == null) CustomAudioPlayer(this) else customPlayer
//            customPlayer?.audioPlayerCreate()

        }

        initializeAgoraEngine()

        if (!checkPermission())
            requestPermissions()

    }

    private var buffer = StringBuilder()
    private fun log(tag: String, msg: String) {
        Log.d(tag, msg)
        buffer.append(msg.plus("\n"))
        runOnUiThread {
            output?.text = buffer
            scrollPanel?.fullScroll(View.FOCUS_DOWN)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSIONS_RECORD
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_RECORD && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }

    private fun initializeAgoraEngine() {
        try {
            mRtcEngine =
                RtcEngine.create(baseContext, getString(R.string.agora_app_id), object : IRtcEngineEventHandler() {
                    override fun onRtcStats(stats: RtcStats?) {
                        super.onRtcStats(stats)
                        log(LOG_TAG, "onRtcStats: ${stats?.cpuAppUsage}")
                    }

                    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                        super.onJoinChannelSuccess(channel, uid, elapsed)
                        log(LOG_TAG, "onJoinChannelSuccess: $channel, $uid, $elapsed")
                        mEngineConfig?.mUid = uid
                    }

                    override fun onUserJoined(uid: Int, elapsed: Int) {
                        super.onUserJoined(uid, elapsed)
                        log(LOG_TAG, "onUserJoined: $uid, $elapsed")
                    }

                    override fun onRejoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
                        super.onRejoinChannelSuccess(channel, uid, elapsed)
                        log(LOG_TAG, "onRejoinChannelSuccess: $channel, $uid, $elapsed")
                    }

                    override fun onConnectionLost() {
                        super.onConnectionLost()
                        log(LOG_TAG, "onConnectionLost")
                    }

                    override fun onActiveSpeaker(uid: Int) {
                        super.onActiveSpeaker(uid)
                        log(LOG_TAG, "onActiveSpeaker: $uid")
                    }

                    override fun onError(err: Int) {
                        super.onError(err)
//                    ErrorCode
                        log(LOG_TAG, "onError: $err")
                    }

                    override fun onLeaveChannel(stats: RtcStats?) {
                        super.onLeaveChannel(stats)
                        log(LOG_TAG, "onLeaveChannel")
                        mRtcEngine?.stopAudioRecording()
                    }

                    override fun onAudioRouteChanged(routing: Int) {
                        super.onAudioRouteChanged(routing)
                        log(LOG_TAG, "onAudioRouteChanged: $routing")
                    }

                })

//            mRtcEngine?.setRecordingAudioFrameParameters(44100, 1, Constants.RAW_AUDIO_FRAME_OP_MODE_READ_WRITE, 2048)
            mRtcEngine?.registerAudioFrameObserver(object : IAudioFrameObserver {
                override fun onRecordFrame(
                    samples: ByteArray?, numOfSamples: Int, bytesPerSample: Int, channels: Int, samplesPerSec: Int
                ): Boolean {
//                    Log.d(
//                        LOG_TAG,
//                        "onRecordFrame: Samples ${samples?.size}, numOfSamples $numOfSamples, bytesPerSample $bytesPerSample, channels $channels samplesPerSec $samplesPerSec"
//                    )
                    return true
                }

                override fun onPlaybackFrame(
                    samples: ByteArray?,
                    numOfSamples: Int,
                    bytesPerSample: Int,
                    channels: Int,
                    samplesPerSec: Int
                ): Boolean {

                    samples?.let { customPlayer?.writeData(it.clone(), samplesPerSec, samples.size, channels) }
                    Arrays.fill(samples, 0x0)
//                    Log.d(
//                        LOG_TAG,
//                        "onPlaybackFrame: Samples ${Arrays.toString(samples)}, numOfSamples $numOfSamples, bytesPerSample $bytesPerSample, channels $channels  samplesPerSec $samplesPerSec"
//                    )
//                    InputStreamSamples: Samples 960, numOfSamples 480, bytesPerSample 2, channels 1  samplesPerSec 48000
                    return true
                }
            })

        } catch (e: Exception) {
            log(LOG_TAG, Log.getStackTraceString(e))
            throw RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e))
        }
    }

}
