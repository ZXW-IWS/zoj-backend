package com.zuu.zojbackendquestion.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zuu.domain.dto.QuestionAdminDto;
import com.zuu.domain.dto.QuestionDto;
import com.zuu.domain.po.Question;
import com.zuu.domain.vo.req.question.QuestionAddReq;
import com.zuu.domain.vo.req.question.QuestionDeleteReq;
import com.zuu.domain.vo.req.question.QuestionPageReq;
import com.zuu.domain.vo.req.question.QuestionUpdateReq;

/**
* @author zuu
* @description 针对表【question】的数据库操作Service
* @createDate 2024-10-19 16:10:34
*/
public interface QuestionService extends IService<Question> {
    /**
     * 添加题目接口
     * @param token  创建人token
     * @param questionAddReq
     * @return
     */
    Long addQuestion(String token, QuestionAddReq questionAddReq);

    /**
     * 删除题目接口
     *
     * @param token
     * @param questionDeleteReq
     */
    void deleteQuestion(String token, QuestionDeleteReq questionDeleteReq);

    /**
     * 更新题目接口
     * @param questionUpdateReq
     */
    void updateQuestion(QuestionUpdateReq questionUpdateReq);

    /**
     * id获取题目
     * @param questionId
     * @return
     */
    QuestionDto getQuestionById(Long questionId);

    /**
     * 分页查询题目
     * @param questionPageReq
     * @return
     */
    Page<QuestionDto> getByPage(QuestionPageReq questionPageReq);

    /**
     * 管理员获取题目
     * @param questionId
     * @return
     */
    QuestionAdminDto adminGet(Long questionId);

    /**
     * 题目有新的通过
     */
    void newAccept(Long questionId);

    /**
     * 有新的做题提交
     * @param questionId
     */
    void submitCountPlus(Long questionId);
}
