package edu.temple.bc2;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import android.widget.ImageView;
import java.util.Objects;


public class BookDetailsFrag extends Fragment {

    ConstraintLayout  bookDetailsView;
    ImageView bookCover;
    TextView bookTitle, bookAuthor, bookPublishedIn, bookPageLength;
    public static final String BOOK_KEY = "book";
    Book book;

    public BookDetailsFrag() {
        // Required empty public constructor
    }


    public static BookDetailsFrag newInstance(Book book) {
        BookDetailsFrag bookDetailsFrag = new BookDetailsFrag();
        Bundle args = new Bundle();
        args.putParcelable(BOOK_KEY, book);
        bookDetailsFrag.setArguments(args);
        return bookDetailsFrag;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            book = args.getParcelable(BOOK_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bookDetailsView = (ConstraintLayout) inflater.inflate(R.layout.fragment_book_details, container, false);
        return bookDetailsView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bookCover = Objects.requireNonNull(getView()).findViewById(R.id.bookCover);
        bookTitle = Objects.requireNonNull(getView()).findViewById(R.id.bookTitle);
        bookAuthor = getView().findViewById(R.id.bookAuthor);
        bookPublishedIn = getView().findViewById(R.id.bookPublishedIn);
        bookPageLength = getView().findViewById(R.id.bookPageLength);
        if (book != null) {
            displayBook(book);
        }
    }



    public void displayBook(Book Book) {
        Picasso.get().load(book.getCoverUrl()).into(bookCover);
        bookTitle.setText(book.getTitle());
        bookTitle.setGravity(Gravity.CENTER);

        bookAuthor.setText(book.getAuthor());
        bookAuthor.setGravity(Gravity.CENTER);

        bookPublishedIn.setText(String.format(getResources().getString(R.string.publishedIn), book.getPublished()));
        bookPublishedIn.setGravity(Gravity.CENTER);

        bookPageLength.setText(String.format(getResources().getString(R.string.pageLength), book.getDuration()));
        bookPageLength.setGravity(Gravity.CENTER);
    }

}
