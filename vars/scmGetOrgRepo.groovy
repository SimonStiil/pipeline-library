#!/usr/bin/env groovy
@NonCPS
LinkedHashMap<String,Object> call(String remoteRepoURL) {
    def matcher = remoteRepoURL =~ '(github.com)\\/(([A-Za-z0-9]+)\\/([A-Za-z0-9-_]+))'
    def firstMatchLine = matcher[0]
    def gitMap = [
        host: firstMatchLine[1],
        repoName: firstMatchLine[4],
        orgName: firstMatchLine[3],
        fullName: firstMatchLine[2]
    ]


    return gitMap
}