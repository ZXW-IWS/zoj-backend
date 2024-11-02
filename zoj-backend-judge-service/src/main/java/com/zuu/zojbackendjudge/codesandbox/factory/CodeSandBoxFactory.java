package com.zuu.zojbackendjudge.codesandbox.factory;


import com.zuu.domain.enums.CodeSandBoxEnum;
import com.zuu.zojbackendcommon.domain.ErrorEnum;
import com.zuu.zojbackendcommon.exeption.BusinessException;
import com.zuu.zojbackendjudge.codesandbox.strategy.CodeSandBox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 14:31
 */
public class CodeSandBoxFactory {
    private static final Map<String, CodeSandBox> CACHE = new HashMap<>();

    public static void register(CodeSandBoxEnum codeSandBoxEnum, CodeSandBox codeSandBox){
        CACHE.put(codeSandBoxEnum.getType(),codeSandBox);
    }

    public static CodeSandBox getIfAbsent(String type){
        CodeSandBox codeSandBox = CACHE.get(type);
        if(Objects.isNull(codeSandBox)){
            throw new BusinessException(ErrorEnum.PARAM_ERROR,"代码沙箱类型错误");
        }
        return codeSandBox;
    }
}
