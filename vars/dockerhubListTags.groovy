#!/usr/bin/env groovy
def call(Map properties) {
    if (!properties.debug){
        properties.debug = false
    }
    if (!properties.quiet){
        properties.quiet = true
    }
    // https://sanjeevrohila.medium.com/dockerhub-push-images-and-delete-with-api-3925f751dfbf
    def response = httpRequest customHeaders: [[name: 'Accept', value: 'application/json'],
                                               [name: 'Authorization', value: 'JWT ' + properties.token]],
            url: "https://hub.docker.com/v2/repositories/${properties.organization}/${properties.PACKAGE_NAME}/tags",
            consoleLogResponseBody: properties.debug,
            quiet: properties.quiet,
            wrapAsMultipart: false
    echo response.toString()
    def jsonResponse = readJSON text: response.content
    return jsonResponse
}