package com.linein.ptplib.connection.ptpusb;

import com.linein.ptplib.connection.PtpConnection;
import com.linein.ptplib.constants.PtpRc;
import com.linein.ptplib.datacallbacks.DataCallback;
import com.linein.ptplib.exceptions.InitFailException;
import com.linein.ptplib.exceptions.ReceiveDataException;
import com.linein.ptplib.exceptions.ResponseNotOkException;
import com.linein.ptplib.exceptions.SendDataException;
import com.linein.ptplib.packets.Packet;

public class PtpConnection_Usb implements PtpConnection
{
	private PtpUsbPort con;
	private String deviceName;

	public PtpConnection_Usb(PtpUsbPort con)
	{
		this.con = con;
		deviceName = "";
	}

	private static short getPacketType(Packet p)
	{
		if(p==null || p.getBufferSize()<6)
			return PacketType.Empty;
		return p.getShortL(4);
	}

	private static short getResponseCode(Packet p)
	{
		if(p==null || p.getBufferSize()<8)
			return PtpRc.General_Error;
		return p.getShortL(6);
	}

	public void sendRequest(short reqcode, int tid, int[] param, int timeout) throws ResponseNotOkException, ReceiveDataException, SendDataException
	{
		Packet cmd = new CommandPacket(reqcode, tid, param!=null?param:new int[]{});
		con.sendPacket(cmd, timeout);

		Packet response = con.receivePacket(true, timeout);
		if(getPacketType(response)==PacketType.Response) {
			checkResponseCode(response);
		}
	}

	private void checkResponseCode(Packet response) throws ResponseNotOkException
	{
		short responseCode = getResponseCode(response);
		if(responseCode!= PtpRc.OK) {
			throw ResponseNotOkException.create(responseCode);
		}
	}

//	@Override
//	public Packet requestPacket(short reqcode, int tid, int[] param, int timeout) throws ReceiveDataException, ResponseNotOkException, SendDataException
//	{
//		//		PtpLog.debug("requestPacket");
//
//		con.sendPacket(new CommandPacket(reqcode, tid, param!=null?param:new int[]{}), timeout);
//
//		Packet datapacket = con.receivePacket(false, timeout);
//		Packet response = con.receivePacket(true, timeout);
//
//		if(getPacketType(response)==PacketType.Response)
//		{
//			if(getResponseCode(response)!= PtpRc.OK)
//				throw new ResponseNotOkException(getResponseCode(response));
//		}
//		return datapacket;
//	}

	@Override
	public void requestData(DataCallback cb, short reqcode, int tid, int[] param, int timeout) throws ReceiveDataException, ResponseNotOkException, SendDataException
	{
		con.sendPacket(new CommandPacket(reqcode, tid, param!=null?param:new int[]{}), timeout);
		con.receiveData(cb, timeout);

		Packet response = con.receivePacket(true, timeout);
		if(getPacketType(response)==PacketType.Response) {
			checkResponseCode(response);
		}
	}

	@Override
	public void sendPacket(Packet data, short reqcode, int tid, int[] param, int timeout) throws ResponseNotOkException, ReceiveDataException, SendDataException
	{
		con.sendPacket(new CommandPacket(reqcode, tid, param!=null?param:new int[]{}), timeout);

		con.sendPacket(new DataPacket(reqcode, tid, data.getBuffer()), timeout);

		Packet response = con.receivePacket(true, timeout);
		if(getPacketType(response)==PacketType.Response) {
			checkResponseCode(response);
		}
	}

	@Override
	public String getDeviceName()
	{
		return deviceName;
	}

	@Override
	public void connectAndInit(byte[] guid, String devicename, Runnable waitForAck) throws InitFailException
	{
		con.initializeInterface();
		con.resetDevice();
		deviceName = devicename;
	}

	public boolean isConnected()
	{
		return con.isInitialized();
	}


	@Override
	public void disconnect()
	{
		con.releaseInterface();
	}


	class PacketType
	{
		final static short Empty = 0;
		final static short Command = 1;
		final static short Data = 2;
		final static short Response = 3;
		final static short Event = 4;
	}

	class CommandPacket extends Packet
	{
		CommandPacket(short opCode, int transactionid, int[] params)
		{
			super(12 + (params!=null?params.length*4:0));

			setIntL(0, 12 + (params!=null?params.length*4:0));
			setShortL(4, PacketType.Command);
			setShortL(6, opCode);
			setIntL(8, transactionid);

			for(int i=0; params!=null && i<params.length; i++)
				setIntL(12+i*4, params[i]);
		}
	}

	class DataPacket extends Packet
	{
		DataPacket(short opCode, int transactionid, byte[] data)
		{
			super(12 + (data.length));

			setIntL(0, 12 + (data.length));
			setShortL(4, PacketType.Data);
			setShortL(6, opCode);
			setIntL(8, transactionid);

			setBytes(12, data);
		}
	}

}
