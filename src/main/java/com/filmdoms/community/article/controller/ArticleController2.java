package com.filmdoms.community.article.controller;

import com.filmdoms.community.account.data.dto.AccountDto;
import com.filmdoms.community.account.data.dto.response.Response;
import com.filmdoms.community.account.exception.ErrorCode;
import com.filmdoms.community.article.data.constant.Category;
import com.filmdoms.community.article.data.dto.response.boardlist.ParentBoardListDto;
import com.filmdoms.community.article.data.dto.response.detail.ArticleDetailResponseDto;
import com.filmdoms.community.article.data.dto.response.mainpage.MovieAndRecentMainPageResponseDto;
import com.filmdoms.community.article.data.dto.response.mainpage.ParentMainPageResponseDto;
import com.filmdoms.community.article.service.ArticleService;
import com.filmdoms.community.article.service.InitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ArticleController2 {

    private final ArticleService articleService;
    private final InitService initService;

    @GetMapping("/main/{category}")
    public Response<List<? extends ParentMainPageResponseDto>> readMain(@PathVariable Category category, @RequestParam(defaultValue = "5") int limit) {
        List<? extends ParentMainPageResponseDto> dtoList = articleService.getMainPageDtoList(category, limit);
        return Response.success(dtoList);
    }

    @GetMapping("/main/recent")
    public Response<List<MovieAndRecentMainPageResponseDto>> readMain(@RequestParam(defaultValue = "5") int limit) {
        List<MovieAndRecentMainPageResponseDto> dtoList = articleService.getRecentMainPageDtoList(limit);
        return Response.success(dtoList);
    }

    @GetMapping("/article/{category}/{articleId}")
    public Response<ArticleDetailResponseDto> readDetail(@PathVariable Category category, @PathVariable Long articleId, @AuthenticationPrincipal AccountDto accountDto) {
        ArticleDetailResponseDto dto = articleService.getDetail(category, articleId, accountDto);
        return Response.success(dto);
    }

    @GetMapping("/article/init-data")
    public Response initData(@RequestParam(defaultValue = "10") int limit) {
        initService.makeArticleData(limit);
        return Response.success();
    }

    @GetMapping("/{category}")
    public Response getBoardCategoryList(@PathVariable Category category, @RequestParam(defaultValue = "24") int size,
                                         @RequestParam(defaultValue = "0") int page) {
        if (size > 30) // 악의적 공격 방지
            size = 30;

        Page<? extends ParentBoardListDto> boardList = articleService.getBoardList(category, size, page);

        if (boardList == null)
            return Response.error(ErrorCode.CATEGORY_NOT_FOUND.getMessage());

        if (boardList.getTotalPages() - 1 < page)
            return Response.error(ErrorCode.INVALID_PAGE_NUMBER.getMessage());

        return Response.success(boardList);
    }
}