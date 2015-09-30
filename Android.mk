# Copyright 2011, The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)


LOCAL_STATIC_JAVA_LIBRARIES := \
        android-support-v13 \
        android-support-v4 \
        libhiveviewcore \
        libtv

LOCAL_JAVA_LIBRARIES := amlogic.pppoe android-support-v4
LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)
LOCAL_SRC_FILES += src/com/hiveview/cloudtv/settings/download/IDownloadService.aidl
LOCAL_SRC_FILES += src/com/hiveview/cloudtv/settings/download/IDownloadCallback.aidl

LOCAL_PACKAGE_NAME := CloudSettings
LOCAL_CERTIFICATE := platform
LOCAL_DEX_PREOPT := false

include $(BUILD_PACKAGE)

##############################################

include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES := libhiveviewcore:libs/hiveviewcore.jar \
																				libtv:libs/tv.jar

include $(BUILD_MULTI_PREBUILT)



# Use the folloing include to make our test apk.
include $(call all-makefiles-under,$(LOCAL_PATH))
