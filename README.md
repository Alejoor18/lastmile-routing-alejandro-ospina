# LastMile Optimizer — Alejandro Ospina

Sistema fullstack para gestion de entregas y optimizacion de rutas de ultima milla usando Spring Boot, JWT, Angular 21, Docker y AWS Elastic Beanstalk.

## Tecnologias

- Spring Boot 3.4.x
- Spring Security 6
- JWT con JJWT
- JPA/Hibernate
- H2 embebido
- Angular 21 standalone
- Docker
- AWS Elastic Beanstalk con plataforma Docker

## Requisitos

- Java 21
- Maven
- Node.js y npm
- Docker
- AWS CLI
- EB CLI

## Estructura

```text
backend/
frontend/
README.md
docker-compose.yml
.env.example
.gitignore
```

## Ejecutar local

Backend con Maven:

```bash
cd backend
mvn spring-boot:run
```

Backend con Docker Compose:

```bash
docker compose up --build
```

Frontend local:

```bash
cd frontend
npm install
npm start
```

## URLs locales

- Backend: `http://localhost:8080`
- Frontend: `http://localhost:4200`
- Health backend: `http://localhost:8080/api/health`
- Actuator health: `http://localhost:8080/actuator/health`

## Credenciales demo

```text
admin@lastmile.test
Admin123*

alejandro@lastmile.test
Admin123*
```

## Flujo de demo para el profesor

1. Abrir el frontend en `http://localhost:4200`.
2. Iniciar sesion con `admin@lastmile.test / Admin123*`.
3. Ver el dashboard con KPIs reales.
4. Ver entregas semilla.
5. Crear una entrega nueva.
6. Editar una entrega.
7. Cambiar estado de una entrega.
8. Entrar a `Optimizar Ruta`.
9. Seleccionar conductor y entregas pendientes.
10. Generar ruta optimizada.
11. Ver tabla de paradas y visualizacion SVG sin mapas externos.
12. Ir a `Rutas`.
13. Ver detalle de ruta generada.
14. Cambiar estado de ruta.
15. Mostrar backend desplegado en AWS Elastic Beanstalk con `/api/health`.
16. Mostrar `backend/Dockerfile`, `docker-compose.yml` y este README.

## Endpoints principales

Publicos:

- `GET /`
- `GET /api/health`
- `GET /actuator/health`
- `POST /api/auth/register`
- `POST /api/auth/login`

Protegidos con JWT Bearer:

- `GET /api/auth/me`
- `GET /api/drivers`
- `GET /api/drivers/{id}`
- `POST /api/drivers`
- `PUT /api/drivers/{id}`
- `DELETE /api/drivers/{id}`
- `GET /api/deliveries`
- `GET /api/deliveries?status=PENDING`
- `GET /api/deliveries?priority=HIGH`
- `GET /api/deliveries/{id}`
- `POST /api/deliveries`
- `PUT /api/deliveries/{id}`
- `PATCH /api/deliveries/{id}/status`
- `DELETE /api/deliveries/{id}`
- `POST /api/routes/optimize`
- `GET /api/routes`
- `GET /api/routes/{id}`
- `PATCH /api/routes/{id}/status`
- `DELETE /api/routes/{id}`

