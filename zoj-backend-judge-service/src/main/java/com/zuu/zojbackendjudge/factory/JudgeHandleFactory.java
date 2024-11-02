package com.zuu.zojbackendjudge.factory;

import com.zuu.domain.enums.CodeLanguageEnum;
import com.zuu.zojbackendcommon.domain.ErrorEnum;
import com.zuu.zojbackendcommon.exeption.BusinessException;
import com.zuu.zojbackendjudge.strategy.JudgeHandleStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 17:22
 */
public class JudgeHandleFactory {
    private static final Map<String, JudgeHandleStrategy> cache = new HashMap<>();

    public static void register(CodeLanguageEnum codeLanguageEnum, JudgeHandleStrategy judgeHandle){
        cache.put(codeLanguageEnum.getLanguage(),judgeHandle);
    }

    public static JudgeHandleStrategy getIfAbsent(String language){
        JudgeHandleStrategy judgeHandle = cache.get(language);
        if(Objects.isNull(judgeHandle)){
            throw new BusinessException(ErrorEnum.NULL_ERROR,"不支持该语言");
        }
        return judgeHandle;
    }

}
