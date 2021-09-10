# Spring-batch DEMO
## Required
* JDK 11
* Spring boot 2.5.4
* Spring batch 4.3.3

## Spring batch trigger
JobController.java
```java
@GetMapping("/start")
ResponseEntity<String> start()
    throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
        JobParametersInvalidException, JobRestartException {

    val jobExecution = jobLauncher.run(job, new JobParameters(Map.of("time", new JobParameter(System.currentTimeMillis()))));
    return ResponseEntity.ok(jobExecution.getEndTime().toString());
}
```
