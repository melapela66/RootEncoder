package com.pedro.streamer.rotation.usb

import android.graphics.SurfaceTexture
import com.jiangdg.ausbc.callback.ICaptureCallBack
import com.jiangdg.ausbc.callback.IPreviewDataCallBack
import com.jiangdg.ausbc.camera.*
import com.jiangdg.ausbc.camera.bean.CameraRequest
import com.jiangdg.ausbc.camera.bean.PreviewSize
import com.jiangdg.ausbc.utils.Utils
import com.jiangdg.usb.USBMonitor
import com.jiangdg.uvc.UVCCamera

/**
 * Created by pedro on 15/2/24.
 */
class CameraClientCustom internal constructor(builder: Builder) {
  private val mCamera: ICameraStrategy? = builder.camera
  private var mRequest: CameraRequest? = builder.cameraRequest
  private var running = false

  init {
    mRequest = mRequest ?: CameraRequest.Builder().create()
  }

  fun openCamera(surfaceTexture : SurfaceTexture){
    mCamera?.startPreview(mRequest!!, surfaceTexture)
    running = true
  }

  /**
   * Close camera
   */
  fun closeCamera() {
    mCamera?.stopPreview()
    running = false
  }


  fun captureImage(callBack: ICaptureCallBack, path: String? = null) {
    mCamera?.captureImage(callBack, path)
  }

  /**
   * check if camera opened
   *
   * @return camera open status, true or false
   */
  fun isCameraOpened() = running

  /**
   * Add preview raw data call back
   *
   * @param callBack camera preview data call back, see [IPreviewDataCallBack]
   */
  fun addPreviewDataCallBack(callBack: IPreviewDataCallBack) {
    mCamera?.addPreviewDataCallBack(callBack)
  }

  /**
   * Remove preview data call back
   *
   * @param callBack preview data call back, see [IPreviewDataCallBack]
   */
  fun removePreviewDataCallBack(callBack: IPreviewDataCallBack) {
    mCamera?.removePreviewDataCallBack(callBack)
  }

  /**
   * Get all preview sizes
   *
   * @param aspectRatio
   * @return [PreviewSize] list of camera
   */
  fun getAllPreviewSizes(aspectRatio: Double? = null): MutableList<PreviewSize>? {
    return mCamera?.getAllPreviewSizes(aspectRatio)
  }

  /**
   * Get camera request
   *
   * @return a camera request, see [CameraRequest]
   */
  fun getCameraRequest() = mRequest

  /**
   * Get camera strategy
   *
   * @return camera strategy, see [ICameraStrategy]
   */
  fun getCameraStrategy() = mCamera

  /**
   * Send camera command
   *
   * Only effect on uvc camera
   *
   * This method cannot be verified, please use it with caution
   */
  fun sendCameraCommand(command: Int): Int? {
    if (mCamera !is CameraUvcStrategy) {
      return null
    }
    return mCamera.sendCameraCommand(command)
  }

  companion object {
    private const val TAG = "CameraClient"

    @JvmStatic
    fun newBuilder() = Builder()
  }

  class Builder constructor() {
    internal var cameraRequest: CameraRequest? = null
    internal var camera: ICameraStrategy? = null

    /**
     * Set camera strategy
     * <p>
     * @param camera camera strategy, see [ICameraStrategy]
     * @return [CameraClientCustom.Builder]
     */
    fun setCameraStrategy(camera: ICameraStrategy?): Builder {
      this.camera = camera
      return this
    }

    /**
     * Set camera request
     * <p>
     * @param request camera request, see [CameraRequest]
     * @return [CameraClientCustom.Builder]
     */
    fun setCameraRequest(request: CameraRequest): Builder {
      this.cameraRequest = request
      return this
    }

    /**
     * Open debug
     *
     * @param debug debug switch
     * @return [CameraClientCustom.Builder]
     */
    fun openDebug(debug: Boolean): Builder {
      UVCCamera.DEBUG = debug
      USBMonitor.DEBUG = debug
      Utils.debugCamera = debug
      return this
    }

    /**
     * Build for [CameraClientCustom]
     *
     * @return [CameraClientCustom.Builder]
     */
    fun build() = CameraClientCustom(this)
  }
}

