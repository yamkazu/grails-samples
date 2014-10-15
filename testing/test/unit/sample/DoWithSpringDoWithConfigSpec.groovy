package sample

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.test.runtime.FreshRuntime
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
@FreshRuntime
@TestMixin(GrailsUnitTestMixin)
class DoWithSpringDoWithConfigSpec extends Specification {

    static doWithSpring = {
        myService(MyService)
    }

    static doWithConfig(ConfigObject config) {
        config.myConfigValue = 'Hello'
    }

    def "doWithSpringとdoWithConfigの動作確認"() {
        expect:
            grailsApplication.mainContext.getBean('myService') != null
            config.myConfigValue == 'Hello'

        when:
            grailsApplication.mainContext.getBean('myService').value = 'World'

        then:
            grailsApplication.mainContext.getBean('myService').value == 'World'
    }

    def "applicationContextはスペック内で共有される"() {
        expect:
            grailsApplication.mainContext.getBean('myService').value == 'World'
    }

    // 以下のIssueによりFreshRuntimeはメソッドにしか付与できない
    @Issue("https://jira.grails.org/browse/GRAILS-11626")
    @FreshRuntime
    def "@FreshRuntimeを設定するとコンテキストが初期化される"() {
        expect:
            !grailsApplication.mainContext.getBean('myService').value
    }
}

