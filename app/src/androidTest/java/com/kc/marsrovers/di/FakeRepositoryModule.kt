package com.kc.marsrovers.di

import com.kc.marsrovers.data.FakeRoverRepository
import com.kc.marsrovers.data.RoverRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(components = [SingletonComponent::class], replaces = [RepositoryModule::class])
abstract class FakeRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRoverRepository(impl: FakeRoverRepository): RoverRepository
}
