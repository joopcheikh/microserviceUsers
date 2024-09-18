package com.getusers.getusers.service;

import java.util.List;

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

    public void saveHistory(String actionType, User user, User oldUser, String adminName, String adminEmail) {
        UserHistory history = new UserHistory();
        history.setActionType(actionType);
        history.setEmail(user.getEmail());
        history.setFirstname(user.getFirstname());
        history.setLastname(user.getLastname());
        history.setType_candidat(user.getType_candidat());
        history.setAdminName(adminName);
        history.setAdminEmail(adminEmail);

        // Save the history record
        userHistoryRepository.save(history);
    }

    public List<UserHistory> getAllHistories() {
        return userHistoryRepository.findAll();
    }
}
