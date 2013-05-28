import demo.security.MyUserDetailsService

// Place your Spring DSL code here
beans = {
    userDetailsService(MyUserDetailsService) {
        grailsApplication = ref('grailsApplication')
    }
}
