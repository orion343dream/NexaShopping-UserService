package lk.ijse.eca.userservice.aspect;

import lk.ijse.eca.userservice.dto.UserRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * NicNormalizationAspect — previously enforced Sri Lankan NIC format (9 digits + V).
 * Now kept as a no-op pass-through since the system accepts any alphanumeric user ID.
 * The POS system uses username-based IDs, so strict NIC formatting is not applicable.
 */
@Aspect
@Component
@Slf4j
public class NicNormalizationAspect {

    @Around("execution(* lk.ijse.eca.userservice.service.UserService.*(..))")
    public Object normalizeNicArguments(ProceedingJoinPoint joinPoint) throws Throwable {
        // Pass-through: no NIC normalization needed for POS user IDs
        return joinPoint.proceed();
    }
}
