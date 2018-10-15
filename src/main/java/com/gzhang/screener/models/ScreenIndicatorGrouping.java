package com.gzhang.screener.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
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
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<ScreenIndicator> screenIndicatorList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    AppUser appUser;

    public void addIndicator(ScreenIndicator screenIndicator) {
        if(screenIndicatorList == null) screenIndicatorList = new ArrayList<>();

        screenIndicator.setGroupId(id);
        screenIndicatorList.add(screenIndicator);
    }

    public boolean removeIndicatorById(int indicatorId) {
        for(int i = 0; i < screenIndicatorList.size(); ++i) {
            if(screenIndicatorList.get(i).getId() == indicatorId) {
                screenIndicatorList.remove(i);
                return true;
            }
        }

        return false;
    }
}
