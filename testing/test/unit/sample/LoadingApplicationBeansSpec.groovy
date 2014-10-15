package sample

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(MyController)
class LoadingApplicationBeansSpec extends Specification {

    static loadExternalBeans = true

    def "loadExternalBeansをtrueにすると自動的にresource.groovyのbeanが読み込まれる"() {
        expect:
            controller.myService
    }
}
