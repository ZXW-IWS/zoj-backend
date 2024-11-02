package com.zuu.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author zuu
 * @Description
 * @Date 2024/10/23 16:22
 */
@Getter
@AllArgsConstructor
public enum CodeLanguageEnum {
    JAVA("java","java"),
    CPP("cpp","c++"),
    ;
    private final String language;
    private final String desc;

    public static final Map<String,CodeLanguageEnum> cache;

    static {
        cache = Arrays.stream(CodeLanguageEnum.values()).collect(Collectors.toMap(CodeLanguageEnum::getLanguage, Function.identity()));
    }

    public static CodeLanguageEnum getLanguageEnum(String language){
        return cache.get(language);
    }
}
