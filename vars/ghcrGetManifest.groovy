#!/usr/bin/env groovy
def call(Map properties, String token, String digestOrTag) {
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
    // Note: Blob requests return 307 redirects, so we need to handle them
    def configDigest = manifest.config.digest
    
    // First request to get the redirect location
    def configRedirectResponse = httpRequest(
        customHeaders: [
            [name: 'Accept', value: 'application/vnd.docker.container.image.v1+json, application/vnd.oci.image.config.v1+json'],
            [name: 'Authorization', value: "Bearer ${token}"]
        ],
        url: "https://ghcr.io/v2/${properties.organization}/${properties.PACKAGE_NAME}/blobs/${configDigest}",
        consoleLogResponseBody: properties.debug,
        quiet: properties.quiet,
        validResponseCodes: '200,307',
        wrapAsMultipart: false
    )
    echo configRedirectResponse.headers.toString()
    def config
    if (configRedirectResponse.status == 307) {
        // Follow the redirect manually
        def redirectLocation = configRedirectResponse.headers.find { it.name == 'Location' }?.value
        if (redirectLocation) {
            if (properties.debug) {
                echo "Following redirect to: ${redirectLocation}"
            }
            def configResponse = httpRequest(
                customHeaders: [
                    [name: 'Accept', value: 'application/vnd.docker.container.image.v1+json, application/vnd.oci.image.config.v1+json']
                ],
                url: redirectLocation,
                consoleLogResponseBody: properties.debug,
                quiet: properties.quiet,
                wrapAsMultipart: false
            )
            config = readJSON text: configResponse.content
        } else {
            error("Received 307 redirect but no Location header found")
        }
    } else {
        config = readJSON text: configRedirectResponse.content
    }
    
    manifestInfo.platform = [
        architecture: config.architecture,
        os: config.os,
        variant: config.variant ?: null
    ]
    
    manifestInfo.layers = manifest.layers.size()
    manifestInfo.configDigest = configDigest
    
    return manifestInfo
}