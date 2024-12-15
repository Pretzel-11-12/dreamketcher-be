package pretzel.dreamketcherbe.common.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

    @Value("${jasypt.encryptor.password}")
    private String password;

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor(); // Jasypt에서 제공하는 스레드 풀을 사용하는 암호화 클래스
        SimpleStringPBEConfig config = new SimpleStringPBEConfig(); // PBE(Password Based Encryption - 비밀번호 기반 암호화 설정을 위한 클래스
        config.setPassword(password); // 암호화에 사용될 비밀번호를 설정
        config.setAlgorithm("PBEWithMD5AndDES"); // 사용할 암호화 알고리즘을 설정
        config.setKeyObtentionIterations("1000"); // 키 생성 시 해싱 반복 횟수 설정. (높을수록 보안은 강화되지만 성능은 저하될 수 있다)
        config.setPoolSize("1"); // 스레드 풀 크기 설정 (애플리케이션 머신의 코어 수와 동일하게 설정하는 것을 권장)
        config.setProviderName("SunJCE"); // 사용할 프로바이더 이름을 설정 (Java의 표준 암호화 제공자)
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // Salt 생성에 사용할 클래스를 지정 (RandomSaltGenerator는 무작위 salt를 생성)
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator"); // 초기화 벡터(IV) 생성에 사용할 클래스를 지정 (NoIvGenerator는 IV를 사용하지 않음을 의미)
        config.setStringOutputType("base64"); // 암호화된 결과물의 출력 형식
        encryptor.setConfig(config); // 위에서 설정한 SimpleStringPBEConfig의 설정들을 encryptor 객체에 적용
        return encryptor;
    }
}