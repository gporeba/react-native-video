package com.brentvatne.exoplayer;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;
import static com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON;
import static com.google.android.exoplayer2.DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;

import com.google.android.exoplayer2.DefaultRenderersFactory.ExtensionRendererMode;
import com.google.android.exoplayer2.util.Log;

public class DolbyDefaultExtensionMode {

    private DolbyDefaultExtensionMode() {
    }

    @ExtensionRendererMode
    public static int getDefaultMode() {
        //check the AC4 and DDP decoder on the device
        MediaCodecList list = null;
        if (SDK_INT >= LOLLIPOP) {
            list = new MediaCodecList(MediaCodecList.ALL_CODECS);
        }
        MediaCodecInfo[] codecInfos = new MediaCodecInfo[0];
        if (SDK_INT >= LOLLIPOP) {
            codecInfos = list.getCodecInfos();
        }
        boolean dlbDDPDevice = false;
        boolean dlbAC4Device = false;

        for (MediaCodecInfo info : codecInfos) {
            if (info.isEncoder()) {
                continue;
            }

            for (String type : info.getSupportedTypes()) {
                if (type.equals("audio/ac4")) {
                    dlbAC4Device = true;
                } else if (type.equals("audio/eac3")) {
                    dlbDDPDevice = true;
                }
            }
        }

        if (dlbAC4Device) {
            Log.i("DAA", "This is a Dolby licensed device with Dolby AC-4 and DDP decoders.");
            Log.i("DAA",
                    "Player should bypass Dolby Audio library and use the existing decoders on the device.");
            return EXTENSION_RENDERER_MODE_OFF;
        } else if (dlbDDPDevice) {
            Log.i("DAA", "This is a Dolby licensed device with DDP decoder only.");
            Log.i("DAA", "For Dolby AC-4 content, player should use Dolby Audio library for decoding.");
            Log.i("DAA",
                    "For DDP content, player should bypass Dolby Audio library and use the decoder on the device.");
            return EXTENSION_RENDERER_MODE_ON;
        } else {
            Log.i("DAA", "This is not a Dolby licensed device.");
            Log.i("DAA", "Player should use Dolby Audio library for decoding Dolby content.");
            return EXTENSION_RENDERER_MODE_PREFER;
        }
    }
}
