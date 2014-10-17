import demo.HelloService

class ByNameController {
    def helloServiceImpl

    def index() {
        println "*" * 100
        grailsApplication.mainContext.getBeanNamesForType(HelloService).each { println it }
        println "*" * 100
        render helloServiceImpl.say()
    }
}
