package com.example.dzandroid.core

/**
 * Sealed класс для представления результатов операций с сетью
 */
sealed class ApiResult<out T> {

    /**
     * Успешный результат операции
     */
    data class Success<T>(val data: T) : ApiResult<T>()

    /**
     * Ошибочный результат операции
     */
    data class Error(val message: String) : ApiResult<Nothing>()

    /**
     * Состояние загрузки данных
     * Используется для отображения индикатора загрузки
     */
    object Loading : ApiResult<Nothing>()
}
