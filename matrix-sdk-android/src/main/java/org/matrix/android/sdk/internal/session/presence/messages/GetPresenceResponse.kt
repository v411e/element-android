/*
 * Copyright 2020 The Matrix.org Foundation C.I.C.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.matrix.android.sdk.internal.session.presence.messages

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
 data class GetPresenceResponse(
        @Json(name = "presence")
        val presence: PresenceEnum,
        @Json(name = "last_active_ago")
        val lastActiveAgo: Long? = null,
        @Json(name = "status_msg")
        val message: String? = null,
        @Json(name = "currently_active")
        val isCurrentlyActive: Boolean? = null
)