package com.example.moonshot.setting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettingRepository extends JpaRepository<Setting, UUID> {
    Optional<Setting> findBySettingKey(String settingKey);
}

