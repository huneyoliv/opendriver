package br.com.opendriver.di

import androidx.room.Room
import br.com.opendriver.data.db.AppDatabase
import br.com.opendriver.data.datastore.DriverPreferencesDataSource
import br.com.opendriver.data.repository.LocationRepositoryImpl
import br.com.opendriver.data.repository.SettingsRepositoryImpl
import br.com.opendriver.data.repository.TripRepositoryImpl
import br.com.opendriver.domain.repository.LocationRepository
import br.com.opendriver.domain.repository.SettingsRepository
import br.com.opendriver.domain.repository.TripRepository
import br.com.opendriver.domain.usecase.GradeOfferUseCase
import br.com.opendriver.ui.dashboard.DashboardViewModel
import br.com.opendriver.ui.history.HistoryViewModel
import br.com.opendriver.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database Room
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "opendriver_local.db"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<AppDatabase>().tripOfferDao() }
    single { get<AppDatabase>().locationPointDao() }

    // DataStore Preferences
    single { DriverPreferencesDataSource(androidContext()) }

    // Repositories
    single<TripRepository> { TripRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single<LocationRepository> { LocationRepositoryImpl(get()) }

    // Use Cases
    factory { GradeOfferUseCase(get()) }

    // ViewModels
    viewModel { DashboardViewModel(get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
}
