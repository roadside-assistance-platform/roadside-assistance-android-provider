package esi.roadside.assistance.provider.auth.di

import com.cloudinary.android.MediaManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import esi.roadside.assistance.provider.BuildConfig
import esi.roadside.assistance.provider.auth.data.AuthRepoImpl
import esi.roadside.assistance.provider.auth.data.CloudinaryRepoImpl
import esi.roadside.assistance.provider.auth.data.PersistentCookieStorage
import esi.roadside.assistance.provider.auth.domain.repository.AuthRepo
import esi.roadside.assistance.provider.auth.domain.repository.CloudinaryRepo
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val RepoModule = module {
    single { PersistentCookieStorage(androidContext()) }
    single<MediaManager> {
        var config: HashMap<String, String> = HashMap()
        config.put("cloud_name", "dsvlwomdq")
        config.put("api_key", "996654622953693")
        config.put("api_secret", "d8gJrtFaBOSAkSrpaPNU4V4M2-Y")
        MediaManager.init(androidContext(), config)
        MediaManager.get()
    }
    single {
        GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .build()
    }
    single<AuthRepo> { AuthRepoImpl(get(), get()) }
    single<CloudinaryRepo> { CloudinaryRepoImpl(get()) }
}