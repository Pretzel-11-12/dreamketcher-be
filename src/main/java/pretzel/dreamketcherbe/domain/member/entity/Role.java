package pretzel.dreamketcherbe.domain.member.entity;

public enum Role {
  MEMBER,
  ADMIN,
  ;

  public static Role of(String role) {
    return valueOf(role);
  }
}
