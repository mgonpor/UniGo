# Etapa 1: Construcción (Build stage)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el archivo pom.xml y descargamos dependencias para aprovechar la caché de capas de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiamos el código fuente de la aplicación
COPY src ./src

# Empaquetamos la aplicación, saltando la compilación y ejecución de tests para un despliegue más rápido
RUN mvn clean package -Dmaven.test.skip=true

# Etapa 2: Ejecución (Run stage)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Exponemos el puerto de la aplicación, esto permite a Dokploy redirigir el tráfico internamente
EXPOSE 8080

# Copiamos el JAR generado desde la etapa de construcción a esta nueva etapa (más ligera)
COPY --from=build /app/target/*.jar app.jar

# Comando de inicio de la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
