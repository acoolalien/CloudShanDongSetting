/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\androidspace\\CloudShanDongSetting\\src\\com\\shandong\\cloudtv\\settings\\download\\IDownloadCallback.aidl
 */
package com.shandong.cloudtv.settings.download;
public interface IDownloadCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.shandong.cloudtv.settings.download.IDownloadCallback
{
private static final java.lang.String DESCRIPTOR = "com.shandong.cloudtv.settings.download.IDownloadCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.shandong.cloudtv.settings.download.IDownloadCallback interface,
 * generating a proxy if needed.
 */
public static com.shandong.cloudtv.settings.download.IDownloadCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.shandong.cloudtv.settings.download.IDownloadCallback))) {
return ((com.shandong.cloudtv.settings.download.IDownloadCallback)iin);
}
return new com.shandong.cloudtv.settings.download.IDownloadCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_downloadProgress:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
long _arg1;
_arg1 = data.readLong();
this.downloadProgress(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_downloadFinish:
{
data.enforceInterface(DESCRIPTOR);
this.downloadFinish();
reply.writeNoException();
return true;
}
case TRANSACTION_downloadFailed:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.downloadFailed(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_downloadStart:
{
data.enforceInterface(DESCRIPTOR);
this.downloadStart();
reply.writeNoException();
return true;
}
case TRANSACTION_downloadStopedFinish:
{
data.enforceInterface(DESCRIPTOR);
this.downloadStopedFinish();
reply.writeNoException();
return true;
}
case TRANSACTION_networkConnected:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.networkConnected(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.shandong.cloudtv.settings.download.IDownloadCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void downloadProgress(long downloadSize, long max) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(downloadSize);
_data.writeLong(max);
mRemote.transact(Stub.TRANSACTION_downloadProgress, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void downloadFinish() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_downloadFinish, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void downloadFailed(java.lang.String cause) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(cause);
mRemote.transact(Stub.TRANSACTION_downloadFailed, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void downloadStart() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_downloadStart, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void downloadStopedFinish() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_downloadStopedFinish, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void networkConnected(boolean result) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((result)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_networkConnected, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_downloadProgress = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_downloadFinish = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_downloadFailed = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_downloadStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_downloadStopedFinish = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_networkConnected = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
}
public void downloadProgress(long downloadSize, long max) throws android.os.RemoteException;
public void downloadFinish() throws android.os.RemoteException;
public void downloadFailed(java.lang.String cause) throws android.os.RemoteException;
public void downloadStart() throws android.os.RemoteException;
public void downloadStopedFinish() throws android.os.RemoteException;
public void networkConnected(boolean result) throws android.os.RemoteException;
}
