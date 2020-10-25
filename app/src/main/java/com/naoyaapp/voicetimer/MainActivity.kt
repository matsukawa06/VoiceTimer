package com.naoyaapp.voicetimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import android.os.CountDownTimer

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener, View.OnClickListener {

    private var textToSpeech: TextToSpeech? = null

    private var timerText: TextView? = null
    private val dataFormat: SimpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.JAPAN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //**********************************************************
        // TTS関連
        //**********************************************************
        // TTS インスタンス生成
        textToSpeech = TextToSpeech(this, this)

        // 仮のボタン設定
        val ttsButton: Button = findViewById(R.id.Button_tts)
        ttsButton.setOnClickListener(this)

        //**********************************************************
        // カウントダウン関連
        //**********************************************************
        // 仮の時間設定
        val countNumber: Long = 180000
        val interval: Long = 10
        val startButton: Button = findViewById(R.id.start_button)
        val stopButton: Button = findViewById(R.id.stop_button)

        timerText = findViewById(R.id.timer)
        timerText!!.text = dataFormat.format(0)

        // インスタンス生成
        val countDown  = CountDown(countNumber, interval)
        startButton.setOnClickListener {
            countDown.start()
        }
        stopButton.setOnClickListener{
            countDown.cancel()
            timerText!!.text = dataFormat.format(0)
        }
    }

    /* カウントダウン処理 */

    internal inner class CountDown(millisInFuture: Long, countDownInterval: Long):
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            // 完了
            timerText!!.text= dataFormat.format(0)
        }

        // インターバルで呼ばれる
        override fun onTick(millisUntilFinished: Long) {
            // 残り時間を分、秒、ミリ秒に分割
            //long mm = millisUntilFinished / 1000 / 60;
            //long ss = millisUntilFinished / 1000 % 60;
            //long ms = millisUntilFinished - ss * 1000 - mm * 1000 * 60;
            //timerText.setText(String.format("%1$02d:%2$02d.%3$03d", mm, ss, ms));
            timerText!!.text = dataFormat.format(millisUntilFinished)
            // TTSを呼び出して喋ってもらう
            startSpeak(dataFormat.format(millisUntilFinished), true)
        }
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

    /* ボタンクリック時 */
    override fun onClick(v: View?) {
        var speakText: String = "てすとです"
        startSpeak(speakText, true)
    }

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
    private fun startSpeak(text: String, isImmediately: Boolean) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
    }

    /* 終了処理 */
    override fun onDestroy(){
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}