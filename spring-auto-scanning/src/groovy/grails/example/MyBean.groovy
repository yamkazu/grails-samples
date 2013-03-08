package grails.example

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class MyBean {

    @Autowired
    MyBeanHoge myBeanHoge

    @Autowired
    @Qualifier("myPiyo")
    def piyo

    @Autowired
    @Qualifier("grailsApplication")
    def grailsApplication

    String toString() { "This is MyBean" }
}

@Component
class MyBeanHoge {
    String toString() { "This is MyBeanHoge" }
}

@Component("myPiyo")
class MyBeanPiyo {
    String toString() { "This is MyBeanPiyo" }
}
