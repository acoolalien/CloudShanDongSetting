/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\androidspace\\CloudShanDongSetting\\src\\com\\shandong\\cloudtv\\settings\\download\\IDownloadService.aidl
 */
package com.shandong.cloudtv.settings.download;
public interface IDownloadService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.shandong.cloudtv.settings.download.IDownloadService
{
private static final java.lang.String DESCRIPTOR = "com.shandong.cloudtv.settings.download.IDownloadService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.shandong.cloudtv.settings.download.IDownloadService interface,
 * generating a proxy if needed.
 */
public static com.shandong.cloudtv.settings.download.IDownloadService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.shandong.cloudtv.settings.download.IDownloadService))) {
return ((com.shandong.cloudtv.settings.download.IDownloadService)iin);
}
return new com.shandong.cloudtv.settings.download.IDownloadService.Stub.Proxy(obj);
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
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.shandong.cloudtv.settings.download.IDownloadCallback _arg0;
_arg0 = com.shandong.cloudtv.settings.download.IDownloadCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
com.shandong.cloudtv.settings.download.IDownloadCallback _arg0;
_arg0 = com.shandong.cloudtv.settings.download.IDownloadCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_startDownload:
{
data.enforceInterface(DESCRIPTOR);
boolean _arg0;
_arg0 = (0!=data.readInt());
this.startDownload(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_stopDownload:
{
data.enforceInterface(DESCRIPTOR);
this.stopDownload();
reply.writeNoException();
return true;
}
case TRANSACTION_isDownloading:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isDownloading();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.shandong.cloudtv.settings.download.IDownloadService
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
@Override public void registerCallback(com.shandong.cloudtv.settings.download.IDownloadCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unregisterCallback(com.shandong.cloudtv.settings.download.IDownloadCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void startDownload(boolean isGroupUser) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(((isGroupUser)?(1):(0)));
mRemote.transact(Stub.TRANSACTION_startDownload, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void stopDownload() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_stopDownload, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean isDownloading() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isDownloading, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_startDownload = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_stopDownload = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_isDownloading = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public void registerCallback(com.shandong.cloudtv.settings.download.IDownloadCallback cb) throws android.os.RemoteException;
public void unregisterCallback(com.shandong.cloudtv.settings.download.IDownloadCallback cb) throws android.os.RemoteException;
public void startDownload(boolean isGroupUser) throws android.os.RemoteException;
public void stopDownload() throws android.os.RemoteException;
public boolean isDownloading() throws android.os.RemoteException;
}
