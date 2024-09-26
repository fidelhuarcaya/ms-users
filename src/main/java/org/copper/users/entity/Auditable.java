package org.copper.users.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class Auditable {
  private String createdBy;

  private LocalDateTime createdDate;

  private String updatedBy;

  private LocalDateTime updatedDate;
}