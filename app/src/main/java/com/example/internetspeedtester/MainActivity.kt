package com.example.internetspeedtester

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import com.example.internet_speed_testing.InternetSpeedBuilder
import com.example.internet_speed_testing.ProgressionModel
import java.lang.Exception
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        *   Internet speed test
        */
        val builder = InternetSpeedBuilder(this)
        builder.setOnEventInternetSpeedListener(object :
                InternetSpeedBuilder.OnEventInternetSpeedListener {
            override fun onDownloadProgress(count: Int, progressModel: ProgressionModel) {}
            override fun onUploadProgress(count: Int, progressModel: ProgressionModel) {}
            override fun onTotalProgress(count: Int, progressModel: ProgressionModel) {
                if (progressModel.progressTotal.toString() == "100.0") {
//                    adapter?.setDataList(count, progressModel)
                    Toast.makeText(applicationContext, "$progressModel", Toast.LENGTH_SHORT).show()
                    getData("${progressModel.downloadSpeed} ${progressModel.uploadSpeed}")
                }
            }
        })
        builder.start("ftp://speedtest.tele2.net/1MB.zip", 3)
    }

    fun getData(s: String) {
//        var data :String = editText.text.toString()
        var data :String = s
//        result.setText(data)
//        println(data)

        val host = "192.168.0.196"
//        val host = "223.171.39.106"
        val port = 10081
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

    inner class UdpDataReceived: Thread() {
        override fun run() {
            println("${Thread.currentThread()} Runnable thread started.")
            while (true) {
//                receiveUDP()
                val buffer = ByteArray(2048)
                var socket: DatagramSocket? = null
                println("thread")
                Thread.sleep(100)
                try {
                    socket = DatagramSocket(Settings.RemotePort, InetAddress.getByName(Settings.RemoteHost))
                    socket.broadcast = true
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    println("Received packet : ${packet.data} // ${packet.address}:${packet.port} // ${packet}")
                } catch (e: Exception) {
                    println("Catched exception : ${e.toString()}")
                    e.printStackTrace()
                } finally {
                    socket?.close()
                }
            }
        }

        private fun receiveUDP() {
            val buffer = ByteArray(2048)
            var socket: DatagramSocket? = null
            println("thread")
            Thread.sleep(1000)
            try {
                socket = DatagramSocket(Settings.RemotePort, InetAddress.getByName(Settings.RemoteHost))
                socket.broadcast = true
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)
                println("Received packet : ${packet.data} // ${packet.address}:${packet.port} // ${packet}")
            } catch (e: Exception) {
                println("Catched exception : ${e.toString()}")
                e.printStackTrace()
            } finally {
                socket?.close()
            }
        }
    }

}