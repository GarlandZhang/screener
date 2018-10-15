package com.gzhang.screener.repositories;


import com.gzhang.screener.models.AppUser;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    UserJpaRepository userJpaRepository;

    @Override
    public AppUser save(AppUser appUser) {
        return userJpaRepository.save(appUser);
    }
}
