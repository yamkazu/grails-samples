import com.mongodb.DB
import grails.plugin.spock.IntegrationSpec
import grails.util.Holders
import org.bson.types.ObjectId
import spock.lang.Shared

class LowLevelApiSpec extends IntegrationSpec {

    @Shared
    DB db

    def setupSpec() {
        db = Holders.grailsApplication.mainContext.getBean("mongo").getDB("testDb")
    }

    def cleanupSpec() {
        db.dropDatabase()
    }

    def setup() {
    }

    def cleanup() {
        db.users.drop()
    }

    def "新規に保存すると引数として渡したマップに_idが設定される"() {
        setup:
        def user = [lastname: "knuth"]

        when:
        db.users.save(user)

        then:
        user._id != null
        db.users.count() == 1
    }

    def "findOneで1件検索する"() {
        setup:
        db.users.save(lastname: "knuth")

        when:
        def user = db.users.findOne()

        then:
        user.lastname == "knuth"
    }

    def "findOneに_idを指定して検索する"() {
        setup:
        def user = [lastname: "smith"]
        db.users.insert(user)
        assert user._id

        when:
        def findUser = db.users.findOne(_id: user._id)

        then:
        user == findUser
    }

    def "検索条件を指定してfindする"() {
        setup:
        db.users.insert(age: 19)
        db.users.insert(age: 20)
        db.users.insert(age: 12)
        db.users.insert(age: 21)
        db.users.insert(age: 30)

        when:
        def users = db.users.find(age: [$gt: 20])

        then:
        users.size() == 2
        users*.age == [21, 30]
    }

    def "updateで更新する"() {
        setup:
        db.users.insert(name: 'jeff', age: 30)
        db.users.insert(name: 'smith', age: 40)

        when:
        db.users.update([name: 'smith'], [$set: [age: 35]])

        then:
        db.users.findOne(name: 'smith').age == 35
    }

    def "デフォルトではupdateはひとつの要素しか更新しない"() {
        setup:
        db.users.insert(name: 'smith', age: 30)
        db.users.insert(name: 'smith', age: 30)

        when:
        db.users.update([name: 'smith'], [$set: [age: 35]])

        then:
        db.users.find()*.age == [35, 30]
    }

    def "updateで複数の値を更新するにはmultiをtrueにする"() {
        setup:
        db.users.insert(name: 'smith', age: 30)
        db.users.insert(name: 'smith', age: 30)

        when:
        db.users.update([name: 'smith'], [$set: [age: 35]], false, true)

        then:
        db.users.find()*.age == [35, 35]
    }

    def "removeで条件を指定して削除する"() {
        setup:
        db.users.insert(name: 'jeff', age: 30)
        db.users.insert(name: 'smith', age: 40)

        when:
        db.users.remove(name: 'jeff')

        then:
        db.users.find()*.name == ['smith']
    }

    def "removeで条件を指定せずに削除する"() {
        setup:
        db.users.insert(name: 'jeff', age: 30)
        db.users.insert(name: 'smith', age: 40)

        when:
        db.users.remove([:])

        then:
        db.users.count() == 0

        and: "コレクション自体は削除されない"
        db.collectionExists("users")
    }

    def "dropですべてを削除する"() {
        setup:
        db.users.insert(name: 'jeff', age: 30)
        db.users.insert(name: 'smith', age: 40)

        when:
        db.users.drop()

        then:
        db.users.count() == 0

        and: "コレクションも削除される"
        !db.collectionExists("users")
    }

    def "コマンドを実行する"() {
        setup:
        db.users.insert(name: 'smith', age: 40)
        db.getCollection("").insert([:])

        when:
        def result = db.command('ismaster')

        then:
        result.ok()
    }

    def "ObjectIdから生成日時を取得する"() {
        setup:
        def user = [name: 'smith']
        db.users.insert(user)
        assert user._id

        when:
        def id = ObjectId.massageToObjectId(user._id)

        then:
        id.getTime()
    }
}
