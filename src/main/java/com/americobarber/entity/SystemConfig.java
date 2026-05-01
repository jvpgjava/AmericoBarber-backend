package com.americobarber.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "system_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemConfig {

    @Id
    @Column(name = "config_key", length = 100)
    private String key;

    @Column(name = "config_value", columnDefinition = "TEXT")
    private String value;
}
