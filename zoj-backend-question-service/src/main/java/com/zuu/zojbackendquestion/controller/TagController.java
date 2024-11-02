package com.zuu.zojbackendquestion.controller;

import com.zuu.zojbackendcommon.domain.ApiResult;
import com.zuu.zojbackendcommon.domain.BaseResponse;
import com.zuu.zojbackendquestion.service.TagService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/21 17:17
 */
@RestController
@RequestMapping("/tag")
@Tag(name = "标签相关接口")
public class TagController {
    @Resource
    private TagService tagService;
    @GetMapping("/public/list")
    public BaseResponse<List<String>> getTagNameList(){
        List<String> tagNameList = tagService.list().stream().map(com.zuu.domain.po.Tag::getName).toList();
        return ApiResult.success(tagNameList);
    }
}
