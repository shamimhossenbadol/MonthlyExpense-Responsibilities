package com.codesphere.monthlyexpense.data.database

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL
import com.codesphere.monthlyexpense.data.dao.ExpenseDao
import com.codesphere.monthlyexpense.data.model.ExpenseTemplate
import com.codesphere.monthlyexpense.data.model.MonthlyExpense

@Database(
    entities = [ExpenseTemplate::class, MonthlyExpense::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao

    companion object {
        /** v2: one record per (template, month), enforced by a unique index. Keeps existing data. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override suspend fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    """
                    DELETE FROM monthly_expenses WHERE id NOT IN (
                        SELECT MIN(id) FROM monthly_expenses GROUP BY templateId, year, month
                    )
                    """.trimIndent()
                )
                connection.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_monthly_expenses_templateId_year_month` " +
                        "ON `monthly_expenses` (`templateId`, `year`, `month`)"
                )
            }
        }

        /** v3: an icon per expense. Existing rows get an icon guessed from their name. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override suspend fun migrate(connection: SQLiteConnection) {
                for (table in listOf("expense_templates", "monthly_expenses")) {
                    connection.execSQL("ALTER TABLE `$table` ADD COLUMN `icon` TEXT NOT NULL DEFAULT 'receipt'")
                    for ((keyword, icon) in listOf(
                        "rent" to "home", "parent" to "family", "sister" to "person",
                        "electric" to "bolt", "internet" to "wifi", "personal" to "shopping"
                    )) {
                        connection.execSQL("UPDATE `$table` SET icon = '$icon' WHERE LOWER(name) LIKE '%$keyword%'")
                    }
                }
            }
        }

        /**
         * v4 (no schema change): older versions seeded six sample expenses on first launch.
         * Remove those samples, but only ones the user never touched: same name, amount and icon
         * as seeded, no notes, and never marked paid in any month. Anything edited or paid stays.
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override suspend fun migrate(connection: SQLiteConnection) {
                val samples = """
                    (name = 'House Rent' AND amount = 25000.0 AND icon = 'home') OR
                    (name = 'Parents'' Hand Cash' AND amount = 10000.0 AND icon = 'family') OR
                    (name = 'Sister''s Hand Cash' AND amount = 5000.0 AND icon = 'person') OR
                    (name = 'Electricity Bill' AND amount = 2500.0 AND icon = 'bolt') OR
                    (name = 'Internet Bill' AND amount = 1200.0 AND icon = 'wifi') OR
                    (name = 'Personal Expenses' AND amount = 8000.0 AND icon = 'shopping')
                """.trimIndent()
                connection.execSQL("DROP TABLE IF EXISTS sample_ids")
                connection.execSQL(
                    """
                    CREATE TEMP TABLE sample_ids AS
                    SELECT t.id FROM expense_templates t
                    WHERE t.notes = '' AND ($samples)
                      AND NOT EXISTS (SELECT 1 FROM monthly_expenses m WHERE m.templateId = t.id AND m.isPaid = 1)
                      AND NOT EXISTS (
                        SELECT 1 FROM monthly_expenses m
                        WHERE m.templateId = t.id AND (m.name != t.name OR m.amount != t.amount)
                      )
                    """.trimIndent()
                )
                connection.execSQL("DELETE FROM monthly_expenses WHERE templateId IN (SELECT id FROM sample_ids)")
                connection.execSQL("DELETE FROM expense_templates WHERE id IN (SELECT id FROM sample_ids)")
                connection.execSQL("DROP TABLE sample_ids")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Pass the generated implementation directly instead of letting Room find
                // "AppDatabase_Impl" by reflection, which is fragile once R8 optimizes release builds.
                INSTANCE ?: Room.databaseBuilder<AppDatabase>(
                    context = context.applicationContext,
                    name = "monthly_expense_database",
                    factory = { AppDatabase_Impl() }
                )
                    .setDriver(AndroidSQLiteDriver())
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
