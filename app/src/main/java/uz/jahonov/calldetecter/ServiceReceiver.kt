package uz.jahonov.calldetecter;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import java.util.*


class ServiceReceiver : BroadcastReceiver(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var audioManager: AudioManager? = null

    override fun onReceive(context: Context?, p1: Intent?) {
        val telephonyManager: TelephonyManager = context?.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager;
        telephonyManager.listen(object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                super.onCallStateChanged(state, phoneNumber)


                tts = TextToSpeech(context, this@ServiceReceiver)
                if (state == TelephonyManager.CALL_STATE_RINGING && !phoneNumber.isNullOrEmpty()) {

                    val partNumber1 = phoneNumber.substring(phoneNumber.length - 4, phoneNumber.length - 2)
                    val partNumber2 = phoneNumber.substring(phoneNumber.length - 2)

                    audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    val thread: Thread = object : Thread() {
                        override fun run() {
                            try {
                                sleep(1000)
                                audioManager!!.mode = AudioManager.MODE_IN_CALL
                                audioManager!!.ringerMode = AudioManager.MODE_IN_CALL
                                if (!audioManager!!.isSpeakerphoneOn) audioManager!!.isSpeakerphoneOn = true
                                tts?.speak(partNumber1, TextToSpeech.QUEUE_FLUSH, null, "")
                                tts?.speak(partNumber2, TextToSpeech.QUEUE_ADD, null, "")
                                sleep(1000)
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                    }

                    thread.start()
                }

            }

        }, PhoneStateListener.LISTEN_CALL_STATE);
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ru"))


            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {

            } else {

            }
        }
    }


}