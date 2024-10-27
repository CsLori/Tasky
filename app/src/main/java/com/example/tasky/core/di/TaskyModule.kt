package com.example.tasky.core.di

import android.app.Application
import android.content.Context
import androidx.room.Room.databaseBuilder
import com.example.tasky.Constants.BASE_URL
import com.example.tasky.agenda.agenda_data.di.BasicOkHttpClient
import com.example.tasky.agenda.agenda_data.local.AgendaDatabase
import com.example.tasky.agenda.agenda_data.local.LocalDatabaseRepository
import com.example.tasky.agenda.agenda_data.local.dao.EventDao
import com.example.tasky.agenda.agenda_data.local.dao.ReminderDao
import com.example.tasky.agenda.agenda_data.local.dao.TaskDao
import com.example.tasky.agenda.agenda_data.remote.AgendaRepositoryImpl
import com.example.tasky.agenda.agenda_data.remote.AuthTokenInterceptor
import com.example.tasky.agenda.agenda_domain.repository.AgendaItemsRepository
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.core.data.local.ProtoUserPrefsRepository
import com.example.tasky.core.data.remote.TaskyApi
import com.example.tasky.onboarding.onboarding_data.repository.DefaultUserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskyModule {

    @Provides
    @Singleton
    fun provideOkhttpClient(
        @BasicOkHttpClient basicOkHttpClient: OkHttpClient,
        authTokenInterceptor: AuthTokenInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                authTokenInterceptor.intercept(it)
            }.build()
    }

    @Provides
    @Singleton
    fun provideTaskyApi(client: OkHttpClient): TaskyApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(TaskyApi::class.java)
    }

    @Provides
    @Singleton
    fun providesNoteDatabase(app: Application): AgendaDatabase {
        return databaseBuilder(
            app,
            AgendaDatabase::class.java,
            AgendaDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AgendaDatabase): TaskDao {
        return database.taskDao
    }

    @Provides
    @Singleton
    fun provideEventDao(database: AgendaDatabase): EventDao {
        return database.eventDao
    }

    @Provides
    @Singleton
    fun provideReminderDao(database: AgendaDatabase): ReminderDao {
        return database.reminderDao
    }

    @Provides
    @Singleton
    fun provideLocalDatabaseRepository(db: AgendaDatabase): AgendaItemsRepository {
        return LocalDatabaseRepository(db.taskDao, db.eventDao, db.reminderDao)
    }

    @Provides
    @Singleton
    fun provideUserPrefs(@ApplicationContext context: Context): ProtoUserPrefsRepository {
        return ProtoUserPrefsRepository(context)
    }

    @Provides
    @Singleton
    fun provideUserRepo(
        api: TaskyApi,
        protoUserPrefsRepository: ProtoUserPrefsRepository
    ): DefaultUserRepository {
        return DefaultUserRepository(api, protoUserPrefsRepository)
    }


    @Provides
    @Singleton
    fun provideAgendaRepository(
        api: TaskyApi,
        userPrefsRepository: ProtoUserPrefsRepository
    ): AgendaRepository {
        return AgendaRepositoryImpl(api, userPrefsRepository)
    }
}