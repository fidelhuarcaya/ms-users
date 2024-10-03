package org.copper.users.mapper;

import org.copper.users.dto.response.StatusResponse;
import org.copper.users.entity.Status;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StatusMapper {

    List<StatusResponse> toDtoList(List<Status> statusList);
}
