def captureError(name, block) {
    try {
        block.call()
    } catch (Exception ex) {
        print("${name} block threw exception: " + ex)
    }
}

def getTarget(job, mlops_version){
        def branch = java.net.URLEncoder.encode(scm.branches[0].name, "UTF-8")
        def versionString = ~/(\d+)\.(\d+).*/
        if(versionString.matcher(mlops_version).matches()){
               branch = java.net.URLEncoder.encode("rel-${mlops_version}", "UTF-8")
        }
        def target = "${job}/${branch}"
        return target
}

/*
    Splits all tests into smaller jobs that can be run in parallel by provided {threadMarkers}, while preserving user provided include and exclude
    markers
*/
def buildParallelJobs(def prefix = "", def target, def user_params, def threadMarkers = [], def paramsIncludeMarkers, def paramsExcludeMarkers, def parallelExcludeMarkers = ""){
    def jobs = [:]
    threadMarkers << "" // tests not belonging to any group run in last thread
    threadMarkers.eachWithIndex{ threadMarker, index ->
        jobs["${prefix}- ${threadMarker ?: 'last thread'}"] = {
            stage("${prefix} @${threadMarker}") {
                def testJob = ""
                def testMarkers = threadMarker
                def includeMarkers = paramsIncludeMarkers
                def excludeMarkers = "${paramsExcludeMarkers},${parallelExcludeMarkers}"
                if(!includeMarkers.isEmpty()){
                    testMarkers = includeMarkers.split(",").collect{"$it and $threadMarker"}.join(" or ")
                }
                if(threadMarker.isEmpty()){
                    _excludeMarkers = threadMarkers << excludeMarkers
                    excludeMarkers = _excludeMarkers.findAll{!it.isEmpty()}.join(",")
                    testMarkers = includeMarkers
                }
                captureError "${prefix}", {
                    test_params = [
                                stringParam(name: 'PYTEST_EXCLUDE_MARKERS', value: excludeMarkers),
                                stringParam(name: 'E2E_TEST_SELECTOR', value: testMarkers),
                            ]
                    testJob = build job: target,
                            parameters: test_params + user_params,
                            propagate: false,
                            wait: true

                    copyArtifacts(
                            projectName: target,
                            selector: specific("${testJob.number}"),
                            filter: "**/allure.zip",
                            flatten: true,
                            target: "allure-be/${index}"
                    )
                }
                if (testJob.getResult() != "SUCCESS") {
                        unstable('Tests failed with result:' + testJob.getResult())
                }

            }
        }
    }
    jobs
}

def joinReports(){
        def dockerBuildArgs = " ./ci/lib"
        image = docker.build("unzip:latest", dockerBuildArgs)
        image.inside() {
            captureError "Unzip tests allure results", { sh(script: "mkdir -p allure-results-fe; for k in \$(find ./ -type f -name 'allure.zip') ; do unzip \$k -d allure-results-fe/\$(date +%s); sleep 1;  done") }
        }
        allure includeProperties: false, jdk: '', results: [[path: '**/allure-results*/**']]
        zip zipFile: 'allure.zip', archive: true, dir: 'allure-results-fe'
}

return this
