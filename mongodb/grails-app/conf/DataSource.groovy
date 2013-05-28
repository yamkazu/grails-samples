grails {
    mongo {
        host = "localhost"
        port = 27017
    }
}
// environment specific settings
environments {
    development {
        grails {
            mongo {
                databaseName = "devDb"
            }
        }
    }
    test {
        grails {
            mongo {
                databaseName = "testDb"
            }
        }
    }
    production {
        grails {
            mongo {
                databaseName = "prodDb"
            }
        }
    }
}
