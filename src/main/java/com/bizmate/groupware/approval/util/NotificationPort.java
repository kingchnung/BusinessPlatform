package com.bizmate.groupware.approval.util;

import java.util.List;

public interface NotificationPort {
    void notifyUsers(List<String> emails, String subject, String body);
}
