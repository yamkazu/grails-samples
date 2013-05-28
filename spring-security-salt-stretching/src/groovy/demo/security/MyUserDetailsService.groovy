package demo.security

import org.codehaus.groovy.grails.plugins.springsecurity.GormUserDetailsService
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class MyUserDetailsService extends GormUserDetailsService {

    @Override
    protected UserDetails createUserDetails(user, Collection<GrantedAuthority> authorities) {
        new MyUser(grailsUser: super.createUserDetails(user, authorities), salt: user.salt)
    }
}