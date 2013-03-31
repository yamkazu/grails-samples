import books.BookController

this.metaClass.mixin(cucumber.api.groovy.JA)
// 以下のようにstaticインポートしても同じ
//import static cucumber.api.groovy.JA.*

// シナリオ状態
BookController bookController

前提(~'^BookTrackerを開く$') {->
    // NOP
}
もし(~'^"([^"]*)"を追加する$') { String bookTitle ->
    bookController = new BookController()
    bookController.params.title = bookTitle
    bookController.add()
}
ならば(~'^"([^"]*)"の詳細を参照できる$') { String bookTitle ->
    def actual = bookController.response.json

    assert actual.id
    assert actual.title  == bookTitle
}