package demo

import grails.plugin.spock.IntegrationSpec
import org.springframework.security.core.codec.Hex

import java.security.MessageDigest

class UserSpec extends IntegrationSpec {

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
}
