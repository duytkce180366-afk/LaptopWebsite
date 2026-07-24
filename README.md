# Laptop Website

Jakarta EE / Tomcat storefront and administration application.

## Admin database setup

Run the scripts in this order against `LaptopWebsiteDB`:

```powershell
sqlcmd -S localhost,1433 -U sa -P <password> -C -b -i .sql/admin_management_migration.sql
sqlcmd -S localhost,1433 -U sa -P <password> -C -b -i .sql/admin_demo_data.sql
```

The demo script is idempotent and provides Customer/Staff accounts, varied orders,
report data, and a sample inventory receipt. Demo account passwords are `123456`.

## Email notifications

Account status and role notifications use these environment variables:

- `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`
- `SMTP_AUTH`, `SMTP_START_TLS_ENABLE`, `SMTP_TRUST_ALL`

Without SMTP configuration, account updates still succeed and the skipped email is logged.

## Build

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
mvn package
```

The generated `target/ROOT.war` deploys at `http://localhost:8080/`.
