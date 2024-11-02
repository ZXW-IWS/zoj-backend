package com.zuu.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zuu.domain.vo.req.question.JudgeCase;
import com.zuu.domain.vo.req.question.JudgeConfig;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/22 11:50
 */
@Data
public class QuestionAdminDto {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目描述
     */
    private String description;

    /**
     * 题目提交数
     */
    private Integer submitCount;

    /**
     * 题目通过数
     */
    private Integer acceptCount;

    /**
     * 题目难度 0-简单 1-中等 2-困难
     */
    private Integer difficult;

    /**
     * 题目标签(JSON)
     */
    private List<String> tagList;

    /**
     * 题目答案
     */
    private String answer;

    /**
     * 判题配置（JSON),时间空间...
     */
    private JudgeConfig judgeConfig;

    /**
     * 判题用例(JSON)
     */
    private List<JudgeCase> judgeCase;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
