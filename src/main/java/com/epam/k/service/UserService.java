package com.epam.k.service;

import com.epam.k.dao.UserDAO;
import com.epam.k.domain.User;
import com.epam.k.domain.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.http.HttpEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class UserService extends BaseService<User, String> {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDAO getRepository() {
        return userDAO;
    }

    @Override
    public QueryDslPredicateExecutor<User> getQueryDSLPredicateExecutor() {
        return userDAO;
    }

    public User findByUsername(final String username) {
        if (username == null) {
            return null;
        }
        return getRepository().findByUsername(username);
    }

    public void addAuthority(User user, Role role) {
        EnumSet<Role> authorities = user.getAuthorities();
        if (authorities == null) {
            authorities = EnumSet.noneOf(Role.class);
        }
        authorities.add(role);
        user.setAuthorities(authorities);
    }

    public void setEncodedPassword(final User user, final String password) {
        user.setPassword(passwordEncoder.encode(password));
    }

    public void updateUserPassword(final User user, final String password) {
        user.setPassword(passwordEncoder.encode(password));
        save(user);
    }

    public User findOneByFirstNameAndLastName(String firstName, String lastName) {
        return getRepository().findOneByFirstNameAndLastName(firstName, lastName);
    }

    public User registerAndGet(User user) {
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        if (findOneByFirstNameAndLastName(firstName, lastName) != null) {
            return user;
        } else {
            addAuthority(user, Role.ROLE_USER);
            return save(user);
        }
    }
}
