package esi.roadside.assistance.provider

import android.app.Application
import android.app.NotificationChannel
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import esi.roadside.assistance.provider.auth.di.authModule
import esi.roadside.assistance.provider.core.di.coreModule
import esi.roadside.assistance.provider.main.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startKoin {
            androidContext(this@App)
            modules(coreModule, authModule, mainModule)
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NotificationService.CHANNEL_ID,
            NotificationService.CHANNEL_NAME,
            NotificationService.CHANNEL_IMPORTANCE
        )
        channel.description = NotificationService.CHANNEL_DESCRIPTION
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .strongReferencesEnabled(true)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .maxSizePercent(.03)
                    .directory(cacheDir)
                    .build()
            }
            .logger(DebugLogger())
            .crossfade(true)
            .build()
    }
}