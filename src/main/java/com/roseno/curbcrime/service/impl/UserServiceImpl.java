package com.roseno.curbcrime.service.impl;

import com.roseno.curbcrime.domain.User;
import com.roseno.curbcrime.dto.user.*;
import com.roseno.curbcrime.exception.ServiceException;
import com.roseno.curbcrime.exception.NotFoundException;
import com.roseno.curbcrime.model.Role;
import com.roseno.curbcrime.repository.UserRepository;
import com.roseno.curbcrime.service.UserService;
import com.roseno.curbcrime.util.EmailSender;
import com.roseno.curbcrime.util.UniqueKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final int USER_ID_LENGTH = 8;
    private static final int USER_CIPHER_LENGTH = 6;

    private final UserRepository userRepository;
    private final EmailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원정보 조회
     * @param idx   회원번호
     * @return      회원정보
     */
    @Override
    @Transactional(readOnly = true)
    public UserInfoResponse findUser(long idx) {
        User user = userRepository.findByIdxAndIsDeletedFalse(idx)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        return UserInfoResponse.builder()
                .idx(user.getIdx())
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createAt(user.getCreateAt())
                .build();
    }

    /**
     * 아이디 찾기
     * @param userFindIdRequest     아이디 찾기 정보
     * @return                      아이디 정보
     */
    @Override
    @Transactional(readOnly = true)
    public UserFindIdResponse findUserId(UserFindIdRequest userFindIdRequest) {
        String name = userFindIdRequest.getName();
        String email = userFindIdRequest.getEmail();

        User user = userRepository.findByNameAndEmailAndIsDeletedFalse(name, email)
                .orElseThrow(() -> new NotFoundException("가입하신 정보와 일치하지 않습니다. 다시 확인해주세요."));

        return UserFindIdResponse.builder()
                .id(user.getId())
                .createAt(user.getCreateAt())
                .build();
    }

    /**
     * 아이디 사용 여부 확인
     * @param id    아이디
     * @return      아이디 사용 여부
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isUsedId(String id) {
        return userRepository.existsById(id);
    }

    /**
     * 현재 비밀번호 확인
     * @param idx                   회원번호
     * @param userPasswordRequest   비밀번호
     * @return                      비밀번호 일치 여부
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isUsedPassword(long idx, UserPasswordRequest userPasswordRequest) {
        User user = userRepository.findByIdxAndIsDeletedFalse(idx)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        String currentPassword = user.getPassword();
        String inputPassword = userPasswordRequest.getPassword();

        return passwordEncoder.matches(inputPassword, currentPassword);
    }

    /**
     * 회원가입
     * @param userJoinRequest   회원정보
     * @return                  회원가입 여부
     */
    @Override
    public Optional<UserInfoResponse> createUser(UserJoinRequest userJoinRequest) {
        User user = User.builder()
                .idx(generateUserIdx())
                .id(userJoinRequest.getId())
                .password(passwordEncoder.encode(userJoinRequest.getPassword()))
                .name(userJoinRequest.getName())
                .email(userJoinRequest.getEmail())
                .role(Role.USER.id())
                .build();

        try {
            User savedUser = userRepository.save(user);

            UserInfoResponse userResponse = UserInfoResponse.builder()
                    .idx(savedUser.getIdx())
                    .build();

            return Optional.ofNullable(userResponse);
        } catch (DataAccessException e) {
            throw new ServiceException("요청을 처리하는 동안 오류가 발생했습니다. 나중에 다시 시도해주세요.");
        }
    }

    /**
     * 비밀번호 인증번호 생성
     * @param userFindPasswordRequest   비밀번호 찾기 정보
     * @return                          비밀번호 인증번호
     */
    @Override
    @Transactional(readOnly = true)
    public String createPasswordCipher(UserFindPasswordRequest userFindPasswordRequest) {
        String id = userFindPasswordRequest.getId();
        String email = userFindPasswordRequest.getEmail();

        userRepository.findByIdAndEmailAndIsDeletedFalse(id, email)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        String cipher = UniqueKeyGenerator.generate(USER_CIPHER_LENGTH);

        try {
            String subject = "[CurbCrime] 비밀번호 찾기 시, 사용되는 인증번호 발송 이메일입니다.";
            String content = "인증번호는 " + cipher + " 입니다.";

            mailSender.send(email, subject, content);

            return cipher;
        } catch (MailException e) {
            throw new ServiceException("이메일 전송 실패에 하였습니다.나중에 다시 시도해주세요.");
        }
    }

    /**
     * 회원정보 변경 
     * @param idx               회원번호
     * @param userInfoRequest   회원정보
     */
    @Override
    @Transactional
    public void updateUser(long idx, UserInfoRequest userInfoRequest) {
        User user = userRepository.findByIdxAndIsDeletedFalse(idx)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        user.setName(userInfoRequest.getName());
        user.setEmail(userInfoRequest.getEmail());
    }

    /**
     * 비밀번호 변경
     * @param idx                   회원번호
     * @param userPasswordRequest   비밀번호
     */
    @Override
    @Transactional
    public void updatePassword(long idx, UserPasswordRequest userPasswordRequest) {
        User user = userRepository.findByIdxAndIsDeletedFalse(idx)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        user.setPassword(passwordEncoder.encode(userPasswordRequest.getPassword()));
    }

    /**
     * 회원 삭제
     * @param idx   회원번호
     */
    @Override
    @Transactional
    public void deleteUser(long idx) {
        User user = userRepository.findByIdxAndIsDeletedFalse(idx)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        user.setDeleted(true);
        user.setDeleteAt(LocalDateTime.now());
    }

    /**
     * 회원번호 생성
     * @return      회원번호
     */
    private long generateUserIdx() {
        long id;

        do {
            id = UniqueKeyGenerator.generateNumeric(USER_ID_LENGTH);
        } while(userRepository.existsById(id));

        return id;
    }
}
