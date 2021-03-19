package com.example.locationproject.util.permission

import android.content.Context
import io.reactivex.Single

interface IPermissionsUtil {
    /**
     * Call to check if the permission is granted or not
     */
    fun isPermissionGranted(context: Context, vararg permissionsNames: String): Single<Boolean>
}