// AddressBook.java
// Main activity for the Address Book app.
package mac.example.com.filmbook;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.Collections;

public class FilmBook extends ListActivity
{
   public static final String ROW_ID = "row_id"; // Intent extra key
   private ListView filmListView; // the ListActivity's ListView
   private CursorAdapter filmAdapter; // adapter for ListView
   
   // called when the activity is first created
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState); // call super's onCreate
      filmListView = getListView(); // get the built-in ListView
      filmListView.setOnItemClickListener(viewFilmListener);

      // map each film's title to a TextView in the ListView layout
      String[] from = new String[] { "title" };
      int[] to = new int[] { R.id.filmTextView };
      filmAdapter = new SimpleCursorAdapter(
         FilmBook.this, R.layout.film_list_item, null, from, to);
      setListAdapter(filmAdapter);// set filmView's adapter
   } // end method onCreate

   @Override
   protected void onResume() 
   {
      super.onResume(); // call super's onResume method
      
       // create new GetFilmsTask and execute it
       new GetFilmsTask().execute((Object[]) null);
    } // end method onResume

   @Override
   protected void onStop() 
   {
      Cursor cursor = filmAdapter.getCursor(); // get current Cursor
      
      if (cursor != null) 
         cursor.deactivate(); // deactivate it
      
      filmAdapter.changeCursor(null); // adapted now has no Cursor
      super.onStop();
   } // end method onStop

   // performs database query outside GUI thread
   private class GetFilmsTask extends AsyncTask<Object, Object, Cursor>
   {
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(FilmBook.this);

      // perform the database access
      @Override
      protected Cursor doInBackground(Object... params)
      {
         databaseConnector.open();

         // get a cursor containing call films
         return databaseConnector.getAllFilms();
      } // end method doInBackground

      // use the Cursor returned from the doInBackground method
      @Override
      protected void onPostExecute(Cursor result)
      {
         filmAdapter.changeCursor(result); // set the adapter's Cursor
         databaseConnector.close();
      } // end method onPostExecute
   } // end class GetFilmsTask
      
   // create the Activity's menu from a menu resource XML file
   @Override
   public boolean onCreateOptionsMenu(Menu menu) 
   {
      super.onCreateOptionsMenu(menu);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.filmbook_menu, menu);
      return true;
   } // end method onCreateOptionsMenu
   
   // handle choice from options menu
   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      // create a new Intent to launch the AddEditFilm Activity
      Intent addNewFilm =
         new Intent(FilmBook.this, AddEditFilm.class);
      startActivity(addNewFilm); // start the AddEditFilm Activity
      return super.onOptionsItemSelected(item); // call super's method
   } // end method onOptionsItemSelected

   // event listener that responds to the user touching a film's title
   // in the ListView
   OnItemClickListener viewFilmListener = new OnItemClickListener()
   {
      @Override
      public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
         long arg3) 
      {
         // create an Intent to launch the ViewFilm Activity
         Intent viewFilm =
            new Intent(FilmBook.this, ViewFilm.class);
         
         // pass the selected film's row ID as an extra with the Intent
         viewFilm.putExtra(ROW_ID, arg3);
         startActivity(viewFilm); // start the ViewFilm Activity
      } // end method onItemClick
   }; // end viewFilmListener
} // end class FilmBook

