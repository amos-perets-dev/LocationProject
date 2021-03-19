package com.example.locationproject.util

import android.content.Context
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.listener.multi.BaseMultiplePermissionsListener
import io.reactivex.Single

class PermissionsUtil  : IPermissionsUtil {

    override fun isPermissionGranted(context: Context, vararg permissionsNames: String): Single<Boolean> {
        return Single.create<Boolean> { emitter ->
            Dexter.withContext(context)
                .withPermissions(permissionsNames.toList())
                .withListener(object : BaseMultiplePermissionsListener() {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        super.onPermissionsChecked(report)
                        if (report.areAllPermissionsGranted()) {
                            emitter.onSuccess(true)
                        } else {
                            emitter.onSuccess(false)
                        }
                    }
                })
                .check()
        }
    }

}