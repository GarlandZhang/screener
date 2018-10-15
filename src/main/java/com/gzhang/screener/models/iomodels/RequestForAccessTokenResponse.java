package com.gzhang.screener.models.iomodels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestForAccessTokenResponse {
    int user_id;
    String access_token;
    String scope;
    String username;
}
