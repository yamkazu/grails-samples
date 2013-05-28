package demo.security

import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUser
import org.springframework.security.core.userdetails.UserDetails

class MyUser implements UserDetails {

    @Delegate
    GrailsUser grailsUser

    String salt
}
