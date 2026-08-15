package com.plantappkmp.platform.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

internal const val PREFERENCES_FILE_NAME = "plantapp.preferences_pb"

/**
 * Builds the one app-wide Preferences store at [producePath].
 *
 * The Android build hides this behind `preferencesDataStore(name = …)`, a
 * property delegate on `Context`. There is no `Context` in common code, so the
 * platform decides *where* the file lives and this assembles the store — which
 * is also what makes the "exactly one instance per file" rule easy to keep:
 * the composition root calls this once.
 */
fun createPreferencesDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(produceFile = { producePath().toPath() })
