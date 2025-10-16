package com.example.dzandroid.data.mock

import com.example.dzandroid.data.models.Repository

/**
 * Объект, содержащий mock-данные для тестирования и разработки.
 * Предоставляет список примеров репозиториев GitHub с русскоязычными описаниями.
 */
object MockData {
    /**
     * Список mock-репозиториев для демонстрации работы приложения.
     * Включает популярные Android и Java/Kotlin репозитории с русскими описаниями.
     */
    val repositories = listOf(
        Repository(
            id = 1,
            name = "android-practices",
            owner = "SolevarLive",
            description = "Практические работы по Android разработке в УрФУ",
            stars = 15,
            forks = 3,
            language = "Kotlin",
            updatedAt = "2024-01-15",
            license = "MIT",
            topics = listOf("android", "kotlin", "jetpack-compose", "учебный-проект"),
            readme = "# Android практики\n\nПроект для выполнения практических работ по Android разработке в Уральском Федеральном Университете."
        ),
        Repository(
            id = 2,
            name = "jetpack-compose-samples",
            owner = "android",
            description = "Официальные примеры использования Jetpack Compose",
            stars = 12500,
            forks = 2300,
            language = "Kotlin",
            updatedAt = "2024-01-14",
            license = "Apache-2.0",
            topics = listOf("android", "compose", "kotlin", "ui", "declarative"),
            readme = "# Jetpack Compose Samples\n\nОфициальные примеры и лучшие практики использования Jetpack Compose для создания современного UI в Android."
        ),
        Repository(
            id = 3,
            name = "retrofit",
            owner = "square",
            description = "Типобезопасный HTTP клиент для Android и Java",
            stars = 42000,
            forks = 7400,
            language = "Java",
            updatedAt = "2024-01-13",
            license = "Apache-2.0",
            topics = listOf("http", "rest", "android", "api", "клиент"),
            readme = "# Retrofit\n\nТипобезопасный HTTP клиент для Android и Java приложений. Упрощает работу с REST API."
        ),
        Repository(
            id = 4,
            name = "okhttp",
            owner = "square",
            description = "HTTP клиент для Android и Java приложений",
            stars = 45000,
            forks = 9300,
            language = "Java",
            updatedAt = "2024-01-12",
            license = "Apache-2.0",
            topics = listOf("http", "networking", "android", "сеть", "клиент"),
            readme = "# OkHttp\n\nЭффективный HTTP клиент для Android и Java приложений с поддержкой HTTP/2 и соединений."
        ),
        Repository(
            id = 5,
            name = "room-db",
            owner = "android",
            description = "Библиотека для объектно-реляционного отображения SQLite",
            stars = 11000,
            forks = 1700,
            language = "Kotlin",
            updatedAt = "2024-01-11",
            license = "Apache-2.0",
            topics = listOf("database", "sqlite", "android", "база-данных", "orm"),
            readme = "# Room\n\nБиблиотека для объектно-реляционного отображения SQLite в Android приложениях."
        ),
        Repository(
            id = 6,
            name = "ktor-framework",
            owner = "jetbrains",
            description = "Фреймворк для построения асинхронных серверов и клиентов",
            stars = 12000,
            forks = 1200,
            language = "Kotlin",
            updatedAt = "2024-01-10",
            license = "Apache-2.0",
            topics = listOf("web", "framework", "kotlin", "async", "сервер"),
            readme = "# Ktor Framework\n\nАсинхронный фреймворк для создания веб-приложений на Kotlin."
        ),
        Repository(
            id = 7,
            name = "compose-multiplatform",
            owner = "jetbrains",
            description = "Кроссплатформенный UI фреймворк на основе Compose",
            stars = 2500,
            forks = 300,
            language = "Kotlin",
            updatedAt = "2024-01-09",
            license = "Apache-2.0",
            topics = listOf("compose", "multiplatform", "kotlin", "ui", "кроссплатформенный"),
            readme = "# Compose Multiplatform\n\nКроссплатформенный фреймворк для создания UI на Kotlin с использованием Compose."
        ),
        Repository(
            id = 8,
            name = "material-components",
            owner = "material-components",
            description = "Модульные и настраиваемые компоненты UI для Android",
            stars = 16000,
            forks = 3200,
            language = "Java",
            updatedAt = "2024-01-08",
            license = "Apache-2.0",
            topics = listOf("material-design", "ui", "components", "android", "дизайн"),
            readme = "# Material Components\n\nРеализация Material Design для Android приложений."
        ),
        Repository(
            id = 9,
            name = "glide-image-loader",
            owner = "bumptech",
            description = "Быстрый и эффективный загрузчик изображений для Android",
            stars = 34000,
            forks = 6200,
            language = "Java",
            updatedAt = "2024-01-07",
            license = "BSD-3-Clause",
            topics = listOf("image", "loading", "android", "кэширование", "изображения"),
            readme = "# Glide\n\nЭффективная библиотека для загрузки и кэширования изображений в Android."
        ),
        Repository(
            id = 10,
            name = "leakcanary",
            owner = "square",
            description = "Инструмент для обнаружения утечек памяти в Android",
            stars = 29000,
            forks = 4100,
            language = "Kotlin",
            updatedAt = "2024-01-06",
            license = "Apache-2.0",
            topics = listOf("memory", "leaks", "debugging", "android", "отладка"),
            readme = "# LeakCanary\n\nБиблиотека для автоматического обнаружения утечек памяти в Android приложениях."
        ),
        Repository(
            id = 11,
            name = "moshi-json",
            owner = "square",
            description = "Современная библиотека для работы с JSON в Kotlin и Java",
            stars = 9000,
            forks = 750,
            language = "Kotlin",
            updatedAt = "2024-01-05",
            license = "Apache-2.0",
            topics = listOf("json", "parsing", "kotlin", "serialization", "сериализация"),
            readme = "# Moshi\n\nСовременная библиотека для парсинга и сериализации JSON в Kotlin и Java."
        ),
        Repository(
            id = 12,
            name = "dagger-dependency-injection",
            owner = "google",
            description = "Библиотека для dependency injection в Java и Android",
            stars = 17000,
            forks = 2100,
            language = "Java",
            updatedAt = "2024-01-04",
            license = "Apache-2.0",
            topics = listOf("dependency-injection", "di", "android", "внедрение-зависимостей"),
            readme = "# Dagger\n\nКомпиляторная система dependency injection для Java и Android."
        ),
        Repository(
            id = 13,
            name = "rxjava-reactive",
            owner = "ReactiveX",
            description = "Реактивные расширения для JVM - RxJava",
            stars = 47000,
            forks = 7800,
            language = "Java",
            updatedAt = "2024-01-03",
            license = "Apache-2.0",
            topics = listOf("reactive", "rxjava", "async", "streams", "реактивное-программирование"),
            readme = "# RxJava\n\nРеактивные расширения для JVM - библиотека для композиции асинхронных и событийных программ."
        ),
        Repository(
            id = 14,
            name = "coroutines-android",
            owner = "kotlin",
            description = "Библиотека корутин для асинхронного программирования в Android",
            stars = 12000,
            forks = 1700,
            language = "Kotlin",
            updatedAt = "2024-01-02",
            license = "Apache-2.0",
            topics = listOf("coroutines", "async", "kotlin", "android", "асинхронность"),
            readme = "# Kotlin Coroutines\n\nБиблиотека для упрощения асинхронного программирования в Kotlin."
        ),
        Repository(
            id = 15,
            name = "firebase-android-sdk",
            owner = "firebase",
            description = "Официальный Firebase SDK для Android приложений",
            stars = 23000,
            forks = 4500,
            language = "Java",
            updatedAt = "2024-01-01",
            license = "Apache-2.0",
            topics = listOf("firebase", "backend", "mobile", "android", "база-данных"),
            readme = "# Firebase Android SDK\n\nОфициальный SDK для интеграции Firebase сервисов в Android приложения."
        ),
        Repository(
            id = 16,
            name = "android-architecture",
            owner = "android",
            description = "Примеры архитектурных паттернов для Android приложений",
            stars = 38000,
            forks = 10500,
            language = "Kotlin",
            updatedAt = "2023-12-31",
            license = "Apache-2.0",
            topics = listOf("architecture", "mvvm", "clean-architecture", "android", "архитектура"),
            readme = "# Android Architecture Components\n\nПримеры использования различных архитектурных паттернов в Android приложениях."
        ),
        Repository(
            id = 17,
            name = "navigation-component",
            owner = "android",
            description = "Компонент навигации для Android приложений",
            stars = 3500,
            forks = 800,
            language = "Kotlin",
            updatedAt = "2023-12-30",
            license = "Apache-2.0",
            topics = listOf("navigation", "android", "fragments", "навигация"),
            readme = "# Navigation Component\n\nБиблиотека для упрощения навигации между экранами в Android приложениях."
        ),
        Repository(
            id = 18,
            name = "work-manager",
            owner = "android",
            description = "Библиотека для управления фоновыми задачами в Android",
            stars = 4500,
            forks = 900,
            language = "Java",
            updatedAt = "2023-12-29",
            license = "Apache-2.0",
            topics = listOf("background", "work", "scheduling", "android", "фоновые-задачи"),
            readme = "# WorkManager\n\nБиблиотека для управления отложенными и периодическими фоновыми задачами."
        ),
        Repository(
            id = 19,
            name = "paging-library",
            owner = "android",
            description = "Библиотека для постраничной загрузки данных в Android",
            stars = 3200,
            forks = 600,
            language = "Kotlin",
            updatedAt = "2023-12-28",
            license = "Apache-2.0",
            topics = listOf("paging", "data", "recyclerview", "android", "пагинация"),
            readme = "# Paging Library\n\nБиблиотека для эффективной постраничной загрузки и отображения данных."
        ),
        Repository(
            id = 20,
            name = "data-binding",
            owner = "android",
            description = "Библиотека Data Binding для Android",
            stars = 2800,
            forks = 500,
            language = "Java",
            updatedAt = "2023-12-27",
            license = "Apache-2.0",
            topics = listOf("data-binding", "mvvm", "android", "ui", "привязка-данных"),
            readme = "# Data Binding\n\nБиблиотека для декларативной привязки данных к UI компонентам в Android."
        )
    )
}