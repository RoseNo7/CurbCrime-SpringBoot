package com.roseno.curbcrime.service.impl;

import com.roseno.curbcrime.domain.User;
import com.roseno.curbcrime.dto.auth.AuthRequest;
import com.roseno.curbcrime.exception.ServiceException;
import com.roseno.curbcrime.mapper.UserMapper;
import com.roseno.curbcrime.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;

    /**
     * 로그인
     * @param authRequest   로그인 정보
     * @return              회원정보
     */
    @Override
    public Optional<User> authenticate(AuthRequest authRequest) {
        String id = authRequest.getId();
        String password = authRequest.getPassword();

        try {
            return userMapper.findUserByIdAndPassword(id, password);
        } catch (DataAccessException e) {
            throw new ServiceException("요청을 처리하는 동안 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }
}
