package com.umc.greaming.domain.calendar.converter;

import com.umc.greaming.domain.calendar.dto.response.ChallengeCalendarResponse;
import com.umc.greaming.domain.calendar.dto.response.ParticipatedAtResponse;
import com.umc.greaming.domain.calendar.dto.response.UserCalendarResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CalendarConverter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yy-MM-dd");

    public UserCalendarResponse toResponse(List<LocalDate> dailyDates, List<LocalDate> weeklyDates) {
        List<ParticipatedAtResponse> daily = dailyDates.stream()
                .map(d -> new ParticipatedAtResponse(d.format(FORMATTER)))
                .toList();

        List<ParticipatedAtResponse> weekly = weeklyDates.stream()
                .map(d -> new ParticipatedAtResponse(d.format(FORMATTER)))
                .toList();

        return new UserCalendarResponse(new ChallengeCalendarResponse(daily, weekly));
    }
}
