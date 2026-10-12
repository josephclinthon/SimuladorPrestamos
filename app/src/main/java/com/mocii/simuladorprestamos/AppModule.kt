package com.mocii.simuladorprestamos

import com.mocii.simuladorprestamos.ui.prestamo.master.CuotasAdapter
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    factory { CuotasAdapter(androidContext()) }
}
