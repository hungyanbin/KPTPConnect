package com.linein.ptplib.packets

import com.linein.ptplib.constants.PtpEventCode

enum class PtpEvent(val eventCode: Short) {
    Undefined(PtpEventCode.Undefined),
    CancelTransaction(PtpEventCode.CancelTransaction),
    ObjectAdded(PtpEventCode.ObjectAdded),
    ObjectRemoved(PtpEventCode.ObjectRemoved),
    StoreAdded(PtpEventCode.StoreAdded),
    StoreRemoved(PtpEventCode.StoreRemoved),
    DevicePropChanged(PtpEventCode.DevicePropChanged),
    ObjectInfoChanged(PtpEventCode.ObjectInfoChanged),
    DeviceInfoChanged(PtpEventCode.DeviceInfoChanged),
    RequestObjectTransfer(PtpEventCode.RequestObjectTransfer),
    StoreFull(PtpEventCode.StoreFull),
    DeviceReset(PtpEventCode.DeviceReset),
    StorageInfoChanged(PtpEventCode.StorageInfoChanged),
    CaptureComplete(PtpEventCode.CaptureComplete),
    UnreportedStatus(PtpEventCode.UnreportedStatus),
    ObjectPropChanged(PtpEventCode.ObjectPropChanged),
    ObjectPropDescChanged(PtpEventCode.ObjectPropDescChanged),
    ObjectReferencesChanged(PtpEventCode.ObjectReferencesChanged),
    ;


    companion object {
        private const val offset_eventCode = 0

        fun fromPacket(packet: Packet): PtpEvent? {
            val rawEventCode = packet.getIntL(offset_eventCode).toShort()
            if (rawEventCode == 0.toShort()) return null

            return PtpEvent.values().firstOrNull { it.eventCode == rawEventCode }
                ?: throw IllegalArgumentException("Unknown event code: $rawEventCode")
        }
    }
}

//#define PTP_EC_Nikon_ObjectAddedInSDRAM		0xC101	/* e1: objecthandle */
//#define PTP_EC_Nikon_CaptureCompleteRecInSdram	0xC102	/* no args */
///* Gets 1 parameter, objectid pointing to DPOF object */
//#define PTP_EC_Nikon_AdvancedTransfer		0xC103
//#define PTP_EC_Nikon_PreviewImageAdded		0xC104
//#define PTP_EC_Nikon_MovieRecordInterrupted	0xC105	/* e1: errocode, e2: recordkind */
//#define PTP_EC_Nikon_1stCaptureComplete		0xC106	/* 1st phase of mirror up is complete */
//#define PTP_EC_Nikon_MirrorUpCancelComplete	0xC107	/* mirror up canceling is complete */
//#define PTP_EC_Nikon_MovieRecordComplete	0xC108	/* e1: recordkind */
//#define PTP_EC_Nikon_MovieRecordStarted		0xC10A	/* e1: recordkind */
//#define PTP_EC_Nikon_PictureControlAdjustChanged	0xC10B	/* e1: picctrlitem e2: shootingmode */
//#define PTP_EC_Nikon_LiveViewStateChanged	0xC10C	/* e1: liveview state */
//#define PTP_EC_Nikon_ManualSettingsLensDataChanged	0xC10E	/* e1: lensnr */
//#define PTP_EC_Nikon_ActiveSelectionInterrupted	0xC112	/* e1: errorcode */
//#define PTP_EC_Nikon_SBAdded			0xC120	/* e1: sbhandle */
//#define PTP_EC_Nikon_SBRemoved			0xC121	/* e1: sbhandle */
//#define PTP_EC_Nikon_SBAttrChanged		0xC122	/* e1: sbhandle, e2: attrid */
//#define PTP_EC_Nikon_SBGroupAttrChanged		0xC123	/* e1: sbgroupid, e2: groupattrid */