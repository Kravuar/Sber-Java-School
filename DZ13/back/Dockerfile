FROM eclipse-temurin:21-jdk-alpine AS builder
ADD . .
RUN --mount=type=cache,target=/root/.m2 ./mvnw -f ./pom.xml clean package -Dmaven.test.skip
RUN ls target/*.jar | xargs -I {} mv {} target/backend.jar
RUN java -Djarmode=layertools -jar target/backend.jar extract

FROM eclipse-temurin:21-jre-alpine

COPY --from=builder dependencies/ ./
COPY --from=builder spring-boot-loader ./
COPY --from=builder modules-dependencies ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder application/ ./

EXPOSE 8080

CMD ["java", "org.springframework.boot.loader.launch.JarLauncher"]