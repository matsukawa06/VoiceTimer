package com.naoyaapp.voicetimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // TTS インスタンス生成
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                val local = Locale.JAPAN
                if (tts.isLanguageAvailable(local) > TextToSpeech.LANG_AVAILABLE) {
                    tts.language = Locale.JAPAN
                } else {
                    // 言語の設定に失敗
                }
            }
        } else {
            // TTS init 失敗
        }
    }

    private fun startSpeak(text: String, isImmediately: Boolean) {
        /*
        * 読み上げ開始
        * text：読み上げるテキスト
        * queueMode：キューイングモード（QUEUE_ADDまたはQUEUE_FLUSH）
        * 　　QUEUE_ADD：キューに追加され、順番に読み上げられる
        *    QUEUE_FLUSH：すぐに読み上げを開始
        * パラメータとしてbundleを渡すことができる
        *    KEY_PARAM_STREAM：オーディオストリームを変更する際に利用
        *    KEY_PARAM_VOLUME：音のボリュームを調整するのに利用
        *    KEY_PARAM_PAN：音の定位を調整するのに利用
        * utteranceId：固有の識別子
        *  */
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
    }

    /* 終了処理 */
    override fun onDestroy(){
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}