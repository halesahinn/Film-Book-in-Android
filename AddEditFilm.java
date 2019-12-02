// AddEditFilm.java
// Activity for adding a new entry to or  
// editing an existing entry in the film book.
package mac.example.com.filmbook;

//mac.example.com.filmbook
import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddEditFilm extends Activity
{
   private long rowID; // id of film being edited, if any
   
   // EditTexts for film information
   private EditText titleEditText;
   private EditText yearEditText;
   private EditText directorEditText;
   private EditText imdbEditText;

   
   // called when the Activity is first started
   @Override
   public void onCreate(Bundle savedInstanceState) 
   {
      super.onCreate(savedInstanceState); // call super's onCreate
      setContentView(R.layout.add_film); // inflate the UI

      titleEditText = (EditText) findViewById(R.id.titleEditText);
      directorEditText = (EditText) findViewById(R.id.yearEditText);
      yearEditText = (EditText) findViewById(R.id.directorEditText);
      imdbEditText = (EditText) findViewById(R.id.imdbEditText);

      
      Bundle extras = getIntent().getExtras(); // get Bundle of extras

      // if there are extras, use them to populate the EditTexts
      if (extras != null)
      {
         rowID = extras.getLong("row_id");
         titleEditText.setText(extras.getString("title"));
         directorEditText.setText(extras.getString("director"));
         yearEditText.setText(extras.getString("year"));
         imdbEditText.setText(extras.getString("imdb"));

      } // end if
      
      // set event listener for the Save Film Button
      Button saveFilmButton =
         (Button) findViewById(R.id.saveFilmButton);
      saveFilmButton.setOnClickListener(saveFilmButtonClicked);
   } // end method onCreate

   // responds to event generated when user clicks the Done Button
   OnClickListener saveFilmButtonClicked = new OnClickListener()
   {
      @Override
      public void onClick(View v) 
      {
         if (titleEditText.getText().length() != 0)
         {
            AsyncTask<Object, Object, Object> saveFilmTask =
               new AsyncTask<Object, Object, Object>() 
               {
                  @Override
                  protected Object doInBackground(Object... params) 
                  {
                     saveFilm(); // save film to the database
                     return null;
                  } // end method doInBackground
      
                  @Override
                  protected void onPostExecute(Object result) 
                  {
                     finish(); // return to the previous Activity
                  } // end method onPostExecute
               }; // end AsyncTask
               
            // save the film to the database using a separate thread
            saveFilmTask.execute((Object[]) null);
         } // end if
         else
         {
            // create a new AlertDialog Builder
            AlertDialog.Builder builder = 
               new AlertDialog.Builder(AddEditFilm.this);
      
            // set dialog title & message, and provide Button to dismiss
            builder.setTitle(R.string.errorTitle); 
            builder.setMessage(R.string.errorMessage);
            builder.setPositiveButton(R.string.errorButton, null); 
            builder.show(); // display the Dialog
         } // end else
      } // end method onClick
   }; // end OnClickListener saveFilmButtonClicked

   // saves film information to the database
   private void saveFilm()
   {
      // get DatabaseConnector to interact with the SQLite database
      DatabaseConnector databaseConnector = new DatabaseConnector(this);

      if (getIntent().getExtras() == null)
      {
         // insert the film information into the database
         databaseConnector.insertFilm(
            titleEditText.getText().toString(),
            directorEditText.getText().toString(),
            yearEditText.getText().toString(),
            imdbEditText.getText().toString());

      } // end if
      else
      {
         databaseConnector.updateFilm(rowID,
            titleEditText.getText().toString(),
            directorEditText.getText().toString(),
            yearEditText.getText().toString(),
            imdbEditText.getText().toString());

      } // end else
   } // end class saveFilm
} // end class AddEditFilm

