# Building and running the Backend

## Java version (important)

This project uses **Java 17** and **Lombok**. Lombok does not yet support **Java 25**.

- **Symptom:**  
  - `Could not find or load main class com.college.complaintportal.DigitalComplaintPortalApplication` when running from the IDE, or  
  - Maven compile fails with `TypeTag :: UNKNOWN` / Lombok errors.

- **Fix:** Use **JDK 17 or JDK 21** to build and run.

### Option A: Install JDK 17 or 21

1. Install [Eclipse Temurin 17](https://adoptium.net/temurin/releases/?version=17) or [21](https://adoptium.net/temurin/releases/?version=21) (or any JDK 17/21).
2. In VS Code, open **Command Palette** → **Java: Configure Java Runtime** and add the new JDK, then set this project to use it.
3. In the same project, run:  
   `mvn clean compile`  
   Then run `DigitalComplaintPortalApplication` again from the IDE.

### Option B: Build and run with Maven (once JDK 17/21 is active)

From the `Backend` directory:

```bash
mvn clean spring-boot:run
```

This compiles and starts the application. The main class is only found after a successful compile.
