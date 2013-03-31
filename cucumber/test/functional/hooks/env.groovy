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