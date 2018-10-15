package com.gzhang.screener.iomodels;

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
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "appUser",
            cascade = CascadeType.ALL)
    List<ScreenIndicatorGrouping> screenIndicatorGroupingList;
}
