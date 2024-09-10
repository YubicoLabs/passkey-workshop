package io.yubicolabs.pawskey

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.yubicolabs.pawskey.net.DebugInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@InstallIn(ViewModelComponent::class)
@Module
object ApiModule {
    @Provides
    fun providePasskeyService(): PassKeyService = PassKeyService()

    @Provides
    fun provideRelyingPartyService(
        relyingPartyHttpService: RelyingPartyHttpService,
    ): RelyingPartyService =
        RelyingPartyService(
            relyingPartyHttpService,
        )

    @Provides
    fun provideRelyingPartyHttpService(
        okHttpClient: OkHttpClient,
        jsonConverter: Converter.Factory,
    ): RelyingPartyHttpService =
        Retrofit.Builder()
            .baseUrl("https://${BuildConfig.API_BASE_URI}/")
            .client(okHttpClient)
            .addConverterFactory(jsonConverter)
            .build()
            .create(RelyingPartyHttpService::class.java)

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient()
            .newBuilder()

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                DebugInterceptor()
            )
        }

        return builder.build()
    }

    @Provides
    fun providesJson(): Json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        prettyPrint = BuildConfig.DEBUG
    }

    @Provides
    fun providesJsonFactory(json: Json): Converter.Factory = json
        .asConverterFactory(
            MediaType.get("application/json")
        )
}