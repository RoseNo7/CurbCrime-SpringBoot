package com.roseno.curbcrime.service.impl;

import com.roseno.curbcrime.domain.User;
import com.roseno.curbcrime.dto.user.UserJoinRequest;
import com.roseno.curbcrime.exception.ServiceException;
import com.roseno.curbcrime.mapper.UserMapper;
import com.roseno.curbcrime.service.UserService;
import com.roseno.curbcrime.util.UniqueKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final int USER_KEY_LENGTH = 8;

    private final UserMapper userMapper;

    /**
     * 회원가입
     * @param userJoinRequest   회원정보
     * @return                  회원가입 여부
     */
    @Override
    public Optional<Long> createUser(UserJoinRequest userJoinRequest) {
        User user = User.builder()
                .idx(generateUserIdx())
                .id(userJoinRequest.getId())
                .password(userJoinRequest.getPassword())
                .name(userJoinRequest.getName())
                .email(userJoinRequest.getEmail())
                .build();

        try {
            int result = userMapper.createUser(user);

            return (result > 0) ? Optional.of(user.getIdx()) : Optional.empty();
        } catch (DataAccessException e) {
            throw new ServiceException("요청을 처리하는 동안 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }

    /**
     * 회원번호 생성
     * @return      회원번호
     */
    private long generateUserIdx() {
        long id;

        do {
            id = UniqueKeyGenerator.generateNumeric(USER_KEY_LENGTH);
        } while(userMapper.isIdxUsed(id));

        return id;
    }
}
