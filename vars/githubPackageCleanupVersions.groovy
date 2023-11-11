#!/usr/bin/env groovy
def call(Map data) {
    if (!data.credentialId){
        data.credentialId = "github-login-secret"
    }
    if (!data.organization){
        data.organization = data.repository.replace("/"+data.repository_short,"")
    }
    def packageVersions = githubPackagesListVersions data
    def count = 0
    for (item in packageVersions){
        if (item['metadata']['container']['tags'].size() == 0) {
            Map config = new LinkedHashMap<String,String>(data)
            config.version = item['id']
            if (githubPackageDeleteVersion(config)){
                count+=1
            }

        } else {
            if (data.tag && data.github_event == "delete") {
                for (tag in item['metadata']['container']['tags']) {
                    if (tag == data.tag){
                        Map config = new LinkedHashMap<String,String>(data)
                        config.version = item['id']
                        if (githubPackageDeleteVersion(config)){
                            count+=1
                        }
                    }
                }
            }
        }
    }
    echo "Deleted ${count} ${data.PACKAGE_TYPE} versions in package ${data.PACKAGE_NAME}"
}
