// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE.md file in the project root for full license information.

package com.microsoft.cognitiveservices.speech.samples.quickstart;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Future;

import static android.Manifest.permission.*;

public class MainActivity extends AppCompatActivity {

    private static String speechSubscriptionKey = "YourSubscriptionKey";
    private static String serviceRegion = "YourServiceRegion";

    private ProgressBar progressBar;
    private TextView logTextView;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO, INTERNET}, 5);

        progressBar = findViewById(R.id.progressBar);
        logTextView = findViewById(R.id.logText);
        resultTextView = findViewById(R.id.hello);
    }

    public void onSpeechButtonClicked(View v) {
        showProgress(true);
        logMessage("Speech recognition started at: " + getCurrentTime());

        try (SpeechConfig config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
             SpeechRecognizer reco = new SpeechRecognizer(config)) {

            Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
            SpeechRecognitionResult result = task.get();

            if (result.getReason() == ResultReason.RecognizedSpeech) {
                resultTextView.setText(result.getText());
                logMessage("Recognized: " + result.getText());
            } else {
                resultTextView.setText("Error recognizing. Did you update the subscription info?\n" + result.toString());
                logMessage("Error: " + result.toString());
            }

        } catch (Exception ex) {
            Log.e("SpeechSDKDemo", "unexpected " + ex.getMessage());
            resultTextView.setText("Exception: " + ex.getMessage());
            logMessage("Exception: " + ex.getMessage());
        } finally {
            showProgress(false);
        }
    }

    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void logMessage(String message) {
        if (logTextView != null) {
            String existing = logTextView.getText().toString();
            logTextView.setText(existing + "\n" + message);
        }
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
    }
}
