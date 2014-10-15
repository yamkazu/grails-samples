package sample

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class MyService {
    String value

    String hello() { value }
}
