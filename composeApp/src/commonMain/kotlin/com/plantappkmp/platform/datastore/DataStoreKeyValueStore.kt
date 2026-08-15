package com.plantappkmp.platform.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.flow.first

internal class DataStoreKeyValueStore(
    private val dataStore: DataStore<Preferences>,
) : KeyValueStore {

    override suspend fun readBoolean(key: String): Boolean? = try {
        dataStore.data.first()[booleanPreferencesKey(key)]
    } catch (cause: IOException) {
        throw StorageException("Could not read '$key'", cause)
    }

    override suspend fun writeBoolean(key: String, value: Boolean) {
        try {
            dataStore.edit { it[booleanPreferencesKey(key)] = value }
        } catch (cause: IOException) {
            throw StorageException("Could not write '$key'", cause)
        }
    }
}
