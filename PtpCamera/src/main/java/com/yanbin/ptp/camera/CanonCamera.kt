package com.yanbin.ptp.camera

import android.content.Context
import com.linein.ptplib.PtpLog
import com.linein.ptplib.connection.PtpSession
import com.linein.ptplib.constants.ObjectFormat
import com.linein.ptplib.constants.PtpOc
import com.linein.ptplib.packets.CannonEvent
import com.linein.ptplib.packets.CanonEventPacket
import com.linein.ptplib.packets.CanonObjectAddedPacket
import com.linein.ptplib.packets.ObjectInfoPacket
import com.linein.ptplib.packets.Packet
import com.yanbin.ptp.utils.toTimestamp
import kotlinx.coroutines.withContext


class CanonCamera(
    context: Context,
) : BasePtpCamera(context) {

    override fun onInitSession(session: PtpSession) {
        session.resolveCanonBusyState()
    }

    override suspend fun getCameraImage(imageId: Int): CameraImage? = withContext(dispatcher) {
        val session = getPtpSession()
        val objectInfoPacket = ObjectInfoPacket.fromPacket(session.getObjectInfo(imageId))
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

    override fun isEventModeSupported(): Boolean {
        return operationsSupported.contains(PtpOc.Canon_SetRemoteMode) && operationsSupported.contains(PtpOc.Canon_SetEventMode)
    }

    override suspend fun setEventMode(enable: Boolean) = withContext(dispatcher) {
        val session = getPtpSession()
        val param = if (enable) 1 else 0
        session.simpleRequest(PtpOc.Canon_SetRemoteMode, intArrayOf(param))
        session.simpleRequest(PtpOc.Canon_SetEventMode, intArrayOf(param))
    }

    override suspend fun getEvents(): List<CameraEvent> = withContext(dispatcher) {
        val session = getPtpSession()

        val eventPacket = session.canon_checkEvents()
        PtpLog.d("getEvents eventPacket: $eventPacket")

        eventPacket.mapNotNull { packet ->
            kotlin.runCatching {
                val canonEventPacket = CanonEventPacket.fromPacket(packet)
                mapCanonEventToCameraEvent(canonEventPacket, packet, session)
            }.onFailure {
                PtpLog.e("Parse event error: ${it.message}", it)
            }.getOrNull()
        }
    }

    private fun mapCanonEventToCameraEvent(
        canonEventPacket: CanonEventPacket?,
        packet: Packet,
        session: PtpSession
    ) = when (val event = canonEventPacket?.event) {
        CannonEvent.ObjectAddedEx -> {
            val objectAddedPacket = CanonObjectAddedPacket.fromPacket(packet)
            PtpLog.d("Object added: $objectAddedPacket")
            val objectInfoPacket = session.getObjectInfo(objectAddedPacket.objectId).let {
                ObjectInfoPacket.fromPacket(it)
            }
            PtpLog.d("objectInfoPacket: $objectInfoPacket")
            CameraEvent.ObjectAddedEvent(
                objectId = objectAddedPacket.objectId,
                storageId = objectInfoPacket.storageId,
                format = objectAddedPacket.format,
                fileName = objectAddedPacket.fileName
            )
        }

        null -> {
            PtpLog.d("No event")
            null
        }

        else -> {
            PtpLog.d("New event: ${event.name}")
            null
        }
    }

    /**
     *  Sometimes the canon camera might be in busy state when transferring data,
     *  sending the Canon_AfCancel command will resolve the issue
     */
    private fun PtpSession.resolveCanonBusyState() {
        simpleRequest(PtpOc.Canon_AfCancel, intArrayOf())
    }
}