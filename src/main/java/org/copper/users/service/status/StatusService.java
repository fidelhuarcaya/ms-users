package org.copper.users.service.status;

import org.copper.users.dto.response.StatusResponse;

import java.util.List;

public interface StatusService {
    List<StatusResponse> getAll();
}
