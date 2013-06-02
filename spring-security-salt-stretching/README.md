GrailsのSpring Security Core Pluginでパスワードのソルトとストレッチング
=======================================================================

パスワードをハッシュで保存して置くのは当たり前ですが、レインボーテーブル使用した総当たり探索の対策として、ソルトとストレッチングを組み合わせ、より安全にパスワードを保存するのが一般的になってきました。

Grailsの[Spring Security Core Plugin](http://grails.org/plugin/spring-security-core)はデフォルトでは`SHA-256`のハッシュでパスワードを保存しますが、ソルトやストレッチングは使用されていません。しかし、そこは認証のデパートSpring Security、ソルトとストレッチングのサポートが組み込まれています。

まずはソルトのサポートから見て行きましょう。

usernameをソルトとする
----------------------

usernameをソルトとして扱うには`Config.groovy`に以下の設定を追加します。

```
grails.plugins.springsecurity.dao.reflectionSaltSourceProperty = 'username'
```

これで認証時に、usernameをソルトとしてパスワードに付与しハッシュを計算するようになります。ただし、この設定はあくまで認証時の設定であり、ユーザのパスワードを保存する時にソルトが付与されるわけではありません。

もし、`s2-quickstart`コマンドを使って生成したユーザドメインクラスを使用している場合は、このドメインクラスに手を入れます。デフォルトでは以下のようにパスワードがエンコードされています。

```groovy
protected void encodePassword() {
    password = springSecurityService.encodePassword(password)
}
```

これを以下のように変更します。

```groovy
protected void encodePassword() {
    password = springSecurityService.encodePassword(password, username)
}
```

この様にspringSecurityService#encodePasswordの第2引数にソルトとしてusernameを指定します。

これでパスワード保存時にusernameをソルトとして付与してハッシュが計算されます。

ソルト専用のプロパティを使う
----------------------------

[徳丸先生の資料](http://www.slideshare.net/ockeghem/how-to-guard-your-password/41)によるとソルトには一定の長さが必要です。usernameとpasswordの組み合わせで一定以上の長さが確保できない場合は、一定の長さをもつソルト専用のプロパティが欲しくなります。

以下のようにユーザドメインクラスを変更します。

```groovy
import org.apache.commons.lang.RandomStringUtils

class User {

    ...

    String salt

    String getSalt() {
        // 新規作成時にsaltがない場合にsaltを生成
        if (!salt) {
            // ランダムである必要ないが一定の長さをもつ文字列として
            // ランダムが扱いやすいのでRandomStringUtilsを使用
            salt = RandomStringUtils.randomAlphanumeric(20) // ランダムな20文字
        }
        return salt
    }

    static constraints = {
        ...
        salt blank: false
    }

    static mapping = {
        ...
        // 安全のためinsert以外でsaltが更新されないようにする
        salt updateable: false
    }

    protected void encodePassword() {
        // usernameをsaltに変更
        password = springSecurityService.encodePassword(password, salt)
    }
}
```

合わせて`Config.groovy`の`reflectionSaltSourceProperty`も変更しましょう。

```
grails.plugins.springsecurity.dao.reflectionSaltSourceProperty = 'salt'
```

これで設定完了！と言いたいところですが、まだいくつかの準備が必要です。

上記の`reflectionSaltSourceProperty`はSpringSecurityの`UserDetails`を実装したクラスのプロパティを指定しています。GrailsのSpring Security Core Pluginのデフォルトでは`UserDetails`の実装クラスに`GrailsUser`が使われます。
しかし、`GrailsUser`には`salt`というプロパティは存在しないため、新たにクラスを用意する必要があります。

また、この`UserDetails`のインスタンスは`UserDetailsService`の実装クラスで生成されます。GrailsのSpring Security Core Pluginでは、これが`GormUserDetailsService`というクラスになります。

つまり独自の`UserDetailsService`を作成し、`salt`というプロパティを持つ独自の`UserDetails`を生成する必要があります。

クラスは`src/groovy`などに作成すると良いでしょう。

まずは`UserDetails`の実装クラスを以下のよう作成します。

```groovy
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUser
import org.springframework.security.core.userdetails.UserDetails

class MyUser implements UserDetails {

    @Delegate
    GrailsUser grailsUser

    String salt
}
```

次に上記の`MyUser`クラスを生成する`UserDetailsService`を作ります。既存の`GormUserDetailsService`を継承して作るがカンタンです。

```groovy
import org.codehaus.groovy.grails.plugins.springsecurity.GormUserDetailsService
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class MyUserDetailsService extends GormUserDetailsService {

    @Override
    protected UserDetails createUserDetails(user, Collection<GrantedAuthority> authorities) {
        new MyUser(grailsUser: super.createUserDetails(user, authorities), salt: user.salt)
    }
}
```

groovyの`@Delegate`を使用したお手軽実装にしてみました。

あとはこのサービスをSpringのビーンとして登録します。`resource.groovy`に以下のように追加してください。

```groovy
beans = {
    ...
    userDetailsService(MyUserDetailsService) {
        grailsApplication = ref('grailsApplication')
    }
}
```

これでソルト専用のプロパティを使うことができます。

ストレッチング
--------------

GrailsのSpring Security Core Pluginのデフォルトでは`passwordEncoder`に`org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder`が使われています。

このクラスはストレッチングをサポートしています。
しかし、プラグインの設定では`grails.plugins.springsecurity.password.algorithm`というハッシュアルゴリズムの設定があるだけで、ストレッチング回数の設定はありません(デフォルトでは1回です)。

自分で新たにビーンを定義することもできますが、ここはGrailsが持つ、[設定ファイルからSpringビーンのプロパティを上書きする仕組みを](http://grails.jp/doc/latest/guide/spring.html#propertyOverrideConfiguration)活用します。

`Config.groovy`に以下の設定を追加します。

```
beans.passwordEncoder.iterations = 1000
```

これで1000回ストレッチングします。

テスト
------

ソルトとストレッチングが機能しているか不安になるのでテストを書いてみましょう。

```groovy
def "ソルトとストレッチングの確認"() {
    setup:
    def password = "password"
    def user = new User(username: "test", password: password, enabled: true)

    and: "パスワードにランダム文字列をソルトとして付与"
    def digest = "$password{$user.salt}".getBytes("UTF-8")

    and: "1000回ストレッチング"
    1000.times { digest = MessageDigest.getInstance("SHA-256").digest(digest) }
    def hashedPassword = new String(Hex.encode(digest))

    when: "新規Userを保存"
    user.save()

    then: "ソルトとストレッチングを使用したハッシュになっていること"
    user.password == hashedPassword
}
```

うまく動いているようです。

おわりに
--------

ということでソルトとストレッチングで安全にパスワードを保存しましょう。
プラグインのリファレンスは以下を詳しく見ておくとよいです。

http://grails-plugins.github.io/grails-spring-security-core/docs/manual/guide/12%20Password%20and%20Account%20Protection.html

なお、この記事の内容は自己責任でご利用ください。ではでは。

この記事はGrails2.2.1、Spring Security Core Plugin1.2.7.3をもとに記述しています。
