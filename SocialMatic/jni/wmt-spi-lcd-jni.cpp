#include <string.h>
#include <jni.h>
#include <fcntl.h>/*包括文件操作，如open() read() close() write()等 */
//----for output the debug log message
#include <android/log.h>
#include <sys/ioctl.h>
#include "wmt-spi-lcd-jni.h"
#define LOG_TAG  "wmt-spi-lcd-jni"

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOG_TAG,POP_LAST(__VA_ARGS__))

#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

#define DEVICE_NAME "/dev/wmt_spi_lcd"

//device point
int fd;

extern "C" {
jstring Java_com_auly_control_wslClass_stringFromJNI(JNIEnv* env,
		jobject thiz) {
	return env->NewStringUTF("Hello from JNI for vib!");
}

jint Java_com_auly_control_wslClass_Init(JNIEnv* env) {
	LOGE("wslClass_Init() /n");
	fd = open(DEVICE_NAME, O_RDWR); //打開裝置
	LOGE("wslClass_Init()-> fd = %d /n", fd);

	if (fd == -1) {
		LOGE("open device %s error /n ", DEVICE_NAME);
		return 0;
	} else {
		return 1;
	}
}

jint Java_com_auly_control_wslClass_IOCTLVIB(JNIEnv* env, jobject thiz,
		jint controlcode) {
	int CTLCODE = controlcode;
	LOGE("IOCTLVIB() = %x --wslClass_IOCTLVIB /n", CTLCODE);

	switch (CTLCODE) {
	case 0: {
		ioctl(fd, WMT_SPI_LCD_STATE_HAPPY, &controlcode); //call ioctrl socket and pass "WMT_SPI_LCD_STATE_HAPPY" instructtion
		break;
	}
	case 1: {
		ioctl(fd, WMT_SPI_LCD_STATE_SUN, &controlcode);
		break;
	}
	case 2: {
		ioctl(fd, WMT_SPI_LCD_STATE_CLOUDY, &controlcode);
		break;
	}
	case 3: {
		ioctl(fd, WMT_SPI_LCD_STATE_SAD, &controlcode);
		break;
	}
	case 4: {
		ioctl(fd, WMT_SPI_LCD_STATE_PRINT, &controlcode);
		break;
	}
	case 5: {
		ioctl(fd, WMT_SPI_LCD_STATE_MESSAGE, &controlcode);
		break;
	}
	default:
		break;
	}
	return 1;

}
}
