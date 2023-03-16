package com.example.graphtest1

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import java.util.*


/**
 * ダミーのセンサーデータを取得し通知するクラスです。
 * こちらはほとんど参考文献のままです。
 *
 * Created by LyricalOriginal on 2015/12/29.
 */
internal class DummySensorEngine(interval: Int) {
    private val mInterval: Int
    private val mThread: HandlerThread
    private val mHandler: Handler
    private val mUiHandler: Handler

    /**
     * ダミーデータ取得中かどうかを取得します。
     *
     * @return ダミーデータ取得中か
     */
    var isRunning = false
        private set
    private var mListener: Listener? = null
    private var mIndex = 0

    /**
     * コンストラクタ
     *
     * @param interval センサー値取得感覚。単位はms。
     */
    init {
        require(interval >= 10) { "intervalは10ms以上の整数を指定してください" }
        mInterval = interval
        mUiHandler = Handler()
        mThread = HandlerThread("DummySensor")
        mThread.start()
        mHandler = Handler(mThread.looper)
    }

    /**
     * ダミーデータ取得リスナーを設定します。
     *
     * @param l ダミーデータ取得リスナー
     */
    fun setListener(l: Listener?) {
        if (!isRunning) {
            mListener = l
        }
    }

    /**
     * ダミーデータ取得処理を行います。<BR></BR>
     * このメソッドはUIスレッドから実行してください。
     */
    fun start() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw RuntimeException("このメソッドはUIスレッドから呼び出してください。")
        } else if (isRunning) {
            return
        }
        isRunning = true
        mHandler.post(object : Runnable {
            override fun run() {
                if (!isRunning) {
                    return
                }
                //val currentDate = Date()
                val value: Double = value

                mUiHandler.post {
                    if (mListener != null) {
                        mListener!!.onValueMonitored(value)
                    }
                }
                mIndex++
                mHandler.postDelayed(this, mInterval.toLong())
            }
        })
    }

    /**
     * ダミーデータ取得処理を停止します。<BR></BR>
     * このメソッドはUIスレッドから実行してください。
     */
    fun stop() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw RuntimeException("このメソッドはUIスレッドから呼び出してください。")
        }
        if (isRunning) {
            isRunning = false
        }
    }

    /**
     * オブジェクトの破棄処理を行います。<BR></BR>
     * オブジェクトないでワーカースレッドを使っているためその処理をするために使う。
     */
    fun destroy() {
        if (isRunning) {
            return
        }
        mThread.quit()
    }

    private val value: Double
        private get() = Math.cos(Math.PI * mIndex / 31) + Math.sin(Math.PI * (mIndex + 1) / 15)

    interface Listener {
        fun onValueMonitored(value: Double)
    }
}