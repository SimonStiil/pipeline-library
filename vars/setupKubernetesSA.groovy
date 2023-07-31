#!/usr/bin/env groovy
/* Can only be used on a node with kubectl available */
@NonCPS
boolean call(Map input) {
    String serviceaccountPath = (service.serviceaccountPath) ? (service.serviceaccountPath) : "/var/run/secrets/kubernetes.io/serviceaccount"
    if (fileExists(serviceaccountPath)){
        withEnv(["SAPATH=${serviceaccountPath}"]) {
            sh '''
              kubectl config set-cluster default --server=https://kubernetes.default --certificate-authority=$SAPATH/ca.crt
              set +x
              token=$(cat $SAPATH/token)
              kubectl config set-credentials default --token=${token}
              set -x
              kubectl config set-context default --cluster=default --user=default
              kubectl config use-context default
            '''
        }
        return true
    }
	return false
}
