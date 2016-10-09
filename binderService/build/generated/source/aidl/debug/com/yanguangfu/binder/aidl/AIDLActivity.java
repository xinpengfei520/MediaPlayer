/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\AndroidStudioProjects\\MediaPlayer\\binderService\\src\\main\\aidl\\com\\yanguangfu\\binder\\aidl\\AIDLActivity.aidl
 */
package com.yanguangfu.binder.aidl;
public interface AIDLActivity extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.yanguangfu.binder.aidl.AIDLActivity
{
private static final java.lang.String DESCRIPTOR = "com.yanguangfu.binder.aidl.AIDLActivity";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.yanguangfu.binder.aidl.AIDLActivity interface,
 * generating a proxy if needed.
 */
public static com.yanguangfu.binder.aidl.AIDLActivity asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.yanguangfu.binder.aidl.AIDLActivity))) {
return ((com.yanguangfu.binder.aidl.AIDLActivity)iin);
}
return new com.yanguangfu.binder.aidl.AIDLActivity.Stub.Proxy(obj);
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
case TRANSACTION_performAction:
{
data.enforceInterface(DESCRIPTOR);
com.yanguangfu.binder.aidl.Rect1 _arg0;
if ((0!=data.readInt())) {
_arg0 = com.yanguangfu.binder.aidl.Rect1.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
this.performAction(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.yanguangfu.binder.aidl.AIDLActivity
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
@Override public void performAction(com.yanguangfu.binder.aidl.Rect1 rect) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((rect!=null)) {
_data.writeInt(1);
rect.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_performAction, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_performAction = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void performAction(com.yanguangfu.binder.aidl.Rect1 rect) throws android.os.RemoteException;
}
