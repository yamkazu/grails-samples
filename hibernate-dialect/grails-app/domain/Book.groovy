class Book {

    String hoge
    String foo
    String bar

    static constraints = {
        hoge maxSize: 254
        foo maxSize: 255
        bar maxSize: 256
    }
}
