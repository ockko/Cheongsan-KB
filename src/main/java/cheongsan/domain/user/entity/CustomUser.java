package cheongsan.domain.user.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
public class CustomUser extends User {
    private cheongsan.domain.user.entity.User user;

    public CustomUser(String username, String password,
                      Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public CustomUser(cheongsan.domain.user.entity.User user) {
        super(user.getUserId(), user.getPassword(), toGrantedAuthorities(user.getRole()));
        this.user = user;
    }

    private static Collection<? extends GrantedAuthority> toGrantedAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
