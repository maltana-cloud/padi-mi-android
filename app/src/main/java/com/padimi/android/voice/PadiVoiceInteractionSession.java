package com.padimi.android.voice;

import android.content.Context;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;

public final class PadiVoiceInteractionSession extends VoiceInteractionSession {
    public PadiVoiceInteractionSession(Context context) {
        super(context);
    }

    @Override
    public void onShow(Bundle args, int showFlags) {
        super.onShow(args, showFlags);
    }
}
