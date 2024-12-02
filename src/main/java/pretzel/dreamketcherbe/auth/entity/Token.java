package pretzel.dreamketcherbe.auth.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Token {

        private String tokenId;

        private Long memberId;

        public Token(Long memberId) {
            this.tokenId = generatedTokenId();
            this.memberId = memberId;
        }

        private String generatedTokenId() {
            return UUID.randomUUID().toString();
        }
}
