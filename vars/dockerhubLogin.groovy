#!/usr/bin/env groovy
def call(Map properties) {
    if (!properties.credentialId){
        properties.credentialId = "dockerhub-login-secret"
    }
    if (!properties.quiet){
        properties.quiet = true
    }
    // https://sanjeevrohila.medium.com/dockerhub-push-images-and-delete-with-api-3925f751dfbf
    withCredentials([usernamePassword(credentialsId: properties.credentialId,
            usernameVariable : 'DOCKERHUB_USERNAME',
            passwordVariable: 'DOCKERHUB_PERSONAL' )]) {
        def response = httpRequest customHeaders: [[name: 'Accept', value: 'application/json'],
                                                   [name: 'Content-Type', value: 'application/json']],
                url: "https://hub.docker.com/v2/users/login/",
                consoleLogResponseBody: false,
                quiet: properties.quiet,
                wrapAsMultipart: false,
                httpMode: "POST",
                validResponseCodes: "200"
                requestBody: '{"username":"'+DOCKERHUB_USERNAME+'", "password":"'+DOCKERHUB_PERSONAL+'"}"'
        def jsonResponse = readJSON text: response.content
        return jsonResponse.token
    }
}
