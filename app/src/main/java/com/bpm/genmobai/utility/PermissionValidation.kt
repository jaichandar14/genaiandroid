package com.bpm.genmobai.utility

import android.Manifest
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.bpm.genmobai.ui.dashboard.DashBoardActivity

object PermissionValidation {

//    fun permissionValidation(
//
//        appCon: DashBoardActivity,
//        appInfo: ApplicationInfo,
//        packageManager: PackageManager
//    ): HashMap<String, Int> {
//         var listOfPermissionApp = HashMap<String, Int>()
//        val appPermissions = getPackageInfo(
//            appInfo.packageName, PackageManager.GET_PERMISSIONS, appCon
//        )
////        var appPermissions = packageManager.getPackageInfoCompat(
////            appInfo.packageName, PackageManager.GET_META_DATA
////        )
//        Log.d("TAG", "permissionValidation:$appPermissions ")
//        if (appPermissions?.contains(Manifest.permission.RECORD_AUDIO) == true) {
//            if (packageManager.checkPermission(
//                    Manifest.permission.RECORD_AUDIO,
//                    appInfo.packageName
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                listOfPermissionApp["Microphone"] = 1
//                listOfPermissionApp.put("Microphone", 1)
//            } else {
//                listOfPermissionApp["Microphone"] = 2
//            }
//        }else{
//            Log.d("TAG", "permissionValidation: no mic ")
//        }
//        if (appPermissions?.contains(Manifest.permission.CAMERA) == true) {
//            if (packageManager.checkPermission(
//                    Manifest.permission.CAMERA,
//                    appInfo.packageName
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                listOfPermissionApp["Camera"] = 1
//            } else {
//                listOfPermissionApp["Camera"] = 2
//            }
//        }
//        if (appPermissions?.contains(Manifest.permission.ACCESS_FINE_LOCATION) == true) {
//            if (packageManager.checkPermission(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    appInfo.packageName
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                listOfPermissionApp["Location"] = 1
//            } else {
//                listOfPermissionApp["Location"] = 2
//            }
//        }
//        if (appPermissions?.contains(Manifest.permission.WRITE_CALENDAR) == true || appPermissions?.contains(
//                Manifest.permission.READ_CALENDAR
//            ) == true
//        ) {
//            if (packageManager.checkPermission(
//                    Manifest.permission.WRITE_CALENDAR,
//                    appInfo.packageName
//                ) == PackageManager.PERMISSION_GRANTED || packageManager.checkPermission(
//                    Manifest.permission.READ_CALENDAR,
//                    appInfo.packageName
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                listOfPermissionApp["Calendar"] = 1
//            } else {
//                listOfPermissionApp["Calendar"] = 2
//            }
//        }
//        if (appPermissions?.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE) == true || appPermissions?.contains(
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) == true
//        ) {
//            if (packageManager.checkPermission(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    appInfo.packageName
//                ) == PackageManager.PERMISSION_GRANTED || packageManager.checkPermission(
//                    Manifest.permission.READ_EXTERNAL_STORAGE,
//                    appInfo.packageName
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                listOfPermissionApp["Storage"] = 1
//            } else {
//                listOfPermissionApp["Storage"] = 2
//            }
//        }
//        if (appPermissions?.contains(Manifest.permission.WRITE_CONTACTS) == true || appPermissions?.contains(
//                Manifest.permission.READ_CONTACTS
//            ) == true
//        ) {
//            if (packageManager.checkPermission(
//                    Manifest.permission.WRITE_CONTACTS,
//                    appInfo.packageName
//                ) == PackageManager.PERMISSION_GRANTED || packageManager.checkPermission(
//                    Manifest.permission.READ_CONTACTS,
//                    appInfo.packageName
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                listOfPermissionApp["Contact"] = 1
//            } else {
//                listOfPermissionApp["Contact"] = 2
//            }
//        }
//        if (appPermissions?.contains(Manifest.permission.WRITE_CALL_LOG) == true || appPermissions?.contains(
//                Manifest.permission.READ_CALL_LOG
//            ) == true
//        ) {
//            if (packageManager.checkPermission(
//                    Manifest.permission.WRITE_CALL_LOG,
//                    appInfo.packageName
//                ) == PackageManager.PERMISSION_GRANTED || packageManager.checkPermission(
//                    Manifest.permission.READ_CALL_LOG,
//                    appInfo.packageName
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                listOfPermissionApp["Call Logs"] = 1
//            } else {
//                listOfPermissionApp["Call Logs"] = 2
//            }
//        }
//
//        return listOfPermissionApp
//    }

    private fun getPackageInfo(
        packageName: String,
        getPermissions: Int,
        appCon: DashBoardActivity
    ): Array<String>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            appCon.packageManager.getPackageInfo(
                packageName, PackageManager.PackageInfoFlags.of(getPermissions.toLong())
            ).requestedPermissions
        } else {
            @Suppress("DEPRECATION")
            appCon.packageManager.getPackageInfo(
                packageName,
                getPermissions
            ).requestedPermissions
        }
    }

    private fun PackageManager.getPackageInfoCompat(
        packageName: String, flags: Int = 0
    ): Array<out String>? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(
            packageName,
            PackageManager.PackageInfoFlags.of(flags.toLong())
        ).requestedPermissions
    } else {
        @Suppress("DEPRECATION") getPackageInfo(packageName, flags).requestedPermissions

    }

    fun permissionValidation(
        appCon: DashBoardActivity,
        appInfo: ApplicationInfo,
    ): java.util.HashMap<String, Int> {
        val listOfPermissionApp = HashMap<String, Int>()

        val packageManager = appCon.packageManager
        val packageName = appInfo.packageName
        Log.d("TAG", "permissionValidation: $packageName")
        val appPermissions = getPackageInfo(
            packageName, PackageManager.GET_PERMISSIONS, appCon
        )

        fun checkPermission(permission: String): Int {
            return if (packageManager.checkPermission(
                    permission,
                    packageName
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                1
            } else {
                2
            }
        }

        if (appPermissions?.contains(Manifest.permission.RECORD_AUDIO) == true) {
            listOfPermissionApp["Microphone"] = checkPermission(Manifest.permission.RECORD_AUDIO)
        }
        if (appPermissions?.contains(Manifest.permission.CAMERA) == true) {
            listOfPermissionApp["Camera"] = checkPermission(Manifest.permission.CAMERA)
        }
        if (appPermissions?.contains(Manifest.permission.ACCESS_FINE_LOCATION) == true) {
            listOfPermissionApp["Location"] =
                checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (appPermissions?.contains(Manifest.permission.WRITE_CALENDAR) == true || appPermissions?.contains(
                Manifest.permission.READ_CALENDAR
            ) == true
        ) {
            listOfPermissionApp["Calendar"] =
                checkPermission(Manifest.permission.WRITE_CALENDAR) or checkPermission(Manifest.permission.READ_CALENDAR)
        }
        if (appPermissions?.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE) == true || appPermissions?.contains(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == true
        ) {
            listOfPermissionApp["Storage"] =
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) or checkPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
        }
        if (appPermissions?.contains(Manifest.permission.WRITE_CONTACTS) == true || appPermissions?.contains(
                Manifest.permission.READ_CONTACTS
            ) == true
        ) {
            listOfPermissionApp["Contact"] =
                checkPermission(Manifest.permission.WRITE_CONTACTS) or checkPermission(Manifest.permission.READ_CONTACTS)
        }
        if (appPermissions?.contains(Manifest.permission.WRITE_CALL_LOG) == true || appPermissions?.contains(
                Manifest.permission.READ_CALL_LOG
            ) == true
        ) {
            listOfPermissionApp["Call Logs"] =
                checkPermission(Manifest.permission.WRITE_CALL_LOG) or checkPermission(Manifest.permission.READ_CALL_LOG)
        }

        return listOfPermissionApp
    }
}

