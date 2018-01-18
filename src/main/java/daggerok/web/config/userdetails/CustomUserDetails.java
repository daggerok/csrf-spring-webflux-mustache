package daggerok.web.config.userdetails;

import daggerok.users.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

class CustomUserDetails extends User implements UserDetails {

  private static final long serialVersionUID = -5306760386322252897L;

  public CustomUserDetails(final User user) {

    this.setId(user.getId())
        .setUsername(user.getUsername())
        .setPassword(user.getPassword())
        .setLastModifiedAt(user.getLastModifiedAt());
  }

  @Override public Collection<? extends GrantedAuthority> getAuthorities() {
    return AuthorityUtils.createAuthorityList("ROLE_USER");
  }

  @Override public boolean isAccountNonExpired() {
    return isEnabled();
  }

  @Override public boolean isAccountNonLocked() {
    return isEnabled();
  }

  @Override public boolean isCredentialsNonExpired() {
    return isEnabled();
  }

  @Override public boolean isEnabled() {
    return true;
  }
}
