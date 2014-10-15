package sample

import grails.test.mixin.TestMixin
import grails.test.mixin.gorm.Domain
import grails.test.mixin.hibernate.HibernateTestMixin
import org.codehaus.groovy.grails.test.support.GrailsTestInterceptor
import org.codehaus.groovy.grails.test.support.GrailsTestMode
import spock.lang.Specification
import spock.lang.Stepwise

@Stepwise
@Domain(Person)
@TestMixin(HibernateTestMixin)
class HibernateTestMixinSpec extends Specification {

    GrailsTestInterceptor interceptor

    def setup() {
        interceptor = new GrailsTestInterceptor(this, new GrailsTestMode(
            autowire: true,
            wrapInRequestEnvironment: true,
            wrapInTransaction: this.hasProperty('transactional') ? this['transactional'] : true),
            grailsApplication.mainContext,
            ['Spec', 'Specification', 'Test', 'Tests'] as String[]
        )
        interceptor.init()
    }

    def cleanup() {
        interceptor.destroy()
    }
}
