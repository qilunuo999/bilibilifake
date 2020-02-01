package com.bilibili_fake.demo.config;

import com.bilibili_fake.demo.entity.Users;
import com.bilibili_fake.demo.repository.UsersRepository;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

@Component("authorizer")
public class ShiroRealm  extends AuthorizingRealm {

    private final UsersRepository usersRepository;

    public ShiroRealm(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String username = token.getUsername();
        String password = new String(token.getPassword());

        Users user = usersRepository.getUserByUsername(username);
        if (user == null) {
            throw new UnknownAccountException();
        } else if (!password.equals(user.getPassword())) {
            throw new IncorrectCredentialsException();
        }
        return new SimpleAuthenticationInfo(user, password, getName());
    }
}
