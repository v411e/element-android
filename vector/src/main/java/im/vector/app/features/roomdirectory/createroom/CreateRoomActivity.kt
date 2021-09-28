/*
 * Copyright 2019 New Vector Ltd
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

package im.vector.app.features.roomdirectory.createroom

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.airbnb.mvrx.MvRx
import com.google.android.material.appbar.MaterialToolbar
import im.vector.app.R
import im.vector.app.core.di.ScreenComponent
import im.vector.app.core.extensions.addFragment
import im.vector.app.core.platform.ToolbarConfigurable
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.ActivitySimpleBinding
import im.vector.app.features.roomdirectory.RoomDirectorySharedAction
import im.vector.app.features.roomdirectory.RoomDirectorySharedActionViewModel

/**
 * Simple container for [CreateRoomFragment]
 */
class CreateRoomActivity : VectorBaseActivity<ActivitySimpleBinding>(), ToolbarConfigurable {

    private lateinit var sharedActionViewModel: RoomDirectorySharedActionViewModel

    override fun getBinding() = ActivitySimpleBinding.inflate(layoutInflater)

    override fun getCoordinatorLayout() = views.coordinatorLayout

    override fun configure(toolbar: MaterialToolbar) {
        configureToolbar(toolbar)
    }

    override fun injectWith(injector: ScreenComponent) {
        injector.inject(this)
    }

    override fun initUiAndData() {
        if (isFirstCreation()) {
            val fragmentArgs: CreateRoomArgs = intent?.extras?.getParcelable(MvRx.KEY_ARG) ?: return
            addFragment(
                    R.id.simpleFragmentContainer,
                    CreateRoomFragment::class.java,
                    fragmentArgs
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedActionViewModel = viewModelProvider.get(RoomDirectorySharedActionViewModel::class.java)
        sharedActionViewModel
                .observe()
                .subscribe { sharedAction ->
                    when (sharedAction) {
                        is RoomDirectorySharedAction.Back,
                        is RoomDirectorySharedAction.Close             -> finish()
                        is RoomDirectorySharedAction.CreateRoomSuccess -> {
                            setResult(Activity.RESULT_OK, Intent().apply { putExtra(RESULT_CREATED_ROOM_ID, sharedAction.createdRoomId) })
                            finish()
                        }
                        else                                           -> {
                            // nop
                        }
                    }
                }
                .disposeOnDestroy()
    }

    companion object {

        const val RESULT_CREATED_ROOM_ID = "RESULT_CREATED_ROOM_ID"

        fun getIntent(context: Context,
                      initialName: String = "",
                      isSpace: Boolean = false,
                      openAfterCreate: Boolean = true,
                      currentSpaceId: String? = null): Intent {
            return Intent(context, CreateRoomActivity::class.java).apply {
                putExtra(MvRx.KEY_ARG, CreateRoomArgs(
                        initialName = initialName,
                        isSpace = isSpace,
                        openAfterCreate = openAfterCreate,
                        parentSpaceId = currentSpaceId
                ))
            }
        }
    }
}
