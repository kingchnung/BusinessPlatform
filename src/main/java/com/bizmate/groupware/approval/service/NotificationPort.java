package com.bizmate.groupware.approval.service;

import java.util.List;

public interface NotificationPort {
    void notifyUsers(List<String> emails, String subject, String body);
}
