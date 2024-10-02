package com.linein.ptplib.datacallbacks;

public interface DataCallback
{
	void receivedDataPacket(int transactionid, long totaldatasize, long cumulateddatasize, byte[] data, int offset, int length);
}
