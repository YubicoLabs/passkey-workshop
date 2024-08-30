package io.yubicolabs.pawskey

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
object ApiModule {
    @Provides
    fun providePasskeyService(): PassKeyService = PassKeyService()

    @Provides
    fun provideRelyingPartyService(): RelyingPartyService =
        RelyingPartyService()
}