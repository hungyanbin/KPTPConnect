package com.yanbin.ptp.camera

import android.content.Context
import com.linein.ptplib.PtpLog
import com.linein.ptplib.connection.PtpSession
import com.linein.ptplib.constants.PtpOc
import com.linein.ptplib.packets.ObjectAddedEventPacket
import com.linein.ptplib.packets.ObjectInfoPacket
import com.linein.ptplib.packets.Packet
import com.linein.ptplib.packets.PtpEvent
import kotlinx.coroutines.withContext

class NikonCamera(
    context: Context,
): BasePtpCamera(context) {

    override fun onInitSession(session: PtpSession) {

    }

    override suspend fun setEventMode(enable: Boolean) {
        // Maybe don't need this
    }

    override suspend fun getEvents(): List<CameraEvent> = withContext(dispatcher) {
        val session = getPtpSession()

        val eventPacket = session.nikon_checkEvents()
        PtpLog.d("getEvents eventPacket: $eventPacket")

        eventPacket.mapNotNull { packet ->
            kotlin.runCatching {
                val event = PtpEvent.fromPacket(packet)
                mapEventToCameraEvent(event, packet, session)
            }.onFailure {
                PtpLog.e("Parse event error: ${it.message}", it)
            }.getOrNull()
        }
    }

    private fun mapEventToCameraEvent(
        event: PtpEvent?,
        packet: Packet,
        session: PtpSession
    ) = when (event) {
        PtpEvent.ObjectAdded -> {
            val objectAddedPacket = ObjectAddedEventPacket(packet.packet)
            PtpLog.d("Object added: $objectAddedPacket")
            val objectInfoPacket = session.getObjectInfo(objectAddedPacket.objectId).let {
                ObjectInfoPacket(it)
            }
            PtpLog.d("objectInfoPacket: $objectInfoPacket")
            CameraEvent.ObjectAddedEvent(
                objectId = objectAddedPacket.objectId,
                storageId = objectInfoPacket.storageId,
                format = objectInfoPacket.objectFormat,
                fileName = objectInfoPacket.filename
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


    override fun isEventModeSupported(): Boolean {
        return operationsSupported.contains(PtpOc.Nikon_CheckEvent)
    }
}