package edu.temple.bc2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BookListFrag.OnBookSelectedInterface {
    BookDetailsFrag bookDetailsFrag;
    Fragment container1Fragment;
    Fragment container2Fragment; // BookDetailsFragment in landscape
    ArrayList<Book> books;
    Button searchBtn;
    EditText searchInput;
    String searchQuery = "";
    boolean singlePane;


    Handler booksHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // Response to process from worker thread, in this case a list of books to parse
            try {
                JSONArray booksArray = new JSONArray(msg.obj.toString());
                books = new ArrayList<>();
                for (int i = 0; i < booksArray.length(); i++) {
                    // Get book at index
                    JSONObject bookObject = booksArray.getJSONObject(i);
                    // Create Book using JSON data
                    Book newBook = new Book(
                            bookObject.getInt("book_id"),
                            bookObject.getString("title"),
                            bookObject.getString("author"),
                            bookObject.getString("duration"),
                            bookObject.getInt("published"),
                            bookObject.getString("cover_url"));
                    // Add newBook to ArrayList<Book>
                    books.add(newBook);
                }

                container1Fragment = getSupportFragmentManager().findFragmentById(R.id.container_1);
                container2Fragment = getSupportFragmentManager().findFragmentById(R.id.container_2);
                singlePane = (findViewById(R.id.container_2) == null); // check if in single pane mode

                if (container1Fragment == null && singlePane) { // App opened in portrait mode
                    // App opened in portrait mode
                    Log.d("App opened in portrait mode. Single pane should be true == ", String.valueOf(singlePane));
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, ViewPagerFragment.newInstance(books))
                            .commit();
                } else if (container1Fragment == null) { // App opened in landscape mode
                    Log.d("App opened in landscape mode. Single pane should be false == ", String.valueOf(singlePane));
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, BookListFrag.newInstance(books))
                            .commit();
                } else if (container1Fragment instanceof ViewPagerFragment && !searchQuery.equals("")) { // Books were searched in portrait mode
                    Log.d("Books were searched for in Portrait mode: Single pane should be true == ", String.valueOf(singlePane));
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, ViewPagerFragment.newInstance(books))
                            .commit();
                } else if (container1Fragment instanceof BookListFrag && !searchQuery.equals("")) { // Books were searched in landscape mode
                    Log.d("Books were searched for in Landscape mode: Single pane should be false == ", String.valueOf(singlePane));
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, BookListFrag.newInstance(books))
                            .commit();
                } else if (searchQuery.equals("") && ((container1Fragment instanceof ViewPagerFragment))) { // Empty search made in portrait mode
                    Log.d("Empty search after searching should get back all books. Single pane == ", String.valueOf(singlePane));
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, ViewPagerFragment.newInstance(books))
                            .commit();
                } else if (searchQuery.equals("") && ((container1Fragment instanceof BookListFrag))) {
                    Log.d("Empty search after searching should get back all books. Single pane == ", String.valueOf(singlePane));
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, BookListFrag.newInstance(books))
                            .commit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        searchInput = findViewById(R.id.searchInput);
        searchBtn = findViewById(R.id.searchBtn);

        container1Fragment = getSupportFragmentManager().findFragmentById(R.id.container_1);
        container2Fragment = getSupportFragmentManager().findFragmentById(R.id.container_2);
        singlePane = (findViewById(R.id.container_2) == null);

        // Start-up query
        if (container1Fragment == null && container2Fragment == null) { // if start up, both of these fragment containers are null, do first fetch to get all books
            Log.d("CONTAINER 1 FRAGMENT NULL, FETCHING BOOKS", String.valueOf(singlePane));
            fetchBooks(null);
        }


        if (container1Fragment instanceof BookListFrag && singlePane) { // From landscape to portrait after initial load without searching
            Log.d("Went back to portrait from landscape. Single pane should be true == ", String.valueOf(singlePane));

            if (container1Fragment != null && ((BookListFrag) container1Fragment).getBooks() != null) {
                Log.d("Passing books from BookListFragment to ViewPagerFragment. Single pane should be true == ", String.valueOf(singlePane));
                books = ((BookListFrag) container1Fragment).getBooks();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_1, ViewPagerFragment.newInstance(books))
                        .commit();
            }
        } else if (container1Fragment instanceof ViewPagerFragment && !singlePane) { // From portrait to landscape after initial load without searching
            Log.d("Went from portrait to landscape. Single pane should be false == ", String.valueOf(singlePane));

            if (container1Fragment != null && ((ViewPagerFragment) container1Fragment).getBooks() != null) {
                Log.d("Passing books from ViewPagerFragment to BookListFragment. Single pane should be false == ", String.valueOf(singlePane));

                for (int i = 0; i < ((ViewPagerFragment) container1Fragment).getBooks().size(); i++) {
                    Log.d("Books currently in ViewPagerFragment passing to BookListFragment: Single pane should be false == ", String.valueOf(singlePane));
                    Log.d("book: ", ((ViewPagerFragment) container1Fragment).getBooks().get(i).toString());
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_1, BookListFrag.newInstance(books))
                        .commit();
            }
        } else if (container1Fragment instanceof BookListFrag) { // Tablet size
            Log.d("Tablet size: ", String.valueOf(container1Fragment));


            if (container1Fragment != null && ((BookListFrag) container1Fragment).getBooks() != null) {
                Log.d("Passing books from ViewPagerFragment to BookListFragment. Single pane should be false == ", String.valueOf(singlePane));
                books = ((BookListFrag) container1Fragment).getBooks();
                for (int i = 0; i < ((BookListFrag) container1Fragment).getBooks().size(); i++) {
                    Log.d("Books currently in ViewPagerFragment passing to BookListFragment: Single pane should be false == ", String.valueOf(singlePane));
                    Log.d("book: ", ((BookListFrag) container1Fragment).getBooks().get(i).toString());
                }
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_1, BookListFrag.newInstance(books))
                        .commit();
            }
        }

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchQuery = searchInput.getText().toString();
                container2Fragment = getSupportFragmentManager().findFragmentById(R.id.container_2);
                if (container2Fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .remove(container2Fragment)
                            .commit();
                }

                // Do query for new books
                fetchBooks(searchQuery);
            }
        });
    }

    /* Fetches books */
    public void fetchBooks(final String searchString) {
        if (searchString == null || searchString.length() == 0) {

            new Thread() {
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = new URL("https://kamorris.com/lab/abdoul/booksearch.php");
                        Log.d("No search query entered. URL is: ", url.toString());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                        StringBuilder builder = new StringBuilder();
                        String response;
                        while ((response = reader.readLine()) != null) {
                            builder.append(response);
                        }

                        Message msg = Message.obtain();
                        msg.obj = builder.toString();
                        booksHandler.sendMessage(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {

            new Thread() {
                @Override
                public void run() {
                    URL url = null;
                    try {

                        url = new URL("https://kamorris.com/lab/abp/booksearch.php?search=" + searchString);
                        Log.d("Search URL is: ", url.toString());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                        StringBuilder builder = new StringBuilder();
                        String response;
                        while ((response = reader.readLine()) != null) {
                            builder.append(response);
                        }

                        Message msg = Message.obtain();
                        msg.obj = builder.toString();
                        booksHandler.sendMessage(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }


    @Override
    public void bookSelected(int position) {

        Book book = books.get(position);

        bookDetailsFrag = new BookDetailsFrag();
        Bundle detailsBundle = new Bundle();
        detailsBundle.putParcelable(BookDetailsFrag.BOOK_KEY, book);
        bookDetailsFrag.setArguments(detailsBundle);

        if (!singlePane) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_2, bookDetailsFrag)
                    .commit();

        }
    }
}