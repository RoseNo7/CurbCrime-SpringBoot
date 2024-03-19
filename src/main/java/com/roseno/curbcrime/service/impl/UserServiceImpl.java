package com.roseno.curbcrime.service.impl;

import com.roseno.curbcrime.domain.User;
import com.roseno.curbcrime.dto.user.*;
import com.roseno.curbcrime.exception.ServiceException;
import com.roseno.curbcrime.mapper.UserMapper;
import com.roseno.curbcrime.service.UserService;
import com.roseno.curbcrime.util.EmailSender;
import com.roseno.curbcrime.util.UniqueKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final int USER_ID_LENGTH = 8;
    private static final int USER_CIPHER_LENGTH = 6;

    private final UserMapper userMapper;
    private final EmailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원정보 조회
     * @param idx   회원번호
     * @return      회원정보
     */
    @Override
    public Optional<UserInfoResponse> findUser(long idx) {
        try {
            UserInfoResponse userResponse = null;

            Optional<User> optUser = userMapper.findUserByIdx(idx);

            if (optUser.isPresent()) {
                User user = optUser.get();

                userResponse = UserInfoResponse.builder()
                        .idx(user.getIdx())
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .createAt(user.getCreateAt())
                        .build();
            }

            return Optional.ofNullable(userResponse);
        } catch (DataAccessException e) {
            throw new ServiceException("요청을 처리하는 동안 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }

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
     * 현재 비밀번호 확인
     * @param idx                   회원번호
     * @param userPasswordRequest   비밀번호
     * @return                      비밀번호 일치 여부
     */
    @Override
    public boolean isUsedPassword(long idx, UserPasswordRequest userPasswordRequest) {
        try {
            Optional<String> optPassword = userMapper.findPasswordByIdx(idx);

            if (optPassword.isPresent()) {
                String currentPassword = optPassword.get();
                String inputPassword = userPasswordRequest.getPassword();

                return passwordEncoder.matches(inputPassword, currentPassword);
            }

            return false;
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
                .password(passwordEncoder.encode(userJoinRequest.getPassword()))
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
     * 비밀번호 인증번호 생성
     * @param userFindPasswordRequest   비밀번호 찾기 정보
     * @return
     */
    @Override
    public Optional<String> createPasswordCipher(UserFindPasswordRequest userFindPasswordRequest) {
        String id = userFindPasswordRequest.getId();
        String email = userFindPasswordRequest.getEmail();

        try {
            String cipher = null;

            Optional<User> optUser = userMapper.findUserByIdAndEmail(id, email);

            if (optUser.isPresent()) {
                cipher = UniqueKeyGenerator.generate(USER_CIPHER_LENGTH);

                String subject = "[CurbCrime] 비밀번호 찾기 시, 사용되는 인증번호 발송 이메일입니다.";
                String content = "인증번호는 " + cipher + " 입니다.";

                mailSender.send(email, subject, content);
            }

            return Optional.ofNullable(cipher);
        } catch (MailException e) {
            throw new ServiceException("이메일 전송 실패에 하였습니다.나중에 다시 시도해주세요.");
        } catch (DataAccessException e) {
            throw new ServiceException("요청을 처리하는 동안 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }

    /**
     * 회원정보 변경
     * @param idx               회원번호
     * @param userInfoRequest   회원정보
     * @return                  회원정보 변경 여부
     */
    @Override
    public boolean updateUser(long idx, UserInfoRequest userInfoRequest) {
        User user = User.builder()
                .name(userInfoRequest.getName())
                .email(userInfoRequest.getEmail())
                .build();

        try {
            return userMapper.updateUser(idx, user) > 0;
        } catch (DataAccessException e) {
            throw new ServiceException("요청을 처리하는 동안 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }

    /**
     * 비밀번호 변경
     * @param idx                   회원번호
     * @param userPasswordRequest   비밀번호
     * @return                      비밀번호 변경 여부
     */
    @Override
    public boolean updatePassword(long idx, UserPasswordRequest userPasswordRequest) {
        String password = passwordEncoder.encode(userPasswordRequest.getPassword());

        try {
            return userMapper.updatePassword(idx, password) > 0;
        } catch (DataAccessException e) {
            throw new ServiceException("요청을 처리하는 동안 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }

    /**
     * 회원 삭제
     * @param idx   회원번호
     * @return      회원 삭제 여부
     */
    @Override
    public boolean deleteUser(long idx) {
        try {
            return userMapper.deleteUser(idx) > 0;
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
            id = UniqueKeyGenerator.generateNumeric(USER_ID_LENGTH);
        } while(userMapper.isUsedIdx(id));

        return id;
    }
}
