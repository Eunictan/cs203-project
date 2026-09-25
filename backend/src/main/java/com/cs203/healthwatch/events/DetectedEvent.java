package com.cs203.healthwatch.events;

import com.cs203.healthwatch.common.BaseEntity;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@Getter @Setter @NoArgsConstructor
public class DetectedEvent extends BaseEntity {

    @Column(nullable = false)
    private String signalType;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private Instant detectedAt;

    @Column(nullable = false)
    private Double deviation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    private boolean isReplay = false;
}