package com.roseno.curbcrime.util;

import org.apache.commons.lang3.RandomStringUtils;

public class UniqueKeyGenerator {

    /**
     * 아이디 생성
     * @param length    자릿수
     * @return
     */
    public static String generate(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    /**
     * 숫자 아이디 생성
     * @param length    자릿수
     * @return          아이디
     */
    public static Long generateNumeric(int length) {
        String key;

        do {
            key = RandomStringUtils.randomNumeric(length);
        } while (key.startsWith("0"));

        return Long.parseLong(key);
    }
}
