
package com.example.demo.service.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingService {

  public void sendNotification(String token, String title, String body, String messageImageUrl,
      String authorAvatarUrl) {
    Notification.Builder notificationBuilder = Notification.builder()
        .setTitle(title)
        .setBody(body);

    // velika slika ako ima imageurl 
    if (messageImageUrl != null && !messageImageUrl.isBlank()) {
      notificationBuilder.setImage(messageImageUrl);
    }

    Message message = Message.builder()
        .setToken(token)
        .setNotification(notificationBuilder.build())
        .putData("authorAvatar", authorAvatarUrl != null ? authorAvatarUrl : "")
        .putData("title", title)
        .putData("body", body)
        .putData("messageImage", messageImageUrl != null ? messageImageUrl : "")
        .build();

    try {
      String response = FirebaseMessaging.getInstance().send(message);
      System.out.println("Successfully sent message: " + response);
    } catch (Exception e) {
      System.err.println("Error sending FCM message: " + e.getMessage());
    }
  }

}
