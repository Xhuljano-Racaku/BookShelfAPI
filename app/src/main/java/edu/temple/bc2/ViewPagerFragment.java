package edu.temple.bc2;

import android.os.Bundle;
import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


public class ViewPagerFragment extends Fragment {

    ViewPager mPager;
    PagerAdapter pagerAdapter;
    ArrayList<Book> books;
    ArrayList<BookDetailsFrag> bookDetailsFrags;
    public final static String BOOKS_KEY = "books";

    public ViewPagerFragment() {

    }

    public static ViewPagerFragment newInstance(ArrayList<Book> books) {
        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(BOOKS_KEY, books);
        viewPagerFragment.setArguments(args);
        return viewPagerFragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        bookDetailsFrags = new ArrayList<>();
        if (args != null) {
            books = args.getParcelableArrayList(BOOKS_KEY);

            if (books != null) {
                for (int i = 0; i < books.size(); i++) {
                    bookDetailsFrags.add(BookDetailsFrag.newInstance(books.get(i)));
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        mPager = view.findViewById(R.id.viewPagerFragment);

        pagerAdapter = new BookDetailsPagerAdapter(getChildFragmentManager(), bookDetailsFrags);
        mPager.setAdapter(pagerAdapter);
        return view;
    }




    private class BookDetailsPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<BookDetailsFrag> bookDetailsFrags;

        BookDetailsPagerAdapter(FragmentManager fm, ArrayList<BookDetailsFrag> bookDetailsFrags) {
            super(fm);
            this.bookDetailsFrags = bookDetailsFrags;
        }

        @Override
        public Fragment getItem(int position) {
            return bookDetailsFrags.get(position);
        }




        @Override
        public int getCount() {
            return bookDetailsFrags.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }
    public ArrayList<BookDetailsFrag> getBooks() {
        return this.bookDetailsFrags;
    }



}
