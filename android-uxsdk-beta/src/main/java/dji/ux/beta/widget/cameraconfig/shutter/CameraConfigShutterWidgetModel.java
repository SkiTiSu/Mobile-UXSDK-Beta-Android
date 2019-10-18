/*
 * Copyright (c) 2018-2019 DJI
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dji.ux.beta.widget.cameraconfig.shutter;

import androidx.annotation.NonNull;

import dji.common.camera.ExposureSettings;
import dji.common.camera.SettingsDefinitions;
import dji.keysdk.CameraKey;
import dji.keysdk.DJIKey;
import dji.thirdparty.io.reactivex.Flowable;
import dji.ux.beta.base.DJISDKModel;
import dji.ux.beta.base.WidgetModel;
import dji.ux.beta.base.uxsdkkeys.ObservableInMemoryKeyedStore;
import dji.ux.beta.util.DataProcessor;
import dji.ux.beta.util.SettingDefinitions;

/**
 * Widget Model for the {@link CameraConfigShutterWidget} used to define
 * the underlying logic and communication
 */
public class CameraConfigShutterWidgetModel extends WidgetModel {
    //region Fields
    private DataProcessor<ExposureSettings> exposureSettingsProcessor;
    private DataProcessor<SettingsDefinitions.ShutterSpeed> shutterSpeedProcessor;
    private int cameraIndex;
    //endregion

    //region Constructor
    public CameraConfigShutterWidgetModel(@NonNull DJISDKModel djiSdkModel,
                                          @NonNull ObservableInMemoryKeyedStore keyedStore) {
        super(djiSdkModel, keyedStore);
        this.cameraIndex = SettingDefinitions.CameraIndex.CAMERA_INDEX_0.getIndex();
        exposureSettingsProcessor = DataProcessor.create(new ExposureSettings(SettingsDefinitions.Aperture.UNKNOWN,
                SettingsDefinitions.ShutterSpeed.UNKNOWN,
                0,
                SettingsDefinitions.ExposureCompensation.UNKNOWN));
        shutterSpeedProcessor = DataProcessor.create(SettingsDefinitions.ShutterSpeed.UNKNOWN);
    }
    //endregion

    //region Data

    /**
     * Set the index of the camera for which the widget model should react
     *
     * @param cameraIndex camera index
     */
    public void setCameraIndex(@NonNull SettingDefinitions.CameraIndex cameraIndex) {
        this.cameraIndex = cameraIndex.getIndex();
        restart();
    }

    /**
     * Get the current index of the camera the widget model is reacting to
     *
     * @return current camera index
     */
    @NonNull
    public SettingDefinitions.CameraIndex getCameraIndex() {
        return SettingDefinitions.CameraIndex.find(cameraIndex);
    }

    /**
     * Get the shutter speed.
     *
     * @return Flowable for the DataProcessor that user should subscribe to.
     */
    public Flowable<SettingsDefinitions.ShutterSpeed> getShutterSpeed() {
        return shutterSpeedProcessor.toFlowable();
    }
    //endregion

    //region LifeCycle
    @Override
    protected void inSetup() {
        DJIKey exposureSettingsKey = CameraKey.create(CameraKey.EXPOSURE_SETTINGS, cameraIndex);
        bindDataProcessor(exposureSettingsKey, exposureSettingsProcessor, exposureSettings -> {
            ExposureSettings settings = (ExposureSettings) exposureSettings;
            if (settings.getShutterSpeed() != null) {
                shutterSpeedProcessor.onNext(settings.getShutterSpeed());
            }
        });
    }

    @Override
    protected void inCleanup() {
        //Nothing to cleanup
    }

    @Override
    protected void updateStates() {
        //Nothing to update
    }
    //endregion
}
