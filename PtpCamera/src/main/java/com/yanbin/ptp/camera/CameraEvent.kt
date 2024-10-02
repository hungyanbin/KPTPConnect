package com.yanbin.ptp.camera

import com.linein.ptplib.constants.ObjectFormat

sealed interface CameraEvent {
    class ObjectAddedEvent(val objectId: Int, val storageId: Int, val format: ObjectFormat, val fileName: String): CameraEvent
    object Unknown: CameraEvent
}