
GrailsでアノテーションベースでBeanを登録する
============================================

[元ネタ](http://mrhaki.blogspot.jp/2013/02/grails-goodness-injecting-grails.html)

Grailsでは`grails-app/service`ディレクトリ配下などにクラスを置くと自動的にSpringのbeanとして認識されますが、`src/groovy`や`src/java`といったディレクトリでは自動的にはbeanとして登録されません。

`src/groovy`、`src/java`配下のクラスをbeanとして登録したい場合は[Spring Bean DSL](http://grails.org/doc/latest/guide/spring.html#springdslAdditional)を使用して登録することができますが、もう一つの方法としてSpringの`component-scan`を使用する方法がGrailsでも提供されています。

`component-scan`を使用すると指定したパッケージ配下のクラスに対してアノテーションベースでbean登録ができるようになります。

設定の準備
----------

Grailsで`component-scan`を使用するにはConfig.groovyで`grails.spring.bean.packages`を指定します。

```groovy
grails.spring.bean.packages = ["grails.example"]
```

これであとは`grails.example`配下にアノテーションベースで定義したクラスを置くことで自動的にbean登録されます。


アノテーションベースでbeanを定義する
------------------------------------

Grails特有のルールというのは基本的になくSpringのルールに従うだけです。詳細はSpringのドキュメントを参照してくだい。

いくつかサンプルを紹介します。

### Springのアノテーションを使用して登録する ###

Springの`@Component`、`@Autowired`、`@Qualifier`などを使用して登録します。

```groovy
package grails.example

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class MyBean {

    @Autowired
    MyBeanHoge myBeanHoge

    @Autowired
    @Qualifier("myPiyo")
    def piyo

    @Autowired
    @Qualifier("grailsApplication")
    def grailsApplication

    String toString() { "This is MyBean" }
}

@Component
class MyBeanHoge {
    String toString() { "This is MyBeanHoge" }
}

@Component("myPiyo")
class MyBeanPiyo {
    String toString() { "This is MyBeanPiyo" }
}
```

### JSR330を使って登録する ###

JSR330の`@Inject`、`@Named`を使用して登録します。

```groovy
package grails.example

import javax.inject.Inject
import javax.inject.Named

@Named
class NamedBean {

    @Inject
    NamedBeanHoge namedBeanHoge

    @Inject
    @Named("namedPiyo")
    def piyo

    @Inject
    @Named("grailsApplication")
    def grailsApplication

    String toString() { "This is NamedBean" }
}

@Named
class NamedBeanHoge {
    String toString() { "This is NamedBeanHoge" }
}

@Named("namedPiyo")
class NamedBeanPiyo {
    String toString() { "This is NamedBeanPiyo" }
}
```

JSR330のアノテーションを使用するには依存ライブラリの追加が必要です。

```groovy
dependencies {
    ...
    compile 'javax.inject:javax.inject:1'
}
```

### JSR250系も使える ###

```groovy
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
```

