/*
 * Copyright (C) 2023 pedroSG94.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pedro.streamer.rotation

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.usb.UsbDevice
import android.util.Log
import com.pedro.library.util.sources.video.VideoSource

/**
 * Created by pedro on 6/2/24.
 */
class CameraUSBSource(context: Context): VideoSource(), USBMonitor.OnDeviceConnectListener {

  private val usbMonitor = USBMonitor(context, this)
  private var uvcCamera: UVCCamera? = null
  private var running = false

  fun register() {
    usbMonitor.register()
  }

  fun unregister() {
    usbMonitor.unregister()
  }

  override fun create(width: Int, height: Int, fps: Int): Boolean {
    this.width = width
    this.height = height
    return true
  }

  override fun start(surfaceTexture: SurfaceTexture) {
    running = true
    try {
      uvcCamera?.setPreviewSize(width, height, UVCCamera.FRAME_FORMAT_MJPEG)
    } catch (e: IllegalArgumentException) {
      uvcCamera?.destroy()
      try {
        uvcCamera?.setPreviewSize(width, height, UVCCamera.DEFAULT_PREVIEW_MODE)
      } catch (e1: IllegalArgumentException) {
        return
      }
    }
    uvcCamera?.setPreviewTexture(surfaceTexture)
    uvcCamera?.startPreview()
  }

  override fun stop() {
    running = false
    uvcCamera?.stopPreview()
  }

  override fun release() {
    uvcCamera?.close()
  }

  override fun isRunning(): Boolean {
    return running
  }

  override fun onAttach(device: UsbDevice?) {
    usbMonitor.requestPermission(device)
  }

  override fun onConnect(
    device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?,
    createNew: Boolean
  ) {
    val camera = UVCCamera()
    camera.open(ctrlBlock)
    uvcCamera = camera
  }

  override fun onDisconnect(device: UsbDevice?, ctrlBlock: USBMonitor.UsbControlBlock?) {
    if (uvcCamera != null) {
      uvcCamera?.close()
      uvcCamera = null
    }
  }

  override fun onDettach(device: UsbDevice?) {
    if (uvcCamera != null) {
      uvcCamera?.close()
      uvcCamera = null
    }
  }

  override fun onCancel(device: UsbDevice?) {

  }
}