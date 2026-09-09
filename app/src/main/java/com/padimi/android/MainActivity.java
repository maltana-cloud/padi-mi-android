package com.padimi.android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.view.Gravity;
import com.padimi.android.actions.AppLauncher;
import com.padimi.android.actions.SystemActions;
import com.padimi.android.accessibility.PadiAccessibilityService;
import com.padimi.android.brain.Command;
import com.padimi.android.brain.CommandInterpreter;
import com.padimi.android.memory.MemoryStore;
import com.padimi.android.speech.SpeechEngine;
import com.padimi.android.voice.TtsEngine;

public final class MainActivity extends Activity {
    private SpeechEngine speech; private TtsEngine tts; private CommandInterpreter brain; private MemoryStore memory; private TextView status;
    @Override protected void onCreate(Bundle b) { super.onCreate(b); buildUi(); brain = new CommandInterpreter(); memory = new MemoryStore(this); tts = new TtsEngine(this); speech = new SpeechEngine(this, new SpeechEngine.Listener() {
        public void onReady() { status.setText("Listening…"); }
        public void onResult(String text) { status.setText("Heard: " + text); handle(text); }
        public void onError(int code) { status.setText("I couldn't hear that. Try again."); }
    }); }
    private void buildUi() {
        LinearLayout box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setGravity(Gravity.CENTER); box.setPadding(32,32,32,32);
        TextView title = new TextView(this); title.setText("PADI MI"); title.setTextSize(34); title.setGravity(Gravity.CENTER); title.setTextColor(Color.BLACK);
        status = new TextView(this); status.setText("Ready. Tap the microphone and speak."); status.setTextSize(18); status.setGravity(Gravity.CENTER); status.setPadding(0,30,0,30);
        Button listen = new Button(this); listen.setText("🎙  Talk to PADI MI"); listen.setOnClickListener(v -> requestMicAndListen());
        Button accessibility = new Button(this); accessibility.setText("Enable phone control"); accessibility.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        box.addView(title); box.addView(status); box.addView(listen); box.addView(accessibility); setContentView(box);
    }
    private void requestMicAndListen() { if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 10); else speech.start(); }
    private void handle(String raw) {
        Command c = brain.parse(raw); String reply;
        switch (c.type) {
            case HOME: if (PadiAccessibilityService.getInstance()!=null) { SystemActions.home(PadiAccessibilityService.getInstance()); reply="Going home."; } else reply="Please enable PADI MI phone control first."; break;
            case BACK: if (PadiAccessibilityService.getInstance()!=null) { SystemActions.back(PadiAccessibilityService.getInstance()); reply="Going back."; } else reply="Please enable PADI MI phone control first."; break;
            case VOLUME_UP: SystemActions.volume(this,true); reply="Volume up."; break;
            case VOLUME_DOWN: SystemActions.volume(this,false); reply="Volume down."; break;
            case OPEN_APP: boolean opened=AppLauncher.open(this,c.argument); reply=opened?"Opening "+c.argument+".":"I couldn't find an app called "+c.argument+"."; break;
            case REMEMBER: memory.remember(c.argument); reply="I'll remember that."; break;
            case STOP: speech.stop(); tts.stop(); reply="Okay."; break;
            default: reply="I heard you, but I don't know how to do that yet.";
        }
        status.setText(reply); tts.speak(reply);
    }
    @Override protected void onDestroy() { if (speech!=null) speech.destroy(); if (tts!=null) tts.shutdown(); super.onDestroy(); }
}
