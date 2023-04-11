package de.farue.autocut.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RunWithAuthorities {
    String[] value() default {};

    RoleEnum role() default RoleEnum.EMPTY;

    String principal() default "system";
}
