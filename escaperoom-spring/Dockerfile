# Utilise l'image officielle OpenJDK 17 basée sur Alpine Linux, légère et adaptée pour exécuter des applications Java
FROM openjdk:17-jdk-alpine

# Crée un volume temporaire accessible à l'intérieur du container (souvent utilisé pour les fichiers temporaires)
VOLUME /tmp

# Copie le fichier JAR généré (dans target/) dans le container et le renomme en app.jar
COPY target/*.jar app.jar

# Indique que le container écoute sur le port 8089 (port utilisé par défaut par Spring Boot)
EXPOSE 8089

# Définit la commande à exécuter quand le container démarre : lance l'application Java
ENTRYPOINT ["java", "-jar", "/app.jar"]
