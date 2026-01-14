# list-service

Kratek opis
-	Dodajanje, brisanje, urejanje, deljenje seznamov
-	Dodajanje, brisanje, urejanje artiklov na seznamih
-	Generiranje seznama artiklov glede na ime recepta in sestavine na uvoženem receptu (razčleni kafka sporočilo s sestavinami)

Gradnja

```bash
# v mapi list-service
./mvnw clean package -DskipTests
docker build -t <your-registry>/list-service:latest .
```

Zagon
- Lokalno z Docker Compose: iz `shopsync-infra/docker-compose.yml` (izpostavljen kot `8082:8082`).
- Kubernetes manifests: `shopsync-infra/k8s/list-service`.

Penv spremenljivke
- `SPRING_DATASOURCE_URL` — JDBC povezava na `list_db`.
- `SPRING_KAFKA_BOOTSTRAP_SERVERS` — Kafka bootstrap server.

Konfiguracija
- `src/main/resources/application.yml` ali `application.properties`
