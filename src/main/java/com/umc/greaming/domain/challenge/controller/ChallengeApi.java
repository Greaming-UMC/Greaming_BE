package com.umc.greaming.domain.challenge.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.domain.challenge.dto.response.ChallengeSubmissionsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Challenge API", description = "챌린지 관련 API")
@Validated
@RequestMapping("/api/challenges")
public interface ChallengeApi {

    @Operation(summary = "현재 진행 중인 챌린지 게시물 목록 조회", description = """
            현재 진행 중인 데일리/주간 챌린지에 참여한 게시물들을 조회합니다.
            
            **챌린지 타입 (challengeType):**
            - `DAILY`: 데일리 챌린지
            - `WEEKLY`: 주간 챌린지
            
            **정렬:**
            - 최신순으로 고정
            
            **페이지 사이즈:**
            - 기본값: 5
            - 최소: 1
            - 최대: 50
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "챌린지 게시물 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "CHALLENGE_200",
                                      "message": "챌린지 게시물 목록 조회 성공",
                                      "result": {
                                        "submissions": [
                                          {
                                            "submissionId": 100,
                                            "thumbnailUrl": "https://s3.../thumbnail.jpg",
                                            "userId": 1,
                                            "nickname": "그림쟁이",
                                            "profileImageUrl": "https://s3.../profile.jpg",
                                            "likesCount": 10,
                                            "commentCount": 5,
                                            "bookmarkCount": 15
                                          },
                                          {
                                            "submissionId": 99,
                                            "thumbnailUrl": "https://s3.../thumbnail2.jpg",
                                            "userId": 2,
                                            "nickname": "화가",
                                            "profileImageUrl": "https://s3.../profile2.jpg",
                                            "likesCount": 8,
                                            "commentCount": 3,
                                            "bookmarkCount": 12
                                          }
                                        ],
                                        "pageInfo": {
                                          "currentPage": 1,
                                          "pageSize": 5,
                                          "totalPages": 10,
                                          "totalElements": 50,
                                          "isLast": false,
                                          "isFirst": true
                                        }
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "현재 진행 중인 챌린지가 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "CHALLENGE_404",
                                      "message": "현재 진행 중인 챌린지를 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 챌린지 타입",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMM_400",
                                      "message": "챌린지 타입은 DAILY 또는 WEEKLY여야 합니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/current/submissions")
    ResponseEntity<ApiResponse<ChallengeSubmissionsResponse>> getCurrentChallengeSubmissions(
            @Parameter(description = "챌린지 타입 (DAILY, WEEKLY)", required = true, example = "DAILY") 
            @RequestParam String challengeType,
            
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1") 
            @RequestParam(defaultValue = "1") @Positive int page,
            
            @Parameter(description = "페이지 사이즈 (1-50)", example = "5")
            @RequestParam(defaultValue = "5") @Min(1) @Max(50) int size
    );

    @Operation(summary = "특정 날짜 챌린지 게시물 목록 조회", description = """
            특정 날짜에 진행된 데일리/주간 챌린지에 참여한 게시물들을 조회합니다.
            
            **챌린지 타입 (challengeType):**
            - `DAILY`: 데일리 챌린지
            - `WEEKLY`: 주간 챌린지
            
            **날짜 형식:**
            - ISO 8601 형식: `2024-02-10T00:00:00`
            - 또는 날짜만: `2024-02-10` (자동으로 00:00:00 추가)
            
            **정렬 기준 (sortBy):**
            - `latest`: 최신순 (기본값)
            - `popular`: 인기순 (좋아요 많은 순)
            - `recommend`: 추천순 (좋아요*2 + 댓글*3 + 북마크*5)
            
            **페이지 사이즈:**
            - 기본값: 50
            - 최소: 1
            - 최대: 50
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "챌린지 게시물 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "CHALLENGE_200",
                                      "message": "챌린지 게시물 목록 조회 성공",
                                      "result": {
                                        "submissions": [
                                          {
                                            "submissionId": 100,
                                            "thumbnailUrl": "https://s3.../thumbnail.jpg",
                                            "userId": 1,
                                            "nickname": "그림쟁이",
                                            "profileImageUrl": "https://s3.../profile.jpg",
                                            "likesCount": 10,
                                            "commentCount": 5,
                                            "bookmarkCount": 15
                                          }
                                        ],
                                        "pageInfo": {
                                          "currentPage": 1,
                                          "pageSize": 50,
                                          "totalPages": 3,
                                          "totalElements": 150,
                                          "isLast": false,
                                          "isFirst": true
                                        }
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "해당 날짜에 진행된 챌린지가 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "CHALLENGE_404",
                                      "message": "해당 날짜에 진행된 챌린지를 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 날짜 형식 또는 챌린지 타입",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMM_400",
                                      "message": "챌린지 타입은 DAILY 또는 WEEKLY여야 합니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/date/submissions")
    ResponseEntity<ApiResponse<ChallengeSubmissionsResponse>> getChallengeSubmissionsByDate(
            @Parameter(description = "챌린지 타입 (DAILY, WEEKLY)", required = true, example = "DAILY") 
            @RequestParam String challengeType,
            
            @Parameter(description = "조회할 날짜 (ISO 8601 형식)", required = true, example = "2024-02-10T00:00:00") 
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTime,
            
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1") 
            @RequestParam(defaultValue = "1") @Positive int page,
            
            @Parameter(description = "페이지 사이즈 (1-50)", example = "50") 
            @RequestParam(defaultValue = "50") @Min(1) @Max(50) int size,
            
            @Parameter(description = "정렬 기준 (latest, popular, recommend)", example = "latest") 
            @RequestParam(defaultValue = "latest") String sortBy
    );
}
