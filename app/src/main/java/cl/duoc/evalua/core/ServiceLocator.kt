package cl.duoc.evalua.core

import android.content.Context
import androidx.room.Room
import cl.duoc.evalua.data.local.AppDatabase

object ServiceLocator {
    lateinit var db: AppDatabase
        private set

    fun init(context: Context) {
        if (!::db.isInitialized) {
            db = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "eval_gastro.db"
            ).build()
        }
    }
}
