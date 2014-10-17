package demo

import grails.gorm.DetachedCriteria
import grails.test.spock.IntegrationSpec

class DetachedCriteriaSpec extends IntegrationSpec {

    def "projection"() {
        setup:
        new Person(firstName: "John", lastName: "Smith", age: 40).save(flush: true, failOnError: true)
        new Person(firstName: "Kazuki", lastName: "Yamamoto", age: 31).save(flush: true, failOnError: true)

        when:
        def results = Person.withCriteria {
//            gtAll "age", new DetachedCriteria(Person).build {
                projections {
                    property "age"
                }
                between 'age', 18, 65
//            order "firstName"
        }

        then:
        results == []
    }
}
