package com.umc.greaming.domain.calendar.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.common.status.success.SuccessStatus;
import com.umc.greaming.domain.calendar.dto.response.UserCalendarResponse;
import com.umc.greaming.domain.calendar.service.CalendarQueryService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class CalendarController {

    private final CalendarQueryService calendarQueryService;

    @GetMapping("/{userId}/calendar")
    public ResponseEntity<ApiResponse<UserCalendarResponse>> getUserCalendar(
            @PathVariable @Positive Long userId,
            @RequestParam @Min(2000) @Max(2100) int year,
            @RequestParam @Min(1) @Max(12) int month
    ) {
        UserCalendarResponse response = calendarQueryService.getUserCalendar(userId, year, month);
        return ApiResponse.success(SuccessStatus.CALENDAR_GET_SUCCESS, response);
    }
}
