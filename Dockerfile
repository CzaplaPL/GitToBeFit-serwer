From openjdk:16
copy ./target/git2befit-v0.2.1.jar git2befit-v0.2.1.jar
CMD ["java","-jar","git2befit-v0.2.1.jar"]
