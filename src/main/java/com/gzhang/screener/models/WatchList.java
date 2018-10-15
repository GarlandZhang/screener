package com.gzhang.screener.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class WatchList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    AppUser appUser;

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "watchList",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<WatchedTicker> watchedTickers;
}
