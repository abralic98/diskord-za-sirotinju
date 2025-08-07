package com.example.demo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

  @PostConstruct
  public void init() throws IOException {
    if (FirebaseApp.getApps().isEmpty()) {
      InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase/serviceAccountKey.json");

      if (serviceAccount == null) {
        throw new IllegalStateException("Firebase serviceAccountKey.json not found");
      }

      FirebaseOptions options = FirebaseOptions.builder()
          .setCredentials(GoogleCredentials.fromStream(serviceAccount))
          .build();

      FirebaseApp.initializeApp(options);
    }
  }
}
