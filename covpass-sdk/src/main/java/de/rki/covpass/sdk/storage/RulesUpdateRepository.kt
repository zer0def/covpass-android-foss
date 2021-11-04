/*
 * (C) Copyright IBM Deutschland GmbH 2021
 * (C) Copyright IBM Corp. 2021
 */

package de.rki.covpass.sdk.storage

import com.ensody.reactivestate.SuspendMutableValueFlow
import de.rki.covpass.sdk.storage.DscRepository.Companion.NO_UPDATE_YET
import java.time.Instant

public class RulesUpdateRepository(
    store: CborSharedPrefsStore
) {

    public val lastRulesUpdate: SuspendMutableValueFlow<Instant> = store.getData("last_rules_update", NO_UPDATE_YET)
    public val localDatabaseVersion: SuspendMutableValueFlow<Int> =
        store.getData("local_database_update_version", 0)

    public suspend fun markRulesUpdated() {
        lastRulesUpdate.set(Instant.now())
    }

    public suspend fun updateLocalDatabaseVersion() {
        localDatabaseVersion.set(CURRENT_LOCAL_DATABASE_VERSION)
    }

    public companion object {
        public const val CURRENT_LOCAL_DATABASE_VERSION: Int = 1
    }
}
