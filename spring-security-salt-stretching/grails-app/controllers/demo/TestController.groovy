package demo

import grails.plugins.springsecurity.Secured

class TestController {

    @Secured('ROLE_ADMIN')
    def index() {
        render "Hello"
    }
}
