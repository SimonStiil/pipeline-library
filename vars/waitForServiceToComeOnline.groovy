#!/usr/bin/env groovy
// vars/waitForServiceToComeOnline.groovy

def call(Map service) {
    String serviceBaseURL = service.serviceBaseURL
    String healthcheckPath = (service.healthcheckPath) ? (service.healthcheckPath) : "actuator/health"
    String validResponseCodes = (service.validResponseCodes) ? (service.validResponseCodes) : "200"
    String acceptType = (service.acceptType) ? (service.acceptType) : "APPLICATION_JSON"
    String contentType = (service.contentType) ? (service.contentType) : "APPLICATION_JSON"
    int maxRetries = (service.maxRetries) ? service.maxRetries : 10
    boolean enableDebugOutput = (service.enableDebugOutput) ? true : false
    boolean ignoreSSL = (service.ignoreSSL) ? true : false

    echo "[INFO] We are waiting for this service to come online: " + serviceBaseURL + "/" + healthcheckPath

    for (i = 1 ; i <= maxRetries ; i++) {
        try {
            httpRequest acceptType: acceptType,
                        consoleLogResponseBody: enableDebugOutput,
                        contentType: contentType,
                        url: serviceBaseURL + "/" + healthcheckPath,
                        validResponseCodes: validResponseCodes,
                        wrapAsMultipart: false,
                        ignoreSslErrors: ignoreSSL
            return true
        } catch (e) {
            if (e instanceof java.lang.IllegalStateException) {
                if (i == maxRetries) {
                    echo "[INFO] Service doesn't seem to be available. Stops trying now."
                    return false
                }
                echo "[INFO] Service not yet running (this is attempt " + i + " of " + maxRetries +
                    "). Trying again in a short while"
                sleep 5
            } else {
                throw e
            }
        }
    }
    return false
}