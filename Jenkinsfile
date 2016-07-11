 node {
 stage "checkout"
 checkout scm

 stage "build"
 sh "./gradlew clean build"

 stage "assemble release"
 sh "./gradlew clean assembleRelease"
 step([$class: 'ArtifactArchiver', artifacts: '**/outputs/apk/*release*.apk', fingerprint: true])
}