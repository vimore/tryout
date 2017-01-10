# Introduction

# Building the Source
The main targets do not run the integration tests that require live instances of servers, such as Impala, to be up and running for the tests to pass. 
The build system will only run the following command.

```
$ mvn clean package
```

# Run Integration Test
For running the integration tests, please run the following command

```
$ mvn clean verify
```

