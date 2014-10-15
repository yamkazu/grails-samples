package sample

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class MyController {

    MyService myService

    def index() {
        render myService.hello()
    }
}
