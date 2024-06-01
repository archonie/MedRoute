package com.example.medroute.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.medroute.domain.model.Route

@Dao
interface RouteDao {

      @Query("SELECT * FROM routes ORDER BY id ASC")
      suspend fun getRoutes(): List<Route>
//
      @Insert(onConflict = OnConflictStrategy.REPLACE)
      suspend fun insertRoute(route: Route)

      @Query("SELECT * FROM routes WHERE id = :routeId")
      suspend fun getRoute(routeId: Int): Route

      @Query("DELETE FROM routes WHERE id = :routeId")
      suspend fun deleteRoute(routeId: Int)

      @Query("DELETE FROM routes")
      suspend fun deleteAllRoutes()


//    @Delete
//    suspend fun deleteRoute(route: RouteEntity)

}