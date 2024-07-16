package com.hh.consertreservation.application.facade;

import com.hh.consertreservation.domain.waiting.Token;
import com.hh.consertreservation.domain.user.User;
import com.hh.consertreservation.domain.user.UserService;
import com.hh.consertreservation.domain.waiting.WaitingService;
import com.hh.consertreservation.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenFacade {
    private final UserService userService;
    private final WaitingService waitingService;

    @Transactional
    public Optional<Token> issued(long userId, long maximum_ongoing_count) throws Exception {
        Optional<User> user = userService.getUser(userId);
        //Facade 에 로직이 있으면 안된다고 하셨는데..
        //이런것도 로직에 포함되는건지...
        if (!user.isPresent()) {
            throw new ResourceNotFoundException("없는 사용자 입니다");
        }
        return waitingService.issued(userId, maximum_ongoing_count);
    }

    public boolean verification(long userId, String token) throws Exception {
        return waitingService.verification(userId, token);
    }
}
