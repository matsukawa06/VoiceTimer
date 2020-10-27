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

    private var txtTimerHours: TextView? = null
    private var txtTimerMinutes: TextView? = null
    private var txtTimerSeconds: TextView? = null
    private val dataFormatHours: SimpleDateFormat = SimpleDateFormat("HH", Locale.US)
    private val dataFormatMinutes: SimpleDateFormat = SimpleDateFormat("mm", Locale.US)
    private val dataFormatSeconds: SimpleDateFormat = SimpleDateFormat("ss", Locale.US)

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
        // 仮の時間設定(ミリ秒)
        val countNumber: Long = 1000 * 15
        val interval: Long = 1000
        val startButton: Button = findViewById(R.id.start_button)
        val stopButton: Button = findViewById(R.id.stop_button)

        // カウントの初期表示
        txtTimerHours = findViewById(R.id.time_hh)
        txtTimerMinutes = findViewById(R.id.time_mm)
        txtTimerSeconds = findViewById(R.id.time_ss)
        txtTimerHours!!.text = dataFormatHours.format(countNumber)
        txtTimerMinutes!!.text = dataFormatMinutes.format(countNumber)
        txtTimerSeconds!!.text = dataFormatSeconds.format(countNumber)

        // インスタンス生成
        val countDown  = CountDown(countNumber, interval)
        startButton.setOnClickListener {
            countDown.start()
        }
        stopButton.setOnClickListener{
            countDown.cancel()
            txtTimerHours!!.text = dataFormatHours.format(0)
            txtTimerMinutes!!.text = dataFormatMinutes.format(0)
            txtTimerSeconds!!.text = dataFormatSeconds.format(0)
        }
    }

    /* カウントダウン処理 */

    internal inner class CountDown(millisInFuture: Long, countDownInterval: Long):
        CountDownTimer(millisInFuture, countDownInterval) {
        override fun onFinish() {
            // 完了
            txtTimerHours!!.text= dataFormatHours.format(0)
            txtTimerMinutes!!.text= dataFormatMinutes.format(0)
            txtTimerSeconds!!.text= dataFormatSeconds.format(0)
        }

        // インターバルで呼ばれる
        override fun onTick(millisUntilFinished: Long) {
            // 残り時間を分、秒、ミリ秒に分割
            //long mm = millisUntilFinished / 1000 / 60;
            //long ss = millisUntilFinished / 1000 % 60;
            //long ms = millisUntilFinished - ss * 1000 - mm * 1000 * 60;
            //timerText.setText(String.format("%1$02d:%2$02d.%3$03d", mm, ss, ms));
            txtTimerHours!!.text = dataFormatHours.format(millisUntilFinished)
            txtTimerMinutes!!.text = dataFormatMinutes.format(millisUntilFinished)
            txtTimerSeconds!!.text = dataFormatSeconds.format(millisUntilFinished)

            // 分と秒を取得
            val numMM = kotlin.math.floor(millisUntilFinished / 1000.0 / 60.0).toInt()
            val numSS = kotlin.math.floor(millisUntilFinished / 1000.0 % 60.0).toInt()

            if(numMM == 0 && numSS <= 5) {
                // 1秒毎にカウント
                startSpeak(numSS.toString(), true)
            }
            if(numSS % 10 == 0) {
                var strSpeech : String = ""
                if(numMM > 0) {
                    // 分があれば設定
                    strSpeech = numMM.toString() + "分"
                }
                if(numSS != 0) {
                    strSpeech = strSpeech + numSS.toShort() + "秒"
                }
                if(strSpeech != "") {
                    strSpeech += "前"
                }

                // TTSを呼び出して喋ってもらう
                startSpeak(strSpeech, true)
            }

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
    *    QUEUE_ADD：キューに追加され、順番に読み上げられる
    *    QUEUE_FLUSH：すぐに読み上げを開始
    * パラメータとしてbundleを渡すことができる
    *    KEY_PARAM_STREAM：オーディオストリームを変更する際に利用
    *    KEY_PARAM_VOLUME：音のボリュームを調整するのに利用
    *    KEY_PARAM_PAN：音の定位を調整するのに利用
    * utteranceId：固有の識別子
    *  */
    private fun startSpeak(text: String, isImmediately: Boolean) {
        // 読み上げのスピード（デフォルト1.0、例えば2.0なら倍のスピード）
        textToSpeech?.setSpeechRate(1.1f)
        // 実行
        textToSpeech?.speak(text, TextToSpeech.QUEUE_ADD, null, "utteranceId")
    }

    /* 終了処理 */
    override fun onDestroy(){
        textToSpeech?.shutdown()
        super.onDestroy()
    }
}