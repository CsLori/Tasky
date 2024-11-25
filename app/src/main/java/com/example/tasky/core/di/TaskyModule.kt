package com.example.tasky.core.di

import android.app.Application
import android.content.Context
import androidx.room.Room.databaseBuilder
import com.example.tasky.Constants.BASE_URL
import com.example.tasky.NotificationPermissionUtil
import com.example.tasky.agenda.agenda_data.alarm.AlarmSchedulerService
import com.example.tasky.agenda.agenda_data.di.BasicOkHttpClient
import com.example.tasky.agenda.agenda_data.local.AgendaDatabase
import com.example.tasky.agenda.agenda_data.local.LocalDataSource
import com.example.tasky.agenda.agenda_data.local.dao.EventDao
import com.example.tasky.agenda.agenda_data.local.dao.ReminderDao
import com.example.tasky.agenda.agenda_data.local.dao.SyncAgendaItemsDao
import com.example.tasky.agenda.agenda_data.local.dao.TaskDao
import com.example.tasky.agenda.agenda_data.remote.AgendaRepositoryImpl
import com.example.tasky.agenda.agenda_data.remote.AuthTokenInterceptor
import com.example.tasky.agenda.agenda_domain.AlarmScheduler
import com.example.tasky.agenda.agenda_domain.repository.AgendaRepository
import com.example.tasky.agenda.agenda_domain.repository.LocalDatabaseRepository
import com.example.tasky.core.data.local.ProtoUserPrefsRepository
import com.example.tasky.core.data.remote.TaskyApi
import com.example.tasky.core.domain.UserPrefsRepository
import com.example.tasky.onboarding.onboarding_data.repository.DefaultUserRepository
import com.example.tasky.onboarding.onboarding_domain.UserRepository
import com.example.tasky.util.ConnectivityService
import com.example.tasky.util.NetworkConnectivityService
import com.example.tasky.util.PhotoCompressor
import com.example.tasky.util.PhotoConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
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
    fun provideSyncAgendaItemsDao(database: AgendaDatabase): SyncAgendaItemsDao {
        return database.syncAgendaItemsDao
    }

    @Provides
    @Singleton
    fun provideLocalDatabaseRepository(db: AgendaDatabase): LocalDatabaseRepository {
        return LocalDataSource(db.taskDao, db.eventDao, db.reminderDao, db.syncAgendaItemsDao)
    }

    @Provides
    @Singleton
    fun provideUserPrefs(@ApplicationContext context: Context): UserPrefsRepository {
        return ProtoUserPrefsRepository(context)
    }

    @Provides
    @Singleton
    fun provideUserRepo(
        api: TaskyApi,
        protoUserPrefsRepository: UserPrefsRepository
    ): UserRepository {
        return DefaultUserRepository(api, protoUserPrefsRepository)
    }


    @Provides
    @Singleton
    fun provideAgendaRepository(
        api: TaskyApi,
        userPrefsRepository: UserPrefsRepository,
        localDatabaseRepository: LocalDatabaseRepository,
        alarmSchedulerService: AlarmScheduler
    ): AgendaRepository {
        return AgendaRepositoryImpl(api, userPrefsRepository, localDatabaseRepository, alarmSchedulerService)
    }

    @Provides
    @Singleton
    fun providePhotoCompressor(@ApplicationContext context: Context): PhotoCompressor {
        return PhotoCompressor(context)
    }

    @Provides
    @Singleton
    fun providePhotoConverter(@ApplicationContext context: Context): PhotoConverter {
        return PhotoConverter(context)
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityService(@ApplicationContext context: Context) : ConnectivityService {
        return NetworkConnectivityService(context)
    }

    @Provides
    @Singleton
    fun provideAlarmSchedulerService(@ApplicationContext context: Context) : AlarmScheduler {
        return AlarmSchedulerService(context)
    }

    @Provides
    @Singleton
    fun provideNotificationPermissionUtil(@ApplicationContext context: Context) : NotificationPermissionUtil {
        return NotificationPermissionUtil(context)
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface BootReceiverEntryPoint {
        fun getRepository(): LocalDatabaseRepository
    }
}