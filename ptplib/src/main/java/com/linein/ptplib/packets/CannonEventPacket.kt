package com.linein.ptplib.packets

// To parse other types of events, See https://github.com/gphoto/libgphoto2/blob/master/camlibs/ptp2/ptp-pack.c#L2080

data class CanonEventPacket(
    val size: Int,
    val event: CannonEvent
) {

    companion object {
        const val offset_size = 0
        const val offset_eventCode = 4

        fun fromPacket(packet: Packet): CanonEventPacket? {
            val (size, eventCode) = parseSizeAndEventCode(packet) ?: return null
            return CanonEventPacket(size, eventCode)
        }

        fun parseSizeAndEventCode(packet: Packet): Pair<Int, CannonEvent>? {
            val size = packet.getIntL(offset_size)
            val rawEventCode = packet.getIntL(offset_eventCode)
            if (rawEventCode == 0) return null

            val eventCode = CannonEvent.values().firstOrNull { it.eventCode == rawEventCode }
                ?: throw IllegalArgumentException("Unknown event code: $rawEventCode")
            return Pair(size, eventCode)
        }
    }
}

enum class CannonEvent(val eventCode: Int) {
    RequestGetEvent(0xc101),
    RequestCancelTransferMA(0xc180),
    ObjectAddedEx(0xc181),
    ObjectRemovedEx(0xc182),
    RequestGetObjectInfoEx(0xc183),
    StorageStatusChanged(0xc184),
    StorageInfoChanged(0xc185),
    RequestObjectTransfer(0xc186),
    ObjectInfoChangedEx(0xc187),
    ObjectContentChanged(0xc188),
    PropValueChanged(0xc189),
    AvailListChanged(0xc18a),
    CameraStatusChanged(0xc18b),
    WillSoonShutdown(0xc18d),
    ShutdownTimerUpdated(0xc18e),
    RequestCancelTransfer(0xc18f),
    RequestObjectTransferDT(0xc190),
    RequestCancelTransferDT(0xc191),
    StoreAdded(0xc192),
    StoreRemoved(0xc193),
    BulbExposureTime(0xc194),
    RecordingTime(0xc195),
    InnerDevelopParam(0xc196),
    RequestObjectTransferDevelop(0xc197),
    GPSLogOutputProgress(0xc198),
    GPSLogOutputComplete(0xc199),
    TouchTrans(0xc19a),
    RequestObjectTransferExInfo(0xc19b),
    PowerZoomInfoChanged(0xc19d),
    RequestPushMode(0xc19f),
    RequestObjectTransferTS(0xc1a2),
    AfResult(0xc1a3),
    CTGInfoCheckComplete(0xc1a4),
    OLCInfoChanged(0xc1a5),
    ObjectAddedEx64(0xc1a7),
    ObjectInfoChangedEx64(0xc1a8),
    RequestObjectTransfer64(0xc1a9),
    RequestObjectTransferDT64(0xc1aa),
    RequestObjectTransferFTP64(0xc1ab),
    RequestObjectTransferInfoEx64(0xc1ac),
    RequestObjectTransferMA64(0xc1ad),
    ImportError(0xc1af),
    BlePairing(0xc1b0),
    RequestAutoSendImages(0xc1b1),
    RequestTranscodedBlockTransfer(0xc1b2),
    RequestCAssistImage(0xc1b4),
    RequestObjectTransferFTP(0xc1f1);
}