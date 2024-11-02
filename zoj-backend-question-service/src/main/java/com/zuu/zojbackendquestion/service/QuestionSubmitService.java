package com.zuu.zojbackendquestion.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zuu.domain.dto.QuestionSubmitDto;
import com.zuu.domain.po.QuestionSubmit;
import com.zuu.domain.vo.req.question_submit.QuestionSubmitPageReq;
import com.zuu.domain.vo.req.question_submit.QuestionSubmitReq;

/**
* @author zuu
* @description 针对表【question_submit】的数据库操作Service
* @createDate 2024-10-19 16:10:34
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    Page<QuestionSubmitDto> getPage(QuestionSubmitPageReq questionSubmitPageReq);

    /**
     * 提交题目
     *
     * @param token
     * @param questionSubmitReq
     * @return
     */
    Long submitQuestion(String token, QuestionSubmitReq questionSubmitReq);
}
