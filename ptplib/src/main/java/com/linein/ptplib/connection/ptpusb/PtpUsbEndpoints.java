package com.linein.ptplib.connection.ptpusb;

import com.linein.ptplib.exceptions.InitFailException;
import com.linein.ptplib.exceptions.ReceiveDataException;
import com.linein.ptplib.exceptions.SendDataException;

public interface PtpUsbEndpoints
{
    int writeDataOut(byte[] buffer, int length) throws SendDataException;
    int readDataIn(byte[] buffer) throws ReceiveDataException;
    int getMaxPacketSizeOut();
    int getMaxPacketSizeIn();
    void initalize() throws InitFailException;
    void release();
    int controlTransfer(int requestType, int request, int value, int index, byte[] buffer);
    int getMaxPacketSizeInterrupt();
    void readEvent(byte[] buffer, boolean bulk);
    void setTimeOut(int timeout);
}
