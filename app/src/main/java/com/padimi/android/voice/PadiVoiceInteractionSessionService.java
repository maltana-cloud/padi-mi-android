package com.padimi.android.voice;

import android.service.voice.VoiceInteractionSession;
import android.service.voice.VoiceInteractionSessionService;

public final class PadiVoiceInteractionSessionService extends VoiceInteractionSessionService {
    @Override
    public VoiceInteractionSession onNewSession(android.os.Bundle args) {
        return new PadiVoiceInteractionSession(this);
    }
}
