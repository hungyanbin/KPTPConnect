package com.yanbin.ptp.camera

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import com.linein.ptplib.connection.PtpConnection
import com.linein.ptplib.connection.PtpSession
import com.linein.ptplib.connection.ptpusb.PtpUsbEndpoints
import com.linein.ptplib.constants.ObjectFormat
import com.linein.ptplib.datacallbacks.DataCallback
import com.linein.ptplib.packets.DeviceInfoPacket
import com.linein.ptplib.packets.ObjectInfoPacket
import com.yanbin.ptp.AndroidUsbEndpoints
import com.yanbin.ptp.utils.getInterfaces
import com.yanbin.ptp.utils.toTimestamp
import com.yanbin.ptp.utils.tryOrNull
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

abstract class BasePtpCamera(
    context: Context,
) : IPtpCamera {

    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private var session: PtpSession? = null

    private var serialNumber: String? = null
    private var manufacturer: String? = null
    private var name: String? = null
    private var usbDevice: UsbDevice? = null

    private var cameraImagesCache = emptyList<CameraImage>()
    protected val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    protected val operationsSupported = mutableSetOf<Short>()

    override suspend fun attachToDevice(usbDevice: UsbDevice) = withContext(dispatcher) {
        this@BasePtpCamera.usbDevice = usbDevice
        val usbEndPoints = createPtpUsbEndpoints(usbDevice)
        val ptpConnection = PtpConnection.create(usbEndPoints)
        ptpConnection.connectAndInit(
            byteArrayOf(
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
            ),
            APP_NAME
        ) { Timber.d( "initPtpSession: ") }

        session = PtpSession(ptpConnection).apply {
            val data = getDeviceInfo(true)
            onInitSession(this)
            val deviceInfo = DeviceInfoPacket(data.packet)
            operationsSupported.addAll(deviceInfo.operationsSupported.toHashSet())

            name = usbDevice.productName
            serialNumber = deviceInfo.serialNumber
            manufacturer = deviceInfo.manufacturer
        }
    }

    protected abstract fun onInitSession(session: PtpSession)
    protected fun getPtpSession(): PtpSession = session ?: throw PtpConnectionError("Session not initialized")

    override suspend fun downloadImage(imageId: Int, file: File) = withContext(dispatcher) {
        val session = getPtpSession()
        FileOutputStream(file).use { stream ->
            var totalReceivedSize = 0
            session.getObject(imageId, object: DataCallback {
                override fun receivedDataPacket(
                    transactionid: Int,
                    totaldatasize: Long,
                    cumulateddatasize: Long,
                    data: ByteArray,
                    offset: Int,
                    length: Int
                ) {
                    stream.write(data, offset, length)
                    totalReceivedSize += length
                    if (totalReceivedSize >= totaldatasize.toInt()) {
                        stream.flush()
                        stream.close()
                    }
                }
            })
        }
    }

    override suspend fun downloadThumbnail(imageId: Int, file: File) = withContext(dispatcher) {
        val session = getPtpSession()
        FileOutputStream(file).use { stream ->
            var totalReceivedSize = 0
            session.getThumb(imageId, object: DataCallback {
                override fun receivedDataPacket(
                    transactionid: Int,
                    totaldatasize: Long,
                    cumulateddatasize: Long,
                    data: ByteArray,
                    offset: Int,
                    length: Int
                ) {
                    stream.write(data, offset, length)
                    totalReceivedSize += length
                    if (totalReceivedSize >= totaldatasize.toInt()) {
                        stream.flush()
                        stream.close()
                    }
                }
            })
        }
    }

    override suspend fun getCameraImages(): List<CameraImage> = withContext(dispatcher) {
        if (cameraImagesCache.isNotEmpty()) {
            return@withContext cameraImagesCache
        }

        val session = getPtpSession()
        val objectIds = session.getObjectHandles(-1, 0) ?: emptyList()

        objectIds.asSequence().mapNotNull { objectId ->
            tryOrNull {
                session.getObjectInfo(objectId).let {
                    objectId to ObjectInfoPacket(it.packet)
                }
            }
        }.filter { (_, objectInfoPacket) -> objectInfoPacket.objectFormat == ObjectFormat.EXIF_JPEG }
            .map { (id, objectInfoPacket) ->
                val timeStamp = objectInfoPacket.dateCreated.toTimestamp()
                // To avoid the same file name, we add the timestamp to the file name
                val uniqueName = timeStamp.toString() + "_" + objectInfoPacket.filename
                CameraImage(
                    id = id,
                    format = objectInfoPacket.objectFormat.name,
                    fileName = uniqueName,
                    width = objectInfoPacket.imagePixWidth,
                    height = objectInfoPacket.imagePixHeight,
                    dataCreated = objectInfoPacket.dateCreated
                )
            }.toList()
            .also {
                cameraImagesCache = it
            }
    }

    override suspend fun getCameraImage(imageId: Int): CameraImage? = withContext(dispatcher) {
        val session = getPtpSession()
        val objectInfoPacket = ObjectInfoPacket(session.getObjectInfo(imageId).packet)
        if (objectInfoPacket.objectFormat != ObjectFormat.EXIF_JPEG) {
            return@withContext null
        }

        val timeStamp = objectInfoPacket.dateCreated.toTimestamp()
        val uniqueName = timeStamp.toString() + "_" + objectInfoPacket.filename

        CameraImage(
            id = imageId,
            format = objectInfoPacket.objectFormat.name,
            fileName = uniqueName,
            width = objectInfoPacket.imagePixWidth,
            height = objectInfoPacket.imagePixHeight,
            dataCreated = objectInfoPacket.dateCreated
        )
    }

    override fun release() {
        session?.closeAndDisconnect()
    }

    private fun createPtpUsbEndpoints(usbDevice: UsbDevice): PtpUsbEndpoints {
        val usbDeviceConnection = usbManager.openDevice(usbDevice)
        return AndroidUsbEndpoints(usbDeviceConnection, selectInterfaceForPtp(usbDevice))
    }

    // TODO check the index of the interface, 3 is a magic number here, UsbEndpoint size always is 3?
    private fun selectInterfaceForPtp(usbDevice: UsbDevice): UsbInterface {
        return usbDevice.getInterfaces().first {
            it.endpointCount >= 3
        }
    }

    override fun getSerialNumber(): String {
        return serialNumber ?: throw PtpConnectionError("usbDevice not initialized")
    }

    override fun getManufacturer(): String {
        return manufacturer ?: throw PtpConnectionError("usbDevice not initialized")
    }

    override fun getUsbDeviceId(): Int {
        return usbDevice?.deviceId ?: throw PtpConnectionError("usbDevice not initialized")
    }

    override fun getName(): String {
        return name ?: throw PtpConnectionError("usbDevice not initialized")
    }

    companion object {
        private const val APP_NAME = "RAPID"
    }

}