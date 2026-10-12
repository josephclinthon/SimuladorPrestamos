# Firebase: Analytics y sugerencias

Proyecto: `simuladorprestamos-defa6`. Se utiliza el archivo proporcionado en `app/google-services.json`.

## Activación en la consola

1. En **Firestore Database**, crea la base `(default)` si falta. En **Reglas**, publica el contenido de `firestore.rules`. Si ya existen reglas para otras colecciones, incorpora solamente el bloque `match /suggestions/{suggestionId}` dentro de `match /databases/{database}/documents`; conserva las otras reglas. Las reglas permiten crear sugerencias sin autenticación, validan los campos y bloquean la lectura, edición y eliminación desde la app.
2. En **Configuración del proyecto → Integraciones**, comprueba que Google Analytics esté vinculado y habilitado.

No es necesario habilitar Firebase Authentication: el formulario guarda directamente el mensaje en Firestore.

La cuenta del CLI de esta máquina no tiene acceso a este proyecto, por lo que estas operaciones no se han aplicado remotamente.

## Consultar sugerencias

En https://console.firebase.google.com/project/simuladorprestamos-defa6/firestore/databases/-default-/data abre la colección `suggestions`. Cada documento contiene solo `message` y `createdAt` (fecha del servidor). La app no adjunta nombres, correos, identificadores del usuario ni datos financieros. El administrador puede consultar los mensajes desde la consola. No existe lectura pública de sugerencias.

## Verificar

- Envía una sugerencia de prueba desde **Enviar sugerencia** en el menú o Configuración. La app confirma el envío únicamente después de que Firestore lo acepta. Revisa el documento en la consola.
- Sin conexión, el formulario conserva el texto y avisa que se debe reintentar. Si la conexión se pierde durante una escritura, se espera la confirmación del servidor antes de mostrar éxito.
- Para Analytics DebugView en un dispositivo de prueba:

```powershell
adb shell setprop debug.firebase.analytics.app com.mocii.simuladorprestamos
```

Desactivar al terminar:

```powershell
adb shell setprop debug.firebase.analytics.app .none.
```

Eventos: `simulation_completed`, `simulation_reopened`, `simulation_deleted`, `simulation_compared`, `simulation_ad_rewarded`, `suggestion_sent`, y vistas `simulator`, `simulation_history`, `settings`, `suggestions`. Los eventos personalizados no incluyen el contenido de sugerencias ni datos del préstamo. Analytics también registra sus eventos automáticos estándar.

## Publicar las reglas mediante CLI

Solo con una cuenta que tenga acceso al proyecto, después de revisar o integrar las reglas existentes:

```powershell
firebase deploy --only firestore:rules --project simuladorprestamos-defa6
```
