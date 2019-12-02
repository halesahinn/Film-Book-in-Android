// ViewContact.java
// Activity for viewing a single contact.
package mac.example.com.filmbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class ViewFilm extends Activity
{
   private long rowID; // selected film's title
   private TextView titleTextView; // displays film's title
   private TextView yearTextView; // displays film's year
   private TextView directorTextView; // displays film's director
   private TextView imdbTextView; // displays film's imdb


   // called when the activity is first created
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.view_film);

      // get the EditTexts
      titleTextView = (TextView) findViewById(R.id.titleTextView);
      yearTextView = (TextView) findViewById(R.id.yearTextView);
      directorTextView = (TextView) findViewById(R.id.directorTextView);
      imdbTextView = (TextView) findViewById(R.id.imdbTextView);

      
      // get the selected film's row ID
      Bundle extras = getIntent().getExtras();
      rowID = extras.getLong(FilmBook.ROW_ID);
   } // end method onCreate

   // called when the activity is first created
   @Override
   protected void onResume()
   {
      super.onResume();
      
      // create new LoadFilmTask and execute it
      new LoadFilmTask().execute(rowID);
   } // end method onResume
   
   // performs database query outside GUI thread
   private class LoadFilmTask extends AsyncTask<Long, Object, Cursor>
   {
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(ViewFilm.this);

      // perform the database access
      @Override
      protected Cursor doInBackground(Long... params)
      {
         databaseConnector.open();
         
         // get a cursor containing all data on given entry
         return databaseConnector.getOneFilm(params[0]);
      } // end method doInBackground

      // use the Cursor returned from the doInBackground method
      @Override
      protected void onPostExecute(Cursor result)
      {
         super.onPostExecute(result);
   
         result.moveToFirst(); // move to the first item 
   
         // get the column index for each data item
         int titleIndex = result.getColumnIndex("title");
         int yearIndex = result.getColumnIndex("year");
         int directorIndex = result.getColumnIndex("director");
         int imdbIndex = result.getColumnIndex("imdb");

         // fill TextViews with the retrieved data
         titleTextView.setText(result.getString(titleIndex));
         yearTextView.setText(result.getString(yearIndex));
         directorTextView.setText(result.getString(directorIndex));
         imdbTextView.setText(result.getString(imdbIndex));

         result.close(); // close the result cursor
         databaseConnector.close(); // close database connection
      } // end method onPostExecute
   } // end class LoadFilmTask
      
   // create the Activity's menu from a menu resource XML file
   @Override
   public boolean onCreateOptionsMenu(Menu menu) 
   {
      super.onCreateOptionsMenu(menu);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.view_film_menu, menu);
      return true;
   } // end method onCreateOptionsMenu
   
   // handle choice from options menu
   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      switch (item.getItemId()) // switch based on selected MenuItem's ID
      {
         case R.id.editItem:
            // create an Intent to launch the AddEditFilm Activity
            Intent addEditFilm =
               new Intent(this, AddEditFilm.class);
            
            // pass the selected film's data as extras with the Intent
            addEditFilm.putExtra(FilmBook.ROW_ID, rowID);
            addEditFilm.putExtra("title", titleTextView.getText());
            addEditFilm.putExtra("year", yearTextView.getText());
            addEditFilm.putExtra("director", directorTextView.getText());
            addEditFilm.putExtra("imdb", imdbTextView.getText());
            startActivity(addEditFilm); // start the Activity
            return true;
         case R.id.deleteItem:
            deleteFilm(); // delete the displayed film
            return true;
         default:
            return super.onOptionsItemSelected(item);
      } // end switch
   } // end method onOptionsItemSelected
   
   // delete a film
   private void deleteFilm()
   {
      // create a new AlertDialog Builder
      AlertDialog.Builder builder = 
         new AlertDialog.Builder(ViewFilm.this);

      builder.setTitle(R.string.confirmTitle); // title bar string
      builder.setMessage(R.string.confirmMessage); // message to display

      // provide an OK button that simply dismisses the dialog
      builder.setPositiveButton(R.string.button_delete,
         new DialogInterface.OnClickListener()
         {
            @Override
            public void onClick(DialogInterface dialog, int button)
            {
               final DatabaseConnector databaseConnector = 
                  new DatabaseConnector(ViewFilm.this);

               // create an AsyncTask that deletes the film in another
               // thread, then calls finish after the deletion
               AsyncTask<Long, Object, Object> deleteTask =
                  new AsyncTask<Long, Object, Object>()
                  {
                     @Override
                     protected Object doInBackground(Long... params)
                     {
                        databaseConnector.deleteFilm(params[0]);
                        return null;
                     } // end method doInBackground

                     @Override
                     protected void onPostExecute(Object result)
                     {
                        finish(); // return to the FilmBook Activity
                     } // end method onPostExecute
                  }; // end new AsyncTask

               // execute the AsyncTask to delete film at rowID
               deleteTask.execute(new Long[] { rowID });               
            } // end method onClick
         } // end anonymous inner class
      ); // end call to method setPositiveButton
      
      builder.setNegativeButton(R.string.button_cancel, null);
      builder.show(); // display the Dialog
   } // end method deleteFilm
} // end class ViewFilm
