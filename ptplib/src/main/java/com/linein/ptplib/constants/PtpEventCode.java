package com.linein.ptplib.constants;

public class PtpEventCode {

    public final static short Undefined = (short)0x4000;
    public final static short CancelTransaction = (short)0x4001;
    public final static short ObjectAdded = (short)0x4002;
    public final static short ObjectRemoved = (short)0x4003;
    public final static short StoreAdded = (short)0x4004;
    public final static short StoreRemoved = (short)0x4005;
    public final static short DevicePropChanged = (short)0x4006;
    public final static short ObjectInfoChanged = (short)0x4007;
    public final static short DeviceInfoChanged = (short)0x4008;
    public final static short RequestObjectTransfer = (short)0x4009;
    public final static short StoreFull = (short)0x400A;
    public final static short DeviceReset = (short)0x400B;
    public final static short StorageInfoChanged = (short)0x400C;
    public final static short CaptureComplete = (short)0x400D;
    public final static short UnreportedStatus = (short)0x400E;
    public final static short ObjectPropChanged = (short)0xC801;
    public final static short ObjectPropDescChanged = (short)0xC802;
    public final static short ObjectReferencesChanged = (short)0xC803;

}
