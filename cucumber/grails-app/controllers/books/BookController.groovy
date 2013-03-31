package books

import grails.converters.JSON

class BookController {

    def add() {
        render new Book(params).save() as JSON
    }

}
