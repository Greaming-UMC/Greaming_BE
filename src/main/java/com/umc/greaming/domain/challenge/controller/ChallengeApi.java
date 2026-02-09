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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Challenge API", description = "챌린지 관련 API")
@Validated
@RequestMapping("/api/challenges")
public interface ChallengeApi {

    @Operation(summary = "챌린지별 게시물 목록 조회", description = """
            특정 챌린지에 참여한 게시물들을 조회합니다.
            
            **정렬 기준 (sortBy):**
            - `latest`: 최신순 (기본값)
            - `popular`: 인기순 (좋아요 많은 순)
            - `bookmarks`: 북마크 많은 순
            
            **페이지 사이즈:**
            - 기본값: 20
            - 최소: 1
            - 최대: 100
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
                                            "nickname": "그림쟁이",
                                            "likesCount": 10,
                                            "commentCount": 5,
                                            "bookmarkCount": 15
                                          }
                                        ],
                                        "pageInfo": {
                                          "currentPage": 1,
                                          "pageSize": 20,
                                          "totalPages": 5,
                                          "totalElements": 100,
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
                    description = "존재하지 않는 챌린지",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "CHALLENGE_404",
                                      "message": "챌린지를 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/{challengeId}/submissions")
    ResponseEntity<ApiResponse<ChallengeSubmissionsResponse>> getChallengeSubmissions(
            @Parameter(description = "챌린지 ID", required = true) 
            @Positive @PathVariable("challengeId") Long challengeId,
            
            @Parameter(description = "페이지 번호 (1부터 시작)", example = "1") 
            @RequestParam(defaultValue = "1") @Positive int page,
            
            @Parameter(description = "페이지 사이즈 (1-100)", example = "20") 
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            
            @Parameter(description = "정렬 기준 (latest, popular, bookmarks)", example = "latest") 
            @RequestParam(defaultValue = "latest") String sortBy
    );
}
