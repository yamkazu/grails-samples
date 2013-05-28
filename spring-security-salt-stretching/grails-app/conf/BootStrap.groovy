import demo.Role
import demo.User

class BootStrap {

    def init = { servletContext ->
        def admin = Role.findOrSaveByAuthority("ROLE_ADMIN")
        def user = Role.findOrSaveByAuthority("ROLE_USER")

        environments {
            development {
                new User(username: "admin", password: "adminpassword").addToAuthorities(admin).save(flush: true)
                new User(username: "user", password: "userpassword").addToAuthorities(user).save(flush: true)
            }
        }
    }

    def destroy = {
    }
}
