package demo

import grails.plugin.spock.IntegrationSpec
import org.springframework.security.core.codec.Hex
import spock.lang.Ignore

import java.security.MessageDigest

class UserSpec extends IntegrationSpec {

    @Ignore
    def "デフォルトではpasswordはSHA-256でハッシュ化される"() {
        setup:
        def password = "password"

        and: "passwordをSHA-256でハッシュ化"
        def digest = MessageDigest.getInstance("SHA-256").digest(password.getBytes("UTF-8"))
        def hashedPassword = new String(Hex.encode(digest))

        when: "passwordを設定し新規Userを保存"
        def user = new User(username: "test", password: password).save()

        then: "保存したパスワードはハッシュ化されて保存されていること"
        user.password == hashedPassword
    }

    @Ignore
    def "usernameをsaltとして使う"() {
        setup:
        def username = "test"
        def password = "password"

        and: "saltはpasswordに{username}のフォーマットで結合されハッシュ化される"
        def digest = MessageDigest.getInstance("SHA-256").digest("$password{$username}".getBytes("UTF-8"))
        def hashedPassword = new String(Hex.encode(digest))

        when: "passwordを設定し新規Userを保存"
        def user = new User(username: username, password: password).save()

        then: "salt付きでハッシュ化されていること"
        user.password == hashedPassword
    }

    @Ignore
    def "salt専用のプロパティを追加する"() {
        setup:
        def password = "password"
        def user = new User(username: "test", password: password)

        and: "User#saltをsaltとして使用する"
        def digest = MessageDigest.getInstance("SHA-256").digest("$password{$user.salt}".getBytes("UTF-8"))
        def hashedPassword = new String(Hex.encode(digest))

        when: "新規Userを保存"
        user.save()

        then: "salt付きでハッシュ化されていること"
        user.password == hashedPassword
    }

    def "ストレッチングを追加"() {
        setup:
        def password = "password"
        def user = new User(username: "test", password: password)

        and: "1000回ストレッチングを実施"
        def digest = "$password{$user.salt}".getBytes("UTF-8")
        1000.times { digest = MessageDigest.getInstance("SHA-256").digest(digest) }
        def hashedPassword = new String(Hex.encode(digest))

        when: "新規Userを保存"
        user.save()

        then: "ストレッチングされてハッシュ化されていること"
        user.password == hashedPassword
    }
}
