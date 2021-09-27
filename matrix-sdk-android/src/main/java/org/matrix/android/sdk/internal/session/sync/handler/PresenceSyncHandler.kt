/*
 * Copyright 2020 The Matrix.org Foundation C.I.C.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.matrix.android.sdk.internal.session.sync.handler

import io.realm.Realm
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.internal.database.model.RoomSummaryEntity
import org.matrix.android.sdk.internal.database.model.presence.UserPresenceEntity
import org.matrix.android.sdk.internal.database.query.presence.insertOrUpdate
import org.matrix.android.sdk.internal.database.query.updateDirectUserPresence
import org.matrix.android.sdk.internal.session.presence.messages.PresenceEnum
import org.matrix.android.sdk.internal.session.sync.model.PresenceSyncResponse
import javax.inject.Inject

internal class PresenceSyncHandler @Inject constructor() {

    fun handle(realm: Realm, presenceSyncResponse: PresenceSyncResponse?) {

        presenceSyncResponse?.events?.filter { event ->
            event.type == EventType.PRESENCE
        }?.forEach { event ->
            val content = event.content ?: return@forEach
            val userId = event.senderId ?: return@forEach
            val presence = content["presence"]?.let { PresenceEnum.from(it.toString()) } ?: return@forEach
            val lastActiveAgo = content["last_active_ago"] as? Long
            val statusMessage = content["status_msg"]?.toString()
            val isCurrentlyActive = content["currently_active"] as? Boolean
            val avatarUrl = content["avatar_url"]?.toString()

            val userPresenceEntity = UserPresenceEntity(
                    userId = userId,
                    lastActiveAgo = lastActiveAgo,
                    statusMessage = statusMessage,
                    isCurrentlyActive = isCurrentlyActive,
                    avatarUrl = avatarUrl
            ).apply {
                this.presence = presence
            }

            storePresenceToDB(realm, userPresenceEntity)

        }
    }

    /**
     * Store user presence to DB and update direct rooms accordingly
     */
    private fun storePresenceToDB(realm: Realm, userPresenceEntity: UserPresenceEntity) =
            UserPresenceEntity.insertOrUpdate(realm, userPresenceEntity)?.apply {
                RoomSummaryEntity.updateDirectUserPresence(realm, userPresenceEntity.userId, this)
            }
}