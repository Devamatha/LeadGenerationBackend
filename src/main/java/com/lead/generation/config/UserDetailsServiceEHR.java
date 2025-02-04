package com.lead.generation.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lead.generation.repository.ClientsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceEHR implements UserDetailsService {
    @Autowired
    private final ClientsRepository clientsRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        String password = "";
        String role = "";
        List<Object[]> result = clientsRepository.findPasswordAndRoleByEmail(username);
        for (Object[] row : result) {
            password = (String) row[0];
            role = (String) row[1];

        }
        List<GrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority(role));

        return new User(username, password, authorityList);
    }
}
