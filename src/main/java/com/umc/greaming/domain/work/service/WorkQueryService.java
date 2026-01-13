package com.umc.greaming.domain.work.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.work.dto.response.WorkPreviewResponse;
import com.umc.greaming.domain.work.entity.Work;
import com.umc.greaming.domain.work.repository.WorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkQueryService {

    private final WorkRepository workRepository;

    public WorkPreviewResponse getWorkPreview(Long workId) {
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WORK_NOT_FOUND));

        // TODO: tags join되면 List<String> tags 채우기
        List<String> tags = List.of();

        return WorkPreviewResponse.from(work, tags);
    }
}
