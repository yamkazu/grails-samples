import grails.plugin.spock.IntegrationSpec
import org.jggug.Book

class GormSpec extends IntegrationSpec {

    def "保存してあれ"() {
        when:
        def book = new Book(title: 'mybook').save()

        then:
        book
    }
}
