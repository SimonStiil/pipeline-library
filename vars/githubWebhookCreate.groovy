#!/usr/bin/env groovy
def call(Map data) {
/*
Required:
    gitMap: map from scmGetOrgRepo
    events: events to set
    url: url to set
Optional:
    credentialId
 */
    if (!data.credentialId){
        data.credentialId = "github-login-secret"
    }
    withCredentials([usernamePassword(credentialsId: data.credentialId,
            usernameVariable: 'GITHUB_USERNAME',
            passwordVariable: 'GITHUB_PERSONAL')]) {
        httpRequest customHeaders: [[name: 'Accept', value: 'application/vnd.github+json'],
                                    [name: 'Authorization', value: 'Bearer ' + GITHUB_PERSONAL],
                                    [name: 'X-GitHub-Api-Version', value: '2022-11-28']],
                httpMode: 'POST',
                url: "https://api.github.com/repos/${data.gitMap.fullName}/hooks",
                requestBody: '{"name":"web","active":true,"events":'+data.events.toString()+','+
                        '"config":{"url":"'+ data.url +'","content_type":"json","insecure_ssl":"0"}}'
    }
}
