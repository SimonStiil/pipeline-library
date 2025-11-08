#!/usr/bin/env groovy
def call(Map properties, String token, String digestOrTag) {
    if (!properties.credentialId){
        properties.credentialId = "github-login-secret"
    }
    if (!properties.debug){
        properties.debug = false
    }
    if (!properties.quiet){
        properties.quiet = true
    }
    // Accept all manifest types
    def acceptHeader = [
        'application/vnd.oci.image.manifest.v1+json',
        'application/vnd.oci.image.index.v1+json',
        'application/vnd.docker.distribution.manifest.v2+json',
        'application/vnd.docker.distribution.manifest.list.v2+json',
        'application/vnd.docker.distribution.manifest.v1+json'
    ].join(', ')
    
    def manifestResponse = httpRequest(
        customHeaders: [
            [name: 'Accept', value: acceptHeader],
            [name: 'Authorization', value: "Bearer ${token}"]
        ],
        url: "https://ghcr.io/v2/${properties.organization}/${properties.PACKAGE_NAME}/manifests/${digestOrTag}",
        consoleLogResponseBody: properties.debug,
        quiet: properties.quiet,
        wrapAsMultipart: false
    )
    
    def manifest = readJSON text: manifestResponse.content
    def manifestInfo = [:]
    
    // Check if this is a manifest list/index (multi-platform)
    if (manifest.mediaType == 'application/vnd.docker.distribution.manifest.list.v2+json' ||
        manifest.mediaType == 'application/vnd.oci.image.index.v1+json') {
        
        manifestInfo.type = 'manifest_list'
        manifestInfo.mediaType = manifest.mediaType
        manifestInfo.schemaVersion = manifest.schemaVersion
        manifestInfo.manifests = []
        
        manifest.manifests.each { m ->
            manifestInfo.manifests << [
                digest: m.digest,
                mediaType: m.mediaType,
                size: m.size,
                platform: [
                    architecture: m.platform.architecture,
                    os: m.platform.os,
                    variant: m.platform.variant ?: null,
                    'os.version': m.platform.'os.version' ?: null
                ]
            ]
        }
        
        if (properties.debug) {
            echo "Found manifest list with ${manifestInfo.manifests.size()} platforms"
        }
        
        return manifestInfo
    }
    
    // This is a single-platform manifest
    manifestInfo.type = 'single_manifest'
    manifestInfo.mediaType = manifest.mediaType
    manifestInfo.schemaVersion = manifest.schemaVersion
    
    // Parse config blob to get OS/arch info
    def configDigest = manifest.config.digest
    def configResponse = httpRequest(
        customHeaders: [
            [name: 'Accept', value: 'application/vnd.docker.container.image.v1+json, application/vnd.oci.image.config.v1+json'],
            [name: 'Authorization', value: "Bearer ${token}"]
        ],
        url: "https://ghcr.io/v2/${properties.organization}/${properties.PACKAGE_NAME}/blobs/${configDigest}",
        consoleLogResponseBody: properties.debug,
        quiet: properties.quiet,
        wrapAsMultipart: false
    )
    
    def config = readJSON text: configResponse.content
    manifestInfo.platform = [
        architecture: config.architecture,
        os: config.os,
        variant: config.variant ?: null
    ]
    
    manifestInfo.layers = manifest.layers.size()
    manifestInfo.configDigest = configDigest
    
    return manifestInfo
}
