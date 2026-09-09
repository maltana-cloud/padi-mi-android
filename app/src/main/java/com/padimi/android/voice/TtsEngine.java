package com.padimi.android.voice;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public final class TtsEngine implements TextToSpeech.OnInitListener {
    private final TextToSpeech tts;
    private boolean ready;
    public TtsEngine(Context context) { tts = new TextToSpeech(context.getApplicationContext(), this); }
    @Override public void onInit(int status) { if (status == TextToSpeech.SUCCESS) { ready = true; tts.setLanguage(Locale.getDefault()); } }
    public void speak(String text) { if (ready && text != null && !text.isEmpty()) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "padi"); }
    public void stop() { tts.stop(); }
    public void shutdown() { tts.shutdown(); }
}
