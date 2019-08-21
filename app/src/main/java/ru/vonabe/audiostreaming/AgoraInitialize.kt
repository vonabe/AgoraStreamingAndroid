package ru.vonabe.audiostreaming

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.util.Log
import io.agora.rtc.Constants
import io.agora.rtc.IAudioFrameObserver
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import ru.vonabe.audiostreaming.only.EngineConfig
import java.lang.ref.WeakReference
import java.util.*

class AgoraInitialize {

    companion object {
        private var reference: WeakReference<Activity>? = null
        private const val LOG_TAG = "AgoraInitialize"
        private const val REQUEST_PERMISSIONS_RECORD = 1000

        private var microphoneIsRead = false
        private var instance: AgoraInitialize? = null

        @Synchronized
        fun getInstance(context: Context): AgoraInitialize? {
            instance = if (instance == null) {
                AgoraInitialize()
            } else {
                instance
            }
            this.reference = WeakReference(context as Activity)
            return instance
        }

    }

    private var customPlayer: CustomAudioPlayer? = null
    private var mEngineConfig: EngineConfig? = null
    private var mRtcEngine: RtcEngine? = null

    init {
        this.mEngineConfig = EngineConfig()

    }

    fun joinChannel(channelName: String, appid: String) {
        this.destroy()

        initializeAgoraEngine(appid)

        microphoneIsRead = false

        mRtcEngine?.leaveChannel()

        mRtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
//            mRtcEngine?.enableAudioVolumeIndication(200, 3) // 200 ms
        mRtcEngine?.setClientRole(Constants.CLIENT_ROLE_AUDIENCE)

        //Create and join a channel.
        val joinChannel = mRtcEngine?.joinChannel(
//                getString(R.string.token_channel),
            null,
            channelName,
            "Test Broadcast Channel Vonabe",
            mEngineConfig!!.mUid
        )
        log(LOG_TAG, "joinChannel - $joinChannel")

        reference?.get()?.volumeControlStream = AudioManager.STREAM_VOICE_CALL

        customPlayer = if (customPlayer == null) CustomAudioPlayer(reference!!) else customPlayer

    }

    fun setDelay(delay: Int) {
        customPlayer?.setDelay(delay)
    }

    fun getDelay(): Int {
        return customPlayer?.getDelay() ?: -1
    }

    fun startStream(channelName: String, appid: String) {
        this.destroy()

        initializeAgoraEngine(appid)

        microphoneIsRead = true

        mRtcEngine?.leaveChannel()

        mEngineConfig?.mClientRole = Constants.CLIENT_ROLE_BROADCASTER

        mRtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        mRtcEngine?.enableAudioVolumeIndication(1000, 3) // 200 ms
        mRtcEngine?.setClientRole(mEngineConfig!!.mClientRole)
//            mRtcEngine?.setLocalVoiceChanger(Constants.VOICE_CHANGER_OLDMAN)

        reference?.get()?.volumeControlStream = AudioManager.STREAM_VOICE_CALL

        //Create and join a channel.
        val joinChannel = mRtcEngine?.joinChannel(
            null,
            channelName,
            "Test Broadcast Channel Vonabe",
            mEngineConfig!!.mUid
        )
        log(LOG_TAG, "joinChannel - $joinChannel")

        customPlayer = if (customPlayer == null) CustomAudioPlayer(reference!!) else customPlayer
    }

    fun destroy() {
        customPlayer?.destroy()
        RtcEngine.destroy()
        customPlayer = null
    }

    private fun initializeAgoraEngine(appid: String) {
        try {
            mRtcEngine =
                RtcEngine.create(reference?.get(), appid, object : IRtcEngineEventHandler() {
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

    private var buffer = StringBuilder()
    private fun log(tag: String, msg: String) {
        Log.d(tag, msg)
        buffer.append(msg.plus("\n"))
    }

}