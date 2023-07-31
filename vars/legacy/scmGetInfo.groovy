#!/usr/bin/env groovy
// File kept as legacy. Unused after finding "scmData = checkout scm"

LinkedHashMap<String,Object> call() {
    def gitMap = [:]
    gitMap["url"] = scmGetRemoteURL()
    gitMap["commit"] = scmGetSha(gitMap["url"])
    gitOrgRepoMap = scmGetOrgRepo(gitMap["url"])
    gitMap["tag"] = scmGetTagName()
    gitMap["branch"] = BRANCH_NAME
    gitMap["orgName"] = gitOrgRepoMap.orgName
    gitMap["repoName"] = gitOrgRepoMap.repoName
    return gitMap
}
/* Why not use information from build...
 * https://javadoc.jenkins.io/plugin/workflow-support/org/jenkinsci/plugins/workflow/support/steps/build/RunWrapper.html
 * https://javadoc.jenkins.io/hudson/model/Run.html?is-external=true
 * Run.getParent() is restricted and cannot be used to gain access to the build :-( as
 * https://javadoc.jenkins.io/plugin/git/hudson/plugins/git/util/BuildData.html
 * https://javadoc.jenkins.io/plugin/git/hudson/plugins/git/util/Build.html
 * would contain all the information :-/
 */
