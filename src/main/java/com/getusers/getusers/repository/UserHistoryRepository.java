package com.getusers.getusers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.getusers.getusers.model.UserHistory;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Integer> {
}
