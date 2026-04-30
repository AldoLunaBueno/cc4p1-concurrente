# Tetris Mix Multiplayer

Tetris Mix Multiplayer es un juego de puzzle distribuido para $N$ jugadores simultáneos, renderizado íntegramente en la consola (TUI) y construido con Java puro usando Sockets TCP crudos (sin frameworks de red ni librerías gráficas).

Este proyecto implementa una arquitectura de **Servidor Autoritativo** con un **Fixed-Step Game Loop** para asegurar una física determinista y sincronización perfecta entre todos los clientes concurrentes.

## Requisitos Previos

* **Java 8 o superior** instalado en tu sistema.
* Terminal o consola de comandos (Command Prompt, PowerShell, bash, etc.).

## IMPORTANTE: Configuración de la Consola (Usuarios de Windows)

El juego utiliza secuencias de escape ANSI para los colores y caracteres combinables Unicode (diacríticos) para diferenciar a los jugadores cuando hay muchas conexiones.

Si utilizas la terminal de Windows (CMD o PowerShell), es **obligatorio** cambiar la codificación a UTF-8 antes de ejecutar el juego para evitar que los caracteres se rompan (ej. viendo un `?` en lugar de la pieza).

Antes de ejecutar cualquier comando de Java, corre esto en tu terminal:

```bash
chcp 65001
```

*(Este comando ajusta la página de códigos a UTF-8 y garantiza que la consola renderice el juego correctamente).*

## Instalación y Ejecución

### 1. Clonar el repositorio

Abre tu terminal y clona el proyecto:

```bash
git clone <URL_DE_TU_REPOSITORIO>
cd tetris-mix-multiplayer
```

### 2. Jugar en Modo Normal (Localhost)

Para iniciar una partida, primero debes levantar el servidor y luego conectar tantos clientes como desees.

**Paso A: Iniciar el Servidor**
Ejecuta la clase principal del servidor (`uni.network.TetrisServer`). Al iniciar, la consola te pedirá definir las reglas del contenedor:

* **Columnas:** (ej. `15`)
* **Filas:** (ej. `20`)
* **Puerto:** (ej. `5000`)

**Paso B: Conectar a los Clientes**
Abre una **nueva ventana de terminal** por cada jugador (recuerda ejecutar `chcp 65001` en cada una si usas Windows). Ejecuta la clase del cliente (`uni.network.TetrisClient`). La consola te pedirá:

* **IP:** Escribe `localhost` (o presiona Enter si está configurado por defecto).
* **Puerto:** El mismo puerto que elegiste para el servidor (ej. `5000`).

¡Controla tus piezas ingresando los comandos en la consola (ej. `a` para izquierda, `d` para derecha, `s` para abajo) y presiona Enter!

## Prueba de Estrés (Stress Testing)

El proyecto incluye una validación de arquitectura de alto rendimiento capaz de levantar decenas o cientos de bots simultáneos (`HeadlessClient`) conectados al servidor mediante *Sockets* para probar la resistencia del servidor frente a la "Manada en Estampida" (Thundering Herd) y la gestión de concurrencia lock-free.

Para ver este espectáculo de concurrencia en tu consola:

1. Ejecuta la clase `StressTestOrchestrator` ubicada en el directorio de pruebas (`src/test/java/uni/stress/StressTestOrchestrator.java`).
2. El orquestador levantará automáticamente:
   * 1 Servidor Tetris local.
   * 100 bots asíncronos enviando comandos aleatoriamente.
   * 1 Cliente humano (tú) para que puedas visualizar el caos en la misma consola y jugar contra ellos.
3. El ritmo de caída y la lluvia de piezas será gestionada eficientemente gracias al patrón Productor-Consumidor y al estrangulamiento de red (Network Throttling) implementado en el `FixedStepGameLoop`.

***

**Nota de Arquitectura:** Las rotaciones y mutaciones se manejan utilizando el patrón *Command* evaluadas por un motor de físicas *Look-Ahead* (Proactivo), asegurando cero "dirty reads" (lecturas sucias) durante la transmisión del estado a los $N$ jugadores.
