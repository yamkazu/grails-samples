import MyExceptionHandler

class MyController implements MyExceptionHandler {

    def index() {
        throw new MyException()
    }
}
