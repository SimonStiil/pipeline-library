#!/usr/bin/env groovy
def call(Map data) {
/*
Required:
    gitMap: map from scmGetOrgRepo
    events: events to set
    url: url to set
Optional:
    credentialId
    debug: dafaults to false
    quiet: defaults to true
 */
    if (!data.credentialId){
        data.credentialId = "github-login-secret"
    }
    if (!data.debug){
        data.debug = false
    }
    if (!data.quiet){
        data.quiet = false
    }
    withCredentials([usernamePassword(credentialsId: data.credentialId,
            usernameVariable: 'GITHUB_USERNAME',
            passwordVariable: 'GITHUB_PERSONAL')]) {
        String jsonEvents = writeJSON returnText: true, json: data.events
        httpRequest customHeaders: [[name: 'Accept', value: 'application/vnd.github+json'],
                                    [name: 'Authorization', value: 'Bearer ' + GITHUB_PERSONAL],
                                    [name: 'X-GitHub-Api-Version', value: '2022-11-28']],
                httpMode: 'POST',
                url: "https://api.github.com/repos/${data.gitMap.fullName}/hooks",
                requestBody: '{"name":"web","active":true,"events":'+jsonEvents+','+
                        '"config":{"url":"'+ data.url +'","content_type":"json","insecure_ssl":"0"}}',
                consoleLogResponseBody: data.debug,
                quiet: data.quiet,
                wrapAsMultipart: false
    }
}
