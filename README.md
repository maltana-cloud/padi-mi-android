# PADI MI — Android Personal Assistant

PADI MI is a phone-first Android voice assistant designed to understand spoken commands, speak back, and perform legitimate user-requested actions on the device.

## Current build: 0.2.0

Implemented:
- Android SpeechRecognizer voice input
- Android TextToSpeech spoken responses
- Command parsing with common natural-language variants
- Multi-command parsing using "and then", "then", "and", commas and semicolons
- App launching by spoken app name
- Home, Back and volume controls
- Accessibility taps by visible text/content description
- Accessibility typing into the focused text field
- Accessibility scrolling and gesture swipes
- Persistent local memory with the latest 20 remembered items
- "What do you remember?" memory query
- GitHub Actions debug APK build and artifact upload
- Android SDK 34-compatible project configuration

## Architecture

Voice -> SpeechEngine -> CommandInterpreter -> Action layer -> Android APIs / Accessibility -> TTS -> Memory

The action layer is deliberately separated from speech and interpretation so more capabilities can be added without rewriting the assistant core.

## Important limitation

The screen-off / locked-device wake phrase **"Hey Padi Mi"** is not claimed as complete yet. Android background microphone and voice-assistant behavior depend on platform APIs, permissions, device firmware and battery policy. The next stage is a proper VoiceInteractionService design followed by real-device testing.

Accessibility automation must be used only for legitimate user-requested assistance and within Android/Google Play policy requirements.

## Build

The GitHub Actions workflow builds a debug APK and uploads it as the `padi-mi-debug` artifact. A real phone test is still required for microphone behavior, TTS, Accessibility, cross-app automation, memory behavior, performance and background/wake behavior.
