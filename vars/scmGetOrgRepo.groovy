#!/usr/bin/env groovy
@NonCPS
LinkedHashMap<String,Object> call(String remoteRepoURL) {
    String[] strings = remoteRepoURL.replace(".git","").split("/")
    String repoName = strings[strings.length-1]
    String orgName = strings[strings.length-2]
    def gitMap = [
        repoName: repoName, 
        orgName: orgName, 
        fullName: orgName+"/"+repoName
    ]
    return gitMap
}