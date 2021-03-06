package com.cchat.utils;

import java.io.File;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Environment;

public class AudioRecorder
{
	private static int SAMPLE_RATE_IN_HZ = 8000; 

	final MediaRecorder recorder = new MediaRecorder();
	final String path;

	public AudioRecorder(String Filepath, String fileName)
	{
		this.path = sanitizePath(Filepath,fileName);
	}

	private String sanitizePath(String Filepath, String fileName)
	{
		/*if (!fileName.startsWith("/"))
		{
			fileName = "/" + fileName;
		}*/
		if (!fileName.contains("."))
		{
			fileName += ".amr";
		}
		return Filepath + fileName;
	}

	public void start() throws IOException
	{
		String state = Environment.getExternalStorageState();
		if (!state.equals(Environment.MEDIA_MOUNTED)) { throw new IOException(
				"SD Card is not mounted,It is  " + state + "."); }
		File directory = new File(path).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) { throw new IOException(
				"Path to file could not be created"); }
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
		recorder.setOutputFile(path);
		recorder.prepare();
		recorder.start();
	}

	public void stop() throws IOException
	{
		recorder.stop();
		recorder.release();
	}
	
	public double getAmplitude() {		
		if (recorder != null){			
			return  (recorder.getMaxAmplitude());		
			}		
		else			
			return 0;	
		}
}