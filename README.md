# Opticloud — Frontend (Mobile App)

## Descripción
Aplicación móvil desarrollada en **Flutter** para la gestión centralizada de ópticas. Forma parte del sistema **Opticloud**, permitiendo a los usuarios gestionar pacientes, historiales optométricos e inventario de productos con alertas de bajo stock de forma intuitiva.

---

## Stack Tecnológico

- **Framework:** Flutter (Dart)
- **Patrón de Arquitectura:** MVVM (Model - View - ViewModel)
- **Gestión de Estado:** Provider
- **Peticiones HTTP:** Http / Dio
- **Plataforma Objetivo:** Android

---

## Estructura del Proyecto

```text
lib/
├── models/          # Entidades y mapeo de datos (JSON)
├── views/           # Pantallas y widgets de la interfaz de usuario
├── viewmodels/      # Lógica de presentación y gestión de estado
├── services/        # Consumo de la API REST Backend
└── utils/           # Constantes, temas y utilidades generales
