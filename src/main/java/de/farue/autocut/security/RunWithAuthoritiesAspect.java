package de.farue.autocut.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RunWithAuthoritiesAspect {

    @Around("@annotation(runWithAuthorities)")
    public Object setAuthorities(ProceedingJoinPoint joinPoint, RunWithAuthorities runWithAuthorities) throws Throwable {
        return SecurityUtils.runAs(
            runWithAuthorities.principal(),
            getAuthorities(runWithAuthorities),
            () -> {
                try {
                    return joinPoint.proceed();
                } catch (Exception e) {
                    throw e;
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    private String[] getAuthorities(RunWithAuthorities runWithAuthorities) {
        if (runWithAuthorities.value().length > 0) {
            return runWithAuthorities.value();
        }
        return runWithAuthorities.role().getAuthorities();
    }
}
