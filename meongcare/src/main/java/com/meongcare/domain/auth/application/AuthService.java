package com.meongcare.domain.auth.application;

import com.meongcare.common.error.ErrorCode;
import com.meongcare.common.error.exception.InvalidTokenException;
import com.meongcare.domain.member.domain.entity.Member;
import com.meongcare.domain.auth.domain.entity.RefreshToken;
import com.meongcare.domain.member.domain.repository.MemberRepository;
import com.meongcare.common.jwt.JwtService;
import com.meongcare.domain.auth.domain.repository.RefreshTokenRedisRepository;
import com.meongcare.domain.auth.presentation.dto.request.LoginRequest;
import com.meongcare.domain.auth.presentation.dto.response.LoginResponse;
import com.meongcare.domain.auth.presentation.dto.response.ReissueResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Transactional
    public LoginResponse login(LoginRequest loginRequest) {
        String providerId = loginRequest.getProviderId();
        Optional<Member> findMemberOptional = memberRepository.findByProviderId(providerId);
        Long memberId = null;

        if (findMemberOptional.isEmpty()) {
            Member member = loginRequest.toMemberEntity();
            memberRepository.save(member);
            memberId = member.getId();
        }
        if (findMemberOptional.isPresent()) {
            memberId = findMemberOptional.get().getId();
        }

        String accessToken = jwtService.createAccessToken(memberId);
        String refreshToken = jwtService.createRefreshToken(memberId);

        refreshTokenRedisRepository.save(RefreshToken.of(refreshToken, memberId));

        LoginResponse loginResponse = LoginResponse.of(accessToken, refreshToken);

        return loginResponse;
    }

    public ReissueResponse reissue(String refreshToken) {
        Long userId = jwtService.parseJwtToken(refreshToken);

        refreshTokenRedisRepository
                .findById(refreshToken)
                .orElseThrow(() -> new InvalidTokenException(ErrorCode.INVALID_REFRESH_TOKEN));

        String accessToken = jwtService.createAccessToken(userId);
        ReissueResponse reissueResponse = new ReissueResponse(accessToken);

        return reissueResponse;
    }

    public void logout(String refreshToken) {
        refreshTokenRedisRepository.deleteById(refreshToken);
    }

}
