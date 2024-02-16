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

package com.pedro.streamer.rotation.usb

import android.content.Context
import android.graphics.SurfaceTexture
import android.os.Message
import com.jiangdg.ausbc.camera.CameraUvcStrategy
import com.jiangdg.ausbc.camera.ICameraStrategy
import com.jiangdg.ausbc.camera.bean.CameraRequest
import com.pedro.library.util.sources.video.VideoSource

/**
 * Created by pedro on 6/2/24.
 */
class CameraUsbSource(context: Context): VideoSource() {

  private var cameraClient: CameraClientCustom? = null
  private val strategy = CameraUvcStrategy(context)

  override fun create(width: Int, height: Int, fps: Int): Boolean {
    this.width = width
    this.height = height
    this.fps = fps
    created = true
    return true
  }

  override fun start(surfaceTexture: SurfaceTexture) {
    this.surfaceTexture = surfaceTexture
    cameraClient = CameraClientCustom.newBuilder().apply {
      setCameraStrategy(strategy)
      setCameraRequest(
        CameraRequest.Builder()
          .setFrontCamera(false)
          .setPreviewWidth(width)
          .setPreviewHeight(height)
          .create()
      )
      openDebug(true)

    }.build()
    cameraClient?.openCamera(this.surfaceTexture!!)
  }

  override fun stop() {
    cameraClient?.closeCamera()
    this.surfaceTexture = null
  }

  override fun release() {
    this.surfaceTexture = null
    cameraClient = null
  }

  override fun isRunning(): Boolean {
    return cameraClient?.isCameraOpened() ?: false
  }
}