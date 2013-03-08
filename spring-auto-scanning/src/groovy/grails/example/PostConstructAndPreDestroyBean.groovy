package grails.example

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Named

@Named
class PostConstructAndPreDestroyBean {

    def number

    @PostConstruct
    def init() { number = 100 }

    @PreDestroy
    def destroy() {}
}
