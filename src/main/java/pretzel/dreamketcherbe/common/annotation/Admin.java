package pretzel.dreamketcherbe.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 관리자 권한 검증 어노테이션
 * ADMIN 권한을 가진 사용자만 접근 가능하며, 관리자 아이디를 컨트롤러 메소드 파라미터로 주입
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Admin {
}
