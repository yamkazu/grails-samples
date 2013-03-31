
# GrailsでCucumberを使用する

## 準備

cucumber pluginを`BuildConfig.groovy`に追加しインストールします。

    plugins {
        ...
        test ":cucumber:0.8.0"
    }

## featurの準備

デフォルトではcucumberに必要なfeatureやstepファイルは`test/functional`に格納します。これは設定ファイルで変更することも可能です。
まず始めに以下の様なfeatureを`test/functional/NewBook.feature`に作成します。

    #language: ja
    
    フィーチャ: 新しい本の登録
        本の所有者として
        私はBookTrackerに自分の本を追加したい
        自分でそれを覚えておく必要がないように
    
    シナリオ: 新しい本
        前提 BookTrackerを開く
        もし "Specification by Example"を追加する
        ならば "Specification by Example"の詳細を参照できる

日本語でfeatureファイルを記述するには`#language: ja`をファイルの先頭に追加する必要があります。

## 実行

実行はGrailsのfuctionalテストとして実行します。

    grails test-app functional:
    grails test-app :cucumber

まだstepを実装していないため、以下のようなエラーが出力されるはずです。

    You can implement missing steps with the snippets below:
    
    前提(~'^BookTrackerを開く$') { ->
        // Express the Regexp above with the code you wish you had
        throw new PendingException()
    }
    もし(~'^"([^"]*)"を追加する$') { String arg1 ->
        // Express the Regexp above with the code you wish you had
        throw new PendingException()
    }
    ならば(~'^"([^"]*)"の詳細を参照できる$') { String arg1 ->
        // Express the Regexp above with the code you wish you had
        throw new PendingException()
    }

## stepの実装

`test/functional/steps/Book_steps.groovy`を作成してstepを実装していきます。デフォルトではstepは`test/functional/steps/`ディレクトリに格納する必要があります。step実装の主な流れは以下のようになります。

  * 先ほどの出力をコピーする
  * `PendingException`をインポートする
  * `JA`のlanguageをインポートする
  * (ダブルクオートで使用している場合は$をエスケープする、GStringの制約のため)

これを実施すると以下のようなファイルになります。

    import cucumber.runtime.PendingException
    
    this.metaClass.mixin(cucumber.api.groovy.JA)
    // 以下のようにstaticインポートしても同じ
    //import static cucumber.api.groovy.JA.*
    
    前提(~'^BookTrackerを開く$') {->
        // Express the Regexp above with the code you wish you had
        throw new PendingException()
    }
    もし(~'^"([^"]*)"を追加する$') { String arg1 ->
        // Express the Regexp above with the code you wish you had
        throw new PendingException()
    }
    ならば(~'^"([^"]*)"の詳細を参照できる$') { String arg1 ->
        // Express the Regexp above with the code you wish you had
        throw new PendingException()
    }

ここで再度テストを実行すると、`PendingException`がスローされます。それでは次に必要な実装を追加していきます。

### 前提(~'^BookTrackerを開く$')

必要なセットアップがあるならここで実装を追加しますが、今回は特に必要なセットアップが存在しないため、単に何もしないように変更します。

    前提(~'^BookTrackerを開く$') {->
        // NOP
    }

### もし(~'^"([^"]*)"を追加する$')

まず、必要となるdomainとcontrollerを実装します。今回作成するcontrollerは単にJSONを返すインタフェースになっています。

domain:

    package books
    
    class Book {
        String title
    }

controller:

    package books
    
    import grails.converters.JSON
    
    class BookController {
    
        def add() {
            render new Book(params).save() as JSON
        }
    
    }

次にstepを次のように実装します。

    もし(~'^"([^"]*)"を追加する$') { String bookTitle ->
        bookController = new BookController()
        bookController.params.title = bookTitle
        bookController.add()
    }

ここでは詳細には説明しませんが、これはGrailsにおけるcontrollerのテストの仕組みにそってテスト実装する必要があります。
この状態で再度テストを実行すると`java.lang.IllegalStateException`がスローされます。
これを解消するには、コントローラが外部から呼ばれているように見せるために、コントローラのテストに必要なセットアップとクリーンアップのコードを追加する必要があります。

### Before & After

`test/functional/hooks/env.groovy`に以下のようなファイルを追加します。これによりコントローラをテストする際のモック機能が有効になります。

    import org.codehaus.groovy.grails.test.support.GrailsTestRequestEnvironmentInterceptor
    
    this.metaClass.mixin(cucumber.api.groovy.Hooks)
    
    GrailsTestRequestEnvironmentInterceptor scenarioInterceptor
    
    Before() {
        scenarioInterceptor = new GrailsTestRequestEnvironmentInterceptor(appCtx)
        scenarioInterceptor.init()
    }
    
    After() {
        scenarioInterceptor.destroy()
    }

この状態でテストを実施すると、先ほどの`java.lang.IllegalStateException`がスローされずにstepの最後のブロックで、PendingExceptionが発生します。

### ならば(~'^"([^"]*)"の詳細を参照できる$')

以下のように実装します。

    ならば(~'^"([^"]*)"の詳細を参照できる$') { String bookTitle ->
        def actual = bookController.response.json
    
        assert actual.id
        assert actual.title  == bookTitle
    }

controllerのレスポンスに格納されているjsonの値を参照し、saveした際に生成されるidと、paramsから取得したtitleが正しく設定されているか検証しています。

最後にもう一度テストを実行してみます。

    $ grails test-app functional:
    | Server running. Browse to http://localhost:8080/cucumber
    | Running 1 cucumber test...
    | Completed 1 cucumber test, 0 failed in 2429ms
    | Tests PASSED - view reports in /Users/yamkazu/IdeaProjects/grails-examples/cucumber/target/test-reports

うまくテストが通りました。

# 参考

* [Automating Specification with Cucumber & Grails](https://github.com/hauner/grails-cucumber/wiki/Automating-Specification-with-Cucumber-and-Grail://github.com/hauner/grails-cucumber/wiki/Automating-Specification-with-Cucumber-and-Grails)

