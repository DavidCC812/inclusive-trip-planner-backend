package com.example.moonshot.usersetting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, UUID> {

    Optional<UserSetting> findByUserIdAndSettingId(UUID userId, UUID settingId);

    List<UserSetting> findByUserId(UUID userId);
}
