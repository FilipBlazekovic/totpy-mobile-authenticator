package com.filipblazekovic.totpy.storage;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.filipblazekovic.totpy.model.db.DBToken;
import java.util.List;

@Dao
public interface TokenDao {

  @Query("SELECT * FROM tokens ORDER BY category, issuer_name, account")
  List<DBToken> getAll();

  @Query("SELECT * FROM tokens WHERE id = :id ORDER BY category, issuer_name, account")
  DBToken getById(Integer id);

  @Query("SELECT * FROM tokens WHERE id IN (:ids) ORDER BY category, issuer_name, account")
  List<DBToken> getByIds(List<Integer> ids);

  @Insert
  void insert(DBToken token);

  @Insert
  void insertAll(List<DBToken> tokens);

  @Update
  void update(DBToken token);

  @Query("DELETE FROM tokens WHERE id IN (:ids)")
  void deleteByIds(List<Integer> ids);

  @Query("DELETE FROM tokens")
  void deleteAll();

}

