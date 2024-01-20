#!/usr/bin/env groovy
boolean call(Map properties) {
    if (!properties.debug){
        properties.debug = false
    }
    if (!properties.quiet){
        properties.quiet = true
    }
    def response = httpRequest customHeaders: [[name: 'Accept', value: 'application/json'],
                                               [name: 'Authorization', value: 'JWT ' + properties.token]],
            url: "https://hub.docker.com/v2/repositories/${properties.organization}/${properties.PACKAGE_NAME}/tags/${properties.version}",
            httpMode: 'DELETE',
            consoleLogResponseBody: properties.debug,
            quiet: properties.quiet,
            wrapAsMultipart: false
    echo response.toString()
    if (response.status != 204) {
        echo "Failed to delete ${properties.PACKAGE_TYPE} ${properties.organization}/${properties.PACKAGE_NAME} version ${properties.version}"
        return false
    }
    return true
}
