#!/usr/bin/env groovy
def call(Map data) {
/*
Required:
    gitMap: map from scmGetOrgRepo
    hookId: id of the hook to update
Optional:
    url: url to set
    events: events to set
    credentialId
 */
    if (!data.credentialId){
        data.credentialId = "github-login-secret"
    }
    if ( data.events ) {
        withCredentials([usernamePassword(credentialsId: data.credentialId,
                usernameVariable: 'GITHUB_USERNAME',
                passwordVariable: 'GITHUB_PERSONAL')]) {
            // curl -L -H "Accept: application/vnd.github+json" -H "Authorization: Bearer $GITHUB_PERSONAL" -H "X-GitHub-Api-Version: 2022-11-28" https://api.github.com/repos/SimonStiil/keyvaluedatabase/hooks/442580848 -X PATCH -d '{"events":["delete", "push"]}'
            def body = '{"events":'+data.events.toString()+'}'
            echo "githubWebhookUpdate (body): " + body
            httpRequest customHeaders: [[name: 'Accept', value: 'application/vnd.github+json'],
                                                       [name: 'Authorization', value: 'Bearer ' + GITHUB_PERSONAL],
                                                       [name: 'X-GitHub-Api-Version', value: '2022-11-28']],
                    httpMode: 'PATCH',
                    url: "https://api.github.com/repos/${data.gitMap.fullName}/hooks/${data.hookId}",
                    requestBody: '{"events":'+data.events.toString()+'}'
        }
    }
    if ( data.url ) {
        withCredentials([usernamePassword(credentialsId: data.credentialId,
                usernameVariable: 'GITHUB_USERNAME',
                passwordVariable: 'GITHUB_PERSONAL')]) {
            httpRequest customHeaders: [[name: 'Accept', value: 'application/vnd.github+json'],
                                        [name: 'Authorization', value: 'Bearer ' + GITHUB_PERSONAL],
                                        [name: 'X-GitHub-Api-Version', value: '2022-11-28']],
                    httpMode: 'PATCH',
                    url: "https://api.github.com/repos/${data.gitMap.fullName}/hooks/${data.hookId}",
                    requestBody: '{"config":{"url":"'+data.events.toString()+'"}}'
        }
    }
}
