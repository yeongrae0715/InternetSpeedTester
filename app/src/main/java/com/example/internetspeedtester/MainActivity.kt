package com.example.internetspeedtester

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.internet_speed_testing.InternetSpeedBuilder
import com.example.internet_speed_testing.ProgressionModel
import java.lang.Exception
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*
UDP target settings
 */
class Declares {
    val RemoteHost: String = ""
    val RemotePort: Int = 10082

    constructor()
    init { }
}
val Settings = Declares()


class MainActivity : AppCompatActivity() {

    lateinit var btnStart:Button
    lateinit var etCount:EditText
    lateinit var address:EditText
    lateinit var udpPort:EditText
    lateinit var curCount:TextView
//    lateinit var lblProcess:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnStart = findViewById(R.id.btnStart)
        etCount = findViewById(R.id.count)
        address = findViewById(R.id.address)
        udpPort = findViewById(R.id.port)
        curCount = findViewById(R.id.labelCurrent)
//        lblProcess = findViewById(R.id.labelProcess)

        var totalCnt = 0
        var limitCount = 0
        var dispCnt = 0

        /*
        *   Internet speed test
        */
        val builder = InternetSpeedBuilder(this)
        builder.setOnEventInternetSpeedListener(object :
                InternetSpeedBuilder.OnEventInternetSpeedListener {
            override fun onDownloadProgress(count: Int, progressModel: ProgressionModel) {}
            override fun onUploadProgress(count: Int, progressModel: ProgressionModel) {
//                lblProcess.text = "${progressModel.progressUpload}%"
            }
            override fun onTotalProgress(count: Int, progressModel: ProgressionModel) {

                if (progressModel.progressTotal == 100.0f) {
//                    adapter?.setDataList(count, progressModel)
//                    Toast.makeText(applicationContext, "$progressModel", Toast.LENGTH_SHORT).show()
                    var time = getNowTime()

                    totalCnt += 1

                    if (totalCnt == limitCount){
                        btnStart.isEnabled = true
                    }
                    Log.d("Completed", "$totalCnt / $limitCount")

                    dispCnt += 1
                    curCount.text = "$dispCnt / ${etCount.text}"

                    getData("t$time\td${progressModel.downloadSpeed}\tu${progressModel.uploadSpeed}")
                }
            }
        })

        btnStart.setOnClickListener {
            dispCnt = 0
            limitCount += etCount.text.toString().toInt()
            btnStart.isEnabled = false
            builder.start("ftp://speedtest.tele2.net/1MB.zip", limitCount)
            curCount.text = "$dispCnt / ${etCount.text}"
        }


    }

    /* keep running when the app is paused */
    override fun onPause() {
        super.onPause()
    }

    /* get now time */
    fun getNowTime(): Any {
        val current = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val formatted = current.format(formatter)
        return  formatted
    }

    fun getData(s: String) {
//        var data :String = editText.text.toString()
        var data :String = s
//        result.setText(data)
//        println(data)

//        val host = "192.168.0.196"
//        val host = "223.171.39.106"
        val host = address.text.toString()
//        val port = 10081
        val port = udpPort.text.toString().toInt()
        val sendData = data.toByteArray()
//        result.setText(sendData.toString())

        send(host, port, sendData)
//        send(Settings.RemoteHost, Settings.RemotePort, sendData)
    }



    fun send(host: String, port: Int, sendData: ByteArray) {
        var socket: DatagramSocket?=null
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            socket= DatagramSocket()
            socket.broadcast = true

            val packet= DatagramPacket(sendData, sendData.size, InetAddress.getByName(host), port)
            socket.send(packet)
        }catch (e: Exception){
            println(e)
        }finally {
            socket?.close()
        }
    }

//    inner class UdpDataReceived: Thread() {
//        override fun run() {
//            println("${Thread.currentThread()} Runnable thread started.")
//            while (true) {
////                receiveUDP()
//                val buffer = ByteArray(2048)
//                var socket: DatagramSocket? = null
//                println("thread")
//                Thread.sleep(100)
//                try {
//                    socket = DatagramSocket(Settings.RemotePort, InetAddress.getByName(Settings.RemoteHost))
//                    socket.broadcast = true
//                    val packet = DatagramPacket(buffer, buffer.size)
//                    socket.receive(packet)
//                    println("Received packet : ${packet.data} // ${packet.address}:${packet.port} // ${packet}")
//                } catch (e: Exception) {
//                    println("Catched exception : ${e.toString()}")
//                    e.printStackTrace()
//                } finally {
//                    socket?.close()
//                }
//            }
//        }
//
//        private fun receiveUDP() {
//            val buffer = ByteArray(2048)
//            var socket: DatagramSocket? = null
//            println("thread")
//            Thread.sleep(1000)
//            try {
//                socket = DatagramSocket(Settings.RemotePort, InetAddress.getByName(Settings.RemoteHost))
//                socket.broadcast = true
//                val packet = DatagramPacket(buffer, buffer.size)
//                socket.receive(packet)
//                println("Received packet : ${packet.data} // ${packet.address}:${packet.port} // ${packet}")
//            } catch (e: Exception) {
//                println("Catched exception : ${e.toString()}")
//                e.printStackTrace()
//            } finally {
//                socket?.close()
//            }
//        }
//    }

}