package grails.example

import grails.plugin.spock.IntegrationSpec
import org.springframework.stereotype.Repository

@Repository
class InjectSpec extends IntegrationSpec {

    def myBean
    def namedBean

    def postConstructAndPreDestroyBean

    def "@Componentを使用してbeanを登録"() {
        expect: "beanが登録されていること"
        "$myBean" == "This is MyBean"

        and: "独自で定義したbeanがinjectされていること"
        "$myBean.myBeanHoge" == "This is MyBeanHoge"
        "$myBean.piyo" == "This is MyBeanPiyo"

        and: "grailsのbeanも取得できること"
        myBean.grailsApplication.config.grails.spring.bean.packages == ["grails.example"]
    }

    def "jsr330の@Namedを使用してbeanを登録"() {
        expect: "beanが登録されていること"
        "$namedBean" == "This is NamedBean"

        and: "独自で定義したbeanがinjectされていること"
        "$namedBean.namedBeanHoge" == "This is NamedBeanHoge"
        "$namedBean.piyo" == "This is NamedBeanPiyo"

        and: "grailsのbeanも取得できること"
        namedBean.grailsApplication.config.grails.spring.bean.packages == ["grails.example"]
    }

    def "PostConstructとPreDestroyBeanも使える"() {
        expect:
        postConstructAndPreDestroyBean.number == 100
    }

}
