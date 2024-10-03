package org.copper.users.service.status;

import lombok.RequiredArgsConstructor;
import org.copper.users.dto.response.StatusResponse;
import org.copper.users.mapper.StatusMapper;
import org.copper.users.repository.StatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService{
private final StatusRepository statusRepository;
private final StatusMapper statusMapper;
    @Override
    public List<StatusResponse> getAll() {
        return statusMapper.toDtoList(statusRepository.findAll());
    }
}
