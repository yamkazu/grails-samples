package grails.example

import javax.inject.Inject
import javax.inject.Named

@Named
class NamedBean {

    @Inject
    NamedBeanHoge namedBeanHoge

    @Inject
    @Named("namedPiyo")
    def piyo

    @Inject
    @Named("grailsApplication")
    def grailsApplication

    String toString() { "This is NamedBean" }
}

@Named
class NamedBeanHoge {
    String toString() { "This is NamedBeanHoge" }
}

@Named("namedPiyo")
class NamedBeanPiyo {
    String toString() { "This is NamedBeanPiyo" }
}
