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
public class ScreenIndicatorGrouping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int userId;

    @OneToMany(fetch = FetchType.EAGER,
            mappedBy = "screenIndicatorGrouping",
            cascade = CascadeType.ALL)
    List<ScreenIndicator> screenIndicatorList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    AppUser appUser;
}
