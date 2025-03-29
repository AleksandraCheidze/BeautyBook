@echo off
echo Running EnvTest...
mvn compile
java -cp target/classes;%USERPROFILE%\.m2\repository\io\github\cdimascio\dotenv-java\3.0.0\dotenv-java-3.0.0.jar com.example.end.RunEnvTest 