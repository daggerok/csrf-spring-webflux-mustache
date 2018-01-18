package daggerok.web;

import daggerok.users.User;
import daggerok.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.result.view.Rendering;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfilePage {

  final UserRepository users;

  @GetMapping({ "/profiles/{username}" })
  public Rendering byUsername(@AuthenticationPrincipal final User owner,
                              @PathVariable(required = false) final String username) {

    final String parsed = null == username ? owner.getUsername() : username;

    return Rendering.view("profiles/view")
                    .modelAttribute("user", users.findByUsername(parsed))
                    .build();
  }
}
