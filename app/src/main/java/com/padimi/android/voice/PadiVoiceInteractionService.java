package com.padimi.android.voice;

import android.content.Intent;
import android.os.Bundle;
import android.service.voice.VoiceInteractionService;

/**
 * Android's global voice-assistant entry point for PADI MI.
 *
 * Android controls the lifecycle of this service. Keeping this service small
 * lets the session do the user-facing work without pretending that a custom
 * hotword is available on every device.
 */
public final class PadiVoiceInteractionService extends VoiceInteractionService {
    @Override
    public void onReady() {
        super.onReady();
    }

    /** Bring the normal PADI MI activity to the foreground when requested. */
    public void openPadiMi() {
        Intent intent = new Intent(this, com.padimi.android.MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onShutdown() {
        super.onShutdown();
    }
}
