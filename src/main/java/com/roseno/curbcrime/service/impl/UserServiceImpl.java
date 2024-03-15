package com.roseno.curbcrime.service.impl;

import com.roseno.curbcrime.domain.User;
import com.roseno.curbcrime.dto.user.UserFindIdRequest;
import com.roseno.curbcrime.dto.user.UserFindIdResponse;
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
     * 아이디 찾기
     * @param userFindIdRequest     아이디 찾기 정보
     * @return                      아이디 정보
     */
    @Override
    public Optional<UserFindIdResponse> findUserId(UserFindIdRequest userFindIdRequest) {
        String name = userFindIdRequest.getName();
        String email = userFindIdRequest.getEmail();

        try {
            UserFindIdResponse userResponse = null;

            Optional<User> optUser = userMapper.findIdByNameAndEmail(name, email);

            if (optUser.isPresent()) {
                User user = optUser.get();

                userResponse = UserFindIdResponse.builder()
                        .id(user.getId())
                        .createAt(user.getCreateAt())
                        .build();
            }

            return Optional.ofNullable(userResponse);
        } catch (DataAccessException e) {
            throw new ServiceException("요청을 처리하는 동안 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }

    /**
     * 아이디 사용 여부 확인
     * @param id    아이디
     * @return      아이디 사용 여부
     */
    @Override
    public boolean isUsedId(String id) {
        try {
            return userMapper.isUsedId(id);
        } catch (DataAccessException e) {
            throw new ServiceException("요청을 처리하는 동안 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }

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
        } while(userMapper.isUsedIdx(id));

        return id;
    }
}
