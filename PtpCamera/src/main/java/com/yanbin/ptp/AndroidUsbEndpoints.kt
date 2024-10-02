package com.yanbin.ptp

import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbRequest
import com.linein.ptplib.connection.ptpusb.PtpUsbEndpoints
import com.linein.ptplib.exceptions.InitFailException
import com.linein.ptplib.exceptions.InitFailException.InitFailReason
import com.linein.ptplib.exceptions.ReceiveDataException
import com.linein.ptplib.exceptions.SendDataException
import java.nio.ByteBuffer

class AndroidUsbEndpoints(
    private val usbConnection: UsbDeviceConnection,
    private val usbInterface: UsbInterface
) : PtpUsbEndpoints {
    private var data_out: UsbEndpoint? = null
    private var data_in: UsbEndpoint? = null
    private var interrupt: UsbEndpoint? = null
    private var timeout: Int

    init {
        timeout = DEFAULT_TIMEOUT
    }

    @Throws(InitFailException::class)
    override fun initalize() {
        if (!usbConnection.claimInterface(usbInterface, true)) throw InitFailException(
            InitFailReason.ClaimFailed
        )
        if (usbInterface.endpointCount < 3) throw InitFailException(InitFailReason.NoEndpoints)
        for (i in 0 until usbInterface.endpointCount) {
            val ep = usbInterface.getEndpoint(i)
            val isout = ep.direction == UsbConstants.USB_DIR_OUT
            val isbulk = ep.type == UsbConstants.USB_ENDPOINT_XFER_BULK
            if (isout && isbulk) data_out = ep
            if (!isout && isbulk) data_in = ep
            if (!isbulk) interrupt = ep
        }
        if (data_out == null || data_in == null || interrupt == null) throw InitFailException(
            InitFailReason.NoEndpoints
        )
    }

    override fun release() {
        usbConnection.releaseInterface(usbInterface)
    }

    override fun controlTransfer(
        requestType: Int,
        request: Int,
        value: Int,
        index: Int,
        buffer: ByteArray?
    ): Int {
        return usbConnection.controlTransfer(
            requestType, request, value, index, buffer,
            buffer?.size ?: 0, 1500
        )
    }

    override fun setTimeOut(to: Int) {
        timeout = if (to > 0) to else DEFAULT_TIMEOUT
    }

    @Throws(SendDataException::class)
    override fun writeDataOut(buffer: ByteArray, length: Int): Int {
        val len = usbConnection.bulkTransfer(data_out, buffer, length, timeout)
        if (len < 0) throw SendDataException("senderror: len is $len")
        return len
    }

    @Throws(ReceiveDataException::class)
    override fun readDataIn(buffer: ByteArray): Int {
        var len = 0
        while (len == 0) len =
            usbConnection.bulkTransfer(data_in, buffer, buffer?.size ?: 0, timeout)
        if (len < 0) throw ReceiveDataException("receiveerror: len is $len")
        return len
    }

    override fun readEvent(buffer: ByteArray, bulk: Boolean) {
        if (bulk) usbConnection.bulkTransfer(interrupt, buffer, buffer.size, 500) else {
            var req: UsbRequest? = UsbRequest()
            req!!.initialize(usbConnection, interrupt)
            val bbuffer = ByteBuffer.wrap(buffer)
            req.queue(bbuffer, buffer.size)
            req = usbConnection.requestWait()
//            PtpLog.debug("received event")
//            if (req != null) {
//                PtpLog.debug("req != null")
//                PtpLog.debug(Packet(bbuffer.array()).toString())
//            } else PtpLog.debug("event is null")
        }
    }

    override fun getMaxPacketSizeOut(): Int {
        return data_out!!.maxPacketSize
    }

    override fun getMaxPacketSizeIn(): Int {
        return data_in!!.maxPacketSize
    }

    override fun getMaxPacketSizeInterrupt(): Int {
        return interrupt!!.maxPacketSize
    }

    companion object {
        private const val DEFAULT_TIMEOUT = 10000
    }
}