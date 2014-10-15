※この記事はGrails 2.4.3を元に記述しています

Grails 2.4から`doWithSpring`、`doWithConfig`といったユニットテストの仕組みが導入された。

これを使うとユニットテスト内でSpringビーンを定義したり、コンフィグの値を変更したりできる。

## doWithSpring、doWithConfigの使い方

まずは実際の使用例から。

```
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

@TestMixin(GrailsUnitTestMixin)
class DoWithSpringDoWithConfigSpec extends Specification {

    static doWithSpring = {
        myService(MyService)
    }

    static doWithConfig(ConfigObject config) {
        config.myConfigValue = 'Hello'
    }

    def "doWithSpringとdoWithConfigの動作確認"() {
        expect:
            grailsApplication.mainContext.getBean('myService') != null
            config.myConfigValue == 'Hello'
    }
}
```

`doWithSpring`は`static`な変数として定義し引数にクロージャを取る。
クロージャ中では、[SpringビーンDSL](http://grails.jp/doc/latest/guide/spring.html)を使ってビーンを定義できる。

`doWithConfig`は変数ではなくメソッドとして定義する(`doWithSpring`との統一性を考えるとクロージャでも良かった気がするのだが...)。
このメソッドの引数には`ConfigObject`のインスタンスが渡されるので、このインスタンスを操作することでコンフィグの変更ができる。

`doWithSpring`と`doWithConfig`の実行順は`doWithConfig`、`doWithSpring`の順番になる。
そのため、ビーンの初期化処理でコンフィグの値に依存していたとしてもうまく機能する。


## FreshRuntime

ConfigObjectやGrailsApplicationを含むアプリケーションコンテキストの初期化はスペッククラス内で1度だけ実行される。
Spockでキーワードで言えば`@Shared`や`setupSpec`で構築されたフィクスチャのように考えれば良い。

もしフィーチャ(テスト)メソッドごとにアプリケーションコンテキストの初期化をしたい場合は`grails.test.runtime.FreshRuntime`アノテーションを使う。

```
@FreshRuntime
@TestMixin(GrailsUnitTestMixin)
class DoWithSpringDoWithConfigSpec extends Specification {
...
```

FreshRuntimeアノテーションは上記のようにクラスレベルに設定するか、フィーチャメソッドに設定できる。
ただし、現状は[この問題](https://jira.grails.org/browse/GRAILS-11626)によりクラスレベルに設定してもうまく動作しない。
この問題は2.4.4で修正される(2014/10/15時点の最新バージョンは2.4.3)。


## モックをSpringビーンとして登録する

先ほどのFreshRuntimeアノテーションと`org.codehaus.groovy.grails.commons.InstanceFactoryBean`を使うとSpockのモックをSpringビーンとして登録することができる。
FreshRuntimeアノテーションと組み合わせる必要があるのは、恐らくフィーチャメソッド間でモックを共有してはならないからだと思う。
Spockではインタラクションを持つモックは特定のフィーチャメソッドへの参照をもっているため、static、または@Sharedを使って共有してはならない。
そのため、モックをSpringビーンとして登録する場合も、FreshRuntimeアノテーションを使ってフィーチャメソッドごとに初期化する必要があるのだろう。

モックをSpringビーンとして登録したい状況としては、テスト対象の背後で動作しているSpringビーンのインスタンスをモックに差し替えたい場合が考えられる。
例えば次のようなコントローラがあったとする。

```
import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
class MyController {

    MyService myService

    def index() {
        render myService.hello()
    }
}
```

このコントローラの`index`アクションでは、このコントローラにDIされる`myService`に処理を委譲している。
このコントローラをテストしようとした時、`myService`をモックに差し替えたい場合がある。
これを、次のように記述できる。

```
import grails.test.mixin.TestFor
import grails.test.runtime.FreshRuntime
import org.codehaus.groovy.grails.commons.InstanceFactoryBean
import spock.lang.Specification

@FreshRuntime
@TestFor(MyController)
class MockedBeanSpec extends Specification {

    def myService = Mock(MyService)

    def doWithSpring = {
        myService(InstanceFactoryBean, myService, MyService)
    }

    def "コントローラの背後で動作するサービスをモックに差し替える"() {
        when:
            controller.index()

        then:
            response.text == 'hello'

        and:
            1 * myService.hello() >> { "hello" }
    }
}
```

まずはじめに`def myService = Mock(MyService)`でSpockのモックを定義している。
次に`myService(InstanceFactoryBean, myService, MyService)`といったように、InstanceFactoryBeanのコンストラクタの引数に、このモックのインスタンスとそのクラスの型を指定する。
これで生成したモックを使って`myService`と名前のSpringビーンを登録できる。


## resources.groovyに定義したビーンをユニットテストで使用する

GrailsではSpringビーンDSLを使って、`grails-app/conf/spring/resources.groovy`にビーンの定義ができる。
インテグレーションテストや、Grailsのアプリケーションが起動する場合はこのファイルに定義したビーンが自動的に登録される。
しかし、ユニットテストでは自動的には登録されない。

これを`static loadExternalBeans = true`という設定をスペッククラスに追加することで、自動的にビーンが登録されるようになる。

```
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(MyController)
class LoadingApplicationBeansSpec extends Specification {

    static loadExternalBeans = true

    ...
}
```

そんなとこで。
