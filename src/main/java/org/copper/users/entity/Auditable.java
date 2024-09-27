package org.copper.users.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class Auditable {
  private String createdBy;

  @CreationTimestamp
  private LocalDateTime createdDate;

  private String updatedBy;

  @UpdateTimestamp
  private LocalDateTime updatedDate;
}