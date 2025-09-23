package com.ssginc.unnie.notification.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notifications")
public class NotificationViewController {

    @GetMapping("")
    public String notificationsPage() {
        // templates/notification/notifications.html 파일을 반환
        return "notification/notifications";
    }
}