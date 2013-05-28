package demo

import org.apache.commons.lang.RandomStringUtils

class User {

    transient springSecurityService

    String username
    String password
    String salt
    boolean enabled = true
    boolean accountExpired = false
    boolean accountLocked = false
    boolean passwordExpired = false

    String getSalt() {
        if (!salt) {
            salt = RandomStringUtils.randomAlphanumeric(20)
        }
        return salt
    }

    static hasMany = [authorities: Role]

    static constraints = {
        username blank: false, unique: true
        password blank: false
        salt blank: false
    }

    static mapping = {
        password column: '`password`'
        salt updateable: false
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }

    protected void encodePassword() {
        password = springSecurityService.encodePassword(password, salt)
    }
}
