LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE    := wmt-spi-lcd-jni
LOCAL_SRC_FILES := wmt-spi-lcd-jni.cpp
LOCAL_LDLIBS := -llog -lGLESv2
include $(BUILD_SHARED_LIBRARY)

# Add prebuilt libiconv
include $(CLEAR_VARS)
APP_ABI := armeabi armeabi-v7a x86 mips
LOCAL_MODULE := libiconv
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libiconv.so
include $(PREBUILT_SHARED_LIBRARY)

# Add prebuilt libzbarjni
include $(CLEAR_VARS)
APP_ABI := armeabi armeabi-v7a x86 mips
LOCAL_MODULE := libzbarjni
LOCAL_SRC_FILES := $(TARGET_ARCH_ABI)/libzbarjni.so
include $(PREBUILT_SHARED_LIBRARY)