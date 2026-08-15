package com.plantappkmp

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.plantappkmp.di.initKoin
import com.plantappkmp.platform.datastore.PREFERENCES_FILE_NAME
import com.plantappkmp.platform.datastore.createPreferencesDataStore
import org.koin.dsl.module

class PlantAppApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // The one binding that needs a platform handle: DataStore wants a file
        // path, and only Android knows where this app's private files live.
        initKoin(
            module {
                single<DataStore<Preferences>> {
                    createPreferencesDataStore {
                        filesDir.resolve(PREFERENCES_FILE_NAME).absolutePath
                    }
                }
            },
        )
    }
}
