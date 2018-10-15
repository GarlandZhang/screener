package com.gzhang.screener.repositories;

import com.gzhang.screener.models.AppUser;

public interface UserRepository {

    AppUser save(AppUser stockMetadata);
}
