package com.sample.springboot.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Table(indexes = {
        @Index(name = "talent_index", columnList = "talentId"),
        @Index(name = "job_index", columnList = "job_id")
})
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Accessors(chain = true)
@EntityListeners(AuditingEntityListener.class)
public class Shift {
    @Id
    private UUID id;
    @ManyToOne
    @NotNull
    @ToString.Exclude
    @JsonBackReference
    private Job job;
    private UUID talentId;
    private Instant startTime;
    private Instant endTime;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
    @Version
    private long version;
}
