class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"/controller"(view:"/controller")
		"500"(view:'/error')
	}
}
