package com.auly.control;

public class wslClass {
	
	static
	{
	    System.loadLibrary("wmt-spi-lcd-jni");
	} 

	public static native String stringFromJNI();

	public static native int Init();

	public static native int IOCTLVIB(int controlcode);

}
