package com.sample.springboot.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(indexes = {
        @Index(name = "company_index", columnList = "companyId")
})
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Job {

    @Id
    private UUID id;
    @NotNull
    private UUID companyId;
    private Instant startTime;
    private Instant endTime;
    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "job", orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<Shift> shifts = new ArrayList<>();
    @Version
    private long version;
    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;

}
