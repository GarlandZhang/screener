package com.gzhang.screener.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class StockTwitsUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int websiteGivenId;
    int userId;
    String accessToken;
    String scope;
    String username;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    AppUser appUser;

    public StockTwitsUser(int websiteGivenId, String accessToken, String scope, String username, int userId) {
        this.websiteGivenId = websiteGivenId;
        this.accessToken = accessToken;
        this.scope = scope;
        this.username = username;
        this.userId = userId;
    }
}
