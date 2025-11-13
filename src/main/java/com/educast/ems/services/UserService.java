package com.educast.ems.services;

import com.educast.ems.models.User;

public interface UserService {

    /**
     * Update the username and/or password of a user.
     * @param userId User ID to update
     * @param newUsername New username (optional, must be unique if provided)
     * @param newPassword New password (optional, min 6 chars if provided)
     * @return Updated User
     */
    User updateUser(Long userId, String newUsername, String newPassword);
}
