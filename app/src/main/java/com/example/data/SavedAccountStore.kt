package com.example.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class SavedAccountStore(context: Context) {
    private val prefs = context.getSharedPreferences("swipejobs_saved_accounts", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCOUNTS = "accounts_list"
        private const val KEY_ACTIVE_UID = "active_uid"
        private const val KEY_THEME_DARK = "theme_is_dark"
    }

    fun saveAccount(account: SavedAccount) {
        val list = getSavedAccounts().toMutableList()
        val index = list.indexOfFirst { it.uid == account.uid || it.email.equals(account.email, ignoreCase = true) }
        if (index != -1) {
            list[index] = account.copy(lastActive = System.currentTimeMillis())
        } else {
            list.add(0, account.copy(lastActive = System.currentTimeMillis()))
        }
        persistList(list)
        setActiveUid(account.uid)
    }

    fun getSavedAccounts(): List<SavedAccount> {
        val raw = prefs.getString(KEY_ACCOUNTS, null) ?: return emptyList()
        val result = mutableListOf<SavedAccount>()
        try {
            val jsonArray = JSONArray(raw)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                result.add(
                    SavedAccount(
                        uid = obj.optString("uid"),
                        email = obj.optString("email"),
                        name = obj.optString("name"),
                        category = obj.optString("category"),
                        photoUrl = obj.optString("photoUrl"),
                        lastActive = obj.optLong("lastActive"),
                        savedPassword = obj.optString("savedPassword")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun removeAccount(uid: String) {
        val list = getSavedAccounts().filter { it.uid != uid }
        persistList(list)
        if (getActiveUid() == uid) {
            prefs.edit().remove(KEY_ACTIVE_UID).apply()
        }
    }

    fun setActiveUid(uid: String) {
        prefs.edit().putString(KEY_ACTIVE_UID, uid).apply()
    }

    fun getActiveUid(): String? {
        return prefs.getString(KEY_ACTIVE_UID, null)
    }

    fun clearActiveUid() {
        prefs.edit().remove(KEY_ACTIVE_UID).apply()
    }

    fun isDarkTheme(): Boolean {
        return prefs.getBoolean(KEY_THEME_DARK, false)
    }

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit().putBoolean(KEY_THEME_DARK, isDark).apply()
    }

    private fun persistList(list: List<SavedAccount>) {
        val jsonArray = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("uid", item.uid)
                put("email", item.email)
                put("name", item.name)
                put("category", item.category)
                put("photoUrl", item.photoUrl)
                put("lastActive", item.lastActive)
                put("savedPassword", item.savedPassword)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_ACCOUNTS, jsonArray.toString()).apply()
    }
}
