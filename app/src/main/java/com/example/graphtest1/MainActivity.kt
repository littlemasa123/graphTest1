package com.example.graphtest1


import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet

import java.text.SimpleDateFormat;
import java.util.Date;


open class MainActivity : AppCompatActivity(), DummySensorEngine.Listener{

    private val mRunning = false

    lateinit var mChart: LineChart
    private var mSensorEngine: DummySensorEngine? = null
    //①Entryにデータ格納
    var entryList = mutableListOf<Entry>()//1本目の線

    val queue = ArrayDeque(List(500){0f})
        var x =List(500){it/100f}
        var y = queue.toList().reversed()//X軸データ
        //val y = x.map{it*it}//Y軸データ（X軸の2乗）

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  0.01秒おきにデータを追加していくため
        mSensorEngine = DummySensorEngine(10)
        mSensorEngine!!.setListener(this)
        mChart = findViewById<View>(R.id.lineChartExample) as LineChart
        initChart()

        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)

        btn1.setOnClickListener {
            //txt1.text = str
            if (mSensorEngine!!.isRunning) {
                stopMonitoring()
            } else {
                startMonitoring()
            }
        }

        btn2.setOnClickListener {
            android.os.Process.killProcess(android.os.Process.myPid())
        }

    }

    override fun onStop() {
        super.onStop()
        if (mSensorEngine!!.isRunning) {
            stopMonitoring()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSensorEngine!!.destroy()
    }

    override fun onValueMonitored(value: Double) {

        //①Entryにデータ格納
        var entryList = mutableListOf<Entry>()//1本目の線

        queue.add(value.toFloat())
        queue.removeFirst()
        y = queue.toList().reversed()//y軸データを逆順にして代入
        for(i in x.indices){
            entryList.add(
                Entry(x[i], y[i])
            )
        }

        //LineDataSetのList
        val lineDataSets = mutableListOf<ILineDataSet>()
        //②DataSetにデータ格納
        //val lineDataSet = LineDataSet(entryList, "square")
        //③DataSetにフォーマット指定(3章で詳説)
        //lineDataSet.color = Color.BLUE
        //リストに格納
        //lineDataSets.add(lineDataSet)

//④LineDataにLineDataSet格納
//        val lineData = LineData(lineDataSets)
//        //⑤LineChartにLineData格納
//        val lineChart = findViewById<LineChart>(R.id.lineChartExample)
//        lineChart.data = lineData
        //mChart.data = data

        var data:LineData = mChart.data
        if (data == null) {
            return;

        }
        //var set:LineDataSet = data.getDataSetByIndex(0)
        var set: LineDataSet? = data.getDataSetByIndex(0) as LineDataSet?

            //②DataSetにデータ格納
            set = LineDataSet(entryList, "receive data")
            //③DataSetにフォーマット指定(3章で詳説)
            //lineDataSet.color = Color.BLUE

            //set = LineDataSet(null, "サンプルデータ")
            set.color = Color.BLUE
            set.setDrawValues(false)
            data.clearValues()//前のデータを消す。これがないと1つずつ追加されていく
            data.addDataSet(set)

        //  追加描画するデータを追加
        //data.addEntry(Entry(0f, value.toFloat()), 0)
        //data.removeDataSet(500)

        //  データを追加したら必ずよばないといけない?
        mChart!!.notifyDataSetChanged()
        mChart!!.setVisibleXRangeMaximum(5f)
        mChart!!.moveViewToX(1f) //  移動する?
    }

    private fun initChart() {
        // no description text
        //mChart!!.setDescription("")
        //mChart.setNoDataTextDescription("You need to provide data for the chart.")

        // enable touch gestures
        mChart!!.setTouchEnabled(true)

        // enable scaling and dragging
        mChart!!.isDragEnabled = true
        mChart!!.setScaleEnabled(true)
        mChart!!.setDrawGridBackground(false)

        // if disabled, scaling can be done on x- and y-axis separately
        mChart!!.setPinchZoom(true)

        // set an alternative background color
        mChart!!.setBackgroundColor(Color.LTGRAY)
        val data = LineData()
        data.setValueTextColor(Color.BLACK)

        // add empty data
        mChart!!.data = data

        //  ラインの凡例の設定
        val l = mChart!!.legend
        l.form = Legend.LegendForm.LINE
        l.textColor = Color.BLACK
        val xl = mChart!!.xAxis
        xl.textColor = Color.BLACK
        xl.setLabelCount(9,true)
        val leftAxis = mChart!!.axisLeft
        leftAxis.textColor = Color.BLACK
        //leftAxis.setAxisMaxValue(3.0f)
        //leftAxis.setAxisMinValue(-3.0f)
        //leftAxis.setStartAtZero(false)
        leftAxis.setDrawGridLines(true)
        val rightAxis = mChart!!.axisRight
        rightAxis.isEnabled = false
    }

    private fun startMonitoring() {
        mSensorEngine!!.start()
        val textView = findViewById<View>(R.id.btn1) as TextView
        textView.text = "計測終了"
    }

    private fun stopMonitoring() {
        mSensorEngine!!.stop()
        val textView = findViewById<View>(R.id.btn1) as TextView
        textView.text = "計測開始"
    }






//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        //表示用サンプルデータの作成//
//        val queue = ArrayDeque(List(500){it/100f})
//        val x = queue.toList()//X軸データ
//        val y = x.map{it*it}//Y軸データ（X軸の2乗）
//
//        //①Entryにデータ格納
//        var entryList = mutableListOf<Entry>()//1本目の線
//        for(i in x.indices){
//            entryList.add(
//                Entry(x[i], y[i])
//            )
//        }
//
//        //LineDataSetのList
//        val lineDataSets = mutableListOf<ILineDataSet>()
//        //②DataSetにデータ格納
//        val lineDataSet = LineDataSet(entryList, "square")
//        //③DataSetにフォーマット指定(3章で詳説)
//        lineDataSet.color = Color.BLUE
//        //リストに格納
//        lineDataSets.add(lineDataSet)
//
//        //④LineDataにLineDataSet格納
//        val lineData = LineData(lineDataSets)
//        //⑤LineChartにLineData格納
//        val lineChart = findViewById<LineChart>(R.id.lineChartExample)
//        lineChart.data = lineData
//        //⑥Chartのフォーマット指定(3章で詳説)
//        //X軸の設定
//        lineChart.xAxis.apply {
//            isEnabled = true
//            textColor = Color.BLACK
//            setLabelCount(9)
//        }
//
//
//        //⑦linechart更新
//        lineChart.invalidate()
//
//
//
//
//
//
//
//
//
//        //⑦linechart更新
//        //lineChart.invalidate()
//    }





}