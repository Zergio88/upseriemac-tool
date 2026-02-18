# 🚀 Guía para ejecutar en Windows

## 📦 Archivos a copiar a Windows

1. **`upseriemac-tool-0.0.1-SNAPSHOT.jar`** (JAR ejecutable)
2. **`prini-tino.json`** (credenciales Firebase, debe estar en la misma carpeta que el JAR)

---

## 🔧 Pasos en Windows (con JDK 17.0.11 instalado)

### 1️⃣ Descargar JavaFX SDK para Windows

El JAR NO incluye JavaFX (así es Java 11+), necesitás descargarlo:

- **URL:** https://gluonhq.com/products/javafx/
- **Versión:** JavaFX Windows SDK **17.0.18**
- **Archivo:** `openjfx-17.0.18_windows-x64_bin-sdk.zip`

### 2️⃣  Ejecutar

** Desde CMD/PowerShell:
```cmd
cd directorio-con-los-archivos
java --module-path C:\javafx-sdk-17.0.13\lib --add-modules javafx.controls,javafx.fxml -jar upseriemac-tool-0.0.1-SNAPSHOT.jar
```

---

## 🎯 Comando de build

Para recompilar en Linux:
```bash
mvn clean package -DskipTests
```

JAR generado: `target/upseriemac-tool-0.0.1-SNAPSHOT.jar`

