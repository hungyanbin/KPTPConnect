package com.linein.ptplib.exceptions;

import com.linein.ptplib.constants.PtpRc;

public class ResponseNotOkException extends Exception
{
	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;
	private short code;
	
	private ResponseNotOkException(String responseCode)
	{
		super("ResponseNotOkException: " + responseCode);
	}

	public short getResponseCode()
	{
		return code;
	}

	public static ResponseNotOkException create(short code) {
		switch (code) {
			case PtpRc.General_Error:
				return new ResponseNotOkException("General_Error");
			case PtpRc.Session_Not_Open:
				return new ResponseNotOkException("Session_Not_Open");
			case PtpRc.Invalid_TransactionID:
				return new ResponseNotOkException("Invalid_TransactionID");
			case PtpRc.Operation_Not_Supported:
				return new ResponseNotOkException("Operation_Not_Supported");
			case PtpRc.Parameter_Not_Supported:
				return new ResponseNotOkException("Parameter_Not_Supported");
			case PtpRc.Incomplete_Transfer:
				return new ResponseNotOkException("Incomplete_Transfer");
			case PtpRc.Invalid_StorageID:
				return new ResponseNotOkException("Invalid_StorageID");
			case PtpRc.Invalid_ObjectHandle:
				return new ResponseNotOkException("Invalid_ObjectHandle");
			case PtpRc.InvalidDevicePropFormat:
				return new ResponseNotOkException("InvalidDevicePropFormat");
			case PtpRc.InvalidDevicePropValue:
				return new ResponseNotOkException("InvalidDevicePropValue");
			case PtpRc.DevicePropNotSupported:
				return new ResponseNotOkException("DevicePropNotSupported");
			case PtpRc.ObjectWriteProtected:
				return new ResponseNotOkException("ObjectWriteProtected");
			case PtpRc.StoreFull:
				return new ResponseNotOkException("StoreFull");
			case PtpRc.Access_Denied:
				return new ResponseNotOkException("Access_Denied");
			case PtpRc.NoThumbnailPresent:
				return new ResponseNotOkException("NoThumbnailPresent");
			case PtpRc.Invalid_Parameter:
				return new ResponseNotOkException("Invalid_Parameter");
			case PtpRc.SessionAlreadyOpened:
				return new ResponseNotOkException("SessionAlreadyOpened");
			case PtpRc.Transaction_Canceled:
				return new ResponseNotOkException("Transaction_Canceled");
			case PtpRc.StoreNotAvailable:
				return new ResponseNotOkException("StoreNotAvailable");
			case PtpRc.Busy:
				return new ResponseNotOkException("Busy");
			default:
				return new ResponseNotOkException("Unknown");
		}
	}
}
