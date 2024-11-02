package com.zuu.zojbackendjudge.service;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 16:40
 */
public interface JudgeService {
    /**
     * 执行判题
     * @param questionSubmitId
     */
    void doJudge(Long questionSubmitId);
}
