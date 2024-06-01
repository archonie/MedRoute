package com.example.medroute.di

import OkHttpClient
import android.app.Application
import androidx.room.Room
import com.example.medroute.data.local.RouteDao
import com.example.medroute.data.local.UserDao
import com.example.medroute.data.local.UserDatabase
//import com.example.medroute.data.local.RouteTypeConverter
import com.example.medroute.data.remote.ApiService
import com.example.medroute.data.repository.UserRepositoryImpl
import com.example.medroute.domain.repository.UserRepository
import com.example.medroute.domain.usecases.app_entry.GetUserUseCase
import com.example.medroute.domain.usecases.app_entry.LoginUseCase
import com.example.medroute.domain.usecases.app_entry.LoginUseCases
import com.example.medroute.domain.usecases.app_entry.SignupUseCase
import com.example.medroute.util.Constants.BASE_URL
import com.example.medroute.util.LocationUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLocationUtils(
        context: Application
    ): LocationUtils = LocationUtils(context)

    @Provides
    @Singleton
    fun provideApiService(): ApiService =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.getUnsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)


    @Provides
    @Singleton
    fun provideUserDatabase(application: Application): UserDatabase {
        return Room.databaseBuilder(
            context = application,
            klass = UserDatabase::class.java,
            name = "user_db"
        )//.addTypeConverter(RouteTypeConverter())
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideLoginUseCases(
        repository: UserRepository
    ) = LoginUseCases(
        loginUseCase = LoginUseCase(repository),
        getUserUseCase = GetUserUseCase(repository),
        signupUseCase = SignupUseCase(repository)
    )

    @Provides
    @Singleton
    fun provideUserDao(
        userDatabase: UserDatabase
    ):UserDao = userDatabase.userDao

    @Provides
    @Singleton
    fun provideRouteDao(
        userDatabase: UserDatabase
    ):RouteDao = userDatabase.routeDao


    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: ApiService,
        userDao: UserDao,
        routeDao: RouteDao
    ): UserRepository = UserRepositoryImpl(apiService, userDao,routeDao)

}