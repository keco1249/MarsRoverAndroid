package com.kc.marsrovers.di

import com.kc.marsrovers.data.RoverRepository
import com.kc.marsrovers.data.RoverRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRoverRepository(impl: RoverRepositoryImpl): RoverRepository
}
