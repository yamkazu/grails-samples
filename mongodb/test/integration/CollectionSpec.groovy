import com.mongodb.DB
import grails.plugin.spock.IntegrationSpec
import grails.util.Holders
import spock.lang.IgnoreRest
import spock.lang.Shared

class CollectionSpec extends IntegrationSpec {

    @Shared
    DB db

    def setupSpec() {
        db = Holders.grailsApplication.mainContext.getBean("mongo").getDB("testDb")
    }

    def cleanupSpec() {
        db.dropDatabase()
    }

    @IgnoreRest
    def "コレクションを新規に作成する"() {
        setup:
        assert !db.collectionExists("newCollection")

        when:
        db.createCollection("newCollection", [:])

        then:
        db.collectionExists("newCollection")

        cleanup:
        db.getCollection("newCollection").drop()
    }

    def "コレクションをサイズ指定して生成する"() {
        setup:
        assert !db.collectionExists("newCollection")

        when:
        db.createCollection("newCollection", [size: 1024 * 1024]) // サイズはbyte

        then:
        db.collectionExists("newCollection")

        and:
        def stats = db.getCollection("newCollection").stats
        stats.storageSize == 1024 * 1024

        cleanup:
        db.getCollection("newCollection").drop()
    }

    def "コレクションの名前を変更する"() {
        setup:
        db.createCollection("before", [:])

        assert db.collectionExists("before")
        assert !db.collectionExists("after")

        when:
        db.getCollection("before").rename("after")

        then:
        !db.collectionExists("before")
        db.collectionExists("after")

        cleanup:
        db.getCollection("before").drop()
        db.getCollection("after").drop()
    }

}
