/*
 * wmt-spi-lcd-jni.h
 *
 *  Created on: 2014/7/22
 *      Author: ¾G²M¤å
 */
extern "C" {
#ifndef WMT_SPI_LCD_JNI_H_
#define WMT_SPI_LCD_JNI_H_

#define WMT_SPI_LCD_IOCTL_MAGIC 0x11
#define WMT_SPI_LCD_STATE_HAPPY _IOW(WMT_SPI_LCD_IOCTL_MAGIC, 0, int)
#define WMT_SPI_LCD_STATE_SUN _IOW(WMT_SPI_LCD_IOCTL_MAGIC, 1, int)
#define WMT_SPI_LCD_STATE_CLOUDY _IOW(WMT_SPI_LCD_IOCTL_MAGIC, 2, int)
#define WMT_SPI_LCD_STATE_SAD _IOW(WMT_SPI_LCD_IOCTL_MAGIC, 3, int)
#define WMT_SPI_LCD_STATE_PRINT _IOW(WMT_SPI_LCD_IOCTL_MAGIC, 4, int)
#define WMT_SPI_LCD_STATE_MESSAGE  _IOW(WMT_SPI_LCD_IOCTL_MAGIC, 5, int)

#endif /* WMT_SPI_LCD_JNI_H_ */
}
