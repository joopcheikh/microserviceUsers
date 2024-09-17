package com.getusers.getusers.service;

import org.springframework.stereotype.Service;

import com.getusers.getusers.model.User;
import com.getusers.getusers.model.UserHistory;
import com.getusers.getusers.repository.UserHistoryRepository;



@Service
public class UserHistoryService {

    private final UserHistoryRepository userHistoryRepository;

    public UserHistoryService(UserHistoryRepository userHistoryRepository) {
        this.userHistoryRepository = userHistoryRepository;
    }

    public void saveHistory(String actionType, User user, User oldUser, String adminName) {
        UserHistory history = new UserHistory();
        history.setUserId(user.getId());
        history.setActionType(actionType);
        history.setEmail(user.getEmail());
        history.setRole(user.getRole());
        history.setFirstname(user.getFirstname());
        history.setLastname(user.getLastname());
        history.setType_candidat(user.getType_candidat());
        // Set admin information
        history.setAdminName(adminName);

        // Save the history record
        userHistoryRepository.save(history);
    }
}
