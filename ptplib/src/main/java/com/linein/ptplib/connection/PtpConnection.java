package com.linein.ptplib.connection;

import com.linein.ptplib.connection.ptpip.PtpConnection_Ip;
import com.linein.ptplib.connection.ptpip.PtpSocket;
import com.linein.ptplib.connection.ptpusb.PtpConnection_Usb;
import com.linein.ptplib.connection.ptpusb.PtpUsbEndpoints;
import com.linein.ptplib.connection.ptpusb.PtpUsbPort;
import com.linein.ptplib.datacallbacks.DataCallback;
import com.linein.ptplib.exceptions.InitFailException;
import com.linein.ptplib.exceptions.ReceiveDataException;
import com.linein.ptplib.exceptions.ResponseNotOkException;
import com.linein.ptplib.exceptions.SendDataException;
import com.linein.ptplib.packets.Packet;

import java.net.InetAddress;

public interface PtpConnection
{
	String getDeviceName();
	void connectAndInit(byte[] guid, String appname, Runnable waitForAck) throws InitFailException;
	boolean isConnected();
	void disconnect();

	void sendRequest(short reqcode, int tid, int[] param, int timeout) throws ResponseNotOkException, ReceiveDataException, SendDataException;
//	Packet requestPacket(short reqcode, int tid, int[] param, int timeout) throws ReceiveDataException, ResponseNotOkException, SendDataException;

	void sendPacket(Packet data, short reqcode, int tid, int[] param, int timeout) throws ResponseNotOkException, ReceiveDataException, SendDataException;
	void requestData(DataCallback cb, short reqcode, int tid, int[] param, int timeout) throws ReceiveDataException, ResponseNotOkException, SendDataException;

	public static PtpConnection create(InetAddress host)
	{
		return new PtpConnection_Ip(new PtpSocket(host));
	}

	public static PtpConnection create(PtpUsbEndpoints endpoints)
	{
		return new PtpConnection_Usb(new PtpUsbPort(endpoints));
	}
}
