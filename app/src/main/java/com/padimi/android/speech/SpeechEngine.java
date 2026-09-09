package com.padimi.android.speech;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import java.util.ArrayList;
import java.util.Locale;

public final class SpeechEngine {
    public interface Listener { void onReady(); void onResult(String text); void onError(int code); }
    private final SpeechRecognizer recognizer;
    private final Listener listener;
    public SpeechEngine(Context context, Listener listener) {
        this.listener = listener;
        recognizer = SpeechRecognizer.createSpeechRecognizer(context.getApplicationContext());
        recognizer.setRecognitionListener(new RecognitionListener() {
            public void onReadyForSpeech(Bundle p) { listener.onReady(); }
            public void onBeginningOfSpeech() {}
            public void onRmsChanged(float r) {}
            public void onBufferReceived(byte[] b) {}
            public void onEndOfSpeech() {}
            public void onError(int e) { listener.onError(e); }
            public void onResults(Bundle b) { ArrayList<String> r = b.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION); if (r != null && !r.isEmpty()) listener.onResult(r.get(0)); }
            public void onPartialResults(Bundle b) {}
            public void onEvent(int t, Bundle p) {}
        });
    }
    public void start() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
        recognizer.startListening(i);
    }
    public void stop() { recognizer.stopListening(); }
    public void destroy() { recognizer.destroy(); }
}
