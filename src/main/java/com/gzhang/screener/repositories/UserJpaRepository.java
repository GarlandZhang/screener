package com.gzhang.screener.repositories;

import com.gzhang.screener.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<AppUser, Integer> {
}
