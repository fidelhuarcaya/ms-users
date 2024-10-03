package org.copper.users.dto.response;

import org.copper.users.common.StatusCode;

public record StatusResponse(
        Integer id,
        StatusCode code,
        String description) {
}
