package com.example.android.moviematch;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.moviematch.data.MovieRepo;

import java.util.List;

public class MovieSearchAdapter extends RecyclerView.Adapter<MovieSearchAdapter.SearchResultViewHolder> {
    private List<MovieRepo> mRepos;
    OnSearchItemClickListener mSeachItemClickListener;

    public interface OnSearchItemClickListener {
        void onSearchItemClick(MovieRepo repo);
    }

    MovieSearchAdapter(OnSearchItemClickListener searchItemClickListener) {
        mSeachItemClickListener = searchItemClickListener;
    }

    public void updateSearchResults(List<MovieRepo> repos) {
        mRepos = repos;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mRepos != null) {
            return mRepos.size();
        } else {
            return 0;
        }
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.search_result_item, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        holder.bind(mRepos.get(position));
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private TextView mSearchResultTV;

        public SearchResultViewHolder(View itemView) {
            super(itemView);
            mSearchResultTV = itemView.findViewById(R.id.tv_search_result);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MovieRepo searchResult = mRepos.get(getAdapterPosition());
                    mSeachItemClickListener.onSearchItemClick(searchResult);
                }
            });
        }

        public void bind(MovieRepo repo) {
            mSearchResultTV.setText(repo.title);
        }
    }
}