// DatabaseConnector.java
// Provides easy connection and creation of UserFilms database.
package mac.example.com.filmbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import java.util.Collections;

public class DatabaseConnector 
{
   // database name
   private static final String DATABASE_NAME = "UserFilms";
   private SQLiteDatabase database; // database object
   private DatabaseOpenHelper databaseOpenHelper; // database helper

   // public constructor for DatabaseConnector
   public DatabaseConnector(Context context) 
   {
      // create a new DatabaseOpenHelper
      databaseOpenHelper = 
         new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
   } // end DatabaseConnector constructor

   // open the database connection
   public void open() throws SQLException 
   {
      // create or open a database for reading/writing
      database = databaseOpenHelper.getWritableDatabase();
   } // end method open

   // close the database connection
   public void close() 
   {
      if (database != null)
         database.close(); // close the database connection
   } // end method close

   // inserts a new record in the database
   public void insertFilm(String title, String director, String year,
      String imdb)
   {
      ContentValues newFilm = new ContentValues();
      newFilm.put("title", title);
      newFilm.put("director", director);
      newFilm.put("year", year);
      newFilm.put("imdb", imdb);

      open(); // open the database
      database.insert("films", null, newFilm);
      close(); // close the database
   } // end method insertFilm

   // inserts a new film in the database
   public void updateFilm(long id, String title, String director,
      String year, String imdb)
   {
      ContentValues editFilm = new ContentValues();
      editFilm.put("title", title);
      editFilm.put("director", director);
      editFilm.put("year", year);
      editFilm.put("imdb", imdb);

      open(); // open the database
      database.update("films", editFilm, "_id=" + id, null);
      close(); // close the database
   } // end method updateFilm

   // return a Cursor with all film information in the database
   public Cursor getAllFilms()
   {
      return database.query("films", new String[] {"_id", "title"},
         null, null, null, null, "title");
   } // end method getAllFilms

   // get a Cursor containing all information about the film specified
   // by the given id
   public Cursor getOneFilm(long id)
   {
      return database.query(
         "films", null, "_id=" + id, null, null, null, null);
   } // end method getOnFilm

   // delete the film specified by the given String title
   public void deleteFilm(long id)
   {
      open(); // open the database
      database.delete("films", "_id=" + id, null);
      close(); // close the database
   } // end method deleteFilm
   
   private class DatabaseOpenHelper extends SQLiteOpenHelper 
   {
      // public constructor
      public DatabaseOpenHelper(Context context, String title,
         CursorFactory factory, int version) 
      {
         super(context, title, factory, version);
      } // end DatabaseOpenHelper constructor

      // creates the films table when the database is created
      @Override
      public void onCreate(SQLiteDatabase db) 
      {
         // query to create a new table title films
         String createQuery = "CREATE TABLE films" +
            "(_id integer primary key autoincrement," +
            "title TEXT, director TEXT, year TEXT," +
            "imdb TEXT);";
         
         db.execSQL(createQuery); // execute the query
      } // end method onCreate

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, 
          int newVersion) 
      {
      } // end method onUpgrade
   } // end class DatabaseOpenHelper
} // end class DatabaseConnector

