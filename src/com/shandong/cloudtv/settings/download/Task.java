package com.shandong.cloudtv.settings.download;

import java.util.Date;
import java.util.concurrent.Callable;

import android.util.Log;

public abstract class Task<V> implements Callable<V>, Comparable, Runnable {
	protected TaskListener listener;

	// default priority is zero, low number,high priority
	protected int priority = 0;

	// the lastest task has higher priority. please refer to compareTo();
	protected long timeStamp;

	// if the task is running.
	protected boolean mIsRunning;

	// if the task has been canceled.
	protected  boolean mIsCanceled;

	public Task(TaskListener listener) {
		this.listener = listener;
		timeStamp = new Date().getTime();
		mIsRunning = false;
		mIsCanceled = false;
	}

	public Task(TaskListener listener, int priority) {
		this(listener);
		this.priority = priority;
		mIsRunning = false;
		mIsCanceled = false;
	}

	// sub class must implement this method.
	public abstract void get() throws Exception;

	@Override
	public void run() {
		try {
			call();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public V call() throws Exception {
		V obj = null;
		try {
			mIsRunning = true;
			get();
		} catch (Throwable ex) {
			ex.printStackTrace();
			Log.e("Task", " --- " + ex.getMessage());
			if (listener != null) {
				if (!mIsCanceled) {
					listener.taskFailed(this, ex.toString()+"");
					listener.taskCancelCompleted(this);
				} else {
					listener.taskCancelled(this);
				}
			}
			return null;
		}

		mIsRunning = false;

/*		if (mIsCanceled == false) {
			if (listener != null) {
				Log.d("aa", " --- task Completed");
				listener.taskCompleted(this);
			}
		} else {
			if (listener != null) {
				Log.d("aa", " --- task Cancelled");
				listener.taskCancelled(this);
			}
		}*/

		return obj;
	}

	@Override
	public int compareTo(Object obj) {
		if (obj == null) {
			return -1;
		}

		Task other = (Task) obj;
		int oPriority = other.getPriority();
		long oTimeStamp = other.getTimeStamp();

		// Priority Queue - Delete Min
		// Little number, higher priority
		if (this.priority < oPriority) {
			return -1;
		}

		if (this.priority > oPriority) {
			return 1;
		}

		// Latest time, higher priority
		if (this.timeStamp > oTimeStamp) {
			return -1;
		}

		if (this.timeStamp < oTimeStamp) {
			return 1;
		}

		return 0;
	}

	public TaskListener getListener() {
		return listener;
	}

	public void setListener(TaskListener listener) {
		this.listener = listener;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public boolean isCancel() {
		return mIsCanceled;
	}

	public boolean cancelTask() {
		Log.e("TestSpeed", "cancelTask....................");
		mIsCanceled = true;
		return mIsCanceled;
	}

	public boolean isRunning() {
		return mIsRunning;
	}

}