Login de prueba:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@lastmile.test","password":"Admin123*"}'
```

## Variables de entorno backend

Valores locales por defecto:

```text
server.port=${PORT:8080}
SPRING_DATASOURCE_URL=jdbc:h2:file:./data/lastmile-db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
JWT_SECRET=change-this-secret-change-this-secret-123456
JWT_EXPIRATION_MS=86400000
CORS_ALLOWED_ORIGINS=http://localhost:4200,http://127.0.0.1:4200
```

Perfiles:

- `default/local`: H2 en `./data/lastmile-db`.
- `docker`: H2 en `/data/lastmile-db`.
- `cloud`: H2 en `/tmp/lastmile-db`.

Este proyecto usa H2 para facilitar la evaluacion local y cloud de demo. Para produccion se recomienda PostgreSQL o Amazon RDS. En AWS, H2 es suficiente para la demo academica, pero no para produccion porque el filesystem del contenedor o instancia puede ser efimero.

## Despliegue del backend en AWS Elastic Beanstalk

AWS es proveedor permitido por el enunciado. Para este proyecto se eligio AWS Elastic Beanstalk con Docker, ambiente Single Instance para demo academica y control de costos. El backend se despliega dockerizado. El frontend se valida localmente consumiendo la URL del backend.

No subas secretos reales al repositorio. Despues de la demo termina el ambiente para evitar cobros.

Pre-requisitos:

- AWS CLI instalado y configurado.
- EB CLI instalado.
- Docker instalado.
- Usuario AWS con permisos para Elastic Beanstalk, EC2, S3, CloudFormation e IAM basico necesario para Beanstalk.

Comandos:

```bash
cd backend

aws configure

eb init -p docker lastmile-optimizer-alejandro --region us-east-1

eb create lastmile-optimizer-env --single --instance_type t2.micro

eb setenv \
  SPRING_PROFILES_ACTIVE=cloud \
  JWT_SECRET=change-this-secret-change-this-secret-123456 \
  JWT_EXPIRATION_MS=86400000 \
  CORS_ALLOWED_ORIGINS=http://localhost:4200,http://127.0.0.1:4200 \
  SPRING_DATASOURCE_URL='jdbc:h2:file:/tmp/lastmile-db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE'

eb deploy

eb status

eb open
```

Probar backend desplegado:

```bash
curl http://URL-ELASTIC-BEANSTALK/api/health
```

Probar login:

```bash
curl -X POST http://URL-ELASTIC-BEANSTALK/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@lastmile.test","password":"Admin123*"}'
```

Elastic Beanstalk normalmente entrega una URL HTTP tipo:

```text
http://nombre-env.region.elasticbeanstalk.com
```

Para demo academica HTTP es suficiente, salvo que el profesor exija HTTPS.

Actualizar frontend despues del despliegue:

```ts
// frontend/src/environments/environment.prod.ts
apiUrl: 'http://URL-ELASTIC-BEANSTALK/api'
```

No usar Render, Railway, Heroku, Netlify u otros como opcion principal porque el enunciado solo permite Google Cloud, AWS, Oracle o Azure. Para este proyecto se eligio AWS.

## Cleanup AWS obligatorio

```bash
cd backend
eb terminate lastmile-optimizer-env
```

Despues revisa manualmente en AWS:

- EC2
- S3 buckets creados por Elastic Beanstalk
- CloudFormation stacks
- Elastic Beanstalk environments

Esto ayuda a evitar costos residuales.

## Troubleshooting

- CORS: valida `CORS_ALLOWED_ORIGINS=http://localhost:4200,http://127.0.0.1:4200`.
- Cambiar URL backend Angular: edita `frontend/src/environments/environment.ts` para local o `environment.prod.ts` para produccion.
- Token expirado: cerrar sesion e iniciar sesion de nuevo.
- Puerto 8080 ocupado: liberar el puerto o ejecutar con `PORT=8081`.
- Error 401: falta header `Authorization: Bearer <token>` o el token expiro.
- Error 403: usuario autenticado sin permisos suficientes para el recurso.
- Reiniciar datos H2 local: detener backend y borrar `backend/data/`.
- Revisar logs AWS: `eb logs`.
- Estado AWS: `eb status` y `eb health`.
- Si AWS no responde, revisar variables de entorno, puerto 8080, endpoint `/` y endpoint `/api/health`.

## Calidad

Comandos de verificacion esperados:

```bash
cd backend
mvn test
mvn package -DskipTests

cd ..
docker build -t lastmile-backend ./backend

cd frontend
npm install
npm run build
```

## Nota academica

Este proyecto usa H2 para facilitar la evaluacion local y cloud de demo. Para produccion se recomienda PostgreSQL o Amazon RDS.
