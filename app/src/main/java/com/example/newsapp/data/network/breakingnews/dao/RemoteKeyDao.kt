package com.example.newsapp.data.network.breakingnews.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.newsapp.data.network.model.RemoteKey

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_keys WHERE url = :query")
    suspend fun remoteKeyByQuery(query: String?): RemoteKey

    @Query("DELETE FROM remote_keys")
    suspend fun deleteAllRemoteKeys()

    @Query("DELETE FROM remote_keys WHERE url = :query")
    suspend fun deleteByQuery(query: String?)
}