package sample

import grails.test.mixin.TestFor
import grails.test.runtime.FreshRuntime
import org.codehaus.groovy.grails.commons.InstanceFactoryBean
import spock.lang.Specification

@TestFor(MyController)
class MockedBeanSpec extends Specification {

    def myService = Mock(MyService)

    def doWithSpring = {
        myService(InstanceFactoryBean, myService, MyService)
    }

    // @FreshRuntimeが重要
    // @FreshRuntimeがないとテストメソッドごとに初期化されないためMockが
    // テストメソッド間で共有されてしまう
    @FreshRuntime
    def "コントローラの背後で動作するサービスをMockのビーンに置き換える"() {
        when:
            controller.index()

        then:
            response.text == 'hello'

        and:
            1 * myService.hello() >> { "hello" }
    }
}
