package com.umc.greaming.domain.submission.controller;

import com.umc.greaming.common.response.ApiResponse;
import com.umc.greaming.domain.comment.dto.response.CommentPageResponse;
import com.umc.greaming.domain.submission.dto.request.SubmissionCreateRequest;
import com.umc.greaming.domain.submission.dto.request.SubmissionUpdateRequest;
import com.umc.greaming.domain.submission.dto.response.SubmissionDetailResponse;
import com.umc.greaming.domain.submission.dto.response.SubmissionInfo;
import com.umc.greaming.domain.submission.dto.response.SubmissionPreviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Submission API", description = "게시글(작품) 관련 API")
@Validated
@RequestMapping("/api/submissions")
public interface SubmissionApi {

    // 1. 게시글 생성
    @Operation(summary = "게시글 생성", description = """
            새로운 게시글을 등록합니다.
            
            - **field**: [WEEKLY, DAILY, FREE] 중 하나 필수
            - **visibility**: [PUBLIC, CIRCLE] 중 하나 필수
            - **thumbnailKey**: S3에서 발급받은 이미지 Key (URL 아님)
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "게시물 추가 성공 (SUBMISSION_200)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "SUBMISSION_200",
                                      "message": "게시물 추가 성공",
                                      "result": {
                                        "submissionId": 100,
                                        "title": "이번주 챌린지 작품",
                                        "nickname": "그림쟁이",
                                        "profileImageUrl": "https://s3.ap-northeast-2.amazonaws.com/bucket/profile/user1.jpg",
                                        "field": "WEEKLY",
                                        "visibility": "PUBLIC",
                                        "uploadAt": "2026-02-10T10:00:00"
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "입력값 검증 실패 (Enum 값 불일치 등)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "COMM_400",
                                      "message": "field 값은 WEEKLY, DAILY, FREE 중 하나여야 합니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @PostMapping
    ResponseEntity<ApiResponse<SubmissionInfo>> createSubmission(
            @RequestBody @Valid SubmissionCreateRequest request,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    // 2. 게시글 미리보기
    @Operation(summary = "게시글 미리보기 조회", description = "게시글 ID를 통해 썸네일과 태그 등 요약 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "작품 미리보기 조회 성공 (SUBMISSION_200)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "SUBMISSION_200",
                                      "message": "작품 미리보기 조회 성공",
                                      "result": {
                                        "submissionId": 100,
                                        "thumbnailUrl": "https://s3.ap-northeast-2.amazonaws.com/bucket/submission/thumb_100.jpg",
                                        "title": "미리보기 제목",
                                        "tags": ["일러스트", "풍경"]
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 게시글",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "SUBMISSION_404",
                                      "message": "작품을 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/{submissionId}/preview")
    ResponseEntity<ApiResponse<SubmissionPreviewResponse>> getSubmissionPreview(
            @Parameter(description = "게시글 ID") @Positive @PathVariable("submissionId") Long submissionId
    );

    // 3. 게시글 상세 조회
    @Operation(summary = "게시글 상세 조회", description = "게시글의 상세 정보와 댓글 목록(1페이지)을 함께 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "게시물 상세조회 성공 (SUBMISSION_200)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "SUBMISSION_200",
                                      "message": "게시물 상세조회 성공",
                                      "result": {
                                        "submission": {
                                          "submissionId": 100,
                                          "nickname": "그림쟁이",
                                          "profileImageUrl": "https://s3.../profile.jpg",
                                          "level": "SKETCHER",
                                          "imageList": [
                                            "https://s3.../img1.jpg",
                                            "https://s3.../img2.jpg"
                                          ],
                                          "title": "상세 제목",
                                          "caption": "상세 설명입니다.",
                                          "field": "DAILY",
                                          "visibility": "PUBLIC",
                                          "tags": [{"tagId": 1, "tagName": "수채화"}],
                                          "liked": false
                                        },
                                        "commentPage": {
                                           "comments": [],
                                           "currentPage": 1,
                                           "totalElements": 0,
                                           "isLast": true
                                        }
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 게시글",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "SUBMISSION_404",
                                      "message": "작품을 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/{submissionId}")
    ResponseEntity<ApiResponse<SubmissionDetailResponse>> getSubmissionDetail(
            @Parameter(description = "게시글 ID") @Positive @PathVariable("submissionId") Long submissionId,
            @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") @Positive int page,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    // 4. 댓글 목록 조회
    @Operation(summary = "댓글 목록 조회 (페이징)", description = "게시글의 댓글만 따로 조회합니다. (더보기 기능 등)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "다음 댓글 불러오기 성공 (COMMENT_200)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "COMMENT_200",
                                      "message": "다음 댓글 불러오기 성공",
                                      "result": {
                                        "comments": [
                                           {
                                             "commentId": 5, 
                                             "writer_nickname": "댓글러",
                                             "content": "멋져요!",
                                             "writer_profileImgUrl": "https://s3.../user2.jpg"
                                           }
                                        ],
                                        "currentPage": 2,
                                        "totalPages": 5,
                                        "isLast": false
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 게시글",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "SUBMISSION_404",
                                      "message": "작품을 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @GetMapping("/{submissionId}/comments")
    ResponseEntity<ApiResponse<CommentPageResponse>> getCommentList(
            @Parameter(description = "게시글 ID") @Positive @PathVariable("submissionId") Long submissionId,
            @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") @Positive int page,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    // 5. 게시글 수정
    @Operation(summary = "게시글 수정", description = """
            게시글 정보를 수정합니다.
            
            - **visibility**: [PUBLIC, CIRCLE] 중 하나
            - **commentEnabled**: true/false
            - **imageList/tags**: 보내면 기존 데이터를 **모두 지우고** 새로 덮어씁니다.
            """)
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "게시물 수정 성공 (SUBMISSION_200)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "SUBMISSION_200",
                                      "message": "게시물 수정 성공",
                                      "result": {
                                        "submissionId": 100,
                                        "title": "수정된 제목",
                                        "visibility": "CIRCLE",
                                        "uploadAt": "2026-02-10T10:00:00"
                                      }
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "수정 권한 없음 (본인 글 아님)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "SUBMISSION_401",
                                      "message": "올바른 사용자가 아닙니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 게시글",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "SUBMISSION_404",
                                      "message": "작품을 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @PutMapping("/{submissionId}")
    ResponseEntity<ApiResponse<SubmissionInfo>> updateSubmission(
            @Parameter(description = "수정할 게시글 ID") @Positive @PathVariable("submissionId") Long submissionId,
            @RequestBody @Valid SubmissionUpdateRequest updateSubmission,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );

    // 6. 게시글 삭제
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다. (본인만 가능)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "게시물 삭제 성공 (SUBMISSION_200)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": true,
                                      "code": "SUBMISSION_200",
                                      "message": "게시물 삭제 성공",
                                      "result": 100
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "삭제 권한 없음 (본인 글 아님)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "SUBMISSION_401",
                                      "message": "올바른 사용자가 아닙니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "존재하지 않는 게시글",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "isSuccess": false,
                                      "code": "SUBMISSION_404",
                                      "message": "작품을 찾을 수 없습니다.",
                                      "result": null
                                    }
                                    """
                            )
                    )
            )
    })
    @DeleteMapping("/{submissionId}")
    ResponseEntity<ApiResponse<Long>> deleteSubmission(
            @Parameter(description = "삭제할 게시글 ID") @Positive @PathVariable("submissionId") Long submissionId,
            @Parameter(hidden = true) @AuthenticationPrincipal Long userId
    );
}