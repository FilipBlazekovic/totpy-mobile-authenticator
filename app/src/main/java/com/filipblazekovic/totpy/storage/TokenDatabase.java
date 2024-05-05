package com.filipblazekovic.totpy.storage;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.filipblazekovic.totpy.model.db.DBToken;

@Database(entities = {DBToken.class}, version = 1)
public abstract class TokenDatabase extends RoomDatabase {

  public abstract TokenDao tokenDao();

}
