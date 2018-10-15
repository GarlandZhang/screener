package com.gzhang.screener.iomodels;

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
public class ScreenIndicator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    int groupId;
    float parameterPercentChange;
    String parameterTimeInterval;
    boolean parameterDirection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupId", insertable = false, updatable = false)
    ScreenIndicatorGrouping screenIndicatorGrouping;
}
