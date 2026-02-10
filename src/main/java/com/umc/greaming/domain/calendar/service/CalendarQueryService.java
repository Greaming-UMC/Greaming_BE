package com.umc.greaming.domain.calendar.service;

import com.umc.greaming.common.exception.GeneralException;
import com.umc.greaming.common.status.error.ErrorStatus;
import com.umc.greaming.domain.calendar.converter.CalendarConverter;
import com.umc.greaming.domain.calendar.dto.response.UserCalendarResponse;
import com.umc.greaming.domain.calendar.enums.ChallengeType;
import com.umc.greaming.domain.calendar.repository.ChallengeParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarQueryService {

    private final ChallengeParticipationRepository challengeParticipationRepository;
    private final CalendarConverter calendarConverter;

    // 특정 월의 DAILY/WEEKLY 참여 날짜 리스트 반환
    public UserCalendarResponse getUserCalendar(Long userId, int year, int month) {
        if (month < 1 || month > 12) {
            throw new GeneralException(ErrorStatus.VALIDATION_ERROR);
        }

        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<LocalDate> dailyDates = challengeParticipationRepository.findDistinctParticipatedAt(
                userId, ChallengeType.DAILY, start, end
        );
        List<LocalDate> weeklyDates = challengeParticipationRepository.findDistinctParticipatedAt(
                userId, ChallengeType.WEEKLY, start, end
        );

        return calendarConverter.toResponse(dailyDates, weeklyDates);
    }
}
