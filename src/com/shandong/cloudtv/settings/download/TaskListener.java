package com.shandong.cloudtv.settings.download;

public interface TaskListener {

	public void taskStarted(Task task);

	// max means the max value in progress bar. value is the current value.
	public void taskProgress(Task task, long value, long max);

	public void taskCompleted(Task task);

	public void taskFailed(Task task, String ex);

	public void taskCancelled(Task task);

	public void taskCancelCompleted(Task task);
}